package org.overlord.sramp.governance;

import static org.overlord.dtgov.common.targets.TargetConstants.CUSTOM_TYPE_NAME;
import static org.overlord.dtgov.common.targets.TargetConstants.PREFIX_CUSTOM_PROPERTY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.common.targets.TargetConstants;
import org.overlord.dtgov.common.targets.TargetType;
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
        String classifier = SrampModelUtils.getCustomProperty(artifact, TargetConstants.TARGET_CLASSIFIERS);

        Target target = null;
        if (typeEnum != null) {
            switch (typeEnum) {
            case RHQ:
                String rhq_baseUrl = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_BASE_URL);
                String rhq_pluginName = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_PLUGIN_NAME);
                String rhq_user = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_USER);
                String rhq_password = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_PASSWORD);
                String rhq_group = SrampModelUtils.getCustomProperty(artifact, TargetConstants.RHQ_GROUP);
                target = Target.rhq(name, classifier, rhq_user, rhq_password, rhq_baseUrl, rhq_pluginName, rhq_group);
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
                target = Target.maven(name, classifier, maven_repository, maven_user, maven_password, isReleaseEnabled, isSnapshotEnabled);
                break;
            case CLI:
                String cli_host = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_HOST);
                String cli_port = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_PORT);
                String cli_user = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_USER);
                String cli_password = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_PASSWORD);
                Boolean cli_domainMode = "true".equals(SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_DOMAIN_MODE)); //$NON-NLS-1$
                String cli_serverGroup = SrampModelUtils.getCustomProperty(artifact, TargetConstants.CLI_SERVER_GROUP);
                target = Target.cli(name, classifier, cli_user, cli_password, cli_host, new Integer(cli_port), cli_domainMode, cli_serverGroup);
                break;
            case COPY:
                String deployDir = SrampModelUtils.getCustomProperty(artifact, TargetConstants.COPY_DEPLOY_DIR);
                target = Target.copy(name, classifier, deployDir);
                break;
            case CUSTOM:
                String customType = SrampModelUtils.getCustomProperty(artifact, CUSTOM_TYPE_NAME);
                Map<String, String> properties = SrampModelUtils.getCustomPropertiesByPrefix(artifact, PREFIX_CUSTOM_PROPERTY);

                Map<String, String> parsed_properties = new HashMap<String, String>();
                for (String key : properties.keySet()) {
                    parsed_properties.put(key.substring(PREFIX_CUSTOM_PROPERTY.length()), properties.get(key));
                }
                target = Target.custom(name, classifier, customType, parsed_properties);
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
