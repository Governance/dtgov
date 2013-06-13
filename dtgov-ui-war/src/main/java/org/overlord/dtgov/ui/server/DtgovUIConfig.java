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

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.overlord.dtgov.ui.server.services.tasks.TaskClientAccessor;

/**
 * Global access to configuration information.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class DtgovUIConfig {

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
        try {
            config.addConfiguration(new PropertiesConfiguration(TaskClientAccessor.class.getResource("/META-INF/config/org.overlord.dtgov.ui.server.api.properties")));
        } catch (ConfigurationException e) {}
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
