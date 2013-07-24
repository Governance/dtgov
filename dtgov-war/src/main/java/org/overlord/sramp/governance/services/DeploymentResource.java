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
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.MediaType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.SlashDecoder;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.overlord.sramp.governance.Target;
import org.overlord.sramp.governance.Target.TYPE;
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
     * The deployment endpoint - processes can invoke this endpoint to deploy
     * a deployment based on configuration of the provided target.
     * @param request
     * @param targetRef
     * @param uuid
     * @throws Exception
     */
    @POST
    @Path("{target}/{uuid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deploy(@Context HttpServletRequest request,
            @PathParam("target") String targetRef,
            @PathParam("uuid") String uuid) throws Exception {
        // 0. run the decoder on the arguments
        targetRef = SlashDecoder.decode(targetRef);
        uuid = SlashDecoder.decode(uuid);

        // 1. get the artifact from the repo
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        BaseArtifactType artifact = client.getArtifactMetaData(uuid);

        // 2. get the deployment environment settings
        Target target = governance.getTargets().get(targetRef);
        if (target == null) {
            logger.error("No target could be found for target '"+ targetRef + "'");
            throw new SrampAtomException("No target could be found for target '"+ targetRef + "'");
        }

        // 3. deploy the artifact (delegate based on target type)
        if (target.getType() == TYPE.COPY) {
            return copy(artifact, target, client);
        } else if (target.getType() == TYPE.AS_CLI) {
            return cli(artifact, target, client);
        } else if (target.getType() == TYPE.MAVEN) {
            return maven(artifact, target, client);
        } else if (target.getType() == TYPE.RHQ) {
            return rhq(artifact, target, client);
        }

        throw new SrampAtomException("Deployment target type not supported: " + target.getType());
    }

    /**
     * Deploys an artifact by copying it onto the file system.
     *
     * @param artifact
     * @param target
     * @param client
     * @throws Exception
     */
    protected Response copy(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            // get the artifact content from the repo
            is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());

            File deployDir = new File(target.getDeployDir());
            if (!deployDir.exists()) {
                logger.info("creating " + deployDir);
                deployDir.mkdirs();
            }

            // 3. deploy the artifact
            File file = new File(deployDir + "/" + artifact.getName());
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

    /**
     * Deploys an artifact to a pre-configured maven repository. The maven GAV properties are
     * required to be set on the artifact.
     *
     * This code is preview at best since it has a lot of loose ends:
     *
     * - if this jar has a parent pom then this parent needs to be in the repo.
     * - credentials should be set in a .settings.xml in the .m2 dir of the user that runs the app
     *
     * @param artifact
     * @param target
     * @param client
     * @throws Exception
     */
    protected Response maven(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
        InputStream isJar = null;
        InputStream isPom = null;
        OutputStream osJar = null;
        OutputStream osPom = null;
        InputStream isPom2 = null;
        try {
            // make sure we have maven properties
            String mavenArtifactId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId");
            String mavenGroupId    = SrampModelUtils.getCustomProperty(artifact, "maven.groupId");
            String mavenVersion    = SrampModelUtils.getCustomProperty(artifact, "maven.version");
            if (mavenArtifactId == null || mavenGroupId == null || mavenVersion == null) {
            	logger.error("MavenDeployment requires artifact " + artifact.getUuid() + " to have maven properties set.");
            	return Response.serverError().status(0).build();
            }
            // find the pom that goes with this artifact
            String pomName = artifact.getName().substring(0, artifact.getName().lastIndexOf(".")) + ".pom";
            QueryResultSet queryResultSet = client.buildQuery("/s-ramp[@name = ?]").parameter(pomName).query();
            if (queryResultSet.size() == 0) {
            	logger.error("MavenDeployment requires artifact " + artifact.getUuid()
            			+ " to have an accompanied pom with name " + pomName);
                return Response.serverError().status(0).build();
            }
            BaseArtifactType pomArtifact = null;
            for (ArtifactSummary artifactSummary2 : queryResultSet) {
                pomArtifact = client.getArtifactMetaData(artifactSummary2);
                String pomMavenArtifactId = SrampModelUtils.getCustomProperty(pomArtifact, "maven.artifactId");
                String pomMavenGroupId    = SrampModelUtils.getCustomProperty(pomArtifact, "maven.groupId");
                String pomMavenVersion    = SrampModelUtils.getCustomProperty(pomArtifact, "maven.version");
                if (mavenArtifactId.equals(pomMavenArtifactId) && mavenGroupId.equals(pomMavenGroupId)
                        && mavenVersion.equals(pomMavenVersion)) {
                    break;
                }
                pomArtifact = null;
			}
            if (pomArtifact == null) {
            	logger.error("MavenDeployment requires artifact " + artifact.getUuid()
            			+ " to have an accompanied pom with name " + pomName
            			+ " with identical maven properties");
                return Response.serverError().status(0).build();
            }

            ArtifactType pomType = ArtifactType.valueOf(pomArtifact);
            isPom = client.getArtifactContent(pomType, pomArtifact.getUuid());
            String name = pomArtifact.getName();

            File pomFile = new File(System.getProperty("java.io.tmpdir") + "/" + name);
            osPom = new FileOutputStream(pomFile);
            IOUtils.copy(isPom, osPom);
            IOUtils.closeQuietly(isPom);
            IOUtils.closeQuietly(osPom);

            isJar = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
            name = artifact.getName();
            File jarFile = new File(System.getProperty("java.io.tmpdir") + "/" + name);
            osJar = new FileOutputStream(jarFile);
            IOUtils.copy(isJar, osJar);
            IOUtils.closeQuietly(isJar);
            IOUtils.closeQuietly(osJar);

            // deploy the artifact to a maven repo as specified in the config of the target
            isPom2 = client.getArtifactContent(pomType, pomArtifact.getUuid());
            MavenRepoUtil util = new MavenRepoUtil();
            MavenRepository repo = util.getMavenReleaseRepo(
            		target.getMavenUrl(),
            		target.isReleaseEnabled(),
            		target.isSnapshotEnabled(),
            		isPom2);
            ReleaseId releaseId = new ReleaseIdImpl(mavenArtifactId, mavenGroupId, mavenVersion);
            //org.sonatype.aether.artifact.Artifact artifact = repo.resolveArtifact(releaseId.toExternalForm());
            repo.deployArtifact(releaseId, jarFile, pomFile);

            InputStream reply = IOUtils.toInputStream("success");
            return Response.ok(reply, MediaType.APPLICATION_OCTET_STREAM).build();
        } catch (Exception e) {
            logger.error("Error deploying artifact. " + e.getMessage(), e);
            throw new SrampAtomException(e);
        } finally {
            IOUtils.closeQuietly(isPom);
            IOUtils.closeQuietly(isPom2);
            IOUtils.closeQuietly(isJar);
            IOUtils.closeQuietly(osPom);
            IOUtils.closeQuietly(osJar);
        }
    }

    /**
     * Deploys an artifact by deploying to an RHQ group.
     *
     * @param target - name of the pre-configured server group in RHQ
     * @param uuid
     *
     * @throws SrampAtomException
     */
    protected Response rhq(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
    	InputStream is = null;
        try {
            RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(),
    				target.getRhqBaseUrl(), target.getPort());

            // Deploy the artifact to each server in the preconfigured RHQ Server Group
            Integer rhqGroupId = rhqDeployUtil.getGroupIdForGroup(target.getName());
            rhqDeployUtil.wipeArchiveIfNecessary(artifact.getName(), rhqGroupId);
    		List<Integer> resourceIds = rhqDeployUtil.getServerIdsForGroup(rhqGroupId);
    		is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
    		byte[] fileContent = IOUtils.toByteArray(is);
    		for (Integer resourceId : resourceIds) {
    		    logger.info(String.format("Deploying %1$s to RHQ Server %2$s", artifact.getName(), resourceId));
    			rhqDeployUtil.deploy(resourceId, fileContent, artifact.getName());
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
     * Deploys an artifact by deploying to a domain group using the (as7) command line
     * interface (CLI). The target name corresponds to the domain group the artifact
     * will be deployed to.
     *
     * @param artifact
     * @param target
     * @param client
     * @throws Exception
     */
    protected Response cli(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
    	InputStream is = null;
    	OutputStream os = null;
    	CommandContext ctx = null;

        try {
            // Get the artifact content
            ArtifactType type = ArtifactType.valueOf(artifact);
            is = client.getArtifactContent(type, artifact.getUuid());
            String name = artifact.getName();
            int dot = name.lastIndexOf(".");
            // Save artifact content to a temp location
            File tmpFile = File.createTempFile(name.substring(0,dot), name.substring(dot+1));
            os = new FileOutputStream(tmpFile);
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);

            // Deploy using AS CLI.
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
