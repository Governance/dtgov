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
package org.overlord.dtgov.ui.client.local.pages;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.targets.DeleteTargetDialog;
import org.overlord.dtgov.ui.client.local.pages.targets.DeleteTargetEvent;
import org.overlord.dtgov.ui.client.local.pages.targets.DeleteTargetHandler;
import org.overlord.dtgov.ui.client.local.pages.targets.TargetsTable;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.TargetsRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Button;

/**
 * Manage Targets initial page. Used to list all the stored targets.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets.html#page")
@Page(path = "targets")
@Dependent
public class TargetsPage extends AbstractPage {

    /** The _event bus. */
    public static EventBus _eventBus = GWT.create(SimpleEventBus.class);

    @Inject
    @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> _backToDashboard;

    /** The create query. */
    @Inject
    @DataField("btn-add")
    private TransitionAnchor<TargetPage> _createTarget;

    /** The delete workflow query dialog. */
    @Inject
    private Instance<DeleteTargetDialog> _deleteTargetDialog;

    /** The i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The no data message. */
    @Inject
    @DataField("targets-none")
    private HtmlSnippet _noDataMessage;

    /** The notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The refresh button. */
    @Inject
    @DataField("btn-refresh")
    private Button _refreshButton;

    /** The search in progress message. */
    @Inject
    @DataField("targets-searching")
    private HtmlSnippet _searchInProgressMessage;


    /** The workflow query service. */
    @Inject
    private TargetsRpcService _targetService;

    /** The _workflow query table. */
    @Inject
    @DataField("targets-table")
    private TargetsTable _targetsTable;

    /**
     * Instantiates a new targets page.
     */
    public TargetsPage() {
    }

    /**
     * On refresh click.
     *
     * @param event
     *            the event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doSearch();
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {

        doSearch();
    }

    /**
     * Initializes the page on the post construct event.
     */
    @PostConstruct
    protected void postConstruct() {
        _targetsTable.setDeleteTargetDialog(_deleteTargetDialog);
        DeleteTargetHandler handlerDeleteTarget = new DeleteTargetHandler() {

            @Override
            public void onTargetDeleted(DeleteTargetEvent event) {
                doSearch();
            }
        };
        // this.addHandler(handlerDeleteQuery, DeleteWorkflowQueryEvent.TYPE);

        DeleteTargetEvent.register(_eventBus, handlerDeleteTarget);
    }

    /**
     * Do search method. Used on refresh, delete event and init method.
     */
    protected void doSearch() {
        onSearchStarting();

        _targetService.list(new IRpcServiceInvocationHandler<List<TargetSummaryBean>>() {
            @Override
            public void onError(Throwable error) {
                _notificationService.sendErrorNotification(_i18n.format("targets.error-loading"), error); //$NON-NLS-1$
                _noDataMessage.setVisible(true);
                _searchInProgressMessage.setVisible(false);
            }

            @Override
            public void onReturn(List<TargetSummaryBean> data) {
                updateTable(data);
            }
        });
    }

    /**
     * Updates the table of targets with the given data. Called when the
     * doSearch asynchronous call is successful.
     * 
     * @param data
     *            the data
     */
    protected void updateTable(List<TargetSummaryBean> data) {
        this._targetsTable.clear();
        this._searchInProgressMessage.setVisible(false);
        if (data.size() > 0) {
            for (TargetSummaryBean targetSummaryBean : data) {
                this._targetsTable.addRow(targetSummaryBean);
            }
            this._targetsTable.setVisible(true);
        } else {
            this._noDataMessage.setVisible(true);
        }
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onSearchStarting() {
        this._searchInProgressMessage.setVisible(true);
        this._targetsTable.setVisible(false);
        this._noDataMessage.setVisible(false);
    }

    /**
     * Gets the back to dashboard.
     *
     * @return the back to dashboard
     */
    public TransitionAnchor<DashboardPage> getBackToDashboard() {
        return _backToDashboard;
    }

    /**
     * Sets the back to dashboard.
     *
     * @param backToDashboard
     *            the new back to dashboard
     */
    public void setBackToDashboard(TransitionAnchor<DashboardPage> backToDashboard) {
        this._backToDashboard = backToDashboard;
    }

    /**
     * Gets the creates the target.
     *
     * @return the creates the target
     */
    public TransitionAnchor<TargetPage> getCreateTarget() {
        return _createTarget;
    }

    /**
     * Sets the creates the target.
     *
     * @param createTarget
     *            the new creates the target
     */
    public void setCreateTarget(TransitionAnchor<TargetPage> createTarget) {
        this._createTarget = createTarget;
    }

    /**
     * Gets the delete target dialog.
     *
     * @return the delete target dialog
     */
    public Instance<DeleteTargetDialog> getDeleteTargetDialog() {
        return _deleteTargetDialog;
    }

    /**
     * Sets the delete target dialog.
     *
     * @param deleteTargetDialog
     *            the new delete target dialog
     */
    public void setDeleteTargetDialog(Instance<DeleteTargetDialog> deleteTargetDialog) {
        this._deleteTargetDialog = deleteTargetDialog;
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
     * Gets the no data message.
     *
     * @return the no data message
     */
    public HtmlSnippet getNoDataMessage() {
        return _noDataMessage;
    }

    /**
     * Sets the no data message.
     *
     * @param noDataMessage
     *            the new no data message
     */
    public void setNoDataMessage(HtmlSnippet noDataMessage) {
        this._noDataMessage = noDataMessage;
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
     * Gets the refresh button.
     *
     * @return the refresh button
     */
    public Button getRefreshButton() {
        return _refreshButton;
    }

    /**
     * Sets the refresh button.
     *
     * @param refreshButton
     *            the new refresh button
     */
    public void setRefreshButton(Button refreshButton) {
        this._refreshButton = refreshButton;
    }

    /**
     * Gets the search in progress message.
     *
     * @return the search in progress message
     */
    public HtmlSnippet getSearchInProgressMessage() {
        return _searchInProgressMessage;
    }

    /**
     * Sets the search in progress message.
     *
     * @param searchInProgressMessage
     *            the new search in progress message
     */
    public void setSearchInProgressMessage(HtmlSnippet searchInProgressMessage) {
        this._searchInProgressMessage = searchInProgressMessage;
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
     * Gets the targets table.
     *
     * @return the targets table
     */
    public TargetsTable getTargetsTable() {
        return _targetsTable;
    }

    /**
     * Sets the targets table.
     *
     * @param targetsTable
     *            the new targets table
     */
    public void setTargetsTable(TargetsTable targetsTable) {
        this._targetsTable = targetsTable;
    }

}
