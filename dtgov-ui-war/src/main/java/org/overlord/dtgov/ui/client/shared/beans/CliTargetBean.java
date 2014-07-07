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
package org.overlord.dtgov.ui.client.shared.beans;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * It contains the cli target fields. It is a subtype of the TargetBean.
 *
 * @author David Virgil Naranjo
 *
 */
@Portable
public class CliTargetBean extends TargetBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4526717452935610828L;

    private String _user;

    private String _password;

    private String _host;

    private Integer _port;

    /**
     * Instantiates a new cli target bean.
     *
     * @param uuid
     *            the uuid
     * @param classifiers
     *            the classifiers
     * @param description
     *            the description
     * @param name
     *            the name
     * @param user
     *            the user
     * @param password
     *            the password
     * @param host
     *            the host
     * @param port
     *            the port
     */
    public CliTargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name, String user, String password, String host,
            Integer port) {
        super(uuid, classifiers, description, name);
        this._user = user;
        this._password = password;
        this._host = host;
        this._port = port;
    }

    /**
     * Instantiates a new cli target bean.
     */
    public CliTargetBean() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#getType()
     */
    @Override
    public TargetType getType() {
        return TargetType.CLI;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return _user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(String user) {
        this._user = user;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this._password = password;
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return _host;
    }

    /**
     * Sets the host.
     *
     * @param host
     *            the new host
     */
    public void setHost(String host) {
        this._host = host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public Integer getPort() {
        return _port;
    }

    /**
     * Sets the port.
     *
     * @param port
     *            the new port
     */
    public void setPort(Integer port) {
        this._port = port;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#toString()
     */
    @Override
    public String toString() {
        return "CliTargetBean [" + super.toString() + ", user=" + _user + ", password=" + _password + ", host=" + _host + ", port=" + _port + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    }


}
