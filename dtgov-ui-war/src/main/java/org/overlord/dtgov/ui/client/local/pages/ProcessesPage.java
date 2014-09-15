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
import org.overlord.commons.gwt.client.local.widgets.Pager;
import org.overlord.commons.gwt.client.local.widgets.SortableTemplatedWidgetTable.SortColumn;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.events.DialogOkCancelEvent;
import org.overlord.dtgov.ui.client.local.events.StopProcessEvent;
import org.overlord.dtgov.ui.client.local.pages.processes.AbortProcessDialog;
import org.overlord.dtgov.ui.client.local.pages.processes.ProcessesFilter;
import org.overlord.dtgov.ui.client.local.pages.processes.ProcessesTable;
import org.overlord.dtgov.ui.client.local.pages.processes.WorkflowsTable;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.ProcessesRpcService;
import org.overlord.dtgov.ui.client.local.services.WorkflowQueriesRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Button;

/**
 * Manage Processes initial page. Used to list all the workflow processes. It
 * shows the RUNNING, STOPPED and COMPLETED processes
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/processes.html#page")
@Page(path = "processes")
@Dependent
public class ProcessesPage extends AbstractPage {

    public static final String PROCESSES_FILTER = "processes.filter-bean"; //$NON-NLS-1$
    public static final String PROCESSES_PAGE = "processes.page"; //$NON-NLS-1$
    public static final String PROCESSES_SORT_COLUMN = "processes.sort-column"; //$NON-NLS-1$

    /** The _event bus. */
    public static EventBus _eventBus = GWT.create(SimpleEventBus.class);

    @Inject
    @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> _backToDashboard;

    /** The delete workflow query dialog. */
    @Inject
    private Instance<AbortProcessDialog> _stopProcessDialog;

    @Inject
    private ApplicationStateService _stateService;

    /** The i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The no data message. */
    @Inject
    @DataField("processes-none")
    private HtmlSnippet _noDataMessage;

    /** The no data message. */
    @Inject
    @DataField("workflows-none")
    private HtmlSnippet workflowsNoDataMessage;

    /** The notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The refresh button. */
    @Inject
    @DataField("btn-refresh")
    private Button _refreshButton;

    /** The search in progress message. */
    @Inject
    @DataField("processes-searching")
    private HtmlSnippet _searchInProgressMessage;

    /** The search in progress message. */
    @Inject
    @DataField("workflows-searching")
    private HtmlSnippet workflowsSearchInProgressMessage;

    /** The workflow query service. */
    @Inject
    private ProcessesRpcService _processesService;

    /** The _workflow query table. */
    @Inject
    @DataField("processes-table")
    private ProcessesTable _processesTable;

    /** The _workflow query table. */
    @Inject
    @DataField("workflows-table")
    private WorkflowsTable workflowsTable;

    @Inject
    @DataField("processes-filter-sidebar")
    private ProcessesFilter _filtersPanel;

    /** The _pager. */
    @Inject
    @DataField("processes-pager")
    private Pager _pager;

    /** The _range span. */
    @DataField("processes-range")
    private final SpanElement _processesRangeSpan = Document.get().createSpanElement();

    /** The _total span. */
    @DataField("processes-total")
    private final SpanElement _processesTotalSpan = Document.get().createSpanElement();

    /** The _range span. */
    @DataField("workflows-range")
    private final SpanElement workflowsRangeSpan = Document.get().createSpanElement();

    /** The _total span. */
    @DataField("workflows-total")
    private final SpanElement workflowsTotalSpan = Document.get().createSpanElement();

    /** The _current page. */
    private int _currentPage = 1;

    /** The _workflow query service. */
    @Inject
    private WorkflowQueriesRpcService _workflowQueryService;

    /**
     * Instantiates a new targets page.
     */
    public ProcessesPage() {
    }

    /**
     * On refresh click.
     *
     * @param event
     *            the event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doSearch(_currentPage);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        _filtersPanel.refresh();
        ProcessesFilterBean filterBean = (ProcessesFilterBean) _stateService.get(PROCESSES_FILTER, new ProcessesFilterBean());
        Integer page = (Integer) _stateService.get(PROCESSES_PAGE, 1);
        SortColumn sortColumn = (SortColumn) _stateService.get(PROCESSES_SORT_COLUMN,
                this._processesTable.getDefaultSortColumn());
        this._filtersPanel.setValue(filterBean);
        this._processesTable.sortBy(sortColumn.columnId, sortColumn.ascending);
        doSearch(page);
        doWorkflowSearch();
    }

    /**
     * Do search.
     */
    protected void doSearch() {
        doSearch(1);
    }

    /**
     * Initializes the page on the post construct event.
     */
    @PostConstruct
    protected void postConstruct() {
        _filtersPanel.addValueChangeHandler(new ValueChangeHandler<ProcessesFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<ProcessesFilterBean> event) {
                doSearch();
            }
        });
        _pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doSearch(event.getValue());
            }
        });

        _processesTable.addStopProcessHandler(new StopProcessEvent.Handler() {
            @Override
            public void onProcessStopped(StopProcessEvent event) {
                AbortProcessDialog dialog = _stopProcessDialog.get();
                final ProcessBean process = (ProcessBean) event.getItem();
                dialog.setProcess(process);
                dialog.addDialogOkCancelHandler(new DialogOkCancelEvent.Handler() {
                    @Override
                    public void onDialogOkCancel(DialogOkCancelEvent event) {
                        stopProcess(process);
                    }
                });
                dialog.show();
            }
        });


        this._processesRangeSpan.setInnerText("?"); //$NON-NLS-1$
        this._processesTotalSpan.setInnerText("?"); //$NON-NLS-1$

        this.workflowsRangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.workflowsTotalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Do search method. Used on refresh, stop event and init method.
     *
     * @param page
     *            the page
     */
    protected void doWorkflowSearch() {
        onWorkflowSearchStarting();
        _workflowQueryService.getWorkflowTypes(new IRpcServiceInvocationHandler<List<Workflow>>() {

            @Override
            public void onReturn(List<Workflow> workflowTypes) {
                updateWorkflowsTable(workflowTypes);
                String rangeText = "1-" + workflowTypes.size(); //$NON-NLS-1$ //$NON-NLS-2$
                workflowsRangeSpan.setInnerText(rangeText); //$NON-NLS-1$
                workflowsTotalSpan.setInnerText(workflowTypes.size() + ""); //$NON-NLS-1$
            }

            @Override
            public void onError(Throwable error) {
                _notificationService.sendErrorNotification(_i18n.format("processes.error-loading"), error); //$NON-NLS-1$
                workflowsNoDataMessage.setVisible(true);
                workflowsSearchInProgressMessage.setVisible(false);
            }
        });

    }

    /**
     * Do search method. Used on refresh, stop event and init method.
     *
     * @param page
     *            the page
     */
    protected void doSearch(int page) {
        onSearchStarting();
        _currentPage = page;

        final ProcessesFilterBean filterBean = _filtersPanel.getValue();
        final SortColumn currentSortColumn = this._processesTable.getCurrentSortColumn();

        _stateService.put(PROCESSES_FILTER, filterBean);
        _stateService.put(PROCESSES_PAGE, _currentPage);
        _stateService.put(PROCESSES_SORT_COLUMN, currentSortColumn);
        _processesService.search(filterBean, page, currentSortColumn.columnId, currentSortColumn.ascending,
                new IRpcServiceInvocationHandler<ProcessesResultSetBean>() {
            @Override
            public void onError(Throwable error) {
                        _notificationService.sendErrorNotification(_i18n.format("processes.error-loading"), error); //$NON-NLS-1$
                        _noDataMessage.setVisible(true);
                        _searchInProgressMessage.setVisible(false);
                        updatePager(null);
            }

            @Override
                    public void onReturn(ProcessesResultSetBean data) {
                updateTable(data);
                        updatePager(data);
            }
        });
    }

    /**
     * Updates the table of processes with the given data. Called when the
     * doSearch asynchronous call is successful.
     *
     * @param data
     *            the data
     */
    protected void updateTable(ProcessesResultSetBean data) {
        this._processesTable.clear();
        this._searchInProgressMessage.setVisible(false);
        if (data.getProcesses() != null && data.getProcesses().size() > 0) {
            for (ProcessBean processBean : data.getProcesses()) {
                this._processesTable.addRow(processBean);
            }
            this._processesTable.setVisible(true);
        } else {
            this._noDataMessage.setVisible(true);
        }
    }

    /**
     * Updates the table of processes with the given data. Called when the
     * doSearch asynchronous call is successful.
     *
     * @param data
     *            the data
     */
    protected void updateWorkflowsTable(List<Workflow> data) {
        this.workflowsTable.clear();
        this.workflowsSearchInProgressMessage.setVisible(false);
        if (data != null && data.size() > 0) {
            this.workflowsTable.setValue(data);

            this.workflowsTable.setVisible(true);
        } else {
            this.workflowsNoDataMessage.setVisible(true);
        }
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onSearchStarting() {
        this._pager.setVisible(false);
        this._searchInProgressMessage.setVisible(true);
        this._processesTable.setVisible(false);
        this._noDataMessage.setVisible(false);
        this._processesRangeSpan.setInnerText("?"); //$NON-NLS-1$
        this._processesTotalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onWorkflowSearchStarting() {
        this.workflowsSearchInProgressMessage.setVisible(true);
        this.workflowsTable.setVisible(false);
        this.workflowsNoDataMessage.setVisible(false);
        this.workflowsRangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.workflowsTotalSpan.setInnerText("?"); //$NON-NLS-1$
    }
    /**
     * Update pager.
     *
     * @param data
     *            the data
     */
    protected void updatePager(ProcessesResultSetBean data) {
        if (data != null && data.getProcesses() != null && data.getProcesses().size() > 0) {
            int numPages = ((int) (data.get_totalResults() / data.getItemsPerPage()))
                    + (data.get_totalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
            int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
            this._pager.setNumPages(numPages);
            this._pager.setPage(thisPage);
            if (numPages > 1)
                this._pager.setVisible(true);

            int startIndex = data.getStartIndex() + 1;
            int endIndex = startIndex + data.getProcesses().size() - 1;
            String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
            String totalText = String.valueOf(data.get_totalResults());
            this._processesRangeSpan.setInnerText(rangeText);
            this._processesTotalSpan.setInnerText(totalText);
        } else {
            this._pager.setVisible(false);
            this._noDataMessage.setVisible(true);
            this._processesRangeSpan.setInnerText("0"); //$NON-NLS-1$
            this._processesTotalSpan.setInnerText("0"); //$NON-NLS-1$
        }

    }

    /**
     * Stop a process workflow.
     *
     * @param process
     *            the process
     */
    private void stopProcess(final ProcessBean process) {
        final NotificationBean notification = _notificationService.startProgressNotification(_i18n.format("abort-process-submit.aborting"), //$NON-NLS-1$
                _i18n.format("abort-process-submit.aborting-msg")); //$NON-NLS-1$

        _processesService.abort(process.getUuid(), new IRpcServiceInvocationHandler<Boolean>() {
            @Override
            public void onReturn(Boolean data) {
                _notificationService.completeProgressNotification(notification.getUuid(), _i18n.format("abort-process-submit.successfully-aborted"), //$NON-NLS-1$
                        _i18n.format("abort-process-submit.successfully-aborted-msg")); //$NON-NLS-1$
                doSearch();
            }

            @Override
            public void onError(Throwable error) {
                _notificationService.sendErrorNotification(
                        _i18n.format("abort-process-submit.error", process.getArtifactName(), process.getWorkflow()), error); //$NON-NLS-1$

            }
        });

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
     * Gets the stop process dialog.
     *
     * @return the stop process dialog
     */
    public Instance<AbortProcessDialog> getStopProcessDialog() {
        return _stopProcessDialog;
    }

    /**
     * Sets the stop process dialog.
     *
     * @param stopProcessDialog
     *            the new stop process dialog
     */
    public void setStopProcessDialog(Instance<AbortProcessDialog> stopProcessDialog) {
        this._stopProcessDialog = stopProcessDialog;
    }

    /**
     * Gets the state service.
     *
     * @return the state service
     */
    public ApplicationStateService getStateService() {
        return _stateService;
    }

    /**
     * Sets the state service.
     *
     * @param stateService
     *            the new state service
     */
    public void setStateService(ApplicationStateService stateService) {
        this._stateService = stateService;
    }

    /**
     * Gets the processes service.
     *
     * @return the processes service
     */
    public ProcessesRpcService getProcessesService() {
        return _processesService;
    }

    /**
     * Sets the processes service.
     *
     * @param processesService
     *            the new processes service
     */
    public void setProcessesService(ProcessesRpcService processesService) {
        this._processesService = processesService;
    }

    /**
     * Gets the processes table.
     *
     * @return the processes table
     */
    public ProcessesTable getProcessesTable() {
        return _processesTable;
    }

    /**
     * Sets the processes table.
     *
     * @param processesTable
     *            the new processes table
     */
    public void setProcessesTable(ProcessesTable processesTable) {
        this._processesTable = processesTable;
    }

    /**
     * Gets the filters panel.
     *
     * @return the filters panel
     */
    public ProcessesFilter getFiltersPanel() {
        return _filtersPanel;
    }

    /**
     * Sets the filters panel.
     *
     * @param filtersPanel
     *            the new filters panel
     */
    public void setFiltersPanel(ProcessesFilter filtersPanel) {
        this._filtersPanel = filtersPanel;
    }

    /**
     * Gets the pager.
     *
     * @return the pager
     */
    public Pager getPager() {
        return _pager;
    }

    /**
     * Sets the pager.
     *
     * @param pager
     *            the new pager
     */
    public void setPager(Pager pager) {
        this._pager = pager;
    }

    /**
     * Gets the current page.
     *
     * @return the current page
     */
    public int getCurrentPage() {
        return _currentPage;
    }

    /**
     * Sets the current page.
     *
     * @param currentPage
     *            the new current page
     */
    public void setCurrentPage(int currentPage) {
        this._currentPage = currentPage;
    }

    /**
     * Gets the range span.
     *
     * @return the range span
     */
    public SpanElement getRangeSpan() {
        return _processesRangeSpan;
    }

    /**
     * Gets the total span.
     *
     * @return the total span
     */
    public SpanElement getTotalSpan() {
        return _processesTotalSpan;
    }

    /**
     * Gets the event bus.
     *
     * @return the event bus
     */
    public static EventBus getEventBus() {
        return _eventBus;
    }

    /**
     * Sets the event bus.
     *
     * @param eventBus
     *            the new event bus
     */
    public static void setEventBus(EventBus eventBus) {
        ProcessesPage._eventBus = eventBus;
    }


}
