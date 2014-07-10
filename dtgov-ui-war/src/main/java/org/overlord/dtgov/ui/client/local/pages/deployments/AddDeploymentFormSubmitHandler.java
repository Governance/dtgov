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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.commons.gwt.client.local.widgets.ModalDialog;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.DeploymentDetailsPage;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.server.servlets.DeploymentUploadServlet;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The form submit handler used by the {@link AddDeploymentDialog}.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class AddDeploymentFormSubmitHandler implements SubmitHandler, SubmitCompleteHandler {

    @Inject
    protected ClientMessages i18n;
    @Inject
    private NotificationService notificationService;
    @Inject
    private TransitionAnchorFactory<DeploymentDetailsPage> toDetailsFactory;

    private ModalDialog dialog;
    private NotificationBean notification;

    /**
     * Constructor.
     */
    public AddDeploymentFormSubmitHandler() {
    }

    /**
     * @param importArtifactDialog
     */
    public void setDialog(ModalDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * @see com.google.gwt.user.client.ui.FormPanel.SubmitHandler#onSubmit(com.google.gwt.user.client.ui.FormPanel.SubmitEvent)
     */
    @Override
    public void onSubmit(SubmitEvent event) {
        dialog.hide(false);
        notification = notificationService.startProgressNotification(
                i18n.format("add-deployment-submit.adding-deployment"), //$NON-NLS-1$
                i18n.format("add-deployment-submit.adding-deployment-msg")); //$NON-NLS-1$
    }

    /**
     * @see com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler#onSubmitComplete(com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent)
     */
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
        dialog.destroy();

        AddResult results = AddResult.fromResult(event.getResults());
        if (results.isError()) {
            if (results.getError() != null) {
                notificationService.completeProgressNotification(
                        notification.getUuid(),
                        i18n.format("add-deployment-submit.error-adding-deployment"), //$NON-NLS-1$
                        results.getError());
            } else {
                notificationService.completeProgressNotification(
                        notification.getUuid(),
                        i18n.format("add-deployment-submit.error-adding-deployment"), //$NON-NLS-1$
                        i18n.format("add-deployment-submit.error-adding-deployment-msg")); //$NON-NLS-1$
            }
        } else if (results.isBatch()) {
            String message = i18n.format("add-deployment-submit.upload-complete-msg", results.getBatchNumSuccess(), results.getBatchNumFailed()); //$NON-NLS-1$
            notificationService.completeProgressNotification(
                    notification.getUuid(),
                    i18n.format("add-deployment-submit.upload-complete"), //$NON-NLS-1$
                    message);
        } else {
            Widget ty = new InlineLabel(i18n.format("add-deployment-submit.thanks")); //$NON-NLS-1$
            TransitionAnchor<DeploymentDetailsPage> clickHere = toDetailsFactory.get("uuid", results.getUuid()); //$NON-NLS-1$
            clickHere.setText(i18n.format("add-deployment-submit.click-here-1")); //$NON-NLS-1$
            Widget postAmble = new InlineLabel(i18n.format("add-deployment-submit.click-here-2")); //$NON-NLS-1$
            FlowPanel body = new FlowPanel();
            body.add(ty);
            body.add(clickHere);
            body.add(postAmble);
            notificationService.completeProgressNotification(
                    notification.getUuid(),
                    i18n.format("add-deployment-submit.successfully-added"), //$NON-NLS-1$
                    body);
        }
    }

    /**
     * The {@link DeploymentUploadServlet} returns a JSON map as the response.
     * @author eric.wittmann@redhat.com
     */
    private static class AddResult extends JavaScriptObject {

        /**
         * Constructor.
         */
        protected AddResult() {
        }

        /**
         * Convert the string returned by the {@link DeploymentUploadServlet} into JSON and
         * then from there into an {@link AddResult} bean.
         * @param resultData
         */
        public static final AddResult fromResult(String resultData) {
            int startIdx = resultData.indexOf('{');
            int endIdx = resultData.lastIndexOf('}') + 1;
            resultData = "(" + resultData.substring(startIdx, endIdx) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            return fromJSON(resultData);
        }

        /**
         * Gets a value from the map.
         * @param key
         */
        public final native String get(String key) /*-{
            if (this[key])
                return this[key];
            else
                return null;
        }-*/;

        /**
         * @return the uuid
         */
        public final String getUuid() {
            return get("uuid"); //$NON-NLS-1$
        }

        /**
         * @return the model
         */
        public final String getModel() {
            return get("model"); //$NON-NLS-1$
        }

        /**
         * @return the type
         */
        public final String getType() {
            return get("type"); //$NON-NLS-1$
        }

        /**
         * Returns true if the response is an error response.
         */
        public final boolean isError() {
            return "true".equals(get("exception")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * @return true if the response is due to a s-ramp package upload
         */
        public final boolean isBatch() {
            return "true".equals(get("batch")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * @return the total number of items in the s-ramp package
         */
        public final int getBatchTotal() {
            return new Integer(get("batchTotal")); //$NON-NLS-1$
        }

        /**
         * @return the number of successful items in the package
         */
        public final int getBatchNumSuccess() {
            return new Integer(get("batchNumSuccess")); //$NON-NLS-1$
        }

        /**
         * @return the number of failed items in the package
         */
        public final int getBatchNumFailed() {
            return new Integer(get("batchNumFailed")); //$NON-NLS-1$
        }

        /**
         * Gets the error.
         */
        public final DtgovUiException getError() {
            String errorMessage = get("exception-message"); //$NON-NLS-1$
            DtgovUiException error = new DtgovUiException(errorMessage);
            return error;
        }

        /**
         * Convert a string of json data into a useful bean.
         * @param jsonData
         */
        public static final native AddResult fromJSON(String jsonData) /*-{ return eval(jsonData); }-*/;

    }
}
