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

import org.apache.commons.io.IOUtils;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.GovernanceConstants;
import org.overlord.sramp.governance.SlashDecoder;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.overlord.sramp.governance.Target;
import org.overlord.sramp.governance.Target.TYPE;
import org.overlord.sramp.governance.ValueEntity;
import org.overlord.sramp.governance.services.rhq.RHQDeployUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JAX-RS resource that handles deployment specific tasks.
 */
@Path("/deploy")
public class DeploymentResource {

    private static Logger logger = LoggerFactory.getLogger(DeploymentResource.class);

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
    @Produces("application/xml")
    public Map<String,ValueEntity> deploy(@Context HttpServletRequest request,
            @PathParam("target") String targetRef,
            @PathParam("uuid") String uuid) throws Exception {
    	
    	Governance governance = new Governance();
    	Map<String, ValueEntity> results = new HashMap<String,ValueEntity>();
    	
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
            logger.error(Messages.i18n.format("DeploymentResource.NoTarget", targetRef)); //$NON-NLS-1$
            throw new SrampAtomException(Messages.i18n.format("DeploymentResource.NoTarget", targetRef)); //$NON-NLS-1$
        }

        // get the previous version of the deployment (so we can undeploy it)
        ////////////////////////////////////////////
        BaseArtifactType prevVersionArtifact = getCurrentlyDeployedVersion(client, artifact, target);
        if (prevVersionArtifact != null) {
            undeploy(client, prevVersionArtifact, target);
        }

