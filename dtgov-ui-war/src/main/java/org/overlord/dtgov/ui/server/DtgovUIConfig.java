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
package org.overlord.dtgov.ui.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.configuration.Configuration;
import org.overlord.commons.config.ConfigurationFactory;
import org.overlord.dtgov.ui.server.services.tasks.TaskClientAccessor;

/**
 * Global access to configuration information.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class DtgovUIConfig {

    public static final String DTGOV_UI_CONFIG_FILE_NAME     = "dtgov-ui.config.file.name"; //$NON-NLS-1$
    public static final String DTGOV_UI_CONFIG_FILE_REFRESH  = "dtgov-ui.config.file.refresh"; //$NON-NLS-1$

    // S-RAMP related properties
    public static final String SRAMP_ATOM_API_ENDPOINT = "dtgov-ui.s-ramp.atom-api.endpoint"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_AUTH_PROVIDER = "dtgov-ui.s-ramp.atom-api.authentication.provider"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_BASIC_AUTH_USER = "dtgov-ui.s-ramp.atom-api.authentication.basic.username"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_BASIC_AUTH_PASS = "dtgov-ui.s-ramp.atom-api.authentication.basic.password"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_ISSUER = "dtgov-ui.s-ramp.atom-api.authentication.saml.issuer"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_SERVICE = "dtgov-ui.s-ramp.atom-api.authentication.saml.service"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_AUTH_SIGN_ASSERTIONS = "dtgov-ui.s-ramp.atom-api.authentication.saml.sign-assertions"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_AUTH_KEYSTORE = "dtgov-ui.s-ramp.atom-api.authentication.saml.keystore"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_AUTH_KEYSTORE_PASSWORD = "dtgov-ui.s-ramp.atom-api.authentication.saml.keystore-password"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_AUTH_KEY_ALIAS = "dtgov-ui.s-ramp.atom-api.authentication.saml.key-alias"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_SAML_AUTH_KEY_PASSWORD = "dtgov-ui.s-ramp.atom-api.authentication.saml.key-password"; //$NON-NLS-1$
    public static final String SRAMP_ATOM_API_VALIDATING = "dtgov-ui.s-ramp.atom-api.validating"; //$NON-NLS-1$
    // Task API related properties
    public static final String TASK_API_ENDPOINT = "dtgov-ui.task-api.endpoint"; //$NON-NLS-1$
    public static final String TASK_CLIENT_CLASS = "dtgov-ui.task-client.class"; //$NON-NLS-1$
    public static final String TASK_API_AUTH_PROVIDER = "dtgov-ui.task-api.authentication.provider"; //$NON-NLS-1$
    public static final String TASK_API_BASIC_AUTH_USER = "dtgov-ui.task-api.authentication.basic.username"; //$NON-NLS-1$
    public static final String TASK_API_BASIC_AUTH_PASS = "dtgov-ui.task-api.authentication.basic.password"; //$NON-NLS-1$
    public static final String TASK_API_SAML_ISSUER = "dtgov-ui.task-api.authentication.saml.issuer"; //$NON-NLS-1$
    public static final String TASK_API_SAML_SERVICE = "dtgov-ui.task-api.authentication.saml.service"; //$NON-NLS-1$
    public static final String TASK_API_SAML_AUTH_SIGN_ASSERTIONS = "dtgov-ui.task-api.authentication.saml.sign-assertions"; //$NON-NLS-1$
    public static final String TASK_API_SAML_AUTH_KEYSTORE = "dtgov-ui.task-api.authentication.saml.keystore"; //$NON-NLS-1$
    public static final String TASK_API_SAML_AUTH_KEYSTORE_PASSWORD = "dtgov-ui.task-api.authentication.saml.keystore-password"; //$NON-NLS-1$
    public static final String TASK_API_SAML_AUTH_KEY_ALIAS = "dtgov-ui.task-api.authentication.saml.key-alias"; //$NON-NLS-1$
    public static final String TASK_API_SAML_AUTH_KEY_PASSWORD = "dtgov-ui.task-api.authentication.saml.key-password"; //$NON-NLS-1$

    // Deployment Lifecycle UI properties
    public static final String DEPLOYMENT_CLASSIFIER_BASE = "dtgov-ui.deployment-lifecycle.classifiers.base"; //$NON-NLS-1$
    public static final String DEPLOYMENT_INITIAL_CLASSIFIER = "dtgov-ui.deployment-lifecycle.classifiers.initial"; //$NON-NLS-1$
    public static final String DEPLOYMENT_ALL_CLASSIFIER = "dtgov-ui.deployment-lifecycle.classifiers.all"; //$NON-NLS-1$
    public static final String DEPLOYMENT_INPROGRESS_CLASSIFIER = "dtgov-ui.deployment-lifecycle.classifiers.in-progress"; //$NON-NLS-1$
    // This next one is a prefix for any property that will indicate a possible classifier stage that
    // should be displayed in the UI.  In the dtgov ui configuration file, multiple properties would
    // be specified that begin with this prefix and have a value of the format  {label}:{classifier}
    public static final String DEPLOYMENT_CLASSIFIER_STAGE_PREFIX = "dtgov-ui.deployment-lifecycle.classifiers.stage"; //$NON-NLS-1$
    // And another one that is a prefix for any property that will indicate a possible deployment type
    // that should be displayed in the UI.  In the dtgov ui configuration file, multiple properties would
    // be specified that begin with this prefix and have a value of the format  {label}:{type}
    public static final String DEPLOYMENT_TYPE_PREFIX = "dtgov-ui.deployment-lifecycle.types"; //$NON-NLS-1$

    //WORKFLOW PROPERTIES
    private static final String WORKFLOW_PREFIX = "dtgov-ui.deployment-lifecycle.workflow"; //$NON-NLS-1$
    public static final String WORKFLOW_TYPE_PREFIX = WORKFLOW_PREFIX+".type";
    public static final String WORKFLOW_PROPERTY_PREFIX = WORKFLOW_PREFIX+".property";
    
    // S-RAMP UI integration properties
    public static final String SRAMP_UI_URL_BASE = "dtgov-ui.s-ramp-browser.url-base"; //$NON-NLS-1$

    private static Configuration config;
    static {
        String configFile = System.getProperty(DTGOV_UI_CONFIG_FILE_NAME);
        String refreshDelayStr = System.getProperty(DTGOV_UI_CONFIG_FILE_REFRESH);
        Long refreshDelay = 5000l;
        if (refreshDelayStr != null) {
            refreshDelay = new Long(refreshDelayStr);
        }

        config = ConfigurationFactory.createConfig(
                configFile,
                "dtgov-ui.properties", //$NON-NLS-1$
                refreshDelay,
                "/META-INF/config/org.overlord.dtgov.ui.server.api.properties", //$NON-NLS-1$
                TaskClientAccessor.class);
    }

    /**
     * Constructor.
     */
    public DtgovUIConfig() {
    }

    /**
     * Public accessor to get the configuration.
     */
    public Configuration getConfiguration() {
        return config;
    }

    /**
     * Gets a list of the configured deployment stages.
     */
    public List<DeploymentStage> getStages() {
        List<DeploymentStage> stages = new ArrayList<DeploymentStage>();

        @SuppressWarnings("unchecked")
        Iterator<String> stageKeys = config.getKeys(DtgovUIConfig.DEPLOYMENT_CLASSIFIER_STAGE_PREFIX);
        while (stageKeys.hasNext()) {
            String stageKey = stageKeys.next();
            String value = config.getString(stageKey);
            if (value.contains(":")) { //$NON-NLS-1$
                int idx = value.indexOf(':');
                String label = value.substring(0, idx);
                String classifier = value.substring(idx+1);
                stages.add(new DeploymentStage(label, classifier));
            }
        }

        return stages;
    }

    /**
     * A stage configured in the config file.
     * @author eric.wittmann@redhat.com
     */
    public static class DeploymentStage {
        private final String label;
        private final String classifier;

        /**
         * Constructor.
         */
        public DeploymentStage(String label, String classifier) {
            this.label = label;
            this.classifier = classifier;
        }

        /**
         * @return the label
         */
        public String getLabel() {
            return label;
        }

        /**
         * @return the classifier
         */
        public String getClassifier() {
            return classifier;
        }
    }
}
