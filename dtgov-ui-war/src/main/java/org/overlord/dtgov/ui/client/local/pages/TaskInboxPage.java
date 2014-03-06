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

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.events.TableSortEvent;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.commons.gwt.client.local.widgets.Pager;
import org.overlord.commons.gwt.client.local.widgets.SortableTemplatedWidgetTable.SortColumn;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.taskInbox.TaskInboxFilters;
import org.overlord.dtgov.ui.client.local.pages.taskInbox.TasksTable;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateKeys;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.TaskInboxRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskSummaryBean;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;

/**
 * The Task Inbox page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/taskInbox.html#page")
@Page(path="taskInbox")
@Dependent
public class TaskInboxPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected TaskInboxRpcService inboxService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected ApplicationStateService stateService;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;

    @Inject @DataField("tasks-filter-sidebar")
    protected TaskInboxFilters filtersPanel;

    @Inject @DataField("btn-refresh")
    protected Anchor refreshButton;

    @Inject @DataField("tasks-none")
    protected HtmlSnippet noDataMessage;
    @Inject @DataField("tasks-searching")
    protected HtmlSnippet searchInProgressMessage;
    @Inject @DataField("tasks-table")
    protected TasksTable tasksTable;

    @Inject @DataField("tasks-pager")
    protected Pager pager;
    @DataField("tasks-range")
    protected SpanElement rangeSpan = Document.get().createSpanElement();
    @DataField("tasks-total")
    protected SpanElement totalSpan = Document.get().createSpanElement();

    private int currentPage = 1;

    /**
     * Constructor.
     */
    public TaskInboxPage() {
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        filtersPanel.addValueChangeHandler(new ValueChangeHandler<TaskInboxFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<TaskInboxFilterBean> event) {
                doSearch();
            }
        });
        pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doSearch(event.getValue());
            }
        });
        this.tasksTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doSearch(currentPage);
            }
        });

        // Hide columns 2-3 when in mobile mode.
        tasksTable.setColumnClasses(2, "desktop-only"); //$NON-NLS-1$
        tasksTable.setColumnClasses(3, "desktop-only"); //$NON-NLS-1$

        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * @param event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doSearch(currentPage);
    }

    /**
     * Kick off a search at this point so that we show some data in the UI.
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        // Refresh the filters
        filtersPanel.refresh();

        TaskInboxFilterBean filterBean = (TaskInboxFilterBean) stateService.get(ApplicationStateKeys.TASK_INBOX_FILTER, new TaskInboxFilterBean());
        Integer page = (Integer) stateService.get(ApplicationStateKeys.TASK_INBOX_PAGE, 1);
        SortColumn sortColumn = (SortColumn) stateService.get(ApplicationStateKeys.TASK_INBOX_SORT_COLUMN, tasksTable.getDefaultSortColumn());

        this.filtersPanel.setValue(filterBean);
        this.tasksTable.sortBy(sortColumn.columnId, sortColumn.ascending);

        // Kick off a search
        doSearch(page);
    }

    /**
     * Search for tasks based on the current filter settings and search text.
     */
    protected void doSearch() {
        doSearch(1);
    }

    /**
     * Search for tasks based on the current filter settings.
     * @param page
     */
    protected void doSearch(int page) {
        onSearchStarting();
        currentPage = page;

        final TaskInboxFilterBean filterBean = filtersPanel.getValue();
        final SortColumn currentSortColumn = tasksTable.getCurrentSortColumn();

        stateService.put(ApplicationStateKeys.TASK_INBOX_FILTER, filterBean);
        stateService.put(ApplicationStateKeys.TASK_INBOX_PAGE, currentPage);
        stateService.put(ApplicationStateKeys.TASK_INBOX_SORT_COLUMN, currentSortColumn);

        inboxService.search(filterBean, page, currentSortColumn.columnId, currentSortColumn.ascending, 
                new IRpcServiceInvocationHandler<TaskInboxResultSetBean>() {
            @Override
            public void onReturn(TaskInboxResultSetBean data) {
                updateTasksTable(data);
                updatePager(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("task-inbox.error-loading"), error); //$NON-NLS-1$
                noDataMessage.setVisible(true);
                searchInProgressMessage.setVisible(false);
            }
        });
    }

    /**
     * Called when a new task search is kicked off.
     */
    protected void onSearchStarting() {
        this.pager.setVisible(false);
        this.searchInProgressMessage.setVisible(true);
        this.tasksTable.setVisible(false);
        this.noDataMessage.setVisible(false);
        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Updates the table of tasks with the given data.
     * @param data
     */
    protected void updateTasksTable(TaskInboxResultSetBean data) {
        this.tasksTable.clear();
        this.searchInProgressMessage.setVisible(false);
        if (data.getTasks().size() > 0) {
            for (TaskSummaryBean taskSummaryBean : data.getTasks()) {
                this.tasksTable.addRow(taskSummaryBean);
            }
            this.tasksTable.setVisible(true);
        } else {
            this.noDataMessage.setVisible(true);
        }
    }

    /**
     * Updates the pager with the given data.
     * @param data
     */
    protected void updatePager(TaskInboxResultSetBean data) {
        int numPages = ((int) (data.getTotalResults() / data.getItemsPerPage())) + (data.getTotalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this.pager.setNumPages(numPages);
        this.pager.setPage(thisPage);
        if (numPages > 1)
            this.pager.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getTasks().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan.setInnerText(rangeText);
        this.totalSpan.setInnerText(totalText);
    }

}
