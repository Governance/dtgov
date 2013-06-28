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
package org.overlord.dtgov.ui.server.servlets;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.overlord.dtgov.ui.server.DtgovUIConfig;

/**
 *
 * @author eric.wittmann@redhat.com
 */
public class UiConfigurationServletTest {

    private static final Object EXPECTED_CONFIGURED = "{\r\n" +
            "  \"srampui\" : {\r\n" +
            "    \"urlBase\" : \"http://localhost:8080/s-ramp-ui\"\r\n" +
            "  },\r\n" +
            "  \"deployments\" : {\r\n" +
            "    \"types\" : {\r\n" +
            "      \"SwitchYard Application\" : \"ext/SwitchYardApplication\",\r\n" +
            "      \"Web Application\" : \"ext/JavaWebApplication\"\r\n" +
            "    },\r\n" +
            "    \"stages\" : {\r\n" +
            "      \"Development\" : \"http://www.jboss.org/overlord/deployment-status.owl#Dev\",\r\n" +
            "      \"Production\" : \"http://www.jboss.org/overlord/deployment-status.owl#Prod\"\r\n" +
            "    }\r\n" +
            "  }\r\n" +
            "}";
    private static final Object EXPECTED_DEFAULT = "{\r\n" +
            "  \"srampui\" : {\r\n" +
            "    \"urlBase\" : \"http://localhost:8080/s-ramp-ui\"\r\n" +
            "  },\r\n" +
            "  \"deployments\" : {\r\n" +
            "    \"types\" : {\r\n" +
            "      \"SwitchYard Application\" : \"ext/SwitchYardApplication\",\r\n" +
            "      \"Web Application\" : \"ext/JavaWebApplication\",\r\n" +
            "      \"J2EE Application\" : \"ext/JavaEnterpriseApplication\"\r\n" +
            "    },\r\n" +
            "    \"stages\" : {\r\n" +
            "      \"Development\" : \"http://www.jboss.org/overlord/deployment-status.owl#Dev\",\r\n" +
            "      \"QA\" : \"http://www.jboss.org/overlord/deployment-status.owl#Qa\",\r\n" +
            "      \"Production\" : \"http://www.jboss.org/overlord/deployment-status.owl#Prod\"\r\n" +
            "    }\r\n" +
            "  }\r\n" +
            "}";

    /**
     * Test method for {@link org.overlord.dtgov.ui.server.servlets.UiConfigurationServlet#generateJSONConfig(org.apache.commons.configuration.Configuration)}.
     */
    @Test
    public void testGenerateJSONConfig_Configured() throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.addProperty(DtgovUIConfig.DEPLOYMENT_TYPE_PREFIX + ".switchyard", "SwitchYard Application:ext/SwitchYardApplication");
        config.addProperty(DtgovUIConfig.DEPLOYMENT_TYPE_PREFIX + ".war", "Web Application:ext/JavaWebApplication");
        config.addProperty(DtgovUIConfig.DEPLOYMENT_CLASSIFIER_STAGE_PREFIX + ".dev", "Development:http://www.jboss.org/overlord/deployment-status.owl#Dev");
        config.addProperty(DtgovUIConfig.DEPLOYMENT_CLASSIFIER_STAGE_PREFIX + ".prod", "Production:http://www.jboss.org/overlord/deployment-status.owl#Prod");
        String rval = UiConfigurationServlet.generateJSONConfig(config);
        Assert.assertEquals(EXPECTED_CONFIGURED, rval);
    }

    /**
     * Test method for {@link org.overlord.dtgov.ui.server.servlets.UiConfigurationServlet#generateJSONConfig(org.apache.commons.configuration.Configuration)}.
     */
    @Test
    public void testGenerateJSONConfig_Default() throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        String rval = UiConfigurationServlet.generateJSONConfig(config);
        Assert.assertEquals(EXPECTED_DEFAULT, rval);
    }

}
