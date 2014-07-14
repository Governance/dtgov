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
package org.overlord.dtgov.ui.client.local.pages.processes;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.ModalDialog;
import org.overlord.dtgov.ui.client.local.events.DialogOkCancelEvent;
import org.overlord.dtgov.ui.client.local.events.DialogOkCancelEvent.Handler;
import org.overlord.dtgov.ui.client.local.events.DialogOkCancelEvent.HasDialogOkCancelHandlers;
import org.overlord.dtgov.ui.client.shared.beans.ProcessBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Dialog Modal widget that allows to abort a Target and refresh the TargetsPage
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/dialogs/abort-process-dialog.html#abort-process-dialog")
@Dependent
public class AbortProcessDialog extends ModalDialog implements HasDialogOkCancelHandlers {
    @Inject
    @DataField("abort-process-submit-button")
    private Button _submitButton;

    private ProcessBean process;

    /** The _projectname. */
    @Inject
    @DataField("form-workflow-input")
    private InlineLabel workflow;

    /** The _projectname. */
    @Inject
    @DataField("form-artifact-input")
    private InlineLabel artifactName;

    /**
     * Instantiates a new delete target dialog.
     */
    public AbortProcessDialog() {
    }

    /**
     * On post construct.
     */
    @PostConstruct
    protected void onPostConstruct() {
        if (process != null) {
            artifactName.setText(process.getArtifactName());
            workflow.setText(process.getWorkflow());
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.sramp.ui.client.local.widgets.bootstrap.ModalDialog#show()
     */
    @Override
    public void show() {
        super.show();
    }

    /**
     * On submit click.
     *
     * @param event
     *            the event
     */
    @EventHandler("abort-process-submit-button")
    public void onSubmitClick(ClickEvent event) {
        this.hide(false);
        DialogOkCancelEvent.fire(this, true);
    }

    /**
     * Gets the submit button.
     *
     * @return the submit button
     */
    public Button getSubmitButton() {
        return _submitButton;
    }

    /**
     * Sets the submit button.
     *
     * @param submitButton
     *            the new submit button
     */
    public void setSubmitButton(Button submitButton) {
        this._submitButton = submitButton;
    }

    /**
     * Sets the process.
     *
     * @param process
     *            the new process
     */
    public void setProcess(ProcessBean process) {
        this.process = process;
        if (process != null) {
            this.artifactName.setText(this.process.getArtifactName());
            this.workflow.setText(this.process.getWorkflow());
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.local.events.DialogOkCancelEvent.
     * HasDialogOkCancelHandlers
     * #addDialogOkCancelHandler(org.overlord.dtgov.ui.client
     * .local.events.DialogOkCancelEvent.Handler)
     */
    @Override
    public HandlerRegistration addDialogOkCancelHandler(Handler handler) {
        return super.addHandler(handler, DialogOkCancelEvent.getType());
    }

}
