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

import org.apache.commons.io.IOUtils;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.targets.MavenTarget;
import org.overlord.dtgov.services.MavenRepoUtil;
import org.overlord.dtgov.services.i18n.Messages;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maven Deployer implementation. It deploys/undeploys an artifact to a
 * pre-configured maven repository.
 *
 * @author David Virgil Naranjo
 */
public class MavenDeployer extends AbstractDeployer<MavenTarget> {

    private static Logger logger = LoggerFactory.getLogger(MavenDeployer.class);

    /**
     * Deploys an artifact to a pre-configured maven repository. The maven GAV
     * properties are required to be set on the artifact.
     *
     * This code is preview at best since it has a lot of loose ends:
     *
     * - if this jar has a parent pom then this parent needs to be in the repo.
     * - credentials should be set in a .settings.xml in the .m2 dir of the user
     * that runs the app
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
    public String deploy(BaseArtifactType artifact, MavenTarget target, SrampAtomApiClient client)
            throws Exception {
        InputStream isJar = null;
        InputStream isPom = null;
        OutputStream osJar = null;
        OutputStream osPom = null;
        InputStream isPom2 = null;
        try {
            // make sure we have maven properties
            String mavenArtifactId = SrampModelUtils.getCustomProperty(artifact, "maven.artifactId"); //$NON-NLS-1$
            String mavenGroupId = SrampModelUtils.getCustomProperty(artifact, "maven.groupId"); //$NON-NLS-1$
            String mavenVersion = SrampModelUtils.getCustomProperty(artifact, "maven.version"); //$NON-NLS-1$
            if (mavenArtifactId == null || mavenGroupId == null || mavenVersion == null) {
                throw new Exception(Messages.i18n.format(
                        "DeploymentResource.MissingMavenProps", artifact.getUuid())); //$NON-NLS-1$
            }
            // find the pom that goes with this artifact
            String pomName = artifact.getName().substring(0, artifact.getName().lastIndexOf(".")) + ".pom"; //$NON-NLS-1$ //$NON-NLS-2$
            QueryResultSet queryResultSet = client
                    .buildQuery("/s-ramp[@name = ?]").parameter(pomName).query(); //$NON-NLS-1$
            if (queryResultSet.size() == 0) {
                throw new Exception(Messages.i18n.format("DeploymentResource.MissingPom", //$NON-NLS-1$
                        artifact.getUuid(), pomName));
            }
            BaseArtifactType pomArtifact = null;
            for (ArtifactSummary artifactSummary2 : queryResultSet) {
                pomArtifact = client.getArtifactMetaData(artifactSummary2);
                String pomMavenArtifactId = SrampModelUtils
                        .getCustomProperty(pomArtifact, "maven.artifactId"); //$NON-NLS-1$
                String pomMavenGroupId = SrampModelUtils.getCustomProperty(pomArtifact, "maven.groupId"); //$NON-NLS-1$
                String pomMavenVersion = SrampModelUtils.getCustomProperty(pomArtifact, "maven.version"); //$NON-NLS-1$
                if (mavenArtifactId.equals(pomMavenArtifactId) && mavenGroupId.equals(pomMavenGroupId)
                        && mavenVersion.equals(pomMavenVersion)) {
                    break;
                }
                pomArtifact = null;
            }
            if (pomArtifact == null) {
                throw new Exception(Messages.i18n.format("DeploymentResource.IncorrectPom", //$NON-NLS-1$
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

            // deploy the artifact to a maven repo as specified in the config of
            // the target
            isPom2 = client.getArtifactContent(pomType, pomArtifact.getUuid());
            MavenRepoUtil util = new MavenRepoUtil();
            MavenRepository repo = util.getMavenReleaseRepo(target.getMavenUrl(), target.isReleaseEnabled(),
                    target.isSnapshotEnabled(), isPom2);
            ReleaseId releaseId = new ReleaseIdImpl(mavenArtifactId, mavenGroupId, mavenVersion);
            // org.sonatype.aether.artifact.Artifact artifact =
            // repo.resolveArtifact(releaseId.toExternalForm());
            repo.deployArtifact(releaseId, jarFile, pomFile);

            // Don't register undeployment info - we never undeploy from maven

            // return maven url
            logger.info(Messages.i18n.format("MavenDeployer.deploymentSuccessfully", artifact.getUuid())); //$NON-NLS-1$
            return target.getMavenUrl();
        } finally {
            IOUtils.closeQuietly(isPom);
            IOUtils.closeQuietly(isPom2);
            IOUtils.closeQuietly(isJar);
            IOUtils.closeQuietly(osPom);
            IOUtils.closeQuietly(osJar);
        }
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.services.deploy.Deployer#undeploy(org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType, org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType, org.overlord.dtgov.common.Target)
     */
    @Override
    public void undeploy(BaseArtifactType prevVersionArtifact,
 BaseArtifactType undeployInfo, MavenTarget target,
            SrampAtomApiClient client) throws Exception {
        // We never undeploy from maven

    }

}
