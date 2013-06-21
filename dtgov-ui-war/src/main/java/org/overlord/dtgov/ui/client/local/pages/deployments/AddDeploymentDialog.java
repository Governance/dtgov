/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.dtgov.ui.client.local.pages.deployments;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.beans.UiConfiguration;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.ModalDialog;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedFormPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;

/**
 * A modal dialog used to allow the user to add (upload) a new deployment file.  A deployment
 * file is typically a binary artifact like a WAR or EAR.
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deployment_dialogs.html#add-deployment-dialog")
@Dependent
public class AddDeploymentDialog extends ModalDialog {

    @Inject
    private ConfigurationService configService;

    @Inject @DataField("add-deployment-dialog-form")
    private TemplatedFormPanel form;
    @Inject
    private DeploymentTypeListBox deploymentType;
    @Inject @DataField("add-deployment-dialog-submit-button")
    private Button submitButton;
    @Inject
    private Instance<AddDeploymentFormSubmitHandler> formHandlerFactory;

    /**
     * Constructor.
     */
    public AddDeploymentDialog() {
    }

    /**
     * Post construct.
     */
    @PostConstruct
    protected void onPostConstruct() {
        AddDeploymentFormSubmitHandler formHandler = formHandlerFactory.get();
        formHandler.setDialog(this);
        form.addSubmitHandler(formHandler);
        form.addSubmitCompleteHandler(formHandler);
    }

    /**
     * @see org.overlord.sramp.ui.client.local.widgets.bootstrap.ModalDialog#show()
     */
    @Override
    public void show() {
        form.setAction(GWT.getModuleBaseURL() + "services/deploymentUpload");
        UiConfiguration uiConfig = configService.getUiConfig();

        // Update the items in the deployment type drop-down
        this.deploymentType.clear();
        Map<String, String> deploymentTypes = uiConfig.getDeploymentTypes();
        for (Entry<String, String> entry : deploymentTypes.entrySet()) {
            this.deploymentType.addItem(entry.getKey(), entry.getValue());
        }

        super.show();
    }

    /**
     * Called when the user clicks the 'submit' (Add) button.
     * @param event
     */
    @EventHandler("add-deployment-dialog-submit-button")
    public void onSubmitClick(ClickEvent event) {
        form.submit();
    }

}
