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
import com.google.gwt.user.client.ui.CheckBox;
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

    @Inject
    @DataField("form-target-domain-mode-input")
    private CheckBox domainMode;

    @Inject
    @DataField("form-target-server-group-input")
    private TextBox serverGroup;

    /**
     * Constructor.
     */
    public CliTargetPanel() {
    }

    /**
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
        cli.setDomainMode(domainMode.getValue());
        cli.setServerGroup(serverGroup.getValue());
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
        /**
         * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
         */
        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (Character.isLetter(event.getCharCode()))
                ((IntegerBox) event.getSource()).cancelKey();
        }

    }

    /**
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        CliTargetBean cli = (CliTargetBean) bean;
        _user.setValue(cli.getUser());
        _password.setValue(cli.getPassword());
        _host.setValue(cli.getHost());
        _port.setValue(cli.getPort());
        domainMode.setValue(cli.getDomainMode());
        serverGroup.setValue(cli.getServerGroup());
    }

}
