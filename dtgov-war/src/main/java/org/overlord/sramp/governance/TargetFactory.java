package org.overlord.sramp.governance;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.common.target.TargetConstants;
import org.overlord.dtgov.common.target.TargetType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;

public class TargetFactory {

    public static Target toTarget(BaseArtifactType artifact) {
        String type = SrampModelUtils.getCustomProperty(artifact, TargetConstants.TARGET_TYPE);
        TargetType typeEnum = TargetType.value(type);
        String name = artifact.getName();
        String description = artifact.getDescription();
        String uuid = artifact.getUuid();
        String classifier = SrampModelUtils.getCustomProperty(artifact, TargetConstants.TARGET_CLASSIFIERS);
        ;
        Target target = null;
        if (typeEnum != null) {
            switch (typeEnum) {
            case RHQ:

                String rhq_baseUrl = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_BASE_URL);
                String rhq_pluginName = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_PLUGIN_NAME);
                String rhq_user = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_USER);
                String rhq_password = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_PASSWORD);
                target = new Target(name, classifier, rhq_user, rhq_password, rhq_baseUrl, rhq_pluginName);
                break;
            case MAVEN:

                String maven_isReleaseEnabled = SrampModelUtils.getCustomProperty(artifact, TargetConstants.MAVEN_IS_RELEASE_ENABLED);
                String maven_isSnapshotEnabled = SrampModelUtils.getCustomProperty(artifact, TargetConstants.MAVEN_SNAPSHOT_ENABLED);
                String maven_user = SrampModelUtils.getCustomProperty(artifact, TargetConstants.MAVEN_USER);
                String maven_password = SrampModelUtils.getCustomProperty(artifact, TargetConstants.MAVEN_PASSWORD);
                String maven_repository = SrampModelUtils.getCustomProperty(artifact, TargetConstants.MAVEN_REPOSITORY_URL);
                boolean isReleaseEnabled = false;
                boolean isSnapshotEnabled = false;
                if (StringUtils.isNotBlank(maven_isReleaseEnabled) && maven_isReleaseEnabled.equals("true")) { //$NON-NLS-1$
                    isReleaseEnabled = true;
                }
                if (StringUtils.isNotBlank(maven_isSnapshotEnabled) && maven_isSnapshotEnabled.equals("true")) { //$NON-NLS-1$
                    isSnapshotEnabled = true;
                }
                target = new Target(name, classifier, maven_repository, maven_user, maven_password, isReleaseEnabled, isSnapshotEnabled);
                break;
            case CLI:

                String cli_host = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_HOST);
                String cli_port = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_PORT);
                String cli_user = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_USER);
                String cli_password = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_PASSWORD);
                Integer port = null;
                if (StringUtils.isNotBlank(cli_port)) {
                    try {
                        port = Integer.parseInt(cli_port);
                    } catch (NumberFormatException e) {

                    }
                }
                target = new Target(name, classifier, cli_user, cli_password, cli_host, cli_port);
                break;
            case COPY:
                String deployDir = SrampModelUtils.getCustomProperty(artifact, TargetConstants.COPY_DEPLOY_DIR);
                target = new Target(name, classifier, deployDir);
                break;
            default:
                break;
            }
        }
        if (target != null) {
            target.setDescription(description);
        }
        return target;
    }

    public static List<Target> asList(QueryResultSet resultSet) throws SrampClientException, SrampAtomException {
        List<Target> list = new ArrayList<Target>();
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        for (ArtifactSummary artifactSummary : resultSet) {
            BaseArtifactType artifact = client.getArtifactMetaData(artifactSummary.getUuid());
            Target target = toTarget(artifact);
            if (target != null) {
                list.add(target);
            }
        }
        return list;
    }

}
