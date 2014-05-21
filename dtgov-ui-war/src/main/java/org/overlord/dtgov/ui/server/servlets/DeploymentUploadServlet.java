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
import org.overlord.dtgov.ui.server.i18n.Messages;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.dtgov.ui.server.util.ExceptionUtils;
import org.overlord.sramp.atom.archive.SrampArchive;
import org.overlord.sramp.atom.archive.expand.DefaultMetaDataFactory;
import org.overlord.sramp.atom.archive.expand.ZipToSrampArchive;
import org.overlord.sramp.atom.archive.expand.registry.ZipToSrampArchiveRegistry;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.common.ArtifactType;

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

    /**
     * Constructor.
     */
    public DeploymentUploadServlet() {
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
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
            String version = null;
            String fileName = null;
            InputStream artifactContent = null;
            try {
                List<FileItem> items = upload.parseRequest(req);
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        if (item.getFieldName().equals("deploymentType")) { //$NON-NLS-1$
                            deploymentType = item.getString();
                        } else if (item.getFieldName().equals("version")) { //$NON-NLS-1$
                            version = item.getString();
                        }
                    } else {
                        fileName = item.getName();
                        if (fileName != null)
                            fileName = FilenameUtils.getName(fileName);
                        artifactContent = item.getInputStream();
                    }
                }

                // Default version is 1.0
                if (version == null || version.trim().length() == 0) {
                    version = "1.0"; //$NON-NLS-1$
                }

                // Now that the content has been extracted, process it (upload the artifact to the s-ramp repo).
                responseMap = uploadArtifact(deploymentType, fileName, version, artifactContent);
            } catch (SrampAtomException e) {
                responseMap = new HashMap<String, String>();
                responseMap.put("exception", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                responseMap.put("exception-message", e.getMessage()); //$NON-NLS-1$
                responseMap.put("exception-stack", ExceptionUtils.getRootStackTrace(e)); //$NON-NLS-1$
            } catch (Throwable e) {
                responseMap = new HashMap<String, String>();
                responseMap.put("exception", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                responseMap.put("exception-message", e.getMessage()); //$NON-NLS-1$
                responseMap.put("exception-stack", ExceptionUtils.getRootStackTrace(e)); //$NON-NLS-1$
            } finally {
                IOUtils.closeQuietly(artifactContent);
            }
            writeToResponse(responseMap, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    Messages.i18n.format("DeploymentUploadServlet.ContentTypeInvalid")); //$NON-NLS-1$
        }
    }

    /**
     * Upload the artifact to the S-RAMP repository.
     * @param deploymentType the type of the deployment (from the UI form)
     * @param fileName the file name of the deployment being uploaded
     * @param version the artifact version
     * @param deploymentContent the content of the deployment
     * @throws Exception
     */
    private Map<String, String> uploadArtifact(String deploymentType, String fileName,
            String version, InputStream deploymentContent) throws Exception {
        if (deploymentContent == null)
            throw new Exception(Messages.i18n.format("DeploymentUploadServlet.NoDeploymentFile")); //$NON-NLS-1$
        File tempFile = stashResourceContent(deploymentContent);
        Map<String, String> responseParams = new HashMap<String, String>();

        if (deploymentType.indexOf('/') != -1) {
            deploymentType = deploymentType.substring(deploymentType.indexOf('/') + 1);
        }

        try {
            if ("DeploymentBundle".equals(deploymentType)) { //$NON-NLS-1$
                uploadBundle(tempFile, responseParams);
            } else {
                uploadSingleDeployment(deploymentType, fileName, tempFile, responseParams, version);
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
            responseParams.put("batch", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            responseParams.put("batchTotal", String.valueOf(numSuccess + numFailed)); //$NON-NLS-1$
            responseParams.put("batchNumSuccess", String.valueOf(numSuccess)); //$NON-NLS-1$
            responseParams.put("batchNumFailed", String.valueOf(numFailed)); //$NON-NLS-1$
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
     * @param version
     * @throws Exception
     */
    private void uploadSingleDeployment(String deploymentType, String fileName,
            File tempFile, Map<String, String> responseParams, String version) throws Exception {
        ArtifactType at = ArtifactType.valueOf(deploymentType);
        String uuid = null;
        // First, upload the deployment
        InputStream contentStream = null;
        try {
            contentStream = FileUtils.openInputStream(tempFile);
            BaseArtifactType artifact = at.newArtifactInstance();
            artifact.setName(fileName);
            artifact.setVersion(version);
            artifact = clientAccessor.getClient().uploadArtifact(artifact, contentStream);
            responseParams.put("model", at.getArtifactType().getModel()); //$NON-NLS-1$
            responseParams.put("type", at.getArtifactType().getType()); //$NON-NLS-1$
            responseParams.put("uuid", artifact.getUuid()); //$NON-NLS-1$
            uuid = artifact.getUuid();
        } finally {
            IOUtils.closeQuietly(contentStream);
        }

        // Try to expand the artifact (works if an expander is available for the given artifact type).
        ZipToSrampArchive j2sramp = null;
        SrampArchive archive = null;
        try {
            j2sramp = ZipToSrampArchiveRegistry.createExpander(at, tempFile);
            if (j2sramp != null) {
                j2sramp.setContextParam(DefaultMetaDataFactory.PARENT_UUID, uuid);
                archive = j2sramp.createSrampArchive();
                clientAccessor.getClient().uploadBatch(archive);
            }
        } finally {
            SrampArchive.closeQuietly(archive);
            ZipToSrampArchive.closeQuietly(j2sramp);
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
            resourceTempFile = File.createTempFile("dtgov-ui-upload", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
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
     * Writes the response values back to the http response.  This allows the calling code to
     * parse the response values for display to the user.
     *
     * @param responseMap the response params to write to the http response
     * @param response the http response
     * @throws IOException
     */
    private void writeToResponse(Map<String, String> responseMap, HttpServletResponse response) throws IOException {
        // Note: setting the content-type to text/html because otherwise IE prompt the user to download
        // the result rather than handing it off to the GWT form response handler.
        // See JIRA issue https://issues.jboss.org/browse/SRAMPUI-103
        response.setContentType("text/html; charset=UTF8"); //$NON-NLS-1$
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
