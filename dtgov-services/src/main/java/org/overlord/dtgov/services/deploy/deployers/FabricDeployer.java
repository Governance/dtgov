package org.overlord.dtgov.services.deploy.deployers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;

import org.apache.commons.io.IOUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.commons.fabric.utils.ProfilesFabricService;
import org.overlord.dtgov.common.targets.FabricTarget;
import org.overlord.dtgov.services.i18n.Messages;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class FabricDeployer extends AbstractDeployer<FabricTarget> {

    private static Logger logger = LoggerFactory.getLogger(FabricDeployer.class);

    private final static String OVERLORD_PROFILE_FOLDER = "overlord/deploys";

    /**
     * Deploys an artifact by copying it onto the file system.
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
    public String deploy(BaseArtifactType artifact, FabricTarget target, SrampAtomApiClient client) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            // get the artifact content from the repo
            is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
            ProfilesFabricService fabricService=new ProfilesFabricService(target.getJolokiaUrl(), target.getUser(), target.getPassword());

            String profileName = "";
            String artifactId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId");
            if (artifactId != null && !artifactId.equals("")) {
                profileName = artifactId;
            } else {
                profileName = artifact.getName();
            }
            profileName = profileName.replaceAll("-", "_");
            String profileId = fabricService.createProfile(OVERLORD_PROFILE_FOLDER, profileName, null, is);
//           target.getJolokiaUrl(), target.getUser(), target.getPassword());
            // ProfilesFabricService fabricService=
            // record (un)deployment information
            Map<String, String> props = new HashMap<String, String>();
            props.put("deploy.profileId", profileId); //$NON-NLS-1$
            recordUndeploymentInfo(artifact, target, props, client);
            logger.info(Messages.i18n.format("FabricDeployer.deploymentSuccessfully", artifact.getUuid())); //$NON-NLS-1$
            return target.getJolokiaUrl();
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Undeploy an artifact that was simply copied to a file location.
     *
     * @param prevVersionArtifact
     *            the prev version artifact
     * @param undeployInfo
     *            the undeploy info
     * @param target
     *            the target
     */
    @Override
    public void undeploy(BaseArtifactType prevVersionArtifact, BaseArtifactType undeployInfo, FabricTarget target, SrampAtomApiClient client) {
        ProfilesFabricService fabricService = new ProfilesFabricService(target.getJolokiaUrl(), target.getUser(), target.getPassword());
        String profileId = SrampModelUtils.getCustomProperty(undeployInfo, "deploy.profileId");
        try {
            fabricService.deleteProfile(null, profileId);
        } catch (MalformedObjectNameException e) {
            logger.error(Messages.i18n.format("FabricDeployer.undeploymentError", profileId));
        }
    }


}
