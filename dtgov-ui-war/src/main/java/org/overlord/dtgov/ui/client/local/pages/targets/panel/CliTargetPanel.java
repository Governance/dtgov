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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.shared.beans.CliTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Client Target Component that includes the injections of the cli target form
 * fields.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets/cli_target.html#target_content")
@Dependent
public class CliTargetPanel extends AbstractTargetPanel {

    @Inject
    @DataField("form-target-user-input")
    private TextBox _user;

    @Inject
    @DataField("form-target-password-input")
    private TextBox _password;

    @Inject
    @DataField("form-target-host-input")
    private TextBox _host;

    @Inject
    @DataField("form-target-port-input")
    private IntegerBox _port;

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#getTargetBean()
     */
    @Override
    public TargetBean getTargetBean() {
        CliTargetBean cli = new CliTargetBean();
        cli.setUser(_user.getValue());
        cli.setPassword(_password.getValue());
        cli.setHost(_host.getValue());
        if (_port.getValue() != null && !_port.getValue().equals("")) { //$NON-NLS-1$
            cli.setPort(_port.getValue());
        }

        return cli;
    }

    /**
     * Post construct.
     */
    @PostConstruct
    public void postConstruct() {
        _port.addKeyPressHandler(new NumbersOnly());

    }

    class NumbersOnly implements KeyPressHandler {

        /* (non-Javadoc)
         * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
         */
        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (!Character.isDigit(event.getCharCode()))
                ((IntegerBox) event.getSource()).cancelKey();
        }
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        CliTargetBean cli = (CliTargetBean) bean;
        _user.setValue(cli.getUser());
        _password.setValue(cli.getPassword());
        _host.setValue(cli.getHost());
        _port.setValue(cli.getPort());

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
     * Gets the host.
     *
     * @return the host
     */
    public TextBox getHost() {
        return _host;
    }

    /**
     * Sets the host.
     *
     * @param host
     *            the new host
     */
    public void setHost(TextBox host) {
        this._host = host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public IntegerBox getPort() {
        return _port;
    }

    /**
     * Sets the port.
     *
     * @param port
     *            the new port
     */
    public void setPort(IntegerBox port) {
        this._port = port;
    }

}
