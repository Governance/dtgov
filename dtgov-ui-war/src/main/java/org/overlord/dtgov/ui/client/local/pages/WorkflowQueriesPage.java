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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.events.TableSortEvent;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.commons.gwt.client.local.widgets.Pager;
import org.overlord.commons.gwt.client.local.widgets.SortableTemplatedWidgetTable.SortColumn;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.workflowQuery.DeleteWorkflowQueryDialog;
import org.overlord.dtgov.ui.client.local.pages.workflowQuery.DeleteWorkflowQueryEvent;
import org.overlord.dtgov.ui.client.local.pages.workflowQuery.DeleteWorkflowQueryHandler;
import org.overlord.dtgov.ui.client.local.pages.workflowQuery.WorkflowQueriesFilter;
import org.overlord.dtgov.ui.client.local.pages.workflowQuery.WorkflowQueryTable;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateKeys;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.WorkflowQueriesRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueriesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQuerySummaryBean;

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
 * Workflow Queries initial page.
 * 
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/workflowQueries.html#page")
@Page(path = "workflowQueries")
@Dependent
public class WorkflowQueriesPage extends AbstractPage {

    /** The _event bus. */
    public static EventBus _eventBus = GWT.create(SimpleEventBus.class);

    // Breadcrumbs
    /** The _back to dashboard. */
    @Inject
    @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> _backToDashboard;

    /** The _create query. */
    @Inject
    @DataField("btn-add")
    private TransitionAnchor<WorkflowQueryPage> _createQuery;

    /** The _current page. */
    private int _currentPage = 1;

    /** The _delete workflow query dialog. */
    @Inject
    private Instance<DeleteWorkflowQueryDialog> _deleteWorkflowQueryDialog;

    /** The _filters panel. */
    @Inject
    @DataField("queries-filter-sidebar")
    private WorkflowQueriesFilter _filtersPanel;

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _no data message. */
    @Inject
    @DataField("queries-none")
    private HtmlSnippet _noDataMessage;

    /** The _notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The _pager. */
    @Inject
    @DataField("queries-pager")
    private Pager _pager;

    /** The _range span. */
    @DataField("queries-range")
    private SpanElement _rangeSpan = Document.get().createSpanElement();

    /** The _refresh button. */
    @Inject
    @DataField("btn-refresh")
    private Button _refreshButton;

    /** The _search in progress message. */
    @Inject
    @DataField("queries-searching")
    private HtmlSnippet _searchInProgressMessage;

    /** The _state service. */
    @Inject
    private ApplicationStateService _stateService;

    /** The _total span. */
    @DataField("queries-total")
    private SpanElement _totalSpan = Document.get().createSpanElement();

    /** The _workflow query service. */
    @Inject
    private WorkflowQueriesRpcService _workflowQueryService;

    /** The _workflow query table. */
    @Inject
    @DataField("queries-table")
    private WorkflowQueryTable _workflowQueryTable;

    /**
     * Constructor.
     */
    public WorkflowQueriesPage() {
    }

    /**
     * Search for artifacts based on the current filter settings and search
     * text.
     */
    protected void doSearch() {
        doSearch(1);
    }

