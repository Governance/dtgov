/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.ui.server.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.ui.server.DtgovUIConfig;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.dtgov.ui.server.util.ExceptionUtils;
import org.overlord.sramp.atom.archive.SrampArchive;
import org.overlord.sramp.atom.archive.jar.DefaultMetaDataFactory;
import org.overlord.sramp.atom.archive.jar.DiscoveredArtifact;
import org.overlord.sramp.atom.archive.jar.JarToSrampArchive;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;

/**
 * A standard servlet that artifact content is POSTed to in order to add new artifacts
 * to the s-ramp repository.
 *
 * @author eric.wittmann@redhat.com
 */
public class DeploymentUploadServlet extends HttpServlet {

	private static final long serialVersionUID = DeploymentUploadServlet.class.hashCode();

    @Inject
    private SrampApiClientAccessor clientAccessor;
    @Inject
    private DtgovUIConfig config;

	/**
	 * Constructor.
	 */
	public DeploymentUploadServlet() {
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		// Extract the relevant content from the POST'd form
		if (ServletFileUpload.isMultipartContent(req)) {
			Map<String, String> responseMap;
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			String deploymentType = null;
			String fileName = null;
			InputStream artifactContent = null;
			try {
				List<FileItem> items = upload.parseRequest(req);
				for (FileItem item : items) {
					if (item.isFormField()) {
						if (item.getFieldName().equals("deploymentType")) {
							deploymentType = item.getString();
						}
					} else {
						fileName = item.getName();
						if (fileName != null)
							fileName = FilenameUtils.getName(fileName);
						artifactContent = item.getInputStream();
					}
				}

				// Now that the content has been extracted, process it (upload the artifact to the s-ramp repo).
				responseMap = uploadArtifact(deploymentType, fileName, artifactContent);
			} catch (SrampAtomException e) {
				responseMap = new HashMap<String, String>();
				responseMap.put("exception", "true");
				responseMap.put("exception-message", e.getMessage());
				responseMap.put("exception-stack", ExceptionUtils.getRootStackTrace(e));
			} catch (Throwable e) {
				responseMap = new HashMap<String, String>();
				responseMap.put("exception", "true");
				responseMap.put("exception-message", e.getMessage());
				responseMap.put("exception-stack", ExceptionUtils.getRootStackTrace(e));
			} finally {
				IOUtils.closeQuietly(artifactContent);
			}
			writeToResponse(responseMap, response);
		} else {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
		}
	}

	/**
	 * Upload the artifact to the S-RAMP repository.
	 * @param deploymentType the type of the deployment (from the UI form)
	 * @param fileName the file name of the deployment being uploaded
	 * @param deploymentContent the content of the deployment
	 * @throws Exception
	 */
	private Map<String, String> uploadArtifact(String deploymentType, String fileName,
			InputStream deploymentContent) throws Exception {
	    if (deploymentContent == null)
	        throw new Exception("No deployment file specified.");
		File tempFile = stashResourceContent(deploymentContent);
		Map<String, String> responseParams = new HashMap<String, String>();

		if (deploymentType.indexOf('/') != -1) {
		    deploymentType = deploymentType.substring(deploymentType.indexOf('/') + 1);
		}

		try {
		    if ("DeploymentBundle".equals(deploymentType)) {
		        uploadBundle(tempFile, responseParams);
		    } else {
		        uploadSingleDeployment(deploymentType, fileName, tempFile, responseParams);
		    }
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }

		return responseParams;
	}

    /**
     * Uploads an S-RAMP package to the repository.
     * @param tempFile
     * @param responseParams
     */
    private void uploadBundle(File tempFile, Map<String, String> responseParams) throws Exception {
        // TODO the bundle should probably be just a .zip containing a bunch of artifacts?  or should it be an actual s-ramp archive?
        SrampArchive archive = null;
        try {
            archive = new SrampArchive(tempFile);
            Map<String, ?> batch = clientAccessor.getClient().uploadBatch(archive);
            int numSuccess = 0;
            int numFailed = 0;
            for (String key : batch.keySet()) {
                Object object = batch.get(key);
                if (object instanceof BaseArtifactType) {
                    numSuccess++;
                } else {
                    numFailed++;
                }
            }
            // TODO turn these things into constants
            responseParams.put("batch", "true");
            responseParams.put("batchTotal", String.valueOf(numSuccess + numFailed));
            responseParams.put("batchNumSuccess", String.valueOf(numSuccess));
            responseParams.put("batchNumFailed", String.valueOf(numFailed));
        } finally {
            SrampArchive.closeQuietly(archive);
        }

    }

