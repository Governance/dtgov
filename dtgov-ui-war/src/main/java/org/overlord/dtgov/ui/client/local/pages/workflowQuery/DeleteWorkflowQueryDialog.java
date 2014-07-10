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
package org.overlord.dtgov.ui.client.local.pages.workflowQuery;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.ModalDialog;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.WorkflowQueriesPage;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.WorkflowQueriesRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQuerySummaryBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * 
 * GWT modal form box that allow to delete from s-ramp a workflow query.
 * 
 * @author David Virgil Naranjo
 * 
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/dialogs/delete-workflow-query-dialog.html#delete-workflow-query-dialog")
@Dependent
public class DeleteWorkflowQueryDialog extends ModalDialog {

    /** The _submit button. */
    @Inject
    @DataField("delete-workflow-submit-button")
    private Button _submitButton;

    /** The _workflow query service. */
    @Inject
    private WorkflowQueriesRpcService _workflowQueryService;

    /** The _notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _workflow query. */
    private WorkflowQuerySummaryBean _workflowQuery;

    /** The _notification. */
    private NotificationBean _notification;

    /** The _projectname. */
    @Inject
    @DataField("form-workflow-query-name-input")
    private InlineLabel _projectname;

    /*
     * @Inject EventBus eventBus;
     */
    /**
     * Constructor.
     */
    public DeleteWorkflowQueryDialog() {
    }

    /**
     * Sets the workflow query.
     * 
     * @param workflowQuery
     *            the new workflow query
     */
    public void setWorkflowQuery(WorkflowQuerySummaryBean workflowQuery) {
        this._workflowQuery = workflowQuery;
        _projectname.setText(workflowQuery.getName());
    }

    /**
     * Post construct.
     */
    @PostConstruct
    protected void onPostConstruct() {
        if (_workflowQuery != null) {
            _projectname.setText(_workflowQuery.getName());
        }

    }

    /**
     * Show.
     * 
     * @see org.overlord.sramp.ui.client.local.widgets.bootstrap.ModalDialog#show()
     */
    @Override
    public void show() {
        super.show();
    }

    /**
     * Called when the user clicks the 'submit' (Add) button.
     * 
     * @param event
     *            the event
     */
    @EventHandler("delete-workflow-submit-button")
    public void onSubmitClick(ClickEvent event) {
        this.hide(false);

        _notification = _notificationService.startProgressNotification(
                _i18n.format("delete-workflow-query-submit.deleting-query"), //$NON-NLS-1$
                _i18n.format("delete-workflow-query-submit.deleting-query-msg")); //$NON-NLS-1$

        _workflowQueryService.delete(_workflowQuery.getUuid(), new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                destroy();
                _notificationService.completeProgressNotification(_notification.getUuid(),
                        _i18n.format("delete-workflow-query-submit.successfully-deleted"), //$NON-NLS-1$
                        _i18n.format(
                                "delete-workflow-query-submit.successfully-deleted-msg", _workflowQuery.getName())); //$NON-NLS-1$
                DeleteWorkflowQueryEvent event = new DeleteWorkflowQueryEvent();
                WorkflowQueriesPage._eventBus.fireEvent(event);
            }

            @Override
            public void onError(Throwable error) {
                _notificationService.sendErrorNotification(
                        _i18n.format("delete-workflow-query-submit.error", _workflowQuery.getName()), error); //$NON-NLS-1$

            }
        });
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
     * Gets the workflow query service.
     * 
     * @return the workflow query service
     */
    public WorkflowQueriesRpcService getWorkflowQueryService() {
        return _workflowQueryService;
    }

    /**
     * Sets the workflow query service.
     * 
     * @param workflowQueryService
     *            the new workflow query service
     */
    public void setWorkflowQueryService(WorkflowQueriesRpcService workflowQueryService) {
        this._workflowQueryService = workflowQueryService;
    }

    /**
     * Gets the notification service.
     * 
     * @return the notification service
     */
    public NotificationService getNotificationService() {
        return _notificationService;
    }

    /**
     * Sets the notification service.
     * 
     * @param notificationService
     *            the new notification service
     */
    public void setNotificationService(NotificationService notificationService) {
        this._notificationService = notificationService;
    }

    /**
     * Gets the i18n.
     * 
     * @return the i18n
     */
    public ClientMessages getI18n() {
        return _i18n;
    }

    /**
     * Sets the i18n.
     * 
     * @param i18n
     *            the new i18n
     */
    public void setI18n(ClientMessages i18n) {
        this._i18n = i18n;
    }

    /**
     * Gets the notification.
     * 
     * @return the notification
     */
    public NotificationBean getNotification() {
        return _notification;
    }

    /**
     * Sets the notification.
     * 
     * @param notification
     *            the new notification
     */
    public void setNotification(NotificationBean notification) {
        this._notification = notification;
    }

    /**
     * Gets the projectname.
     * 
     * @return the projectname
     */
    public InlineLabel getProjectname() {
        return _projectname;
    }

    /**
     * Sets the projectname.
     * 
     * @param projectname
     *            the new projectname
     */
    public void setProjectname(InlineLabel projectname) {
        this._projectname = projectname;
    }

    /**
     * Gets the workflow query.
     * 
     * @return the workflow query
     */
    public WorkflowQuerySummaryBean getWorkflowQuery() {
        return _workflowQuery;
    }

}
