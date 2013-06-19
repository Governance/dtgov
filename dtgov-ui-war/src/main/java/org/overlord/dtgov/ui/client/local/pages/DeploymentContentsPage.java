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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Anchor;

/**
 * The "Deployment Contents" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentContents.html#page")
@Page(path="deploymentContents")
@Dependent
public class DeploymentContentsPage extends AbstractPage {

    @PageState
    private String uuid;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    TransitionAnchor<DashboardPage> backToDashboard;
    @Inject @DataField("back-to-deployments")
    TransitionAnchor<DeploymentsPage> backToDeployments;
    @Inject @DataField("back-to-deployment")
    Anchor backToDeployment;
    @Inject
    TransitionTo<DeploymentDetailsPage> goToDeploymentDetails;

    /**
     * Constructor.
     */
    public DeploymentContentsPage() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        // TODO enhance TransitionAnchor so that its state can be dynamically set (contribute back to Errai)
        backToDeployment.setHref(createPageHref("deploymentDetails", "uuid", uuid));
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
