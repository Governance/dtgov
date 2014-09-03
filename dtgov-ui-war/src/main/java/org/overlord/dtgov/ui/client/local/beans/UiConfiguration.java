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
package org.overlord.dtgov.ui.client.local.beans;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.errai.ui.nav.client.local.HistoryToken;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.GWT;

/**
 * Bean that reads and holds UI configuration. This bean reads its information
 * from a global JavaScript variable
 *
 * @author eric.wittmann@redhat.com
 */
public class UiConfiguration {

    private String srampUiUrlBase;
    private String currentUser;
    private boolean admin;
    private final Map<String, String> deploymentStages = new LinkedHashMap<String, String>();
    private final Map<String, String> deploymentTypes = new TreeMap<String, String>();
    private final Map<String, String> workflowPropertyKeyTypes = new LinkedHashMap<String, String>();
    private final Map<String, String> targetKeyTypes = new LinkedHashMap<String, String>();
    private final Map<String, String> customDeployerTypes = new LinkedHashMap<String, String>();

    /**
     * Constructor.
     */
    public UiConfiguration() {
        read();
    }

    /**
     * Adds a single deployment stage to the map.
     *
     * @param label
     *            the label
     * @param classifier
     *            the classifier
     */
    private void addDeploymentStage(String label, String classifier) {
        this.getDeploymentStages().put(label, classifier);
        GWT.log("[UiConfig] - Registered Deployment Stage: " + label + "=" + classifier); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Adds a single deployment type to the map.
     *
     * @param label
     *            the label
     * @param type
     *            the type
     */
    private void addDeploymentType(String label, String type) {
        this.getDeploymentTypes().put(label, type);
        GWT.log("[UiConfig] - Registered Deployment Type: " + label + "=" + type); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Adds a single deployment stage to the map.
     *
     * @param label
     *            the label
     * @param example
     *            the example
     */
    private void addWorkflowPropertyKeyType(String label, String example) {
        this.getWorkflowPropertyKeyTypes().put(label, example);
        GWT.log("[UiConfig] - Registered Working Type: " + label + " example: " + example); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Adds the target key type.
     *
     * @param label
     *            the label
     * @param example
     *            the example
     */
    private void addTargetKeyType(String label, String example) {
        this.getTargetKeyTypes().put(label, example);
        GWT.log("[UiConfig] - Registered Working Type: " + label + " example: " + example); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Adds the target key type.
     * 
     * @param label
     *            the label
     * @param example
     *            the example
     */
    private void addCustomDeployerType(String label, String example) {
        this.getCustomDeployerTypes().put(label, example);
        GWT.log("[UiConfig] - Registered Custom Deployer Type: " + label + " example: " + example); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Creates a link into the s-ramp UI.
     *
     * @param pageName
     *            the page name
     * @param state
     *            the state
     * @return the string
     */
    public String createSrampUiUrl(String pageName, Multimap<String, String> state) {
        HistoryToken token = HistoryToken.of(pageName, state);
        String href = srampUiUrlBase + "#" + token.toString(); //$NON-NLS-1$
        return href;
    }

    /**
     * Creates a link into the s-ramp UI.
     *
     * @param pageName
     *            the page name
     * @param stateKey
     *            the state key
     * @param stateValue
     *            the state value
     * @return the string
     */
    public String createSrampUiUrl(String pageName, String stateKey, String stateValue) {
        Multimap<String, String> state = HashMultimap.create();
        state.put(stateKey, stateValue);
        return createSrampUiUrl(pageName, state);
    }

    /**
     * Gets the deployment stages.
     *
     * @return the deploymentStages
     */
    public Map<String, String> getDeploymentStages() {
        return deploymentStages;
    }

    /**
     * Gets the deployment types.
     *
     * @return the deploymentTypes
     */
    public Map<String, String> getDeploymentTypes() {
        return deploymentTypes;
    }

    /**
     * Gets the workflow property key types.
     *
     * @return the deploymentStages
     */
    public Map<String, String> getWorkflowPropertyKeyTypes() {
        return workflowPropertyKeyTypes;
    }


    /**
     * Gets the target key types.
     *
     * @return the deploymentStages
     */
    public Map<String, String> getTargetKeyTypes() {
        return targetKeyTypes;
    }

    public Map<String, String> getCustomDeployerTypes() {
        return customDeployerTypes;
    }

    /**
     * Read the configuration information from the OVERLORD_DTGOVUI_CONFIG
     * javascript variable.
     */
    private final native void read() /*-{
		var dis = this;
		try {
			var deploymentConfig = $wnd.OVERLORD_DTGOVUI_CONFIG.deployments;
			// Read the deployment types
			var dTypes = deploymentConfig.types;
			for ( var k in dTypes) {
				if (dTypes.hasOwnProperty(k)) {
					var label = k;
					var type = dTypes[k];
					dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::addDeploymentType(Ljava/lang/String;Ljava/lang/String;)(label, type);
				}
			}

			// Read the deployment stages
			var dStages = deploymentConfig.stages;
			for ( var k in dStages) {
				if (dStages.hasOwnProperty(k)) {
					var label = k;
					var classifier = dStages[k];
					dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::addDeploymentStage(Ljava/lang/String;Ljava/lang/String;)(label, classifier);
				}
			}

			var workflowConfig = $wnd.OVERLORD_DTGOVUI_CONFIG.workflow;

			// Read the deployment stages
			var propertyTypes = workflowConfig.propertyTypes;
			for ( var k in propertyTypes) {
				if (propertyTypes.hasOwnProperty(k)) {
					var label = k;
					var example = propertyTypes[k];
					dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::addWorkflowPropertyKeyType(Ljava/lang/String;Ljava/lang/String;)(label,example);
				}
			}

			var targetConfig = $wnd.OVERLORD_DTGOVUI_CONFIG.target;

			// Read the targets
			var targetTypes = targetConfig.types;
			for ( var k in targetTypes) {
				if (targetTypes.hasOwnProperty(k)) {
					var label = k;
					var example = targetTypes[k];
					dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::addTargetKeyType(Ljava/lang/String;Ljava/lang/String;)(label,example);
				}
			}

			// Read the targets
			var customDeployerTypes = $wnd.OVERLORD_DTGOVUI_CONFIG.customDeployers;
			for ( var k in customDeployerTypes) {
				if (customDeployerTypes.hasOwnProperty(k)) {
					var label = k;
					var example = customDeployerTypes[k];
					dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::addCustomDeployerType(Ljava/lang/String;Ljava/lang/String;)(label,example);
				}
			}

			// Read the s-ramp UI config
			var srampUiConfig = $wnd.OVERLORD_DTGOVUI_CONFIG.srampui;
			var urlBase = srampUiConfig.urlBase;

			// Read the auth config
			var authConfig = $wnd.OVERLORD_DTGOVUI_CONFIG.auth;
			var currentUser = authConfig.currentUser;
			var isAdmin = authConfig.isAdmin;
			dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::setSrampUiUrlBase(Ljava/lang/String;)(urlBase);
			dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::setCurrentUser(Ljava/lang/String;)(currentUser);
			dis.@org.overlord.dtgov.ui.client.local.beans.UiConfiguration::setAdmin(Ljava/lang/String;)(''+isAdmin);
		} catch (e) {
			// TODO do something interesting here?
		}
    }-*/;

    /**
     * Sets the s-ramp-ui URL base.
     *
     * @param urlBase
     *            the new sramp ui url base
     */
    private void setSrampUiUrlBase(String urlBase) {
        this.srampUiUrlBase = urlBase;
    }

    /**
     * Gets the sramp ui url base.
     *
     * @return the sramp ui url base
     */
    public String getSrampUiUrlBase() {
        return srampUiUrlBase;
     }

   /**
     * @return the currentUser
     */
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(String admin) {
        this.admin = Boolean.valueOf(admin);
    }

}
