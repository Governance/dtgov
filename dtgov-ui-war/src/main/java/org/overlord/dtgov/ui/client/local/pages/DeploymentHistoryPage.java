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
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.beans.DeploymentHistoryFilterBean;
import org.overlord.dtgov.ui.client.local.pages.deployments.DeploymentHistoryFilters;
import org.overlord.dtgov.ui.client.local.pages.deployments.HistoryEventsList;
import org.overlord.dtgov.ui.client.local.services.HistoryRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingParentheticalConverter;
import org.overlord.dtgov.ui.client.shared.beans.ArtifactHistoryBean;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The "Deployment History" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentHistory.html#page")
@Page(path="deploymentHistory")
@Dependent
public class DeploymentHistoryPage extends AbstractPage {

    @Inject
    protected HistoryRpcService historyService;
    @Inject
    protected NotificationService notificationService;

    @PageState
    private String uuid;

    @Inject @AutoBound
    protected DataBinder<ArtifactHistoryBean> historyBean;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;
    @Inject @DataField("back-to-deployments")
    TransitionAnchor<DeploymentsPage> backToDeployments;
    @Inject @DataField("back-to-deployment")
    Anchor backToDeployment;
    @Inject
    TransitionTo<DeploymentDetailsPage> goToDeploymentDetails;

    // Deployment header
    @Inject @DataField("deployment-name") @Bound(property="artifactName")
    InlineLabel name;
    @Inject @DataField("deployment-type") @Bound(property="artifactType", converter=DataBindingParentheticalConverter.class)
    InlineLabel type;
    @Inject @DataField("deployment-version") @Bound(property="artifactVersion", converter=DataBindingParentheticalConverter.class)
    InlineLabel deploymentVersion;

    // History summary information
    @Inject @DataField("deployment-name-2") @Bound(property="artifactName")
    InlineLabel name2;
    @Inject @DataField("deployment-version-2") @Bound(property="artifactVersion", converter=DataBindingParentheticalConverter.class)
    InlineLabel deploymentVersion2;

    // History event list
    @Inject @DataField("deployment-history-items") @Bound
    HistoryEventsList events;

    // History Filters
    @Inject @DataField("deployment-history-filters")
    protected DeploymentHistoryFilters filtersPanel;

    @Inject @DataField("deployment-history-loading-spinner")
    protected HtmlSnippet historyLoading;
    protected Element pageContent;

    /**
     * Constructor.
     */
    public DeploymentHistoryPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "deployment-history-content-wrapper");
        pageContent.addClassName("hide");
        filtersPanel.addValueChangeHandler(new ValueChangeHandler<DeploymentHistoryFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<DeploymentHistoryFilterBean> event) {
                events.setFilters(event.getValue());
                events.render();
            }
        });
    }

    /**
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide");
        historyLoading.getElement().removeClassName("hide");
        historyService.listEvents(uuid, new IRpcServiceInvocationHandler<ArtifactHistoryBean>() {
            @Override
            public void onReturn(ArtifactHistoryBean data) {
                update(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification("Error Getting Deployment History.", error);
            }
        });
        backToDeployment.setHref(createPageHref("deploymentDetails", "uuid", uuid));
    }

    /**
     * Called when the history is loaded.
     * @param deployment
     */
    protected void update(ArtifactHistoryBean bean) {
        this.historyBean.setModel(bean, InitialState.FROM_MODEL);
        historyLoading.getElement().addClassName("hide");
        pageContent.removeClassName("hide");
    }

    /**
     * Called when the user clicks the back-to-deployment breadcrumb.
     * @param event
     */
    @EventHandler("back-to-deployment")
    protected void onBackToDeployment(ClickEvent event) {
        Multimap<String, String> state = HashMultimap.create();
        state.put("uuid", uuid);
        goToDeploymentDetails.go(state);
        event.stopPropagation();
        event.preventDefault();
    }

}
