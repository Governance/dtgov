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
package org.overlord.dtgov.ui.server.api;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * Global access to configuration information.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class DtgovUIConfig {

    private static CompositeConfiguration config;
    static {
        config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try {
            config.addConfiguration(new PropertiesConfiguration(TaskClientAccessor.class.getResource("/META-INF/config/org.overlord.dtgov.ui.server.api.properties")));
        } catch (ConfigurationException e) {}
        System.out.println("DTGov user interface configuration loaded.  S-RAMP Atom API endpoint: " + DtgovUIConfig.config.getString("dtgov-ui.atom-api.endpoint"));
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
