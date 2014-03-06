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
import javax.enterprise.inject.Instance;
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
import org.overlord.dtgov.ui.client.local.pages.deployments.AddDeploymentDialog;
import org.overlord.dtgov.ui.client.local.pages.deployments.DeploymentFilters;
import org.overlord.dtgov.ui.client.local.pages.deployments.DeploymentTable;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateKeys;
import org.overlord.dtgov.ui.client.local.services.ApplicationStateService;
import org.overlord.dtgov.ui.client.local.services.DeploymentsRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The deployment lifecycle page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deployments.html#page")
@Page(path="deployments")
@Dependent
public class DeploymentsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected DeploymentsRpcService deploymentsService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected ApplicationStateService stateService;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;

    @Inject @DataField("deployments-filter-sidebar")
    protected DeploymentFilters filtersPanel;
    @Inject @DataField("deployment-search-box")
    protected TextBox searchBox;

    @Inject @DataField("btn-refresh")
    protected Button refreshButton;
    @Inject @DataField("btn-add")
    protected Button addButton;
    @Inject
    protected Instance<AddDeploymentDialog> addDeploymentDialog;

    @Inject @DataField("deployments-none")
    protected HtmlSnippet noDataMessage;
    @Inject @DataField("deployments-searching")
    protected HtmlSnippet searchInProgressMessage;
    @Inject @DataField("deployments-table")
    protected DeploymentTable deploymentsTable;

    @Inject @DataField("deployments-pager")
    protected Pager pager;
    @DataField("deployments-range")
    protected SpanElement rangeSpan = Document.get().createSpanElement();
    @DataField("deployments-total")
    protected SpanElement totalSpan = Document.get().createSpanElement();

    private int currentPage = 1;

    /**
     * Constructor.
     */
    public DeploymentsPage() {
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        filtersPanel.addValueChangeHandler(new ValueChangeHandler<DeploymentsFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<DeploymentsFilterBean> event) {
                doSearch();
            }
        });
        searchBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                doSearch();
            }
        });
        pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doSearch(event.getValue());
            }
        });
        deploymentsTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doSearch(currentPage);
            }
        });

        // Hide column 1 when in mobile mode.
        deploymentsTable.setColumnClasses(1, "desktop-only"); //$NON-NLS-1$

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
     * Event handler that fires when the user clicks the Add Deployment button.
     * @param event
     */
    @EventHandler("btn-add")
    public void onAddClick(ClickEvent event) {
        addDeploymentDialog.get().show();
    }

    /**
     * Kick off a search at this point so that we show some data in the UI.
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        // Refresh the filters
        filtersPanel.refresh();

        DeploymentsFilterBean filterBean = (DeploymentsFilterBean) stateService.get(ApplicationStateKeys.DEPLOYMENTS_FILTER, new DeploymentsFilterBean());
        String searchText = (String) stateService.get(ApplicationStateKeys.DEPLOYMENTS_SEARCH_TEXT, ""); //$NON-NLS-1$
        Integer page = (Integer) stateService.get(ApplicationStateKeys.DEPLOYMENTS_PAGE, 1);
        SortColumn sortColumn = (SortColumn) stateService.get(ApplicationStateKeys.DEPLOYMENTS_SORT_COLUMN, this.deploymentsTable.getDefaultSortColumn());

        this.filtersPanel.setValue(filterBean);
        this.searchBox.setValue(searchText);
        this.deploymentsTable.sortBy(sortColumn.columnId, sortColumn.ascending);
        
        // Kick off a search
        doSearch(page);
    }

    /**
     * Search for artifacts based on the current filter settings and search text.
     */
    protected void doSearch() {
        doSearch(1);
    }

    /**
     * Search for deployments based on the current filter settings.
     * @param page
     */
    protected void doSearch(int page) {
        onSearchStarting();
        currentPage = page;

        final DeploymentsFilterBean filterBean = filtersPanel.getValue();
        final String searchText = this.searchBox.getValue();
        final SortColumn currentSortColumn = this.deploymentsTable.getCurrentSortColumn();
        
        stateService.put(ApplicationStateKeys.DEPLOYMENTS_FILTER, filterBean);
        stateService.put(ApplicationStateKeys.DEPLOYMENTS_SEARCH_TEXT, searchText);
        stateService.put(ApplicationStateKeys.DEPLOYMENTS_PAGE, currentPage);
        stateService.put(ApplicationStateKeys.DEPLOYMENTS_SORT_COLUMN, currentSortColumn);

        deploymentsService.search(filterBean, searchText, page, currentSortColumn.columnId, currentSortColumn.ascending,
                new IRpcServiceInvocationHandler<DeploymentResultSetBean>() {
            @Override
            public void onReturn(DeploymentResultSetBean data) {
                updateTable(data);
                updatePager(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("deployments.error-loading"), error); //$NON-NLS-1$
                noDataMessage.setVisible(true);
                searchInProgressMessage.setVisible(false);
            }
        });
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onSearchStarting() {
        this.pager.setVisible(false);
        this.searchInProgressMessage.setVisible(true);
        this.deploymentsTable.setVisible(false);
        this.noDataMessage.setVisible(false);
        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Updates the table of deployments with the given data.
     * @param data
     */
    protected void updateTable(DeploymentResultSetBean data) {
        this.deploymentsTable.clear();
        this.searchInProgressMessage.setVisible(false);
        if (data.getDeployments().size() > 0) {
            for (DeploymentSummaryBean deploymentSummaryBean : data.getDeployments()) {
                this.deploymentsTable.addRow(deploymentSummaryBean);
            }
            this.deploymentsTable.setVisible(true);
        } else {
            this.noDataMessage.setVisible(true);
        }
    }

    /**
     * Updates the pager with the given data.
     * @param data
     */
    protected void updatePager(DeploymentResultSetBean data) {
        int numPages = ((int) (data.getTotalResults() / data.getItemsPerPage())) + (data.getTotalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this.pager.setNumPages(numPages);
        this.pager.setPage(thisPage);
        if (numPages > 1)
            this.pager.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getDeployments().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan.setInnerText(rangeText);
        this.totalSpan.setInnerText(totalText);
    }

}
