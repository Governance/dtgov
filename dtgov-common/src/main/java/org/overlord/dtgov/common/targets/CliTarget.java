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
 * Cli Target Implementation
 * 
 * @author David Virgil Naranjo
 */
public class CliTarget extends Target implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -4323018470513316694L;

    private String user;
    private String password;
    private boolean domainMode;
    private String serverGroup;
    private String host;
    private Integer port;



    /**
     * Instantiates a new cli target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param user
     *            the user
     * @param password
     *            the password
     * @param domainMode
     *            the domain mode
     * @param serverGroup
     *            the server group
     * @param host
     *            the host
     * @param port
     *            the port
     */
    public CliTarget(String name, String classifier, String user, String password, boolean domainMode, String serverGroup, String host, Integer port) {
        super(name, classifier, TYPE.AS_CLI);
        this.user = user;
        this.password = password;
        this.domainMode = domainMode;
        this.serverGroup = serverGroup;
        this.host = host;
        this.port = port;
    }

    /**
     * Instantiates a new cli target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     */
    public CliTarget(String name, String classifier) {
        super(name, classifier, TYPE.AS_CLI);
    }

    /**
     * Constructs a Target of Type AS_CLI 'Application Server Command Line
     * Interface.
     *
     * @param name
     *            - name of the target
     * @param classifier
     *            the classifier
     * @param asUser
     *            - AS user with admin rights
     * @param asPassword
     *            - password of the asUser
     * @param asHost
     *            - Application Server Hostname (defaults to localhost)
     * @param asPort
     *            - Application Server Port (defaults to 9999)
     * @param asDomainMode
     *            - whether the server is in domain mode or standalone
     * @param asServerGroup
     *            - the domain mode server group (optional)
     * @return the target
     */
    public static final Target getTarget(String name, String classifier, String asUser, String asPassword, String asHost, Integer asPort,
            Boolean asDomainMode, String asServerGroup) {
        CliTarget target = new CliTarget(name, classifier);
        target.setUser(asUser);
        target.setPassword(asPassword);
        if (asDomainMode == null) {
            target.setDomainMode(false);
        } else {
            target.setDomainMode(asDomainMode);
        }
        target.setServerGroup(asServerGroup);
        if (asHost != null) {
            target.setHost(asHost);
        } else {
            target.setHost("localhost"); //$NON-NLS-1$
        }
        if (asPort != null && asPort > 0) {
            target.setPort(asPort);
        } else {
            target.setPort(9999);
        }
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
     * Checks if is domain mode.
     *
     * @return true, if is domain mode
     */
    public boolean isDomainMode() {
        return domainMode;
    }

    /**
     * Sets the domain mode.
     *
     * @param domainMode
     *            the new domain mode
     */
    public void setDomainMode(boolean domainMode) {
        this.domainMode = domainMode;
    }

    /**
     * Gets the server group.
     *
     * @return the server group
     */
    public String getServerGroup() {
        return serverGroup;
    }

    /**
     * Sets the server group.
     *
     * @param serverGroup
     *            the new server group
     */
    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }


    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }


    /**
     * Sets the host.
     *
     * @param host
     *            the new host
     */
    public void setHost(String host) {
        this.host = host;
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
