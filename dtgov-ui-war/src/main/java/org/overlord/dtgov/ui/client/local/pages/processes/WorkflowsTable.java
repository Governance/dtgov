/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.dtgov.ui.client.local.pages.processes;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.beans.UiConfiguration;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.shared.beans.Workflow;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Custom table used in the Processes page. It stores all the workflows used in
 * dtgov
 *
 * @author David Virgil Naranjo
 */
@Dependent
public class WorkflowsTable extends TemplatedWidgetTable implements HasValue<List<Workflow>> {

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _config service. */
    @Inject
    private ConfigurationService _configService;

    private List<Workflow> _workflows;

    private String _srampUIBaseUrl;

    /**
     * Constructor.
     */
    public WorkflowsTable() {
    }

    /**
     * Post construct.
     */
    @PostConstruct
    public void postConstruct() {
        UiConfiguration uiConfig = _configService.getUiConfig();
        _srampUIBaseUrl = uiConfig.getSrampUiUrlBase();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#
     * addValueChangeHandler
     * (com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Workflow>> handler) {
        return super.addHandler(handler, ValueChangeEvent.getType());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<Workflow> getValue() {
        return _workflows;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<Workflow> value) {
        setValue(value, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object,
     * boolean)
     */
    @Override
    public void setValue(List<Workflow> value, boolean fireEvents) {
        _workflows = value;
        clear();
        refresh();

    }

    /**
     * Refresh the display with the current value.
     */
    public void refresh() {
        if (_workflows != null && !_workflows.isEmpty()) {
            for (Workflow workflow : _workflows) {
                addRow(workflow);
            }
        }
    }

    /**
     * Adds a single row to the table.
     *
     * @param workflow
     *            the workflow
     */
    public void addRow(final Workflow workflow) {
        int rowIdx = this.rowElements.size();
        Anchor artifact_name = new Anchor();
        artifact_name.setText(workflow.getName());
        String url = _srampUIBaseUrl;
        if (!url.endsWith("/")) { //$NON-NLS-1$
            url += "/"; //$NON-NLS-1$
        }
        url += "#details;uuid=" + workflow.getUuid(); //$NON-NLS-1$
        artifact_name.setHref(url);

        InlineLabel description = new InlineLabel(workflow.getDescription());

        add(rowIdx, 0, artifact_name);
        add(rowIdx, 1, description);
    }

    /**
     * Gets the _i18n.
     *
     * @return the _i18n
     */
    public ClientMessages getI18n() {
        return _i18n;
    }

    /**
     * Sets the _i18n.
     *
     * @param i18n
     *            the new _i18n
     */
    public void setI18n(ClientMessages i18n) {
        this._i18n = i18n;
    }

    /**
     * Gets the _config service.
     *
     * @return the _config service
     */
    public ConfigurationService getConfigService() {
        return _configService;
    }

    /**
     * Sets the _config service.
     *
     * @param configService
     *            the new _config service
     */
    public void setConfigService(ConfigurationService configService) {
        this._configService = configService;
    }

    /**
     * Gets the workflows.
     *
     * @return the workflows
     */
    public List<Workflow> getWorkflows() {
        return _workflows;
    }

    /**
     * Sets the workflows.
     *
     * @param workflows
     *            the new workflows
     */
    public void setWorkflows(List<Workflow> workflows) {
        this._workflows = workflows;
    }

    /**
     * Gets the sramp ui base url.
     *
     * @return the sramp ui base url
     */
    public String getSrampUIBaseUrl() {
        return _srampUIBaseUrl;
    }

    /**
     * Sets the sramp ui base url.
     *
     * @param srampUIBaseUrl
     *            the new sramp ui base url
     */
    public void setSrampUIBaseUrl(String srampUIBaseUrl) {
        this._srampUIBaseUrl = srampUIBaseUrl;
    }

}
