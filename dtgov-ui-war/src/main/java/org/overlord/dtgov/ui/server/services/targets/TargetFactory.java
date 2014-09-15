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
package org.overlord.dtgov.ui.server.services.targets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.overlord.dtgov.common.model.DtgovModel;
import org.overlord.dtgov.ui.client.shared.beans.CliTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CopyTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetProperty;
import org.overlord.dtgov.ui.client.shared.beans.MavenTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.RHQTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetClassifier;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetType;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;

/**
 * Factory class that contains all the code related conversions from S-ramp
 * Object to TargetBeand and viceversa.
 *
 * @author David Virgil Naranjo
 */
public class TargetFactory {



    /**
     * Gets the classifiers as a List of TargetClassifier object. The input
     * param is a String with the items separated by '|'
     *
     * @param classifiers
     *            the classifiers
     * @return the classifiers
     */
    private static List<TargetClassifier> getClassifiers(String classifiers) {
        List<TargetClassifier> list = null;

        if (StringUtils.isNotBlank(classifiers)) {
            list = new ArrayList<TargetClassifier>();
            String[] classifiers_split = StringUtils.split(classifiers, DtgovModel.TARGET_CLASSIFIER_SEPARATOR);
            for (int i = 0; i < classifiers_split.length; i++) {
                list.add(new TargetClassifier(classifiers_split[i]));
            }
        }
        return list;
    }

    /**
     * Converts the List of classifiers into a String object with all the items
     * separated by a '|'.
     *
     * @param list
     *            the list
     * @return the classifiers
     */
    private static String getClassifiers(List<TargetClassifier> list) {
        String classifiers = ""; //$NON-NLS-1$
        for (TargetClassifier classifier : list) {
            classifiers += classifier.getValue() + DtgovModel.TARGET_CLASSIFIER_SEPARATOR;
        }
        if (StringUtils.isNotBlank(classifiers)) {
            classifiers = classifiers.substring(0, classifiers.length() - 1);
        }

        return classifiers;
    }

