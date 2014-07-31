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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.services.i18n.Messages;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deploys/Undeploys an artifact by deploying to a domain group using the (as7)
 * command line interface (CLI).
 *
 * * @author David Virgil Naranjo
 */
public class CliDeployer extends AbstractDeployer {

    private static Logger logger = LoggerFactory.getLogger(CliDeployer.class);


    /**
     * Deploys an artifact by deploying to a domain group using the (as7)
     * command line interface (CLI). The target name corresponds to the domain
     * group the artifact will be deployed to.
     *
     * @param artifact
     * @param target
     * @throws Exception
     */
    @Override
    public String deploy(BaseArtifactType artifact, Target target, SrampAtomApiClient client)
            throws Exception {
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
            File tmpFile = File.createTempFile(name.substring(0, dot), name.substring(dot));
            os = new FileOutputStream(tmpFile);
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);

            Map<String, String> undeployProperties = new HashMap<String, String>();
            undeployProperties.put("deploy.cli.host", target.getHost()); //$NON-NLS-1$
            undeployProperties.put("deploy.cli.port", String.valueOf(target.getPort())); //$NON-NLS-1$
            undeployProperties.put("deploy.cli.name", tmpFile.getName()); //$NON-NLS-1$

            // Deploy using AS CLI.
            if (target.getUser() == null || target.getUser().isEmpty()) {
                ctx = CommandContextFactory.getInstance().newCommandContext();
            } else {
                ctx = CommandContextFactory.getInstance().newCommandContext(target.getUser(),
                        target.getPassword().toCharArray());
            }
            ctx.connectController(target.getHost(), target.getPort());
            
            StringBuilder commandString = new StringBuilder();
            commandString.append("deploy "); //$NON-NLS-1$
            commandString.append(tmpFile.getAbsolutePath());
            if (target.isCliDomainMode()) {
                String serverGroup = target.getCliServerGroup();
                undeployProperties.put("deploy.cli.domainMode", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                if (serverGroup != null && serverGroup.trim().length() > 0) {
                    commandString.append(" --server-groups="); //$NON-NLS-1$
                    commandString.append(serverGroup);
                    undeployProperties.put("deploy.cli.serverGroups", serverGroup); //$NON-NLS-1$
                } else {
                    commandString.append(" --all-server-groups"); //$NON-NLS-1$
                }
            } else {
                // Nothing to do for standalone mode!  Just don't include the domain options above.
            }
            ctx.handle(commandString.toString());
            tmpFile.delete();

            recordUndeploymentInfo(artifact, target, undeployProperties, client);
            logger.info(Messages.i18n.format("CliDeployer.deploymentSuccessfully", artifact.getUuid())); //$NON-NLS-1$
            return target.getName() + " " + target.getHost(); //$NON-NLS-1$
        } finally {
            if (ctx != null)
                ctx.terminateSession();
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Undeploy using the JBoss AS CLI.
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
    public void undeploy(BaseArtifactType prevVersionArtifact, BaseArtifactType undeployInfo, Target target,
            SrampAtomApiClient client) throws Exception {
        CommandContext ctx = null;
        try {
            String deploymentName = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.name"); //$NON-NLS-1$
            String cliHost = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.host"); //$NON-NLS-1$
            Integer cliPort = new Integer(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.port")); //$NON-NLS-1$
            boolean domainMode = "true".equals(SrampModelUtils.getCustomProperty(undeployInfo, "deploy.cli.domainMode")); //$NON-NLS-1$ //$NON-NLS-2$

            // Deploy using AS CLI.
            // TODO CLI creds should probably be separate from the configuration
            // of the target so they can be used here
            if (target.getUser() == null || target.getUser().isEmpty()) {
                ctx = CommandContextFactory.getInstance().newCommandContext();
            } else {
                ctx = CommandContextFactory.getInstance().newCommandContext(target.getUser(),
                        target.getPassword().toCharArray());
            }
            ctx.connectController(cliHost, cliPort);
            if (domainMode) {
                ctx.handle("undeploy " + deploymentName + " --all-relevant-server-groups"); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                ctx.handle("undeploy " + deploymentName); //$NON-NLS-1$
            }
        } finally {
            if (ctx != null)
                ctx.terminateSession();
        }
    }

}
