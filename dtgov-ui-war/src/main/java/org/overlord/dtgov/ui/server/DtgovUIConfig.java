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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
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
    public static final String SRAMP_ATOM_API_VALIDATING = "dtgov-ui.s-ramp.atom-api.validating"; //$NON-NLS-1$
    // Task API related properties
    public static final String TASK_API_ENDPOINT = "dtgov-ui.task-api.endpoint"; //$NON-NLS-1$
    public static final String TASK_CLIENT_CLASS = "dtgov-ui.task-client.class"; //$NON-NLS-1$
    public static final String TASK_API_AUTH_PROVIDER = "dtgov-ui.task-api.authentication.provider"; //$NON-NLS-1$
    public static final String TASK_API_BASIC_AUTH_USER = "dtgov-ui.task-api.authentication.basic.username"; //$NON-NLS-1$
    public static final String TASK_API_BASIC_AUTH_PASS = "dtgov-ui.task-api.authentication.basic.password"; //$NON-NLS-1$
    public static final String TASK_API_SAML_ISSUER = "dtgov-ui.task-api.authentication.saml.issuer"; //$NON-NLS-1$
    public static final String TASK_API_SAML_SERVICE = "dtgov-ui.task-api.authentication.saml.service"; //$NON-NLS-1$

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

    // S-RAMP UI integration properties
    public static final String SRAMP_UI_URL_BASE = "dtgov-ui.s-ramp-browser.url-base"; //$NON-NLS-1$

    private static CompositeConfiguration config;
    static {
        config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        String configFile = config.getString(DTGOV_UI_CONFIG_FILE_NAME);
        Long refreshDelay = config.getLong(DTGOV_UI_CONFIG_FILE_REFRESH, 30000l);
        URL url = findDtgovUiConfig(configFile);
        try {
            if (url != null) {
                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(url);
                FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
                fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
                propertiesConfiguration.setReloadingStrategy(fileChangedReloadingStrategy);
                config.addConfiguration(propertiesConfiguration);
            }
            config.addConfiguration(new PropertiesConfiguration(TaskClientAccessor.class.getResource("/META-INF/config/org.overlord.dtgov.ui.server.api.properties"))); //$NON-NLS-1$
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to find the dtgov-ui.properties configuration file.  This will look for the
     * config file in a number of places, depending on the value for 'config file'
     * found on the system properties.
     * @param configFile
     * @throws MalformedURLException
     */
    private static URL findDtgovUiConfig(String configFile) {
        try {
            // If a config file was given (via system properties) then try to
            // find it.  If not, then look for a 'standard' config file.
            if (configFile != null) {
                // Check on the classpath
                URL fromClasspath = DtgovUIConfig.class.getClassLoader().getResource(configFile);
                if (fromClasspath != null)
                    return fromClasspath;

                // Check on the file system
                File file = new File(configFile);
                if (file.isFile())
                    return file.toURI().toURL();
            } else {
                // Check the current user's home directory
                String userHomeDir = System.getProperty("user.home"); //$NON-NLS-1$
                if (userHomeDir != null) {
                    File dirFile = new File(userHomeDir);
                    if (dirFile.isDirectory()) {
                        File cfile = new File(dirFile, "dtgov-ui.properties"); //$NON-NLS-1$
                        if (cfile.isFile())
                            return cfile.toURI().toURL();
                    }
                }

                // Next, check for JBoss
                String jbossConfigDir = System.getProperty("jboss.server.config.dir"); //$NON-NLS-1$
                if (jbossConfigDir != null) {
                    File dirFile = new File(jbossConfigDir);
                    if (dirFile.isDirectory()) {
                        File cfile = new File(dirFile, "dtgov-ui.properties"); //$NON-NLS-1$
                        if (cfile.isFile())
                            return cfile.toURI().toURL();
                    }
                }
                String jbossConfigUrl = System.getProperty("jboss.server.config.url"); //$NON-NLS-1$
                if (jbossConfigUrl != null) {
                    File dirFile = new File(jbossConfigUrl);
                    if (dirFile.isDirectory()) {
                        File cfile = new File(dirFile, "dtgov-ui.properties"); //$NON-NLS-1$
                        if (cfile.isFile())
                            return cfile.toURI().toURL();
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
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
