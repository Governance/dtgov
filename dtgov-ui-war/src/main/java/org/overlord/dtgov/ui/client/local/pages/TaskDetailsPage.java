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

import java.util.HashMap;

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
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.taskInbox.TaskFormPanel;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.TaskInboxRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingDateConverter;
import org.overlord.dtgov.ui.client.local.util.DataBindingIntegerConverter;
import org.overlord.dtgov.ui.client.local.widgets.common.DescriptionInlineLabel;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;

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
    protected ClientMessages i18n;
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
    InlineLabel priority;
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

    @Inject @DataField("task-form")
    TaskFormPanel taskFormWrapper;

    @Inject @DataField("action-complete")
    Button completeButton;
    @Inject @DataField("action-fail")
    Button failButton;

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
        pageContent = DOMUtil.findElementById(getElement(), "task-details-content-wrapper"); //$NON-NLS-1$
        task.addPropertyChangeHandler(new PropertyChangeHandler<Object>() {
            @Override
            public void onPropertyChange(PropertyChangeEvent<Object> event) {
                if ("description".equals(event.getPropertyName())) { //$NON-NLS-1$
                    pushModelToServer();
                }
            }
        });
    }

    /**
     * Sends the model back up to the server (saves local changes).
     */
    protected void pushModelToServer() {
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("task-details.updating-task"), //$NON-NLS-1$
                i18n.format("task-details.updating-task-msg", task.getModel().getName())); //$NON-NLS-1$
        taskInboxService.update(task.getModel(), new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("task-details.update-complete"), //$NON-NLS-1$
                        i18n.format("task-details.update-complete-msg", task.getModel().getName())); //$NON-NLS-1$
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("task-details.error-updating-task"), //$NON-NLS-1$
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
        pageContent.addClassName("hide"); //$NON-NLS-1$
        taskLoading.getElement().removeClassName("hide"); //$NON-NLS-1$
        taskInboxService.get(id, new IRpcServiceInvocationHandler<TaskBean>() {
            @Override
            public void onReturn(TaskBean data) {
                currentTask = data;
                updateTaskMetaData(data);
                updateActionStates(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("task-details.error-getting-details"), error); //$NON-NLS-1$
            }
        });
    }

    /**
     * Called when the task is loaded.
     * @param task
     */
    protected void updateTaskMetaData(TaskBean task) {
        this.task.setModel(task, InitialState.FROM_MODEL);
        taskLoading.getElement().addClassName("hide"); //$NON-NLS-1$
        pageContent.removeClassName("hide"); //$NON-NLS-1$
        if (task.getTaskForm() != null) {
            taskFormWrapper.setHTML(task.getTaskForm());
            taskFormWrapper.setData(task.getTaskData());
        } else {
            taskFormWrapper.setHTML(""); //$NON-NLS-1$
            taskFormWrapper.setData(new HashMap<String, String>());
        }
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
    }

    /**
     * Called when the user clicks the Claim button.
     * @param event
     */
    @EventHandler("action-claim")
    public void onClaimClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.claim,
                i18n.format("task-details.claiming-task"), //$NON-NLS-1$
                i18n.format("task-details.claiming-task-msg", task.getModel().getName()), //$NON-NLS-1$
                i18n.format("task-details.task-claimed"), //$NON-NLS-1$
                i18n.format("task-details.task-claimed-msg", task.getModel().getName())); //$NON-NLS-1$
    }

    /**
     * Called when the user clicks the Release button.
     * @param event
     */
    @EventHandler("action-release")
    public void onReleaseClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.release,
                i18n.format("task-details.releasing-task"), //$NON-NLS-1$
                i18n.format("task-details.releasing-task-msg", task.getModel().getName()), //$NON-NLS-1$
                i18n.format("task-details.task-released"), //$NON-NLS-1$
                i18n.format("task-details.task-released-msg", task.getModel().getName())); //$NON-NLS-1$
    }

    /**
     * Called when the user clicks the Start button.
     * @param event
     */
    @EventHandler("action-start")
    public void onStartClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.start,
                i18n.format("task-details.starting-task"), //$NON-NLS-1$
                i18n.format("task-details.starting-task-msg", task.getModel().getName()), //$NON-NLS-1$
                i18n.format("task-details.task-started"), //$NON-NLS-1$
                i18n.format("task-details.task-started-msg", task.getModel().getName())); //$NON-NLS-1$
    }

    /**
     * Called when the user clicks the Stop button.
     * @param event
     */
    @EventHandler("action-stop")
    public void onStopClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.stop,
                i18n.format("task-details.stopping-task"), //$NON-NLS-1$
                i18n.format("task-details.stopping-task-msg", task.getModel().getName()), //$NON-NLS-1$
                i18n.format("task-details.task-stopped"), //$NON-NLS-1$
                i18n.format("task-details.task-stopped-msg", task.getModel().getName())); //$NON-NLS-1$
    }

    /**
     * Called when the user clicks the Complete button.
     * @param event
     */
    @EventHandler("action-complete")
    public void onCompleteClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.complete,
                i18n.format("task-details.completing-task"), //$NON-NLS-1$
                i18n.format("task-details.completing-task-msg", task.getModel().getName()), //$NON-NLS-1$
                i18n.format("task-details.task-comleted"), //$NON-NLS-1$
                i18n.format("task-details.task-comleted-msg", task.getModel().getName())); //$NON-NLS-1$
    }

    /**
     * Called when the user clicks the Fail button.
     * @param event
     */
    @EventHandler("action-fail")
    public void onFailClick(ClickEvent event) {
        doTaskAction(TaskActionEnum.fail,
                i18n.format("task-details.failing-task"), //$NON-NLS-1$
                i18n.format("task-details.failing-task-msg", task.getModel().getName()), //$NON-NLS-1$
                i18n.format("task-details.task-failed"), //$NON-NLS-1$
                i18n.format("task-details.task-failed-msg", task.getModel().getName())); //$NON-NLS-1$
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
        final TaskBean model = task.getModel();
        model.setTaskData(taskFormWrapper.getData());
        taskInboxService.executeAction(model, action, new IRpcServiceInvocationHandler<TaskBean>() {
            @Override
            public void onReturn(TaskBean data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(), successTitle,
                        successDescription);
                data.setTaskForm(model.getTaskForm());
                data.getTaskData().putAll(taskFormWrapper.getData());
                updateTaskMetaData(data);
                updateActionStates(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("task-details.error", inProgressTitle), error); //$NON-NLS-1$
            }
        });
    }

}
