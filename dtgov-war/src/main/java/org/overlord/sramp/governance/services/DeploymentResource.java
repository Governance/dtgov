/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.sramp.governance.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.overlord.sramp.atom.MediaType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.SlashDecoder;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.overlord.sramp.governance.Target;
import org.overlord.sramp.governance.services.rhq.RHQDeployUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JAX-RS resource that handles deployment specific tasks.
 *
 *
 */
@Path("/deploy")
public class DeploymentResource {

    private static Logger logger = LoggerFactory.getLogger(DeploymentResource.class);
    private Governance governance = new Governance();

    /**
     * Constructor.
     */
    public DeploymentResource() {
    }

    /**
     * Governance POST to deploy an artifact by copying it onto the file system.
     *
     * @param target - name of the pre-configured deployment target
     * @param uuid
     *
     * @throws SrampAtomException
     */
    @POST
    @Path("copy/{target}/{uuid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response copy(@Context HttpServletRequest request,
            @PathParam("target") String targetRef,
            @PathParam("uuid") String uuid) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            // 0. run the decoder on the arguments
            targetRef = SlashDecoder.decode(targetRef);
            uuid = SlashDecoder.decode(uuid);

            // 1. get the artifact from the repo
            SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
            String query = String.format("/s-ramp[@uuid='%s']", uuid);
            QueryResultSet queryResultSet = client.query(query);
            if (queryResultSet.size() == 0) {
                return Response.serverError().status(0).build();
            }
            ArtifactSummary artifactSummary = queryResultSet.iterator().next();
            is = client.getArtifactContent(artifactSummary.getType(), uuid);

            // 2. get the deployment environment settings
            Target target = governance.getTargets().get(targetRef);
            if (target==null) {
                logger.error("No target could be found for target '"+ targetRef + "'");
                throw new SrampAtomException("No target could be found for target '"+ targetRef + "'");
            }
            if (! target.getType().equals(Target.TYPE.COPY)) {
            	logger.error("Target '" + target.getName() + "' should be of type COPY, not '"+ target.getType() + "'");
                throw new SrampAtomException("Target '" + target.getName() + "' should be of type COPY, not '"+ target.getType() + "'");
            }
            File deployDir = new File(target.getDeployDir());
            if (!deployDir.exists()) {
                logger.info("creating " + deployDir);
                deployDir.mkdirs();
            }

            // 3. deploy the artifact
            File file = new File(deployDir + "/" + artifactSummary.getName());
            if (file.exists())
                file.delete();
            file.createNewFile();
            os = new FileOutputStream(file);
            IOUtils.copy(is, os);

            InputStream reply = IOUtils.toInputStream("success");
            return Response.ok(reply, MediaType.APPLICATION_OCTET_STREAM).build();
        } catch (Exception e) {
            logger.error("Error deploying artifact. " + e.getMessage(), e);
            throw new SrampAtomException(e);
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }
    
