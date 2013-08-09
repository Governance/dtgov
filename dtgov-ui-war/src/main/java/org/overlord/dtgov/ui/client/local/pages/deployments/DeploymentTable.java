/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.dtgov.ui.client.local.pages.deployments;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.DeploymentDetailsPage;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedWidgetTable;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of deployments.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class DeploymentTable extends TemplatedWidgetTable {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected TransitionAnchorFactory<DeploymentDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public DeploymentTable() {
    }

    /**
     * Adds a single row to the table.
     * @param deploymentSummaryBean
     */
    public void addRow(final DeploymentSummaryBean deploymentSummaryBean) {
        int rowIdx = this.rowElements.size();
        DateTimeFormat format = DateTimeFormat.getFormat(i18n.format("date-format")); //$NON-NLS-1$

        Anchor name = toDetailsPageLinkFactory.get("uuid", deploymentSummaryBean.getUuid()); //$NON-NLS-1$
        name.setText(deploymentSummaryBean.getName());
        InlineLabel type = new InlineLabel(deploymentSummaryBean.getType());
        InlineLabel initiatedOn = new InlineLabel(format.format(deploymentSummaryBean.getInitiatedDate()));

        add(rowIdx, 0, name);
        add(rowIdx, 1, type);
        add(rowIdx, 2, initiatedOn);
    }

}
