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
import org.overlord.dtgov.ui.server.i18n.Messages;

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
        response.setContentType("text/javascript"); //$NON-NLS-1$

        try {
            String json = generateJSONConfig(req, config);
            response.getOutputStream().write("var OVERLORD_DTGOVUI_CONFIG = ".getBytes("UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
            response.getOutputStream().write(json.getBytes("UTF-8")); //$NON-NLS-1$
            response.getOutputStream().write(";".getBytes("UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception e) {
            throw new ServletException(e);
        }
	}

	/**
	 * Called to generate the JSON.
	 * @param request
	 * @param config
	 * @throws Exception
	 */
	protected static String generateJSONConfig(HttpServletRequest request, DtgovUIConfig config) throws Exception {
        StringWriter json = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(json);
        g.useDefaultPrettyPrinter();
        g.writeStartObject();

        // Some s-ramp UI/browser integration settings
        g.writeObjectFieldStart("srampui"); //$NON-NLS-1$
        g.writeStringField("urlBase", config.getConfiguration().getString(DtgovUIConfig.SRAMP_UI_URL_BASE, autoGenerateSrampUiUrlBase(request))); //$NON-NLS-1$
        g.writeEndObject();

        g.writeObjectFieldStart("deployments"); //$NON-NLS-1$
        // Pull in any configured deployment types.
        @SuppressWarnings("unchecked")
        Iterator<String> typeKeys = config.getConfiguration().getKeys(DtgovUIConfig.DEPLOYMENT_TYPE_PREFIX);
        int count = 0;
        g.writeObjectFieldStart("types"); //$NON-NLS-1$
        while (typeKeys.hasNext()) {
            String typeKey = typeKeys.next();
            String value = config.getConfiguration().getString(typeKey);
            if (value.contains(":")) { //$NON-NLS-1$
                int idx = value.indexOf(':');
                String label = value.substring(0, idx);
                String type = value.substring(idx+1);
                g.writeStringField(label, type);
                count++;
            }
        }
        if (count == 0) {
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.TypeSwitchYard"), "ext/SwitchYardApplication"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.TypeWebApp"), "ext/JavaWebApplication"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.TypeJ2EEApp"), "ext/JavaEnterpriseApplication"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        g.writeEndObject();

        // Pull in any configured deployment stages.
        List<DeploymentStage> stages = config.getStages();
        g.writeObjectFieldStart("stages"); //$NON-NLS-1$
        if (stages.isEmpty()) {
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.StageDevelopment"), "http://www.jboss.org/overlord/deployment-status.owl#Dev"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.StageQA"), "http://www.jboss.org/overlord/deployment-status.owl#Qa"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.StageProd"), "http://www.jboss.org/overlord/deployment-status.owl#Prod"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            for (DeploymentStage deploymentStage : stages) {
                g.writeStringField(deploymentStage.getLabel(), deploymentStage.getClassifier());
            }
        }
        g.writeEndObject();
        g.writeEndObject();

        
        g.writeObjectFieldStart("workflow"); //$NON-NLS-1$
     // Pull in any configured workflow types.
        @SuppressWarnings("unchecked")
        Iterator<String> workflowTypes = config.getConfiguration().getKeys(DtgovUIConfig.WORKFLOW_TYPE_PREFIX);
        count = 0;
        g.writeObjectFieldStart("types"); //$NON-NLS-1$
        while (workflowTypes.hasNext()) {
            String workflowTypeKey = workflowTypes.next();
            String value = config.getConfiguration().getString(workflowTypeKey);
            if (value.contains(":")) { //$NON-NLS-1$
                int idx = value.indexOf(':');
                String label = value.substring(0, idx);
                String type = value.substring(idx+1);
                g.writeStringField(label, type);
                count++;
            }
        }
        if (count == 0) {
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.workflow.SimpleReleaseProcess"), "overlord.demo.SimpleReleaseProcess"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.workflow.SimplifiedProjectLifeCycle"), "overlord.demo.SimplifiedProjectLifeCycle"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        g.writeEndObject();
        
        
        // Pull in any configured workflow property default types.
        @SuppressWarnings("unchecked")
        Iterator<String> workflowPropertyTypes = config.getConfiguration().getKeys(DtgovUIConfig.WORKFLOW_PROPERTY_PREFIX);
        count = 0;
        g.writeObjectFieldStart("propertyTypes"); //$NON-NLS-1$
        while (workflowPropertyTypes.hasNext()) {
            String workflowPropertyTypeKey = workflowTypes.next();
            String value = config.getConfiguration().getString(workflowPropertyTypeKey);
            if (value.contains(":")) { //$NON-NLS-1$
                int idx = value.indexOf(':');
                String label = value.substring(0, idx);
                String type = value.substring(idx+1);
                g.writeStringField(label, type);
                count++;
            }
        }
        if (count == 0) {
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.workflow.property.deploymentUrl"), "{governance.url}/rest/deploy/{target}/{uuid}"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.workflow.property.notificationUrl"), "{governance.url}/rest/notify/email/{group}/deployed/{target}/{uuid}"); //$NON-NLS-1$ //$NON-NLS-2$
            g.writeStringField(Messages.i18n.format("UiConfigurationServlet.workflow.property.updateMetadataUrl"), "{governance.url}/rest/update/{name}/{value}/{uuid}"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        g.writeEndObject();
        g.writeEndObject();
        g.flush();
        g.close();

        return json.toString();
	}

    /**
     * @param request
     */
    private static String autoGenerateSrampUiUrlBase(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        String port = String.valueOf(request.getServerPort());
        return String.format("%1$s://%2$s:%3$s/s-ramp-ui", scheme, host, port); //$NON-NLS-1$
    }

    /**
     * Make sure to tell the browser not to cache it.
     *
     * @param response
     */
    private void noCache(HttpServletResponse response) {
        Date now = new Date();
        response.setDateHeader("Date", now.getTime()); //$NON-NLS-1$
        // one day old
        response.setDateHeader("Expires", now.getTime() - 86400000L); //$NON-NLS-1$
        response.setHeader("Pragma", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
