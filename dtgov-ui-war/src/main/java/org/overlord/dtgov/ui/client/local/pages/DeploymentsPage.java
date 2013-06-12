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
import org.overlord.dtgov.ui.client.local.pages.deployments.DeploymentFilters;
import org.overlord.dtgov.ui.client.local.pages.deployments.DeploymentTable;
import org.overlord.dtgov.ui.client.local.services.DeploymentsRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.Pager;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
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
    protected DeploymentsRpcService deploymentsService;
    @Inject
    protected NotificationService notificationService;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;

    @Inject @DataField("deployments-filter-sidebar")
    protected DeploymentFilters filtersPanel;
    @Inject @DataField("deployment-search-box")
    protected TextBox searchBox;

    @Inject @DataField("btn-refresh")
    protected Anchor refreshButton;

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

        // Hide column 1 when in mobile mode.
        deploymentsTable.setColumnClasses(1, "desktop-only");

        this.rangeSpan.setInnerText("?");
        this.totalSpan.setInnerText("?");
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
        // Kick off an artifact search
        doSearch();
        // Refresh the artifact filters
        filtersPanel.refresh();
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
        deploymentsService.search(filtersPanel.getValue(), searchBox.getValue(), page, new IRpcServiceInvocationHandler<DeploymentResultSetBean>() {
            @Override
            public void onReturn(DeploymentResultSetBean data) {
                updateTable(data);
                updatePager(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification("Error loading deployments.", error);
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
        this.rangeSpan.setInnerText("?");
        this.totalSpan.setInnerText("?");
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
        String rangeText = "" + startIndex + "-" + endIndex;
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan.setInnerText(rangeText);
        this.totalSpan.setInnerText(totalText);
    }

}
