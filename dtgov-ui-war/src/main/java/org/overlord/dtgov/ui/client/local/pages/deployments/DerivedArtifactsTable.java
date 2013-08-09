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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.common.WidgetTable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of derived artifacts.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class DerivedArtifactsTable extends WidgetTable {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected ConfigurationService configService;

    /**
     * Constructor.
     */
    public DerivedArtifactsTable() {
    }

    /**
     * @see org.overlord.sramp.ui.client.local.widgets.common.WidgetTable#init()
     */
    @Override
    protected void init() {
        super.init();
        getElement().setClassName("table table-striped table-condensed table-hover"); //$NON-NLS-1$
    }

    @PostConstruct
    protected void postConstruct() {
        setColumnLabels(i18n.format("derived-artifacts.name"), i18n.format("derived-artifacts.type")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Adds a single row to the table.
     * @param summaryBean
     */
    public void addRow(final DerivedArtifactSummaryBean summaryBean) {
        int rowIdx = this.rowElements.size();

        Anchor name = new Anchor();
        String href = configService.getUiConfig().createSrampUiUrl("details", "uuid", summaryBean.getUuid()); //$NON-NLS-1$ //$NON-NLS-2$
        name.setHref(href);
        name.setText(summaryBean.getName());
        name.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
            }
        });
        InlineLabel type = new InlineLabel(summaryBean.getType());

        add(rowIdx, 0, name);
        add(rowIdx, 1, type);
    }

}
