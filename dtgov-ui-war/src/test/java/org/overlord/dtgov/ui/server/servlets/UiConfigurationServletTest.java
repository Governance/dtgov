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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.overlord.dtgov.ui.server.DtgovUIConfig;

/**
 *
 * @author eric.wittmann@redhat.com
 */
public class UiConfigurationServletTest {

    @SuppressWarnings("unused")
    private static final Object EXPECTED_CONFIGURED = "{\r\n" + //$NON-NLS-1$
            "  \"srampui\" : {\r\n" + //$NON-NLS-1$
            "    \"urlBase\" : \"http://localhost:8080/s-ramp-ui\"\r\n" + //$NON-NLS-1$
            "  },\r\n" + //$NON-NLS-1$
            "  \"deployments\" : {\r\n" + //$NON-NLS-1$
            "    \"types\" : {\r\n" + //$NON-NLS-1$
            "      \"SwitchYard Application\" : \"ext/SwitchYardApplication\",\r\n" + //$NON-NLS-1$
            "      \"Web Application\" : \"ext/JavaWebApplication\"\r\n" + //$NON-NLS-1$
            "    },\r\n" + //$NON-NLS-1$
            "    \"stages\" : {\r\n" + //$NON-NLS-1$
            "      \"Development\" : \"http://www.jboss.org/overlord/deployment-status.owl#Dev\",\r\n" + //$NON-NLS-1$
            "      \"Production\" : \"http://www.jboss.org/overlord/deployment-status.owl#Prod\"\r\n" + //$NON-NLS-1$
            "    }\r\n" + //$NON-NLS-1$
            "  }\r\n" + //$NON-NLS-1$
            "}"; //$NON-NLS-1$
    @SuppressWarnings("unused")
    private static final Object EXPECTED_DEFAULT = "{\r\n" + //$NON-NLS-1$
            "  \"srampui\" : {\r\n" + //$NON-NLS-1$
            "    \"urlBase\" : \"http://localhost:8080/s-ramp-ui\"\r\n" + //$NON-NLS-1$
            "  },\r\n" + //$NON-NLS-1$
            "  \"deployments\" : {\r\n" + //$NON-NLS-1$
            "    \"types\" : {\r\n" + //$NON-NLS-1$
            "      \"SwitchYard Application\" : \"ext/SwitchYardApplication\",\r\n" + //$NON-NLS-1$
            "      \"Web Application\" : \"ext/JavaWebApplication\",\r\n" + //$NON-NLS-1$
            "      \"J2EE Application\" : \"ext/JavaEnterpriseApplication\"\r\n" + //$NON-NLS-1$
            "    },\r\n" + //$NON-NLS-1$
            "    \"stages\" : {\r\n" + //$NON-NLS-1$
            "      \"Development\" : \"http://www.jboss.org/overlord/deployment-status.owl#Dev\",\r\n" + //$NON-NLS-1$
            "      \"QA\" : \"http://www.jboss.org/overlord/deployment-status.owl#Qa\",\r\n" + //$NON-NLS-1$
            "      \"Production\" : \"http://www.jboss.org/overlord/deployment-status.owl#Prod\"\r\n" + //$NON-NLS-1$
            "    }\r\n" + //$NON-NLS-1$
            "  }\r\n" + //$NON-NLS-1$
            "}"; //$NON-NLS-1$

    /**
     * Test method for {@link org.overlord.dtgov.ui.server.servlets.UiConfigurationServlet#generateJSONConfig(org.apache.commons.configuration.Configuration)}.
     */
    @Test
    public void testGenerateJSONConfig_Configured() throws Exception {
        final PropertiesConfiguration config = new PropertiesConfiguration();
        config.addProperty(DtgovUIConfig.DEPLOYMENT_TYPE_PREFIX + ".switchyard", "SwitchYard Application:ext/SwitchYardApplication"); //$NON-NLS-1$ //$NON-NLS-2$
        config.addProperty(DtgovUIConfig.DEPLOYMENT_TYPE_PREFIX + ".war", "Web Application:ext/JavaWebApplication"); //$NON-NLS-1$ //$NON-NLS-2$
        config.addProperty(DtgovUIConfig.DEPLOYMENT_CLASSIFIER_STAGE_PREFIX + ".dev", "Development:http://www.jboss.org/overlord/deployment-status.owl#Dev"); //$NON-NLS-1$ //$NON-NLS-2$
        config.addProperty(DtgovUIConfig.DEPLOYMENT_CLASSIFIER_STAGE_PREFIX + ".prod", "Production:http://www.jboss.org/overlord/deployment-status.owl#Prod"); //$NON-NLS-1$ //$NON-NLS-2$
        HttpServletRequest request = new MockHttpServletRequest();
        String rval = UiConfigurationServlet.generateJSONConfig(request, new DtgovUIConfig() {
            @Override
            public Configuration getConfiguration() {
                return config;
            }
        });
        Assert.assertNotNull(rval);
        Assert.assertTrue(rval.contains("http://test.overlord.org:8080/s-ramp-ui")); //$NON-NLS-1$
        // TODO re-enable this assertion but make it cross-platform :(
//        Assert.assertEquals(EXPECTED_CONFIGURED, rval);
    }

    /**
     * Test method for {@link org.overlord.dtgov.ui.server.servlets.UiConfigurationServlet#generateJSONConfig(org.apache.commons.configuration.Configuration)}.
     */
    @Test
    public void testGenerateJSONConfig_Default() throws Exception {
        final PropertiesConfiguration config = new PropertiesConfiguration();
        HttpServletRequest request = new MockHttpServletRequest();
        String rval = UiConfigurationServlet.generateJSONConfig(request, new DtgovUIConfig() {
            @Override
            public Configuration getConfiguration() {
                return config;
            }
        });
        Assert.assertNotNull(rval);
        // TODO re-enable this assertion but make it cross-platform :(
//        Assert.assertEquals(EXPECTED_DEFAULT, rval);
    }

}
