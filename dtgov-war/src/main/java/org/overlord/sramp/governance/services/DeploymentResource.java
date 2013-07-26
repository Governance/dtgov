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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.overlord.sramp.atom.MediaType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
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

        // get the artifact from the repo
        ////////////////////////////////////////////
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        BaseArtifactType artifact = client.getArtifactMetaData(uuid);

        // get the deployment environment settings
        ////////////////////////////////////////////
        Target target = governance.getTargets().get(targetRef);
        if (target == null) {
            logger.error("No target could be found for target '"+ targetRef + "'");
            throw new SrampAtomException("No target could be found for target '"+ targetRef + "'");
        }

        // get the previous version of the deployment (so we can undeploy it)
        ////////////////////////////////////////////
        BaseArtifactType prevVersionArtifact = getCurrentlyDeployedVersion(client, artifact, target);
        if (prevVersionArtifact != null) {
            undeploy(client, prevVersionArtifact, target);
        }

        // deploy the artifact (delegate based on target type)
        ////////////////////////////////////////////
        try {
            if (target.getType() == TYPE.COPY) {
                deployCopy(artifact, target, client);
            } else if (target.getType() == TYPE.AS_CLI) {
                deployCLI(artifact, target, client);
            } else if (target.getType() == TYPE.MAVEN) {
                deployMaven(artifact, target, client);
            } else if (target.getType() == TYPE.RHQ) {
                deployRHQ(artifact, target, client);
            } else {
                throw new Exception("Deployment target type not supported: " + target.getType());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.serverError().status(0).build();
        }

        // update the artifact meta-data to set the classifier
        ////////////////////////////////////////////
        String deploymentClassifier = target.getClassifier();
        try {
            // refresh the artifact meta-data in case something changed since we originally retrieved it
            artifact = client.getArtifactMetaData(uuid);
            artifact.getClassifiedBy().add(deploymentClassifier);
            client.updateArtifactMetaData(artifact);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        InputStream reply = IOUtils.toInputStream("success");
        return Response.ok(reply, MediaType.APPLICATION_OCTET_STREAM).build();
    }

    /**
     * Finds the currently deployed version of the given artifact.  This basically needs to
     * return the version of the artifact that is currently deployed to the given target.
     * This is important so that we can undeploy that old version prior to deploying the
     * new version.
     * @param client
     * @param artifact
     * @param target
     * @throws Exception
     */
    protected BaseArtifactType getCurrentlyDeployedVersion(SrampAtomApiClient client,
            BaseArtifactType artifact, Target target) throws Exception {
        BaseArtifactType currentVersionArtifact = null;

        // Let's try to find the currently deployed version.
        String classifier = target.getClassifier();

        // Try to find a currently deployed version of this artifact based on maven information.
        String mavenArtifactId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId");
        String mavenGroupId = SrampModelUtils.getCustomProperty(artifact, "maven.groupId");
        if (mavenArtifactId != null && mavenGroupId != null) {
            QueryResultSet resultSet = client.buildQuery("/s-ramp[@maven.artifactId = ? and @maven.groupId = ? and s-ramp:exactlyClassifiedByAllOf(., ?)]")
                    .parameter(mavenArtifactId).parameter(mavenGroupId).parameter(classifier).count(2).query();
            if (resultSet.size() == 2) {
                throw new Exception("Found multiple (maven) 'current' deployments in " + target.getName() + " for " + artifact.getName());
            }
            if (resultSet.size() == 1) {
                // Found a previous maven version deployed to the target
                currentVersionArtifact = client.getArtifactMetaData(resultSet.get(0));
            }
        }

        // Try to find a currently deployed version of this artifact based on a simple deployment name match.
        if (currentVersionArtifact == null) {
            String name = artifact.getName();
            QueryResultSet resultSet = client.buildQuery("/s-ramp[@name = ? and s-ramp:exactlyClassifiedByAllOf(., ?)]")
                    .parameter(name).parameter(classifier).count(2).query();
            if (resultSet.size() == 2) {
                throw new Exception("Found multiple (simple name) 'current' deployments in " + target.getName() + " for " + artifact.getName());
            }
            if (resultSet.size() == 1) {
                // Found a previous maven version deployed to the target
                currentVersionArtifact = client.getArtifactMetaData(resultSet.get(0));
            }
        }

        // Try to find a currently deployed version of this artifact based on a simple deployment name match.
        if (currentVersionArtifact == null) {
            // TODO: try to find currently deployed version of this artifact based on some form of versioning (TBD)
        }

        return currentVersionArtifact;
    }

    /**
     * Undeploys the given artifact.  Uses information recorded when that artifact was originally
     * deployed (see {@link #recordUndeploymentInfo(BaseArtifactType, Target, Map, SrampAtomApiClient)}).
     * @param client
     * @param prevVersionArtifact
     * @param target
     * @throws Exception
     */
    protected void undeploy(SrampAtomApiClient client, BaseArtifactType prevVersionArtifact, Target target)
            throws Exception {
        // Find the undeployment information for the artifact
        QueryResultSet resultSet = client.buildQuery("/s-ramp/ext/UndeploymentInformation[describesDeployment[@uuid = ?] and @deploy.target = ?]")
                .parameter(prevVersionArtifact.getUuid()).parameter(target.getName()).count(2).query();
        if (resultSet.size() == 1) {
            // Found it
            BaseArtifactType undeployInfo = client.getArtifactMetaData(resultSet.get(0));
            Target.TYPE type = Target.TYPE.valueOf(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.type"));
            switch (type) {
                case AS_CLI:
                    undeployCLI(client, prevVersionArtifact, undeployInfo, target);
                    break;
                case COPY:
                    undeployCopy(client, prevVersionArtifact, undeployInfo, target);
                    break;
                case MAVEN:
                    // We never undeploy from maven
                    break;
                case RHQ:
                    undeployRHQ(client, prevVersionArtifact, undeployInfo, target);
                    break;
                default:
                    break;
            }
            String deploymentClassifier = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.classifier");
            // re-fetch the artifact to get the latest meta-data
            prevVersionArtifact = client.getArtifactMetaData(ArtifactType.valueOf(prevVersionArtifact), prevVersionArtifact.getUuid());
            // remove the deployment classifier from the deployment
            prevVersionArtifact.getClassifiedBy().remove(deploymentClassifier);
            client.updateArtifactMetaData(prevVersionArtifact);
            // remove the undeployment information (no longer needed)
            client.deleteArtifact(undeployInfo.getUuid(), ArtifactType.valueOf(undeployInfo));
        } else {
            logger.warn("Failed to find undeployment information for " + prevVersionArtifact.getName());
        }
    }

    /**
     * Deploys an artifact by copying it onto the file system.
     *
     * @param artifact
     * @param target
     * @param client
     * @throws Exception
     */
    protected void deployCopy(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
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

            // deploy the artifact
            File file = new File(deployDir + "/" + artifact.getName());
            if (file.exists())
                file.delete();
            file.createNewFile();
            os = new FileOutputStream(file);
            IOUtils.copy(is, os);

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.copy.file", file.getCanonicalPath());
            recordUndeploymentInfo(artifact, target, props, client);
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
    protected void deployMaven(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
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
            	throw new Exception("MavenDeployment requires artifact " + artifact.getUuid() + " to have maven properties set.");
            }
            // find the pom that goes with this artifact
            String pomName = artifact.getName().substring(0, artifact.getName().lastIndexOf(".")) + ".pom";
            QueryResultSet queryResultSet = client.buildQuery("/s-ramp[@name = ?]").parameter(pomName).query();
            if (queryResultSet.size() == 0) {
                throw new Exception("MavenDeployment requires artifact " + artifact.getUuid()
            			+ " to have an accompanied pom with name " + pomName);
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
                throw new Exception("MavenDeployment requires artifact " + artifact.getUuid()
            			+ " to have an accompanied pom with name " + pomName
            			+ " with identical maven properties");
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

            // Don't register undeployment info - we never undeploy from maven
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
     * @throws SrampAtomException
     */
    protected void deployRHQ(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
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

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.rhq.groupId", String.valueOf(rhqGroupId));
            props.put("deploy.rhq.baseUrl", target.getRhqBaseUrl());
            props.put("deploy.rhq.port", String.valueOf(target.getPort()));
            props.put("deploy.rhq.name", artifact.getName());
            recordUndeploymentInfo(artifact, target, props, client);
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
    protected void deployCLI(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
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

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.cli.serverGroups", target.getName());
            props.put("deploy.cli.host", target.getHost());
            props.put("deploy.cli.port", String.valueOf(target.getPort()));
            props.put("deploy.cli.name", tmpFile.getName());
            recordUndeploymentInfo(artifact, target, props, client);
        } finally {
        	if (ctx != null) ctx.terminateSession();
        	IOUtils.closeQuietly(is);
        }
    }

    /**
     * Records undeployment information for the given artifact.  The undeployment information
     * is saved as another artifact in the repository with a relationship back to the
     * deployment artifact.
     * @param artifact
     * @param target
     * @param props
     * @throws SrampAtomException
     * @throws SrampClientException
     */
    protected void recordUndeploymentInfo(BaseArtifactType artifact, Target target, Map<String, String> props,
            SrampAtomApiClient client) throws SrampClientException, SrampAtomException {
        ExtendedArtifactType undeploymentArtifact = new ExtendedArtifactType();
        undeploymentArtifact.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        undeploymentArtifact.setExtendedType("UndeploymentInformation");
        undeploymentArtifact.setName(artifact.getName() + ".undeploy");
        undeploymentArtifact.setDescription("Contains undeployment information for deployment '"
                + artifact.getName() + "'");
        SrampModelUtils.setCustomProperty(undeploymentArtifact, "deploy.target", target.getName());
        SrampModelUtils.setCustomProperty(undeploymentArtifact, "deploy.type", target.getType().name());
        SrampModelUtils.setCustomProperty(undeploymentArtifact, "deploy.classifier", target.getClassifier());
        if (props != null) {
            for (String propKey : props.keySet()) {
                String propVal = props.get(propKey);
                SrampModelUtils.setCustomProperty(undeploymentArtifact, propKey, propVal);
            }
        }
        SrampModelUtils.addGenericRelationship(undeploymentArtifact, "describesDeployment", artifact.getUuid());
        client.createArtifact(undeploymentArtifact);
    }

    /**
     * Undeploy using the JBoss AS CLI.
     *
     * @param client
     * @param prevVersionArtifact
     * @param undeployInfo
     * @param target
     * @throws Exception
     */
    protected void undeployCLI(SrampAtomApiClient client, BaseArtifactType prevVersionArtifact,
            BaseArtifactType undeployInfo, Target target) throws Exception {
        CommandContext ctx = null;
        try {
            String deploymentName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.name");
            String cliHost = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.host");
            Integer cliPort = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.port"));

            // Deploy using AS CLI.
            // TODO CLI creds should probably be separate from the configuration of the target so they can be used here
            if (target.getUser() == null || target.getUser().isEmpty()) {
                ctx = CommandContextFactory.getInstance().newCommandContext();
            } else {
                ctx = CommandContextFactory.getInstance().newCommandContext(target.getUser(),
                        target.getPassword().toCharArray());
            }
            ctx.connectController(cliHost, cliPort);
            ctx.handle("undeploy " + deploymentName);
        } finally {
            if (ctx != null) ctx.terminateSession();
        }
    }

    /**
     * Undeploy an artifact that was simply copied to a file location.
     * @param client
     * @param prevVersionArtifact
     * @param undeployInfo
     * @param target
     */
    protected void undeployCopy(SrampAtomApiClient client, BaseArtifactType prevVersionArtifact,
            BaseArtifactType undeployInfo, Target target) {
        String deployedFile = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.copy.file");
        File file = new File(deployedFile);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * Undeploy using JBoss RHQ.
     * @param client
     * @param prevVersionArtifact
     * @param undeployInfo
     * @param target
     * @throws Exception
     */
    protected void undeployRHQ(SrampAtomApiClient client, BaseArtifactType prevVersionArtifact,
            BaseArtifactType undeployInfo, Target target) throws Exception {
        if (target.getUser() == null || target.getPassword() == null || target.getUser().isEmpty() || target.getPassword().isEmpty()) {
            throw new Exception("Cannot find user/pass for target '" + target.getName() + "' during undeployment.  Improvements needed (store RHQ creds separately from targets).");
        }
        String baseUrl = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.baseUrl");
        Integer port = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.port"));
        Integer rhqGroupId = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.groupId"));
        String artifactName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.name");

        RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(),
                baseUrl, port);

        // Deploy the artifact to each server in the preconfigured RHQ Server Group
        rhqDeployUtil.wipeArchiveIfNecessary(artifactName, rhqGroupId);
    }

}
