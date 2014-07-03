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
package org.overlord.dtgov.ui.client.local.pages.targets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.TargetsPage;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.TargetsRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.ModalDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Dialog Modal widget that allows to delete a Target and refresh the
 * TargetsPage
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/dialogs/delete-target-dialog.html#delete-target-dialog")
@Dependent
public class DeleteTargetDialog extends ModalDialog {
    @Inject
    @DataField("delete-target-submit-button")
    private Button _submitButton;

    @Inject
    private TargetsRpcService _targetService;

    /** The _notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _workflow query. */
    private TargetSummaryBean _target;

    /** The _notification. */
    private NotificationBean _notification;

    /** The _projectname. */
    @Inject
    @DataField("form-target-name-input")
    private InlineLabel _targetName;

    /**
     * Instantiates a new delete target dialog.
     */
    public DeleteTargetDialog() {
    }

    /**
     * On post construct.
     */
    @PostConstruct
    protected void onPostConstruct() {
        if (_target != null) {
            _targetName.setText(_target.getName());
        }

    }

    /* (non-Javadoc)
     * @see org.overlord.sramp.ui.client.local.widgets.bootstrap.ModalDialog#show()
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
    @EventHandler("delete-target-submit-button")
    public void onSubmitClick(ClickEvent event) {
        this.hide(false);

        _notification = _notificationService.startProgressNotification(_i18n.format("delete-target-submit.deleting"), //$NON-NLS-1$
                _i18n.format("delete-target-submit.deleting-msg")); //$NON-NLS-1$

        _targetService.delete(_target.getUuid(), new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                destroy();
                _notificationService.completeProgressNotification(_notification.getUuid(), _i18n.format("delete-target-submit.successfully-deleted"), //$NON-NLS-1$
                        _i18n.format("delete-target-submit.successfully-deleted-msg", _target.getName())); //$NON-NLS-1$
                DeleteTargetEvent event = new DeleteTargetEvent();
                TargetsPage._eventBus.fireEvent(event);
            }

            @Override
            public void onError(Throwable error) {
                _notificationService.sendErrorNotification(_i18n.format("delete-target-submit.error", _target.getName()), error); //$NON-NLS-1$

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
     * Gets the target service.
     *
     * @return the target service
     */
    public TargetsRpcService getTargetService() {
        return _targetService;
    }

    /**
     * Sets the target service.
     *
     * @param targetService
     *            the new target service
     */
    public void setTargetService(TargetsRpcService targetService) {
        this._targetService = targetService;
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
     * Gets the target.
     *
     * @return the target
     */
    public TargetSummaryBean getTarget() {
        return _target;
    }

    /**
     * Sets the target.
     *
     * @param target
     *            the new target
     */
    public void setTarget(TargetSummaryBean target) {
        this._target = target;
        if (target != null) {
            this._targetName.setText(this._target.getName());
        }

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
     * Gets the target name.
     *
     * @return the target name
     */
    public InlineLabel getTargetName() {
        return _targetName;
    }

    /**
     * Sets the target name.
     *
     * @param targetName
     *            the new target name
     */
    public void setTargetName(InlineLabel targetName) {
        this._targetName = targetName;
    }


}
