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
public class FabricTargetBean extends TargetBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6218082576119574314L;

    private String _user;

    private String _password;

    private String jbolokiaURL;


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
    public FabricTargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name, String user, String password,
            String jolokiaURL) {
        super(uuid, classifiers, description, name);
        this._user = user;
        this._password = password;
        this.jbolokiaURL = jolokiaURL;

    }

    /**
     * Instantiates a new RHQ target bean.
     */
    public FabricTargetBean() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#getType()
     */
    @Override
    public TargetType getType() {
        return TargetType.FABRIC;
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




    public String getJolokiaURL() {
        return jbolokiaURL;
    }

    public void setJolokiaURL(String jbolokiaURL) {
        this.jbolokiaURL = jbolokiaURL;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#toString()
     */
    @Override
    public String toString() {
        return "RHQTargetBean [" + super.toString() + ", user=" + _user + ", password=" + _password + ", baseUrl=" + jbolokiaURL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "]"; //$NON-NLS-1$
    }



}
