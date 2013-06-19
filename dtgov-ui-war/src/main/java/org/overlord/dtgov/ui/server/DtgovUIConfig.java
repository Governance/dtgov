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

    public static final String DTGOV_UI_CONFIG_FILE_NAME     = "dtgov-ui.config.file.name";
    public static final String DTGOV_UI_CONFIG_FILE_REFRESH  = "dtgov-ui.config.file.refresh";

    // S-RAMP related properties
    public static final String SRAMP_ATOM_API_ENDPOINT = "dtgov-ui.s-ramp.atom-api.endpoint";
    public static final String SRAMP_ATOM_API_AUTH_PROVIDER = "dtgov-ui.s-ramp.atom-api.authentication.provider";
    public static final String SRAMP_ATOM_API_BASIC_AUTH_USER = "dtgov-ui.s-ramp.atom-api.authentication.basic.username";
    public static final String SRAMP_ATOM_API_BASIC_AUTH_PASS = "dtgov-ui.s-ramp.atom-api.authentication.basic.password";
    public static final String SRAMP_ATOM_API_SAML_ISSUER = "dtgov-ui.s-ramp.atom-api.authentication.saml.issuer";
    public static final String SRAMP_ATOM_API_SAML_SERVICE = "dtgov-ui.s-ramp.atom-api.authentication.saml.service";
    public static final String SRAMP_ATOM_API_VALIDATING = "dtgov-ui.s-ramp.atom-api.validating";
    // Task API related properties
    public static final String TASK_API_ENDPOINT = "dtgov-ui.task-api.endpoint";
    public static final String TASK_CLIENT_CLASS = "dtgov-ui.task-client.class";
    public static final String TASK_API_AUTH_PROVIDER = "dtgov-ui.task-api.authentication.provider";
    public static final String TASK_API_BASIC_AUTH_USER = "dtgov-ui.task-api.authentication.basic.username";
    public static final String TASK_API_BASIC_AUTH_PASS = "dtgov-ui.task-api.authentication.basic.password";
    public static final String TASK_API_SAML_ISSUER = "dtgov-ui.task-api.authentication.saml.issuer";
    public static final String TASK_API_SAML_SERVICE = "dtgov-ui.task-api.authentication.saml.service";

    // Deployment Lifecycle UI properties
    public static final String DEPLOYMENT_INITIAL_CLASSIFIER = "dtgov-ui.deployment-lifecycle.classifiers.initial";
    public static final String DEPLOYMENT_ALL_CLASSIFIER = "dtgov-ui.deployment-lifecycle.classifiers.all";
    public static final String DEPLOYMENT_INPROGRESS_CLASSIFIER = "dtgov-ui.deployment-lifecycle.classifiers.in-progress";
    // This next one is a prefix for any property that will indicate a possible classifier stage that
    // should be displayed in the UI.  In the dtgov ui configuration file, multiple properties would
    // be specified that begin with this prefix and have a value of the format  {label}:{classifier}
    public static final String DEPLOYMENT_CLASSIFIER_STAGE_PREFIX = "dtgov-ui.deployment-lifecycle.classifiers.stage";
    // And another one that is a prefix for any property that will indicate a possible deployment type
    // that should be displayed in the UI.  In the dtgov ui configuration file, multiple properties would
    // be specified that begin with this prefix and have a value of the format  {label}:{type}
    public static final String DEPLOYMENT_TYPE_PREFIX = "dtgov-ui.deployment-lifecycle.types";

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
            config.addConfiguration(new PropertiesConfiguration(TaskClientAccessor.class.getResource("/META-INF/config/org.overlord.dtgov.ui.server.api.properties")));
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
                String userHomeDir = System.getProperty("user.home");
                if (userHomeDir != null) {
                    File dirFile = new File(userHomeDir);
                    if (dirFile.isDirectory()) {
                        File cfile = new File(dirFile, "dtgov-ui.properties");
                        if (cfile.isFile())
                            return cfile.toURI().toURL();
                    }
                }

                // Next, check for JBoss
                String jbossConfigDir = System.getProperty("jboss.server.config.dir");
                if (jbossConfigDir != null) {
                    File dirFile = new File(jbossConfigDir);
                    if (dirFile.isDirectory()) {
                        File cfile = new File(dirFile, "dtgov-ui.properties");
                        if (cfile.isFile())
                            return cfile.toURI().toURL();
                    }
                }
                String jbossConfigUrl = System.getProperty("jboss.server.config.url");
                if (jbossConfigUrl != null) {
                    File dirFile = new File(jbossConfigUrl);
                    if (dirFile.isDirectory()) {
                        File cfile = new File(dirFile, "dtgov-ui.properties");
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

}
