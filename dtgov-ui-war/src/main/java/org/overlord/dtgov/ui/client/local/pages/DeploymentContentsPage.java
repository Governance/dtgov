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
import org.overlord.dtgov.ui.client.local.pages.deployments.ExpandedArtifactList;
import org.overlord.dtgov.ui.client.local.services.DeploymentsRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingListCountConverter;
import org.overlord.dtgov.ui.client.local.util.DataBindingParentheticalConverter;
import org.overlord.dtgov.ui.client.shared.beans.ExpandedArtifactsBean;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The "Deployment Contents" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentContents.html#page")
@Page(path="deploymentContents")
@Dependent
public class DeploymentContentsPage extends AbstractPage {

    @Inject
    protected DeploymentsRpcService deploymentsService;
    @Inject
    protected NotificationService notificationService;

    @PageState
    private String uuid;

    @Inject @AutoBound
    protected DataBinder<ExpandedArtifactsBean> deploymentContentsBean;

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

    // Summary information
    @Inject @DataField("expanded-artifact-count") @Bound(property="expandedArtifacts", converter=DataBindingListCountConverter.class)
    InlineLabel artifactCount;
    @Inject @DataField("deployment-name-2") @Bound(property="artifactName")
    InlineLabel name2;

    // Expanded artifact list
    @Inject @DataField("deployment-contents-items") @Bound
    ExpandedArtifactList expandedArtifacts;

    @Inject @DataField("deployment-contents-loading-spinner")
    protected HtmlSnippet loading;
    protected Element pageContent;

    /**
     * Constructor.
     */
    public DeploymentContentsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "deployment-contents-content-wrapper");
        pageContent.addClassName("hide");
    }

    /**
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide");
        loading.getElement().removeClassName("hide");
        deploymentsService.listExpandedArtifacts(uuid, new IRpcServiceInvocationHandler<ExpandedArtifactsBean>() {
            @Override
            public void onReturn(ExpandedArtifactsBean data) {
                update(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification("Error Getting Expanded Artifacts", error);
            }
        });
        backToDeployment.setHref(createPageHref("deploymentDetails", "uuid", uuid));
    }

    /**
     * Called when the history is loaded.
     * @param deployment
     */
    protected void update(ExpandedArtifactsBean bean) {
        this.deploymentContentsBean.setModel(bean, InitialState.FROM_MODEL);
        loading.getElement().addClassName("hide");
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
