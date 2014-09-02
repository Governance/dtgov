package org.overlord.sramp.demos.deployer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.services.deploy.deployers.AbstractDeployer;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Deployer used to proof that it can be added on runtime more deployers
 * than the default ones.
 *
 * @author David Virgil Naranjo
 */
public class CustomDeployer extends AbstractDeployer {

    private static Logger logger = LoggerFactory.getLogger(CustomDeployer.class);

    private final static String CUSTOM_DEPLOY_DIR_PROPERTY = "deploy_dir";

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
    public String deploy(BaseArtifactType artifact, Target target, SrampAtomApiClient client) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            // get the artifact content from the repo
            is = client.getArtifactContent(ArtifactType.valueOf(artifact), artifact.getUuid());
            String deployDirProperty = target.getProperty(CUSTOM_DEPLOY_DIR_PROPERTY);
            File deployDir = new File(deployDirProperty);
            if (!deployDir.exists()) {
                logger.info("Creating Directory for deployment:"+ deployDir); //$NON-NLS-1$
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

            // now actually deploy it by copying it to the right (configured)
            // directory
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
            logger.info("The deployment of the artifact with UUID=" + artifact.getUuid() + " has been successfully done."); //$NON-NLS-1$ $NON-NLS-2$
            return file.getAbsolutePath();
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
    public void undeploy(BaseArtifactType prevVersionArtifact, BaseArtifactType undeployInfo, Target target, SrampAtomApiClient client) {
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

}
