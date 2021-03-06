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
package org.overlord.dtgov.ui.client.local.pages.targets.panel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.shared.beans.FabricTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.user.client.ui.TextBox;

/**
 * RHQ Target Component that includes the injections of the rqh target form
 * fields.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets/fabric_target.html#target_content")
@Dependent
public class FabricTargetPanel extends AbstractTargetPanel {

    @Inject
    @DataField("form-target-user-input")
    private TextBox _user;

    @Inject
    @DataField("form-target-password-input")
    private TextBox _password;

    @Inject
    @DataField("form-target-url-input")
    private TextBox _jolokiaURL;


    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#getTargetBean()
     */
    @Override
    public TargetBean getTargetBean() {
        FabricTargetBean bean = new FabricTargetBean();
        bean.setJolokiaURL(_jolokiaURL.getValue());
        bean.setUser(_user.getValue());
        bean.setPassword(_password.getValue());
        return bean;
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        FabricTargetBean rhq = (FabricTargetBean) bean;
        _user.setValue(rhq.getUser());
        _password.setValue(rhq.getPassword());
        _jolokiaURL.setValue(rhq.getJolokiaURL());
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public TextBox getUser() {
        return _user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(TextBox user) {
        this._user = user;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public TextBox getPassword() {
        return _password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(TextBox password) {
        this._password = password;
    }


    /**
     * Gets the base url.
     *
     * @return the base url
     */
    public TextBox getJolokiaURL() {
        return _jolokiaURL;
    }

    /**
     * Sets the base url.
     *
     * @param baseURL
     *            the new base url
     */
    public void setJolokiaURL(TextBox baseURL) {
        this._jolokiaURL = baseURL;
    }



}