//    /**
//     * Governance POST to deploy an artifact to a pre-configure maven repository.
//     * The maven GAV properties are required to be set on the artifact.
//     *
//     * @param target repo name
//     * @param uuid
//     *
//     * @throws SrampAtomException
//     */
//    @POST
//    @Path("maven/{target}/{uuid}")
//    @Produces(MediaType.APPLICATION_XML)
//    public Response maven(@Context HttpServletRequest request,
//            @PathParam("target") String targetRef,
//            @PathParam("uuid") String uuid) throws Exception {
//        InputStream is = null;
//        OutputStream os = null;
//        try {
//            // 0. run the decoder on the arguments
//            targetRef = SlashDecoder.decode(targetRef);
//            uuid = SlashDecoder.decode(uuid);
//
//            // 1. get the artifact from the repo
//            SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
//            String query = String.format("/s-ramp[@uuid='%s']", uuid);
//            QueryResultSet queryResultSet = client.query(query);
//            if (queryResultSet.size() == 0) {
//                return Response.serverError().status(0).build();
//            }
//            ArtifactSummary artifactSummary = queryResultSet.iterator().next();
//            is = client.getArtifactContent(artifactSummary.getType(), uuid);
//
//            // 2. get the deployment environment settings
//            Target target = governance.getTargets().get(targetRef);
//            if (target==null) {
//                logger.error("No target could be found for target '"+ targetRef + "'");
//                throw new SrampAtomException("No target could be found for target '"+ targetRef + "'");
//            }
//            //target.
//            MavenArtifactRepository mavenRepo = new MavenArtifactRepository();
//            Authentication auth = new Authentication("kurt", "kurt");
//            mavenRepo.setAuthentication(auth);
//            mavenRepo.
//            
//            File deployDir = new File(target.getDeployDir());
//            if (!deployDir.exists()) {
//                logger.info("creating " + deployDir);
//                deployDir.mkdirs();
//            }
//
//            // 3. deploy the artifact
//            File file = new File(deployDir + "/" + artifactSummary.getName());
//            if (file.exists())
//                file.delete();
//            file.createNewFile();
//            os = new FileOutputStream(file);
//            IOUtils.copy(is, os);
//
//            InputStream reply = IOUtils.toInputStream("success");
//            return Response.ok(reply, MediaType.APPLICATION_OCTET_STREAM).build();
//        } catch (Exception e) {
//            logger.error("Error deploying artifact. " + e.getMessage(), e);
//            throw new SrampAtomException(e);
//        } finally {
//            IOUtils.closeQuietly(os);
//            IOUtils.closeQuietly(is);
//        }
//    }
    
    /**
     * Governance POST to deploy an artifact by deploying to an RHQ group.
     *
     * @param target - name of the pre-configured server group in RHQ
     * @param uuid
     *
     * @throws SrampAtomException
     */
    @POST
    @Path("rhq/{target}/{uuid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response rhq(@Context HttpServletRequest request,
            @PathParam("target") String targetRef,
            @PathParam("uuid") String uuid) throws Exception {
       
    	InputStream is = null;
        try {
            // 0. run the decoder on the arguments
            targetRef = SlashDecoder.decode(targetRef);
            uuid = SlashDecoder.decode(uuid);

            // 1. get the artifact from the repo
            SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
            String query = String.format("/s-ramp[@uuid='%s']", uuid);
            QueryResultSet queryResultSet = client.query(query);
            if (queryResultSet.size() == 0) {
                return Response.serverError().status(0).build();
            }
            ArtifactSummary artifactSummary = queryResultSet.iterator().next();
      
            // 2. get the deployment environment settings
            Target target = governance.getTargets().get(targetRef);
            if (target==null) {
                logger.error("No target could be found for target '"+ targetRef + "'");
                throw new SrampAtomException("No target could be found for target '"+ targetRef + "'");
            }
            if (! target.getType().equals(Target.TYPE.RHQ)) {
            	logger.error("Target '" + target.getName() + "' should be of type RHQ, not '"+ target.getType() + "'");
                throw new SrampAtomException("Target '" + target.getName() + "' should be of type RHQ, not '"+ target.getType() + "'");
            }
            RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(),
    				target.getRhqBaseUrl(), target.getPort());

            // 3. deploy the artifact to each server in the preconfigured RHQ Server Group
            Integer rhqGroupId = rhqDeployUtil.getGroupIdForGroup(targetRef);
            rhqDeployUtil.wipeArchiveIfNecessary(artifactSummary.getName(), rhqGroupId);
    		List<Integer> resourceIds = rhqDeployUtil.getServerIdsForGroup(rhqGroupId);
    		is = client.getArtifactContent(artifactSummary.getType(), uuid);
    		byte[] fileContent = IOUtils.toByteArray(is);
    		for (Integer resourceId : resourceIds) {
    		    logger.info(String.format("Deploying %1$s to RHQ Server %2$s", artifactSummary.getName(), resourceId));
    			rhqDeployUtil.deploy(resourceId, fileContent, artifactSummary.getName());
    		}
            InputStream reply = IOUtils.toInputStream("success");
            return Response.ok(reply, MediaType.APPLICATION_OCTET_STREAM).build();
        } catch (Exception e) {
            logger.error("Error deploying artifact. " + e.getMessage(), e);
            throw new SrampAtomException(e);
        } finally {
        	IOUtils.closeQuietly(is);
        }
    }
    
    /**
     * Governance POST to deploy an artifact by deploying to a domain group
     * using the (as7) command line interface (CLI). The target name correcsponds to 
     * the domain group the artifact will be deployed to.
     *
     * @param target - name of the pre-configured server group in RHQ
     * @param uuid
     *
     * @throws SrampAtomException
     */
    @POST
    @Path("cli/{target}/{uuid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response cli(@Context HttpServletRequest request,
            @PathParam("target") String targetRef,
            @PathParam("uuid") String uuid) throws Exception {
       
    	InputStream is = null;
    	OutputStream os = null;
    	CommandContext ctx = null;
    	
        try {
            // 0. run the decoder on the arguments
            targetRef = SlashDecoder.decode(targetRef);
            uuid = SlashDecoder.decode(uuid);

            // 1. get the artifact from the repo
            SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
            String query = String.format("/s-ramp[@uuid='%s']", uuid);
            QueryResultSet queryResultSet = client.query(query);
            if (queryResultSet.size() == 0) {
                return Response.serverError().status(0).build();
            }
            ArtifactSummary artifactSummary = queryResultSet.iterator().next();
            
            is = client.getArtifactContent(artifactSummary.getType(), uuid);
            String name = artifactSummary.getName();
            int dot = name.lastIndexOf(".");
            
            File tmpFile = File.createTempFile(name.substring(0,dot), name.substring(dot+1));
            os = new FileOutputStream(tmpFile);
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            
            
            // 2. get the deployment environment settings
            Target target = governance.getTargets().get(targetRef);
            if (target==null) {
                logger.error("No target could be found for target '"+ targetRef + "'");
                throw new SrampAtomException("No target could be found for target '"+ targetRef + "'");
            }
            if (! target.getType().equals(Target.TYPE.AS_CLI)) {
            	logger.error("Target '" + target.getName() + "' should be of type AS_CLI, not '"+ target.getType() + "'");
                throw new SrampAtomException("Target '" + target.getName() + "' should be of type AS_CLI, not '"+ target.getType() + "'");
            }
            
            // 3. Deploy using AS CLI. 
        	if (target.getUser()==null || target.getUser().isEmpty()) {
        		ctx = CommandContextFactory.getInstance().newCommandContext();
        	} else {
        		ctx = CommandContextFactory.getInstance().newCommandContext(target.getUser(), 
        				target.getPassword().toCharArray());
        	}
            ctx.connectController(target.getHost(), target.getPort());
            // execute deploy to a servergroup or update if it's already deployed
            ctx.handle("deploy " + tmpFile.getAbsolutePath() + " --force --server-groups=" + target.getName());
            tmpFile.delete();
                	
            InputStream reply = IOUtils.toInputStream("success");
            return Response.ok(reply, MediaType.APPLICATION_OCTET_STREAM).build();
        } catch (Exception e) {
            logger.error("Error deploying artifact. " + e.getMessage(), e);
            throw new SrampAtomException(e);
        } finally {
        	if (ctx != null) ctx.terminateSession();
        	IOUtils.closeQuietly(is);
        }
    }
}
