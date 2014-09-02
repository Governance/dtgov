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
import org.overlord.dtgov.ui.client.shared.beans.RHQTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.user.client.ui.TextBox;

/**
 * RHQ Target Component that includes the injections of the rqh target form
 * fields.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets/rhq_target.html#target_content")
@Dependent
public class RhqTargetPanel extends AbstractTargetPanel {

    @Inject
    @DataField("form-target-user-input")
    private TextBox _user;

    @Inject
    @DataField("form-target-password-input")
    private TextBox _password;

    @Inject
    @DataField("form-target-plugin-name-input")
    private TextBox _pluginName;

    @Inject
    @DataField("form-target-url-input")
    private TextBox _baseURL;

    @Inject
    @DataField("form-target-group-input")
    private TextBox _group;

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#getTargetBean()
     */
    @Override
    public TargetBean getTargetBean() {
        RHQTargetBean bean = new RHQTargetBean();
        bean.setBaseUrl(_baseURL.getValue());
        bean.setRhqPlugin(_pluginName.getValue());
        bean.setUser(_user.getValue());
        bean.setPassword(_password.getValue());
        bean.setRhqGroup(_group.getValue());
        return bean;
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        RHQTargetBean rhq = (RHQTargetBean) bean;
        _user.setValue(rhq.getUser());
        _password.setValue(rhq.getPassword());
        _baseURL.setValue(rhq.getBaseUrl());
        _pluginName.setValue(rhq.getRhqPlugin());
        _group.setValue(rhq.getRhqGroup());
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
     * Gets the plugin name.
     *
     * @return the plugin name
     */
    public TextBox getPluginName() {
        return _pluginName;
    }

    /**
     * Sets the plugin name.
     *
     * @param pluginName
     *            the new plugin name
     */
    public void setPluginName(TextBox pluginName) {
        this._pluginName = pluginName;
    }

    /**
     * Gets the base url.
     *
     * @return the base url
     */
    public TextBox getBaseURL() {
        return _baseURL;
    }

    /**
     * Sets the base url.
     *
     * @param baseURL
     *            the new base url
     */
    public void setBaseURL(TextBox baseURL) {
        this._baseURL = baseURL;
    }

    public TextBox getGroup() {
        return _group;
    }

    public void setGroup(TextBox group) {
        this._group = group;
    }

}
