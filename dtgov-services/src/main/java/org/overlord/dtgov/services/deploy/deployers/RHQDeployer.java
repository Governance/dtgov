/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.dtgov.services.deploy.deployers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.targets.RHQTarget;
import org.overlord.dtgov.services.i18n.Messages;
import org.overlord.dtgov.services.rhq.RHQDeployUtil;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RQQ Deployer. .It deploys/undeploys an artifact via an RHQ group.
 *
 * @author David Virgil Naranjo
 */
public class RHQDeployer extends AbstractDeployer<RHQTarget> {

    private static Logger logger = LoggerFactory.getLogger(RHQDeployer.class);

    /**
     * Deploys an artifact by deploying to an RHQ group.
     *
     * @param artifact
     *            the artifact
     * @param target
     *            the target
     * @return the string
     * @throws Exception
     *             the exception
     */
    @Override
    public String deploy(BaseArtifactType artifact, RHQTarget target, SrampAtomApiClient client)
            throws Exception {
        InputStream is = null;
        try {
            RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(),
 target.getBaseUrl(), target.getPort(),
                    target.getPluginName());

            // Deploy the artifact to each server in the preconfigured RHQ
            // Server Group
            Integer rhqGroupId = rhqDeployUtil.getGroupIdForGroup(target.getGroup());
            rhqDeployUtil.wipeArchiveIfNecessary(artifact.getName(), rhqGroupId);
            List<Integer> resourceIds = rhqDeployUtil.getServerIdsForGroup(rhqGroupId);
            is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
            byte[] fileContent = IOUtils.toByteArray(is);
            for (Integer resourceId : resourceIds) {
                logger.info(Messages.i18n.format(
                        "DeploymentResource.DeployingToRHQ", artifact.getName(), resourceId)); //$NON-NLS-1$
                rhqDeployUtil.deploy(resourceId, fileContent, artifact.getName());
            }

            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.rhq.groupId", String.valueOf(rhqGroupId)); //$NON-NLS-1$
            props.put("deploy.rhq.baseUrl", target.getBaseUrl()); //$NON-NLS-1$
            props.put("deploy.rhq.port", String.valueOf(target.getPort())); //$NON-NLS-1$
            props.put("deploy.rhq.name", artifact.getName()); //$NON-NLS-1$
            props.put("deploy.rhq.pluginName", target.getPluginName()); //$NON-NLS-1$
            recordUndeploymentInfo(artifact, target, props, client);

            logger.info(Messages.i18n.format("RHQDeployer.deploymentSuccessfully", artifact.getUuid())); //$NON-NLS-1$
            return target.getBaseUrl();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Undeploy using JBoss RHQ.
     *
     * @param prevVersionArtifact
     *            the prev version artifact
     * @param undeployInfo
     *            the undeploy info
     * @param target
     *            the target
     * @throws Exception
     *             the exception
     */
    @Override
    public void undeploy(BaseArtifactType prevVersionArtifact, BaseArtifactType undeployInfo, RHQTarget target,
            SrampAtomApiClient client) throws Exception {
        if (target.getUser() == null || target.getPassword() == null || target.getUser().isEmpty()
                || target.getPassword().isEmpty()) {
            throw new Exception(Messages.i18n.format(
                    "DeploymentResource.MissingTargetCreds", target.getName())); //$NON-NLS-1$
        }
        String baseUrl = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.baseUrl"); //$NON-NLS-1$
        Integer port = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.port")); //$NON-NLS-1$
        Integer rhqGroupId = new Integer(
                SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.groupId")); //$NON-NLS-1$
        String artifactName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.name"); //$NON-NLS-1$
        String rhqPluginName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.rhq.pluginName"); //$NON-NLS-1$

        RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getUser(), target.getPassword(), baseUrl,
                port, rhqPluginName);

        // Deploy the artifact to each server in the preconfigured RHQ Server
        // Group
        rhqDeployUtil.wipeArchiveIfNecessary(artifactName, rhqGroupId);
    }
}
