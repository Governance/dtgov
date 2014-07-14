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

import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;

/**
 * The "Dashboard" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/dashboard.html#page")
@Page(path="dashboard", role=DefaultPage.class)
@Dependent
public class DashboardPage extends AbstractPage {

    @Inject @DataField("to-taskInbox-page")
    private TransitionAnchor<TaskInboxPage> toTaskInboxPage;
    @Inject @DataField("to-deploymentLifecycle-page")
    private TransitionAnchor<DeploymentsPage> toDeploymentsPage;

    @Inject @DataField("to-adminQueries-page")
    private TransitionAnchor<WorkflowQueriesPage> toAdminQueriesPage;

    @Inject
    private ConfigurationService config;

    @Inject
    @DataField("to-targets-page")
    private TransitionAnchor<TargetsPage> toTargetsPage;

    @Inject
    @DataField("to-processes-page")
    private TransitionAnchor<ProcessesPage> toProcessesPage;

    /**
     * Constructor.
     */
    public DashboardPage() {
    }
    
    /**
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        Element adminDashElem = DOMUtil.findElementById(getElement(), "admin-dash"); //$NON-NLS-1$
        if (adminDashElem != null) {
            boolean admin = config.getUiConfig().isAdmin();
            if (admin) {
                adminDashElem.getStyle().clearDisplay();
            } else {
                adminDashElem.getStyle().setDisplay(Display.NONE);
            }
        }
    }
    
}
