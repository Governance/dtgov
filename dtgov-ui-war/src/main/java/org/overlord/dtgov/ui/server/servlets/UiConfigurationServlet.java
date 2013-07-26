/*
 * Copyright 2012 JBoss Inc
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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.overlord.dtgov.ui.server.DtgovUIConfig;
import org.overlord.dtgov.ui.server.DtgovUIConfig.DeploymentStage;

/**
 * A standard servlet that delivers all of the UI configuration as a handy bunch 'o JSON.
 *
 * @author eric.wittmann@redhat.com
 */
public class UiConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = UiConfigurationServlet.class.hashCode();

    @Inject
    private DtgovUIConfig config;

	/**
	 * Constructor.
	 */
	public UiConfigurationServlet() {
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException,
			IOException {
        // Tell the browser to never cache this JavaScript
        noCache(response);

        // Now generate the JavaScript data (JSON)
        response.setContentType("text/javascript");

        try {
            String json = generateJSONConfig(config);
            response.getOutputStream().write("var OVERLORD_DTGOVUI_CONFIG = ".getBytes("UTF-8"));
            response.getOutputStream().write(json.getBytes("UTF-8"));
            response.getOutputStream().write(";".getBytes("UTF-8"));
        } catch (Exception e) {
            throw new ServletException(e);
        }
	}

	/**
	 * Called to generate the JSON.
	 * @param config
	 * @throws Exception
	 */
	protected static String generateJSONConfig(DtgovUIConfig config) throws Exception {
        StringWriter json = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(json);
        g.useDefaultPrettyPrinter();
        g.writeStartObject();

        // Some s-ramp UI/browser integration settings
        g.writeObjectFieldStart("srampui");
        g.writeStringField("urlBase", config.getConfiguration().getString(DtgovUIConfig.SRAMP_UI_URL_BASE, "http://localhost:8080/s-ramp-ui"));
        g.writeEndObject();

        g.writeObjectFieldStart("deployments");
        // Pull in any configured deployment types.
        Iterator<String> typeKeys = config.getConfiguration().getKeys(DtgovUIConfig.DEPLOYMENT_TYPE_PREFIX);
        int count = 0;
        g.writeObjectFieldStart("types");
        while (typeKeys.hasNext()) {
            String typeKey = typeKeys.next();
            String value = config.getConfiguration().getString(typeKey);
            if (value.contains(":")) {
                int idx = value.indexOf(':');
                String label = value.substring(0, idx);
                String type = value.substring(idx+1);
                g.writeStringField(label, type);
                count++;
            }
        }
        if (count == 0) {
            g.writeStringField("SwitchYard Application", "ext/SwitchYardApplication");
            g.writeStringField("Web Application", "ext/JavaWebApplication");
            g.writeStringField("J2EE Application", "ext/JavaEnterpriseApplication");
        }
        g.writeEndObject();

        // Pull in any configured deployment stages.
        List<DeploymentStage> stages = config.getStages();
        g.writeObjectFieldStart("stages");
        if (stages.isEmpty()) {
            g.writeStringField("Development", "http://www.jboss.org/overlord/deployment-status.owl#Dev");
            g.writeStringField("QA", "http://www.jboss.org/overlord/deployment-status.owl#Qa");
            g.writeStringField("Production", "http://www.jboss.org/overlord/deployment-status.owl#Prod");
        } else {
            for (DeploymentStage deploymentStage : stages) {
                g.writeStringField(deploymentStage.getLabel(), deploymentStage.getClassifier());
            }
        }
        g.writeEndObject();

        g.writeEndObject();
        g.flush();
        g.close();

        return json.toString();
	}

    /**
     * Make sure to tell the browser not to cache it.
     *
     * @param response
     */
    private void noCache(HttpServletResponse response) {
        Date now = new Date();
        response.setDateHeader("Date", now.getTime());
        // one day old
        response.setDateHeader("Expires", now.getTime() - 86400000L);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
    }
}