    /**
     * Search for deployments based on the current filter settings.
     * 
     * @param page
     */
    protected void doSearch(int page) {
        onSearchStarting();
        _currentPage = page;

        final WorkflowQueriesFilterBean filterBean = _filtersPanel.getValue();
        final SortColumn currentSortColumn = this._workflowQueryTable.getCurrentSortColumn();

        _stateService.put(ApplicationStateKeys.WORKFLOW_QUERIES_FILTER, filterBean);
        _stateService.put(ApplicationStateKeys.WORKFLOW_QUERIES_PAGE, _currentPage);
        _stateService.put(ApplicationStateKeys.WORKFLOW_QUERIES_SORT_COLUMN, currentSortColumn);

        _workflowQueryService.search(filterBean, page, currentSortColumn.columnId,
                currentSortColumn.ascending, new IRpcServiceInvocationHandler<WorkflowQueryResultSetBean>() {
                    @Override
                    public void onError(Throwable error) {
                        _notificationService.sendErrorNotification(
                                _i18n.format("deployments.error-loading"), error); //$NON-NLS-1$
                        _noDataMessage.setVisible(true);
                        _searchInProgressMessage.setVisible(false);
                    }

                    @Override
                    public void onReturn(WorkflowQueryResultSetBean data) {
                        updateTable(data);
                        updatePager(data);
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
     * Gets the creates the query.
     * 
     * @return the creates the query
     */
    public TransitionAnchor<WorkflowQueryPage> getCreateQuery() {
        return _createQuery;
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
     * Gets the delete workflow query dialog.
     * 
     * @return the delete workflow query dialog
     */
    public Instance<DeleteWorkflowQueryDialog> getDeleteWorkflowQueryDialog() {
        return _deleteWorkflowQueryDialog;
    }

    /**
     * Gets the filters panel.
     * 
     * @return the filters panel
     */
    public WorkflowQueriesFilter getFiltersPanel() {
        return _filtersPanel;
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
     * Gets the no data message.
     * 
     * @return the no data message
     */
    public HtmlSnippet getNoDataMessage() {
        return _noDataMessage;
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
     * Gets the pager.
     * 
     * @return the pager
     */
    public Pager getPager() {
        return _pager;
    }

    /**
     * Gets the range span.
     * 
     * @return the range span
     */
    public SpanElement getRangeSpan() {
        return _rangeSpan;
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
     * Gets the search in progress message.
     * 
     * @return the search in progress message
     */
    public HtmlSnippet getSearchInProgressMessage() {
        return _searchInProgressMessage;
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
     * Gets the total span.
     * 
     * @return the total span
     */
    public SpanElement getTotalSpan() {
        return _totalSpan;
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
     * Gets the workflow query table.
     * 
     * @return the workflow query table
     */
    public WorkflowQueryTable getWorkflowQueryTable() {
        return _workflowQueryTable;
    }

    /**
     * Kick off a search at this point so that we show some data in the UI.
     * 
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        // Refresh the filters
        _filtersPanel.refresh();

        WorkflowQueriesFilterBean filterBean = (WorkflowQueriesFilterBean) _stateService.get(
                ApplicationStateKeys.WORKFLOW_QUERIES_FILTER, new WorkflowQueriesFilterBean());
        //String searchText = (String) stateService.get(ApplicationStateKeys.DEPLOYMENTS_SEARCH_TEXT, ""); //$NON-NLS-1$
        Integer page = (Integer) _stateService.get(ApplicationStateKeys.WORKFLOW_QUERIES_PAGE, 1);
        SortColumn sortColumn = (SortColumn) _stateService.get(
                ApplicationStateKeys.WORKFLOW_QUERIES_SORT_COLUMN,
                this._workflowQueryTable.getDefaultSortColumn());

        this._filtersPanel.setValue(filterBean);
        this._workflowQueryTable.sortBy(sortColumn.columnId, sortColumn.ascending);

        // Kick off a search
        doSearch(page);
    }

    /**
     * Called whenver the page is shown.
     */
    @PageShown
    public void onPageShown() {
        // doSearch();
    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * 
     * @param event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doSearch(_currentPage);
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onSearchStarting() {
        this._pager.setVisible(false);
        this._searchInProgressMessage.setVisible(true);
        this._workflowQueryTable.setVisible(false);
        this._noDataMessage.setVisible(false);
        this._rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this._totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        _filtersPanel.addValueChangeHandler(new ValueChangeHandler<WorkflowQueriesFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<WorkflowQueriesFilterBean> event) {
                doSearch();
            }
        });

        _pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doSearch(event.getValue());
            }
        });
        _workflowQueryTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doSearch(_currentPage);
            }
        });

        // Hide column 1 when in mobile mode.
        _workflowQueryTable.setColumnClasses(1, "desktop-only"); //$NON-NLS-1$
        _workflowQueryTable.setColumnClasses(2, "desktop-only"); //$NON-NLS-1$

        this._rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this._totalSpan.setInnerText("?"); //$NON-NLS-1$
        _workflowQueryTable.setDeleteWorkflowQueryDialog(_deleteWorkflowQueryDialog);
        DeleteWorkflowQueryHandler handlerDeleteQuery = new DeleteWorkflowQueryHandler() {

            @Override
            public void onWorkflowQueryDeleted(DeleteWorkflowQueryEvent event) {
                doSearch();
            }
        };
        // this.addHandler(handlerDeleteQuery, DeleteWorkflowQueryEvent.TYPE);

        DeleteWorkflowQueryEvent.register(_eventBus, handlerDeleteQuery);
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
     * Sets the creates the query.
     * 
     * @param createQuery
     *            the new creates the query
     */
    public void setCreateQuery(TransitionAnchor<WorkflowQueryPage> createQuery) {
        this._createQuery = createQuery;
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
     * Sets the delete workflow query dialog.
     * 
     * @param deleteWorkflowQueryDialog
     *            the new delete workflow query dialog
     */
    public void setDeleteWorkflowQueryDialog(Instance<DeleteWorkflowQueryDialog> deleteWorkflowQueryDialog) {
        this._deleteWorkflowQueryDialog = deleteWorkflowQueryDialog;
    }

    /**
     * Sets the filters panel.
     * 
     * @param filtersPanel
     *            the new filters panel
     */
    public void setFiltersPanel(WorkflowQueriesFilter filtersPanel) {
        this._filtersPanel = filtersPanel;
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
     * Sets the no data message.
     * 
     * @param noDataMessage
     *            the new no data message
     */
    public void setNoDataMessage(HtmlSnippet noDataMessage) {
        this._noDataMessage = noDataMessage;
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
     * Sets the pager.
     * 
     * @param pager
     *            the new pager
     */
    public void setPager(Pager pager) {
        this._pager = pager;
    }

    /**
     * Sets the range span.
     * 
     * @param rangeSpan
     *            the new range span
     */
    public void setRangeSpan(SpanElement rangeSpan) {
        this._rangeSpan = rangeSpan;
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
     * Sets the search in progress message.
     * 
     * @param searchInProgressMessage
     *            the new search in progress message
     */
    public void setSearchInProgressMessage(HtmlSnippet searchInProgressMessage) {
        this._searchInProgressMessage = searchInProgressMessage;
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
     * Sets the total span.
     * 
     * @param totalSpan
     *            the new total span
     */
    public void setTotalSpan(SpanElement totalSpan) {
        this._totalSpan = totalSpan;
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
     * Sets the workflow query table.
     * 
     * @param workflowQueryTable
     *            the new workflow query table
     */
    public void setWorkflowQueryTable(WorkflowQueryTable workflowQueryTable) {
        this._workflowQueryTable = workflowQueryTable;
    }

    /**
     * Updates the pager with the given data.
     * 
     * @param data
     */
    protected void updatePager(WorkflowQueryResultSetBean data) {
        int numPages = ((int) (data.get_totalResults() / data.getItemsPerPage()))
                + (data.get_totalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this._pager.setNumPages(numPages);
        this._pager.setPage(thisPage);
        if (numPages > 1)
            this._pager.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getQueries().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.get_totalResults());
        this._rangeSpan.setInnerText(rangeText);
        this._totalSpan.setInnerText(totalText);
    }

    /**
     * Updates the table of deployments with the given data.
     * 
     * @param data
     */
    protected void updateTable(WorkflowQueryResultSetBean data) {
        this._workflowQueryTable.clear();
        this._searchInProgressMessage.setVisible(false);
        if (data.getQueries().size() > 0) {
            for (WorkflowQuerySummaryBean deploymentSummaryBean : data.getQueries()) {
                this._workflowQueryTable.addRow(deploymentSummaryBean);
            }
            this._workflowQueryTable.setVisible(true);
        } else {
            this._noDataMessage.setVisible(true);
        }
    }
}
