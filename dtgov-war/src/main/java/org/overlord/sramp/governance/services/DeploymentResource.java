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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.downloads.overlord.sramp._2013.auditing.AuditEntry;
import org.jboss.downloads.overlord.sramp._2013.auditing.AuditItemType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.dtgov.services.deploy.Deployer;
import org.overlord.dtgov.services.deploy.DeployerFactory;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.common.audit.AuditUtils;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.GovernanceConstants;
import org.overlord.sramp.governance.SlashDecoder;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.overlord.sramp.governance.ValueEntity;
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
        String targetType;
        String targetTypeStr;
        if (target.getType().equals(Target.TYPE.CUSTOM)) {
            targetType = target.getCustomType();
            targetTypeStr = targetType;
        } else {
            targetType = target.getType().name();
            targetTypeStr = target.getType().toString();
        }

        // get the previous version of the deployment (so we can undeploy it)
        ////////////////////////////////////////////
        BaseArtifactType prevVersionArtifact = getCurrentlyDeployedVersion(client, artifact, target);
        Deployer deployer = DeployerFactory.createDeployer(targetType);
        if (deployer == null) {
            throw new Exception(Messages.i18n.format(
                    "DeploymentResource.TargetTypeNotFound", target.getType())); //$NON-NLS-1$
        }

        if (prevVersionArtifact != null) {
            undeploy(request, client, prevVersionArtifact, target, deployer);
        }

        // deploy the artifact (delegate based on target type)
        ////////////////////////////////////////////
        String deploymentTarget = targetTypeStr + ":"; //$NON-NLS-1$

        try {
            deploymentTarget += deployer.deploy(artifact, target, client);
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

        // Add a custom audit record to the artifact
        ////////////////////////////////////////////
        try {
            AuditEntry auditEntry = new AuditEntry();
            auditEntry.setType("deploy:deploy"); //$NON-NLS-1$
            DatatypeFactory dtFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar now = dtFactory.newXMLGregorianCalendar((GregorianCalendar)Calendar.getInstance());
            auditEntry.setWhen(now);
            auditEntry.setWho(request.getRemoteUser());
            AuditItemType item = AuditUtils.getOrCreateAuditItem(auditEntry, "deploy:info"); //$NON-NLS-1$
            AuditUtils.setAuditItemProperty(item, "target", target.getName()); //$NON-NLS-1$
            AuditUtils.setAuditItemProperty(item, "classifier", target.getClassifier()); //$NON-NLS-1$
            client.addAuditEntry(uuid, auditEntry);
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
        String mavenClassifier = SrampModelUtils.getCustomProperty(artifact, "maven.classifier"); //$NON-NLS-1$
        if (mavenArtifactId != null && mavenGroupId != null) {
            String q = "/s-ramp[@maven.artifactId = ? and @maven.groupId = ? and s-ramp:exactlyClassifiedByAllOf(., ?)]"; //$NON-NLS-1$
            if (mavenClassifier != null) {
                q = "/s-ramp[@maven.artifactId = ? and @maven.groupId = ? and s-ramp:exactlyClassifiedByAllOf(., ?) and @maven.classifier = ?]"; //$NON-NLS-1$
            }
            SrampClientQuery qbuilder = client.buildQuery(q).parameter(mavenArtifactId).parameter(mavenGroupId).parameter(classifier);
            if (mavenClassifier != null) {
                qbuilder.parameter(mavenClassifier);
            }
            QueryResultSet resultSet = qbuilder.count(2).query();
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
     * @param request
     * @param client
     * @param prevVersionArtifact
     * @param target
     * @param deployer
     * @throws Exception
     */
    protected void undeploy(HttpServletRequest request, SrampAtomApiClient client,
            BaseArtifactType prevVersionArtifact, Target target, Deployer deployer) throws Exception {
        // Find the undeployment information for the artifact
        QueryResultSet resultSet = client.buildQuery("/s-ramp/ext/UndeploymentInformation[describesDeployment[@uuid = ?] and @deploy.target = ?]") //$NON-NLS-1$
                .parameter(prevVersionArtifact.getUuid()).parameter(target.getName()).count(2).query();
        if (resultSet.size() == 1) {
            // Found it
            BaseArtifactType undeployInfo = client.getArtifactMetaData(resultSet.get(0));
            deployer.undeploy(prevVersionArtifact, undeployInfo, target, client);

            String deploymentClassifier = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.classifier"); //$NON-NLS-1$
            // re-fetch the artifact to get the latest meta-data
            prevVersionArtifact = client.getArtifactMetaData(ArtifactType.valueOf(prevVersionArtifact), prevVersionArtifact.getUuid());
            // remove the deployment classifier from the deployment
            prevVersionArtifact.getClassifiedBy().remove(deploymentClassifier);
            client.updateArtifactMetaData(prevVersionArtifact);
            // remove the undeployment information (no longer needed)
            client.deleteArtifact(undeployInfo.getUuid(), ArtifactType.valueOf(undeployInfo));

            // Add a custom audit record to the artifact
            ////////////////////////////////////////////
            try {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setType("deploy:undeploy"); //$NON-NLS-1$
                DatatypeFactory dtFactory = DatatypeFactory.newInstance();
                XMLGregorianCalendar now = dtFactory.newXMLGregorianCalendar((GregorianCalendar)Calendar.getInstance());
                auditEntry.setWhen(now);
                auditEntry.setWho(request.getRemoteUser());
                AuditItemType item = AuditUtils.getOrCreateAuditItem(auditEntry, "deploy:info"); //$NON-NLS-1$
                AuditUtils.setAuditItemProperty(item, "target", target.getName()); //$NON-NLS-1$
                AuditUtils.setAuditItemProperty(item, "classifier", target.getClassifier()); //$NON-NLS-1$
                client.addAuditEntry(prevVersionArtifact.getUuid(), auditEntry);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        } else {
            logger.warn(Messages.i18n.format("DeploymentResource.UndeploymentInfoNotFound", prevVersionArtifact.getName())); //$NON-NLS-1$
        }
    }
}
