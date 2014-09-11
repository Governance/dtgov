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
package org.overlord.dtgov.common.targets;

import java.io.Serializable;

import org.overlord.dtgov.common.Target;

/**
 * RHQ Target Implementation
 * 
 * @author David Virgil Naranjo
 */
public class RHQTarget extends Target implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4387955655881622176L;

    private String user;
    private String password;

    private String baseUrl;
    private String pluginName;
    private String group;
    private Integer port;

    /**
     * Instantiates a new RHQ target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param user
     *            the user
     * @param password
     *            the password
     * @param baseUrl
     *            the base url
     * @param pluginName
     *            the plugin name
     * @param group
     *            the group
     */
    public RHQTarget(String name, String classifier, String user, String password, String baseUrl, String pluginName, String group) {
        super(name, classifier, TYPE.RHQ);
        this.user = user;
        this.password = password;
        this.baseUrl = baseUrl;
        this.pluginName = pluginName;
        this.group = group;
    }

    /**
     * Instantiates a new RHQ target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     */
    public RHQTarget(String name, String classifier) {
        super(name, classifier, TYPE.RHQ);
    }

    /**
     * Constructs a Target of Type RHQ to use it (JON) to deploy archives to a
     * RHQ server group. The RHQ Server group needs to be prefined and needs to
     * contain Application Server resources only.
     *
     * @param name
     *            - name of the target - which needs to correspond to the RHQ
     *            Server Group.
     * @param classifier
     *            the classifier
     * @param rhqUser
     *            - username of the RHQ user with rights to deploy to that
     *            group.
     * @param rhqPassword
     *            - password of the rhqUser.
     * @param rhqBaseUrl
     *            - baseUrl of the RHQ Server i.e. http://localhost:7080/
     * @param rhqPluginName
     *            the rhq plugin name
     * @param rhqGroup
     *            the rhq group
     * @return the target
     */
    public static final Target getTarget(String name, String classifier, String rhqUser, String rhqPassword, String rhqBaseUrl, String rhqPluginName,
            String rhqGroup) {
        RHQTarget target = new RHQTarget(name, classifier);
        target.user = rhqUser;
        target.password = rhqPassword;
        int secondColon = rhqBaseUrl.indexOf(":", rhqBaseUrl.indexOf(":") + 1); //$NON-NLS-1$ //$NON-NLS-2$
        if (secondColon > 0) {
            target.baseUrl = rhqBaseUrl.substring(0, secondColon);
            String portStr = rhqBaseUrl.substring(secondColon + 1);
            int slashPosition = portStr.indexOf("/"); //$NON-NLS-1$
            if (slashPosition > 0) {
                portStr = portStr.substring(0, slashPosition);
            }
            target.port = Integer.valueOf(portStr);
        } else {
            target.baseUrl = rhqBaseUrl;
            target.port = 7080;
        }
        target.pluginName = rhqPluginName;
        target.group = rhqGroup;
        return target;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the base url.
     *
     * @return the base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the base url.
     *
     * @param baseUrl
     *            the new base url
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Gets the plugin name.
     *
     * @return the plugin name
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Sets the plugin name.
     *
     * @param pluginName
     *            the new plugin name
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Gets the group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group
     *            the new group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port.
     *
     * @param port
     *            the new port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

}