    /**
     * Converts a S-ramp Target input to a TargetBean object.
     *
     * @param artifact
     *            the artifact
     * @return the target bean
     */
    public static TargetBean toTarget(BaseArtifactType artifact){
        TargetBean bean=null;

        String type=SrampModelUtils.getCustomProperty(artifact, DtgovModel.TARGET_TYPE);
        TargetType typeEnum=TargetType.value(type);
        String name=artifact.getName();
        String description=artifact.getDescription();
        String uuid=artifact.getUuid();
        String classifiers=SrampModelUtils.getCustomProperty(artifact, DtgovModel.TARGET_CLASSIFIERS);
        List<TargetClassifier> classifiersList = getClassifiers(classifiers);
        if(typeEnum!=null){
            switch (typeEnum) {
            case RHQ:


                String rhq_baseUrl=SrampModelUtils.getCustomProperty(artifact, DtgovModel.RHQ_BASE_URL);
                String rhq_pluginName=SrampModelUtils.getCustomProperty(artifact, DtgovModel.RHQ_PLUGIN_NAME);
                String rhq_user=SrampModelUtils.getCustomProperty(artifact, DtgovModel.RHQ_USER);
                String rhq_password=SrampModelUtils.getCustomProperty(artifact, DtgovModel.RHQ_PASSWORD);
                String rhq_group = SrampModelUtils.getCustomProperty(artifact, DtgovModel.RHQ_GROUP);
                bean = new RHQTargetBean(uuid, classifiersList, description, name, rhq_user, rhq_password, rhq_baseUrl, rhq_pluginName, rhq_group);

                break;
            case MAVEN:
                String maven_isReleaseEnabled = SrampModelUtils.getCustomProperty(artifact, DtgovModel.MAVEN_IS_RELEASE_ENABLED);
                String maven_isSnapshotEnabled = SrampModelUtils.getCustomProperty(artifact, DtgovModel.MAVEN_SNAPSHOT_ENABLED);
                String maven_user = SrampModelUtils.getCustomProperty(artifact, DtgovModel.MAVEN_USER);
                String maven_password = SrampModelUtils.getCustomProperty(artifact, DtgovModel.MAVEN_PASSWORD);
                String maven_repository = SrampModelUtils.getCustomProperty(artifact, DtgovModel.MAVEN_REPOSITORY_URL);
                boolean isReleaseEnabled = false;
                boolean isSnapshotEnabled = false;
                if (StringUtils.isNotBlank(maven_isReleaseEnabled) && maven_isReleaseEnabled.equals("true")) { //$NON-NLS-1$
                    isReleaseEnabled = true;
                }
                if (StringUtils.isNotBlank(maven_isSnapshotEnabled) && maven_isSnapshotEnabled.equals("true")) { //$NON-NLS-1$
                    isSnapshotEnabled = true;
                }
                bean = new MavenTargetBean(uuid, classifiersList, description, name, maven_repository,
                        isReleaseEnabled, isSnapshotEnabled, maven_user, maven_password);
                break;
            case CLI:
                String cli_host = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CLI_HOST);
                String cli_port = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CLI_PORT);
                String cli_user = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CLI_USER);
                String cli_password = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CLI_PASSWORD);
                String cli_domainMode = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CLI_DOMAIN_MODE);
                String cli_serverGroup = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CLI_SERVER_GROUP);
                Integer port = null;
                if (StringUtils.isNotBlank(cli_port)) {
                    port = new Integer(cli_port);
                }
                bean = new CliTargetBean(uuid, classifiersList, description, name, cli_user, cli_password,
                        cli_host, port, "true".equals(cli_domainMode), cli_serverGroup); //$NON-NLS-1$
                break;
            case COPY:
                String deployDir = SrampModelUtils.getCustomProperty(artifact, DtgovModel.COPY_DEPLOY_DIR);
                bean = new CopyTargetBean(uuid, classifiersList, description, name, deployDir);
                break;
            case CUSTOM:
                String customType = SrampModelUtils.getCustomProperty(artifact, DtgovModel.CUSTOM_TYPE_NAME);
                Map<String,String> properties=SrampModelUtils.getCustomPropertiesByPrefix(artifact, DtgovModel.PREFIX_CUSTOM_PROPERTY);

                List<CustomTargetProperty> parsed_properties = new ArrayList<CustomTargetProperty>();
                for (String key : properties.keySet()) {
                    parsed_properties.add(new CustomTargetProperty(key.substring(DtgovModel.PREFIX_CUSTOM_PROPERTY.length()), properties.get(key)));
                }
                bean = new CustomTargetBean(uuid, classifiersList, description, name, customType, parsed_properties);
                break;
            default:
                break;
            }
        }

        return bean;
    }


    /**
     * Converts a TargetBean object into a S-ramp Object
     *
     * @param target
     * @return the base artifact type
     */
    public static BaseArtifactType toBaseArtifact(TargetBean target) {
        ExtendedArtifactType artifact = new ExtendedArtifactType();
        artifact.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        artifact.setExtendedType(DtgovModel.DeploymentTargetType);
        artifact.setName(target.getName());
        artifact.setDescription(target.getDescription());
        artifact.setUuid(target.getUuid());
        if (target.getClassifiers() != null && !target.getClassifiers().isEmpty()) {
            SrampModelUtils.setCustomProperty(artifact, DtgovModel.TARGET_CLASSIFIERS, getClassifiers(target.getClassifiers()));
        }
        SrampModelUtils.setCustomProperty(artifact, DtgovModel.TARGET_TYPE, target.getType().getValue());

        switch (target.getType()) {
        case RHQ:
            RHQTargetBean rhq = (RHQTargetBean) target;
            if (StringUtils.isNotBlank(rhq.getBaseUrl())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.RHQ_BASE_URL, rhq.getBaseUrl());
            }
            if (StringUtils.isNotBlank(rhq.getRhqPlugin())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.RHQ_PLUGIN_NAME, rhq.getRhqPlugin());
            }
            if (StringUtils.isNotBlank(rhq.getUser())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.RHQ_USER, rhq.getUser());
            }
            if (StringUtils.isNotBlank(rhq.getPassword())) {
                SrampModelUtils.setCustomEncryptedProperty(artifact, DtgovModel.RHQ_PASSWORD, rhq.getPassword());
            }
            if (StringUtils.isNotBlank(rhq.getRhqGroup())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.RHQ_GROUP, rhq.getRhqGroup());
            }
            break;
        case CLI:
            CliTargetBean cli = (CliTargetBean) target;
            if (StringUtils.isNotBlank(cli.getHost())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.CLI_HOST, cli.getHost());
            }
            if (cli.getPort() != null) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.CLI_PORT, String.valueOf(cli.getPort()));
            }
            if (StringUtils.isNotBlank(cli.getUser())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.CLI_USER, cli.getUser());
            }
            if (StringUtils.isNotBlank(cli.getPassword())) {
                SrampModelUtils.setCustomEncryptedProperty(artifact, DtgovModel.CLI_PASSWORD, cli.getPassword());
            }
            if (cli.getDomainMode() != null) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.CLI_DOMAIN_MODE, String.valueOf(cli.getDomainMode()));
            }
            if (StringUtils.isNotBlank(cli.getServerGroup())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.CLI_SERVER_GROUP, cli.getServerGroup());
            }
            break;
        case COPY:
            CopyTargetBean copy = (CopyTargetBean) target;
            if (StringUtils.isNotBlank(copy.getDeployDirectory())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.COPY_DEPLOY_DIR, copy.getDeployDirectory());
            }
            break;
        case MAVEN:
            MavenTargetBean maven = (MavenTargetBean) target;
            if (StringUtils.isNotBlank(maven.getRepositoryUrl())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.MAVEN_REPOSITORY_URL, maven.getRepositoryUrl());
            }
            if (StringUtils.isNotBlank(maven.getUser())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.MAVEN_USER, maven.getUser());
            }
            if (StringUtils.isNotBlank(maven.getPassword())) {
                SrampModelUtils.setCustomEncryptedProperty(artifact, DtgovModel.MAVEN_PASSWORD, maven.getPassword());
            }
            if (maven.isReleaseEnabled()) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.MAVEN_IS_RELEASE_ENABLED, "true"); //$NON-NLS-1$
            } else {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.MAVEN_IS_RELEASE_ENABLED, "false"); //$NON-NLS-1$
            }
            if (maven.isSnapshotEnabled()) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.MAVEN_SNAPSHOT_ENABLED, "true"); //$NON-NLS-1$
            } else {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.MAVEN_SNAPSHOT_ENABLED, "false"); //$NON-NLS-1$
            }
            break;
        case CUSTOM:
            CustomTargetBean custom = (CustomTargetBean) target;
            if (StringUtils.isNotBlank(custom.getCustomTypeName())) {
                SrampModelUtils.setCustomProperty(artifact, DtgovModel.CUSTOM_TYPE_NAME, custom.getCustomTypeName());
            }
            if (custom.getProperties() != null && custom.getProperties().size() > 0) {
                for (CustomTargetProperty key : custom.getProperties()) {
                    SrampModelUtils.setCustomProperty(artifact, DtgovModel.PREFIX_CUSTOM_PROPERTY + key.getKey(), key.getValue());
                }
            }
        default:
            break;
        }
        return artifact;
    }

    /**
     * Convert an S-ramp QueryResultSet object into a List of TargetSummaryBean.
     *
     * @param resultSet
     *            the result set
     * @return the list
     */
    public static List<TargetSummaryBean> asList(QueryResultSet resultSet) {
        List<TargetSummaryBean> targets = new ArrayList<TargetSummaryBean>();
        for (ArtifactSummary artifactSummary : resultSet) {
            TargetSummaryBean bean = new TargetSummaryBean();
            bean.setName(artifactSummary.getName());
            bean.setDescription(artifactSummary.getDescription());
            bean.setUuid(artifactSummary.getUuid());
            bean.setType(TargetType.value(artifactSummary.getCustomPropertyValue(DtgovModel.TARGET_TYPE)));
            targets.add(bean);
        }
        return targets;
    }
}
