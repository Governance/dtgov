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
 * It contains the rqh target fields. It is a subtype of the TargetBean.
 *
 * @author David Virgil Naranjo
 *
 */
@Portable
public class RHQTargetBean extends TargetBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6218082576119574314L;

    private String _user;

    private String _password;

    private String _baseUrl;

    private String _rhqPlugin;

    private String rhqGroup;

    /**
     * Instantiates a new RHQ target bean.
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
     * @param baseUrl
     *            the base url
     * @param rhqPlugin
     *            the rhq plugin
     * @param rhqGroup
     *            the rhq group
     */
    public RHQTargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name, String user, String password,
            String baseUrl,
 String rhqPlugin, String rhqGroup) {
        super(uuid, classifiers, description, name);
        this._user = user;
        this._password = password;
        this._baseUrl = baseUrl;
        this._rhqPlugin = rhqPlugin;
        this.rhqGroup = rhqGroup;
    }

    /**
     * Instantiates a new RHQ target bean.
     */
    public RHQTargetBean() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#getType()
     */
    @Override
    public TargetType getType() {
        return TargetType.RHQ;
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
     * Gets the base url.
     *
     * @return the base url
     */
    public String getBaseUrl() {
        return _baseUrl;
    }

    /**
     * Sets the base url.
     *
     * @param baseUrl
     *            the new base url
     */
    public void setBaseUrl(String baseUrl) {
        this._baseUrl = baseUrl;
    }

    /**
     * Gets the rhq plugin.
     *
     * @return the rhq plugin
     */
    public String getRhqPlugin() {
        return _rhqPlugin;
    }

    /**
     * Sets the rhq plugin.
     *
     * @param rhqPlugin
     *            the new rhq plugin
     */
    public void setRhqPlugin(String rhqPlugin) {
        this._rhqPlugin = rhqPlugin;
    }

    /**
     * Gets the rhq group.
     *
     * @return the rhq group
     */
    public String getRhqGroup() {
        return rhqGroup;
    }

    /**
     * Sets the rhq group.
     *
     * @param rhqGroup
     *            the new rhq group
     */
    public void setRhqGroup(String rhqGroup) {
        this.rhqGroup = rhqGroup;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#toString()
     */
    @Override
    public String toString() {
        return "RHQTargetBean [" + super.toString() + ", user=" + _user + ", password=" + _password + ", baseUrl=" + _baseUrl + ", rhqPlugin=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + _rhqPlugin + "]"; //$NON-NLS-1$
    }



}
