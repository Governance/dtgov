/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.dtgov.ui.client.local.pages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.TaskInboxRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingDateConverter;
import org.overlord.dtgov.ui.client.local.util.DataBindingIntegerConverter;
import org.overlord.dtgov.ui.client.local.widgets.common.DescriptionInlineLabel;
import org.overlord.dtgov.ui.client.local.widgets.common.EditableInlineLabel;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The Task Details page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/taskDetails.html#page")
@Page(path="taskDetails")
@Dependent
public class TaskDetailsPage extends AbstractPage {

    @Inject
    protected TaskInboxRpcService taskInboxService;
    @Inject
    protected NotificationService notificationService;
    protected TaskBean currentTask;

    @PageState
    private String id;

    @Inject @AutoBound
    protected DataBinder<TaskBean> task;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;
    @Inject @DataField("back-to-tasks")
    TransitionAnchor<TaskInboxPage> backToTaskInbox;

    // Properties
    @Inject @DataField("task-name") @Bound(property="name")
    InlineLabel name;
    @Inject @DataField("task-id") @Bound(property="id")
    InlineLabel taskId;
    @Inject @DataField("task-status") @Bound(property="status")
    InlineLabel status;
    @Inject @DataField("task-owner") @Bound(property="owner")
    InlineLabel owner;
    @Inject @DataField("task-priority") @Bound(property="priority", converter=DataBindingIntegerConverter.class)
    EditableInlineLabel priority;
    @Inject @DataField("task-dueDate") @Bound(property="dueDate", converter=DataBindingDateConverter.class)
    InlineLabel dueOn;
    @Inject @DataField("task-description") @Bound(property="description")
    DescriptionInlineLabel description;

    // Actions
    @Inject @DataField("action-claim")
    Button claimButton;
    @Inject @DataField("action-release")
    Button releaseButton;

    @Inject @DataField("action-start")
    Button startButton;
    @Inject @DataField("action-stop")
    Button stopButton;
    @Inject @DataField("action-complete")
    Button completeButton;
    @Inject @DataField("action-fail")
    Button failButton;

    @Inject @DataField("action-save")
    Button saveButton;

    @Inject @DataField("task-details-loading-spinner")
    protected HtmlSnippet taskLoading;
    protected Element pageContent;

    /**
     * Constructor.
     */
    public TaskDetailsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "task-details-content-wrapper");
        task.addPropertyChangeHandler(new PropertyChangeHandler<Object>() {
            @Override
            public void onPropertyChange(PropertyChangeEvent<Object> event) {
                pushModelToServer();
            }
        });
    }

    /**
     * Sends the model back up to the server (saves local changes).
     */
    // TODO i18n
    protected void pushModelToServer() {
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                "Updating Task", "Updating task '" + task.getModel().getName() + "', please wait...");
        taskInboxService.update(task.getModel(), new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        "Update Complete",
                        "You have successfully updated task '" + task.getModel().getName() + "'.");
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        "Error Updating Task",
                        error);
            }
        });
    }

    /**
     * @see org.overlord.sramp.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        currentTask = null;
        pageContent.addClassName("hide");
        taskLoading.getElement().removeClassName("hide");
        taskInboxService.get(id, new IRpcServiceInvocationHandler<TaskBean>() {
            @Override
            public void onReturn(TaskBean data) {
                currentTask = data;
                updateTaskMetaData(data);
                updateActionStates(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification("Error getting task details.", error);
            }
        });
    }

    /**
     * Called when the task is loaded.
     * @param task
     */
    protected void updateTaskMetaData(TaskBean task) {
        this.task.setModel(task, InitialState.FROM_MODEL);
        taskLoading.getElement().addClassName("hide");
        pageContent.removeClassName("hide");
    }

    /**
     * Updates the states of the action buttons to reflect the available actions
     * found on the task.
     * @param data
     */
    protected void updateActionStates(TaskBean task) {
        claimButton.setEnabled(task.isActionAllowed(TaskActionEnum.claim));
        releaseButton.setEnabled(task.isActionAllowed(TaskActionEnum.release));

        startButton.setEnabled(task.isActionAllowed(TaskActionEnum.start));
        stopButton.setEnabled(task.isActionAllowed(TaskActionEnum.stop));
        completeButton.setEnabled(task.isActionAllowed(TaskActionEnum.complete));
        failButton.setEnabled(task.isActionAllowed(TaskActionEnum.fail));

        saveButton.setEnabled(task.isActionAllowed(TaskActionEnum.save));
    }

    /**
     * Called when the user clicks the Claim button.
     * @param event
     */
    @EventHandler("action-claim")
    public void onClaimClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.claim, "Claiming Task", "Claiming task '" + task.getModel().getName() + "', please wait...",
                "Task Claimed", "You have successfully claimed task '" + task.getModel().getName() + "'.");
    }

    /**
     * Called when the user clicks the Release button.
     * @param event
     */
    @EventHandler("action-release")
    public void onReleaseClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.release, "Releasing Task", "Releasing task '" + task.getModel().getName() + "', please wait...",
                "Task Released", "You have successfully released task '" + task.getModel().getName() + "'.");
    }

    /**
     * Called when the user clicks the Start button.
     * @param event
     */
    @EventHandler("action-start")
    public void onStartClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.start, "Starting Task", "Starting task '" + task.getModel().getName() + "', please wait...",
                "Task Started", "You have successfully started task '" + task.getModel().getName() + "'.");
    }

    /**
     * Called when the user clicks the Stop button.
     * @param event
     */
    @EventHandler("action-stop")
    public void onStopClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.stop, "Stopping Task", "Stopping task '" + task.getModel().getName() + "', please wait...",
                "Task Stopped", "You have successfully stopped task '" + task.getModel().getName() + "'.");
    }

    /**
     * Called when the user clicks the Complete button.
     * @param event
     */
    @EventHandler("action-complete")
    public void onCompleteClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.complete, "Completing Task", "Completing task '" + task.getModel().getName() + "', please wait...",
                "Task Completed", "You have successfully completed task '" + task.getModel().getName() + "'.");
    }

    /**
     * Called when the user clicks the Fail button.
     * @param event
     */
    @EventHandler("action-fail")
    public void onFailClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.fail, "Failing Task", "Failing task '" + task.getModel().getName() + "', please wait...",
                "Task Failed", "You have successfully failed task '" + task.getModel().getName() + "'.");
    }

    /**
     * Executes the action by making an RPC call to the server.
     * @param action
     * @param inProgressTitle
     * @param inProgressDescription
     * @param successTitle
     * @param successDescription
     */
    private void doTaskAction(TaskActionEnum action, final String inProgressTitle, final String inProgressDescription,
            final String successTitle, final String successDescription) {
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                inProgressTitle, inProgressDescription);
        taskInboxService.executeAction(task.getModel(), action, new IRpcServiceInvocationHandler<TaskBean>() {
            @Override
            public void onReturn(TaskBean data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(), successTitle,
                        successDescription);
                updateTaskMetaData(data);
                updateActionStates(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        "Error " + inProgressTitle, error);
            }
        });
    }

}
