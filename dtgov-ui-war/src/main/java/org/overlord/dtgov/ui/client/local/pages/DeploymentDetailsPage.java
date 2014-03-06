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
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.local.services.DeploymentsRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingDateConverter;
import org.overlord.dtgov.ui.client.local.util.DataBindingParentheticalConverter;
import org.overlord.dtgov.ui.client.local.widgets.common.DescriptionInlineLabel;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentBean;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The Deployment Details page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentDetails.html#page")
@Page(path="deploymentDetails")
@Dependent
public class DeploymentDetailsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected ConfigurationService configurationService;
    @Inject
    protected DeploymentsRpcService deploymentsService;
    @Inject
    protected NotificationService notificationService;

    @PageState
    private String uuid;

    @Inject @AutoBound
    protected DataBinder<DeploymentBean> deployment;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;
    @Inject @DataField("back-to-deployments")
    TransitionAnchor<DeploymentsPage> backToDeployments;

    // Deployment header
    @Inject @DataField("deployment-name") @Bound(property="name")
    InlineLabel name;
    @Inject @DataField("deployment-type") @Bound(property="type", converter=DataBindingParentheticalConverter.class)
    InlineLabel type;
    @Inject @DataField("deployment-version") @Bound(property="version", converter=DataBindingParentheticalConverter.class)
    InlineLabel deploymentVersion;

    // Properties
    @Inject @DataField @Bound(property="uuid")
    InlineLabel deploymentUuid;
    // Properties
    @Inject @DataField @Bound(property="stage")
    InlineLabel stage;
    @Inject @DataField @Bound(property="version")
    InlineLabel version;
    @Inject @DataField @Bound(property="type")
    InlineLabel deploymentType;
    @Inject @DataField @Bound(property="initiatedDate", converter=DataBindingDateConverter.class)
    InlineLabel initiatedDate;
    @Inject @DataField @Bound(property="initiatedBy")
    InlineLabel initiatedBy;

    protected Element mavenPropsWrapper;
    @Inject @DataField @Bound(property="mavenGroup")
    InlineLabel mavenGroupId;
    @Inject @DataField @Bound(property="mavenId")
    InlineLabel mavenArtifactId;
    @Inject @DataField @Bound(property="mavenVersion")
    InlineLabel mavenVersion;

    @Inject @DataField("deployment-description") @Bound(property="description")
    DescriptionInlineLabel description;

    @Inject @DataField("deployment-details-loading-spinner")
    protected HtmlSnippet deploymentLoading;
    protected Element pageContent;

    // Navigation panels
    @Inject @DataField("to-deploymentHistory-page")
    Anchor toDeploymentHistory;
    @Inject
    TransitionTo<DeploymentHistoryPage> goToDeploymentHistory;
    @Inject @DataField("to-deploymentContents-page")
    Anchor toDeploymentContents;
    @Inject
    TransitionTo<DeploymentContentsPage> goToDeploymentContents;
    @Inject @DataField("to-sramp")
    Anchor toSramp;

    /**
     * Constructor.
     */
    public DeploymentDetailsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "deployment-details-content-wrapper"); //$NON-NLS-1$
        pageContent.addClassName("hide"); //$NON-NLS-1$
        mavenPropsWrapper = DOMUtil.findElementById(getElement(), "maven-details"); //$NON-NLS-1$
        deployment.addPropertyChangeHandler(new PropertyChangeHandler<Object>() {
            @Override
            public void onPropertyChange(PropertyChangeEvent<Object> event) {
                pushModelToServer();
            }
        });

        Element navPanel = DOMUtil.findElementById(getElement(), "deployment-nav-history"); //$NON-NLS-1$
        DOMUtil.addClickHandlerToElement(navPanel, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onDeploymentHistoryNav(event);
            }
        });
        navPanel = DOMUtil.findElementById(getElement(), "deployment-nav-contents"); //$NON-NLS-1$
        DOMUtil.addClickHandlerToElement(navPanel, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onDeploymentContentsNav(event);
            }
        });
        navPanel = DOMUtil.findElementById(getElement(), "deployment-nav-browse"); //$NON-NLS-1$
        DOMUtil.addClickHandlerToElement(navPanel, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onBrowseToSrampNav(event);
            }
        });
    }

    /**
     * Sends the model back up to the server (saves local changes).
     */
    protected void pushModelToServer() {
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("deployment-details.updating-deployment"), //$NON-NLS-1$
                i18n.format("deployment-details.updating-deployment-msg", deployment.getModel().getName())); //$NON-NLS-1$
        deploymentsService.update(deployment.getModel(), new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("deployment-details.update-complete"), //$NON-NLS-1$
                        i18n.format("deployment-details.update-complete-msg", deployment.getModel().getName())); //$NON-NLS-1$
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("deployment-details.error-updating"), //$NON-NLS-1$
                        error);
            }
        });
    }

    /**
     * @see org.overlord.sramp.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide"); //$NON-NLS-1$
        deploymentLoading.getElement().removeClassName("hide"); //$NON-NLS-1$
        deploymentsService.get(uuid, new IRpcServiceInvocationHandler<DeploymentBean>() {
            @Override
            public void onReturn(DeploymentBean data) {
                updateMetaData(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("deployment-details.error-fetching-details"), error); //$NON-NLS-1$
            }
        });

        toDeploymentHistory.setHref(createPageHref("deploymentDetails", "uuid", uuid)); //$NON-NLS-1$ //$NON-NLS-2$
        toDeploymentContents.setHref(createPageHref("deploymentContents", "uuid", uuid)); //$NON-NLS-1$ //$NON-NLS-2$
        toSramp.setHref(configurationService.getUiConfig().createSrampUiUrl("details", "uuid", uuid)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Called when the deployment is loaded.
     * @param deployment
     */
    protected void updateMetaData(DeploymentBean deployment) {
        this.deployment.setModel(deployment, InitialState.FROM_MODEL);
        deploymentLoading.getElement().addClassName("hide"); //$NON-NLS-1$
        if (deployment.hasMavenInfo()) {
            mavenPropsWrapper.removeClassName("hide"); //$NON-NLS-1$
        } else {
            mavenPropsWrapper.addClassName("hide"); //$NON-NLS-1$
        }
        pageContent.removeClassName("hide"); //$NON-NLS-1$
    }

    /**
     * Called when the user clicks the deployment history nav panel.
     * @param event
     */
    @EventHandler("to-deploymentHistory-page")
    protected void onDeploymentHistoryNav(ClickEvent event) {
        Multimap<String, String> state = HashMultimap.create();
        state.put("uuid", uuid); //$NON-NLS-1$
        goToDeploymentHistory.go(state);
        if (event != null) {
            event.stopPropagation();
            event.preventDefault();
        }
    }

    /**
     * Called when the user clicks the deployment contents nav panel.
     * @param event
     */
    @EventHandler("to-deploymentContents-page")
    protected void onDeploymentContentsNav(ClickEvent event) {
        Multimap<String, String> state = HashMultimap.create();
        state.put("uuid", uuid); //$NON-NLS-1$
        goToDeploymentContents.go(state);
        if (event != null) {
            event.stopPropagation();
            event.preventDefault();
        }
    }

    /**
     * Called when the user clicks the browse to s-ramp nav panel.
     * @param event
     */
    @EventHandler("to-sramp")
    protected void onBrowseToSrampNav(ClickEvent event) {
        String srampUrl = configurationService.getUiConfig().createSrampUiUrl("details", "uuid", uuid); //$NON-NLS-1$ //$NON-NLS-2$
        Window.Location.assign(srampUrl);
        if (event != null) {
            event.stopPropagation();
            event.preventDefault();
        }
    }

}