        // deploy the artifact (delegate based on target type)
        ////////////////////////////////////////////
        String deploymentTarget = target.getType().toString() + ":"; //$NON-NLS-1$
        try {
            if (target.getType() == TYPE.COPY) {
            	deploymentTarget += deployCopy(artifact, target, client);
            } else if (target.getType() == TYPE.AS_CLI) {
            	deploymentTarget += deployCLI(artifact, target, client);
            } else if (target.getType() == TYPE.MAVEN) {
            	deploymentTarget += deployMaven(artifact, target, client);
            } else if (target.getType() == TYPE.RHQ) {
            	deploymentTarget += deployRHQ(artifact, target, client);
            } else {
                throw new Exception(Messages.i18n.format("DeploymentResource.TargetTypeNotFound", target.getType())); //$NON-NLS-1$
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            results.put(GovernanceConstants.STATUS, new ValueEntity("fail")); //$NON-NLS-1$
        	results.put(GovernanceConstants.MESSAGE, new ValueEntity(e.getMessage()));
        	return results;
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
 
        results.put(GovernanceConstants.STATUS, new ValueEntity("success")); //$NON-NLS-1$
        results.put(GovernanceConstants.TARGET, new ValueEntity(deploymentTarget));
        
        return results;
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
        String mavenArtifactId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId"); //$NON-NLS-1$
        String mavenGroupId = SrampModelUtils.getCustomProperty(artifact, "maven.groupId"); //$NON-NLS-1$
        if (mavenArtifactId != null && mavenGroupId != null) {
            QueryResultSet resultSet = client.buildQuery("/s-ramp[@maven.artifactId = ? and @maven.groupId = ? and s-ramp:exactlyClassifiedByAllOf(., ?)]") //$NON-NLS-1$
                    .parameter(mavenArtifactId).parameter(mavenGroupId).parameter(classifier).count(2).query();
            if (resultSet.size() == 2) {
                throw new Exception(Messages.i18n.format("DeploymentResource.MultipleMavenDeployments", target.getName(), artifact.getName())); //$NON-NLS-1$
            }
            if (resultSet.size() == 1) {
                // Found a previous maven version deployed to the target
                currentVersionArtifact = client.getArtifactMetaData(resultSet.get(0));
            }
        }

        // Try to find a currently deployed version of this artifact based on a simple deployment name match.
        if (currentVersionArtifact == null) {
            String name = artifact.getName();
            QueryResultSet resultSet = client.buildQuery("/s-ramp[@name = ? and s-ramp:exactlyClassifiedByAllOf(., ?)]") //$NON-NLS-1$
                    .parameter(name).parameter(classifier).count(2).query();
            if (resultSet.size() == 2) {
                throw new Exception(Messages.i18n.format("DeploymentResource.MultipleSimpleDeployments", target.getName(), artifact.getName())); //$NON-NLS-1$
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
        QueryResultSet resultSet = client.buildQuery("/s-ramp/ext/UndeploymentInformation[describesDeployment[@uuid = ?] and @deploy.target = ?]") //$NON-NLS-1$
                .parameter(prevVersionArtifact.getUuid()).parameter(target.getName()).count(2).query();
        if (resultSet.size() == 1) {
            // Found it
            BaseArtifactType undeployInfo = client.getArtifactMetaData(resultSet.get(0));
            Target.TYPE type = Target.TYPE.valueOf(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.type")); //$NON-NLS-1$
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
            String deploymentClassifier = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.classifier"); //$NON-NLS-1$
            // re-fetch the artifact to get the latest meta-data
            prevVersionArtifact = client.getArtifactMetaData(ArtifactType.valueOf(prevVersionArtifact), prevVersionArtifact.getUuid());
            // remove the deployment classifier from the deployment
            prevVersionArtifact.getClassifiedBy().remove(deploymentClassifier);
            client.updateArtifactMetaData(prevVersionArtifact);
            // remove the undeployment information (no longer needed)
            client.deleteArtifact(undeployInfo.getUuid(), ArtifactType.valueOf(undeployInfo));
        } else {
            logger.warn(Messages.i18n.format("DeploymentResource.UndeploymentInfoNotFound", prevVersionArtifact.getName())); //$NON-NLS-1$
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
    protected String deployCopy(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            // get the artifact content from the repo
            is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());

            File deployDir = new File(target.getDeployDir());
            if (!deployDir.exists()) {
                logger.info(Messages.i18n.format("DeploymentResource.CreatingDeployDir", deployDir)); //$NON-NLS-1$
                deployDir.mkdirs();
            }

            // deploy the artifact
            String deploymentName = artifact.getName();

            // use the maven info for the deployment name if we have it
            String mavenId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId"); //$NON-NLS-1$
            String version = SrampModelUtils.getCustomProperty(artifact, "maven.version"); //$NON-NLS-1$
            String classifier = SrampModelUtils.getCustomProperty(artifact, "maven.classifier"); //$NON-NLS-1$
            String type = SrampModelUtils.getCustomProperty(artifact, "maven.type"); //$NON-NLS-1$
            if (mavenId != null) {
                StringBuilder nameBuilder = new StringBuilder();
                nameBuilder.append(mavenId);
                nameBuilder.append("-"); //$NON-NLS-1$
                nameBuilder.append(version);
                if (classifier != null) {
                    nameBuilder.append("-"); //$NON-NLS-1$
                    nameBuilder.append(classifier);
                }
                nameBuilder.append("."); //$NON-NLS-1$
                nameBuilder.append(type);
                deploymentName = nameBuilder.toString();
            }

            // now actually deploy it by copying it to the right (configured) directory
            File file = new File(deployDir + "/" + deploymentName); //$NON-NLS-1$
            if (file.exists())
                file.delete();
            file.createNewFile();
            os = new FileOutputStream(file);
            IOUtils.copy(is, os);

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.copy.file", file.getCanonicalPath()); //$NON-NLS-1$
            recordUndeploymentInfo(artifact, target, props, client);
            return file.getAbsolutePath();
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
    protected String deployMaven(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
        InputStream isJar = null;
        InputStream isPom = null;
        OutputStream osJar = null;
        OutputStream osPom = null;
        InputStream isPom2 = null;
        try {
            // make sure we have maven properties
            String mavenArtifactId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId"); //$NON-NLS-1$
            String mavenGroupId    = SrampModelUtils.getCustomProperty(artifact, "maven.groupId"); //$NON-NLS-1$
            String mavenVersion    = SrampModelUtils.getCustomProperty(artifact, "maven.version"); //$NON-NLS-1$
            if (mavenArtifactId == null || mavenGroupId == null || mavenVersion == null) {
            	throw new Exception(Messages.i18n.format("DeploymentResource.MissingMavenProps", artifact.getUuid())); //$NON-NLS-1$
            }
            // find the pom that goes with this artifact
            String pomName = artifact.getName().substring(0, artifact.getName().lastIndexOf(".")) + ".pom"; //$NON-NLS-1$ //$NON-NLS-2$
            QueryResultSet queryResultSet = client.buildQuery("/s-ramp[@name = ?]").parameter(pomName).query(); //$NON-NLS-1$
            if (queryResultSet.size() == 0) {
                throw new Exception(Messages.i18n.format(
                        "DeploymentResource.MissingPom", //$NON-NLS-1$
                        artifact.getUuid(), pomName));
            }
            BaseArtifactType pomArtifact = null;
            for (ArtifactSummary artifactSummary2 : queryResultSet) {
                pomArtifact = client.getArtifactMetaData(artifactSummary2);
                String pomMavenArtifactId = SrampModelUtils.getCustomProperty(pomArtifact, "maven.artifactId"); //$NON-NLS-1$
                String pomMavenGroupId    = SrampModelUtils.getCustomProperty(pomArtifact, "maven.groupId"); //$NON-NLS-1$
                String pomMavenVersion    = SrampModelUtils.getCustomProperty(pomArtifact, "maven.version"); //$NON-NLS-1$
                if (mavenArtifactId.equals(pomMavenArtifactId) && mavenGroupId.equals(pomMavenGroupId)
                        && mavenVersion.equals(pomMavenVersion)) {
                    break;
                }
                pomArtifact = null;
			}
            if (pomArtifact == null) {
                throw new Exception(Messages.i18n.format(
                        "DeploymentResource.IncorrectPom", //$NON-NLS-1$
                        artifact.getUuid(), pomName));
            }

            ArtifactType pomType = ArtifactType.valueOf(pomArtifact);
            isPom = client.getArtifactContent(pomType, pomArtifact.getUuid());
            String name = pomArtifact.getName();

            File pomFile = new File(System.getProperty("java.io.tmpdir") + "/" + name); //$NON-NLS-1$ //$NON-NLS-2$
            osPom = new FileOutputStream(pomFile);
            IOUtils.copy(isPom, osPom);
            IOUtils.closeQuietly(isPom);
            IOUtils.closeQuietly(osPom);

            isJar = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
            name = artifact.getName();
            File jarFile = new File(System.getProperty("java.io.tmpdir") + "/" + name); //$NON-NLS-1$ //$NON-NLS-2$
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
            
            //return maven url
            return target.getMavenUrl();
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
    protected String deployRHQ(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
    	InputStream is = null;
        try {
            RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(),
    				target.getRhqBaseUrl(), target.getPort(), target.getRhqPluginName());

            // Deploy the artifact to each server in the preconfigured RHQ Server Group
            Integer rhqGroupId = rhqDeployUtil.getGroupIdForGroup(target.getName());
            rhqDeployUtil.wipeArchiveIfNecessary(artifact.getName(), rhqGroupId);
    		List<Integer> resourceIds = rhqDeployUtil.getServerIdsForGroup(rhqGroupId);
    		is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
    		byte[] fileContent = IOUtils.toByteArray(is);
    		for (Integer resourceId : resourceIds) {
    		    logger.info(Messages.i18n.format("DeploymentResource.DeployingToRHQ", artifact.getName(), resourceId)); //$NON-NLS-1$
    			rhqDeployUtil.deploy(resourceId, fileContent, artifact.getName());
    		}

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.rhq.groupId", String.valueOf(rhqGroupId)); //$NON-NLS-1$
            props.put("deploy.rhq.baseUrl", target.getRhqBaseUrl()); //$NON-NLS-1$
            props.put("deploy.rhq.port", String.valueOf(target.getPort())); //$NON-NLS-1$
            props.put("deploy.rhq.name", artifact.getName()); //$NON-NLS-1$
            props.put("deploy.rhq.pluginName", target.getRhqPluginName()); //$NON-NLS-1$
            recordUndeploymentInfo(artifact, target, props, client);
            
            return target.getRhqBaseUrl();
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
    protected String deployCLI(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
    	InputStream is = null;
    	OutputStream os = null;
    	CommandContext ctx = null;

        try {
            // Get the artifact content
            ArtifactType type = ArtifactType.valueOf(artifact);
            is = client.getArtifactContent(type, artifact.getUuid());
            String name = artifact.getName();
            int dot = name.lastIndexOf("."); //$NON-NLS-1$
            // Save artifact content to a temp location
            File tmpFile = File.createTempFile(name.substring(0,dot), name.substring(dot));
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
            ctx.handle("deploy " + tmpFile.getAbsolutePath() + " --server-groups=" + target.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            tmpFile.delete();

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.cli.serverGroups", target.getName()); //$NON-NLS-1$
            props.put("deploy.cli.host", target.getHost()); //$NON-NLS-1$
            props.put("deploy.cli.port", String.valueOf(target.getPort())); //$NON-NLS-1$
            props.put("deploy.cli.name", tmpFile.getName()); //$NON-NLS-1$
            recordUndeploymentInfo(artifact, target, props, client);
            
            return target.getName() + " " + target.getHost(); //$NON-NLS-1$
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
        undeploymentArtifact.setExtendedType("UndeploymentInformation"); //$NON-NLS-1$
        undeploymentArtifact.setName(artifact.getName() + ".undeploy"); //$NON-NLS-1$
        undeploymentArtifact.setDescription(Messages.i18n.format("DeploymentResource.UndeploymentInfoDescription", artifact.getName())); //$NON-NLS-1$
        SrampModelUtils.setCustomProperty(undeploymentArtifact, "deploy.target", target.getName()); //$NON-NLS-1$
        SrampModelUtils.setCustomProperty(undeploymentArtifact, "deploy.type", target.getType().name()); //$NON-NLS-1$
        SrampModelUtils.setCustomProperty(undeploymentArtifact, "deploy.classifier", target.getClassifier()); //$NON-NLS-1$
        if (props != null) {
            for (String propKey : props.keySet()) {
                String propVal = props.get(propKey);
                SrampModelUtils.setCustomProperty(undeploymentArtifact, propKey, propVal);
            }
        }
        SrampModelUtils.addGenericRelationship(undeploymentArtifact, "describesDeployment", artifact.getUuid()); //$NON-NLS-1$
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
            String deploymentName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.name"); //$NON-NLS-1$
            String cliHost = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.host"); //$NON-NLS-1$
            Integer cliPort = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.port")); //$NON-NLS-1$

            // Deploy using AS CLI.
            // TODO CLI creds should probably be separate from the configuration of the target so they can be used here
            if (target.getUser() == null || target.getUser().isEmpty()) {
                ctx = CommandContextFactory.getInstance().newCommandContext();
            } else {
                ctx = CommandContextFactory.getInstance().newCommandContext(target.getUser(),
                        target.getPassword().toCharArray());
            }
            ctx.connectController(cliHost, cliPort);
            ctx.handle("undeploy " + deploymentName); //$NON-NLS-1$
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
        String deployedFile = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.copy.file"); //$NON-NLS-1$
        File file = new File(deployedFile);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        // Delete any JBoss AS 7/EAP 6 deployment files.
        File deployFile = new File(deployedFile + ".deployed"); //$NON-NLS-1$
        if (deployFile.isFile()) {
            deployFile.delete();
        }
        File failedFile = new File(deployedFile + ".failed"); //$NON-NLS-1$
        if (failedFile.isFile()) {
            failedFile.delete();
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
            throw new Exception(Messages.i18n.format("DeploymentResource.MissingTargetCreds", target.getName())); //$NON-NLS-1$
        }
        String baseUrl = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.baseUrl"); //$NON-NLS-1$
        Integer port = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.port")); //$NON-NLS-1$
        Integer rhqGroupId = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.groupId")); //$NON-NLS-1$
        String artifactName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.name"); //$NON-NLS-1$
        String rhqPluginName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.pluginName"); //$NON-NLS-1$

        RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(),
                baseUrl, port, rhqPluginName);

        // Deploy the artifact to each server in the preconfigured RHQ Server Group
        rhqDeployUtil.wipeArchiveIfNecessary(artifactName, rhqGroupId);
    }

}