    /**
     * Uploads a single deployment to S-RAMP.
     * @param deploymentType
     * @param fileName
     * @param client
     * @param tempFile
     * @param responseParams
     * @throws Exception
     */
    private void uploadSingleDeployment(String deploymentType, String fileName,
            File tempFile, Map<String, String> responseParams) throws Exception {
        String uuid = null;
		// First, upload the deployment
        InputStream contentStream = null;
		try {
			contentStream = FileUtils.openInputStream(tempFile);
			ArtifactType at = ArtifactType.valueOf(deploymentType);
			BaseArtifactType artifact = at.newArtifactInstance();
			artifact.setName(fileName);
            artifact.getClassifiedBy().add(
                    config.getConfiguration().getString(DtgovUIConfig.DEPLOYMENT_INITIAL_CLASSIFIER,
                            "http://www.jboss.org/overlord/deployment-status.owl#DevTest"));
			artifact = clientAccessor.getClient().uploadArtifact(artifact, contentStream);
			responseParams.put("model", at.getArtifactType().getModel());
			responseParams.put("type", at.getArtifactType().getType());
			responseParams.put("uuid", artifact.getUuid());
			uuid = artifact.getUuid();
		} finally {
			IOUtils.closeQuietly(contentStream);
		}

		// Check if this is an expandable file type.  If it is, then expand it!
		if (isExpandable(fileName)) {
			JarToSrampArchive j2sramp = null;
			SrampArchive archive = null;
			try {
				final String parentUUID = uuid;
				j2sramp = new JarToSrampArchive(tempFile);
				j2sramp.setMetaDataFactory(new DefaultMetaDataFactory() {
					@Override
					public BaseArtifactType createMetaData(DiscoveredArtifact artifact) {
						BaseArtifactType metaData = super.createMetaData(artifact);
						SrampModelUtils.addGenericRelationship(metaData, "expandedFromDocument", parentUUID);
						return metaData;
					}
				});
				archive = j2sramp.createSrampArchive();
				clientAccessor.getClient().uploadBatch(archive);
			} finally {
				SrampArchive.closeQuietly(archive);
				JarToSrampArchive.closeQuietly(j2sramp);
			}
		}
    }

	/**
	 * Make a temporary copy of the resource by saving the content to a temp file.
	 * @param resourceInputStream
	 * @throws IOException
	 */
	private File stashResourceContent(InputStream resourceInputStream) throws IOException {
		File resourceTempFile = null;
		OutputStream oStream = null;
		try {
			resourceTempFile = File.createTempFile("dtgov-ui-upload", ".tmp");
			oStream = FileUtils.openOutputStream(resourceTempFile);
            IOUtils.copy(resourceInputStream, oStream);
            return resourceTempFile;
		} catch (IOException e) {
			FileUtils.deleteQuietly(resourceTempFile);
			throw e;
		} finally {
			IOUtils.closeQuietly(resourceInputStream);
			IOUtils.closeQuietly(oStream);
		}
	}

	/**
	 * Returns true if the uploaded file should be expanded.  We support expanding JAR, WAR,
	 * and EAR files.
	 * @param fileName the name of the uploaded file
	 * @return true if the file should be expanded
	 */
	private boolean isExpandable(String fileName) {
		String name = fileName.toLowerCase();
		return name.endsWith(".jar") || name.endsWith(".war") || name.endsWith(".ear");
	}

	/**
	 * Writes the response values back to the http response.  This allows the calling code to
	 * parse the response values for display to the user.
	 *
	 * @param responseMap the response params to write to the http response
	 * @param response the http response
	 * @throws IOException
	 */
	private void writeToResponse(Map<String, String> responseMap, HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF8");
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(response.getOutputStream(), JsonEncoding.UTF8);
        g.useDefaultPrettyPrinter();
        g.writeStartObject();
        for (java.util.Map.Entry<String, String> entry : responseMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            g.writeStringField(key, val);
        }
        g.writeEndObject();
        g.flush();
        g.close();
	}
}
