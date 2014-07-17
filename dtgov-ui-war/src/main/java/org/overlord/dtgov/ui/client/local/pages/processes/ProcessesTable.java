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

import org.overlord.commons.gwt.client.local.widgets.SortableTemplatedWidgetTable;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.beans.UiConfiguration;
import org.overlord.dtgov.ui.client.local.events.StopProcessEvent;
import org.overlord.dtgov.ui.client.local.events.StopProcessEvent.HasStopProcessHandlers;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.shared.beans.ProcessBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessStatusEnum;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Custom table used in the Processes page. It stores all the processes that are
 * in status RUNNING, FINALIZED and STOPPED
 *
 * @author David Virgil Naranjo
 */
@Dependent
public class ProcessesTable extends SortableTemplatedWidgetTable implements HasValue<List<ProcessBean>>, HasStopProcessHandlers {


    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _config service. */
    @Inject
    private ConfigurationService configService;


    private List<ProcessBean> processes;

    private String srampUIBaseUrl;

    /**
     * Constructor.
     */
    public ProcessesTable() {
    }

    /**
     * Post construct.
     */
    @PostConstruct
    public void postConstruct() {
        UiConfiguration uiConfig = configService.getUiConfig();
        srampUIBaseUrl = uiConfig.getSrampUiUrlBase();
    }



    /**
     * Gets the default sort column.
     *
     * @return the default sort column
     * @see org.overlord.sramp.ui.client.local.widgets.common.SortableTemplatedWidgetTable#getDefaultSortColumn()
     */
    @Override
    public SortColumn getDefaultSortColumn() {
        SortColumn sortColumn = new SortColumn();
        sortColumn.columnId = ProcessConstants.SORT_COL_ID_PROCESS_ARTIFACT_NAME;
        sortColumn.ascending = true;
        return sortColumn;
    }

    /**
     * Configure column sorting.
     *
     * @see org.overlord.monitoring.ui.client.local.widgets.common.SortableTemplatedWidgetTable#configureColumnSorting()
     */
    @Override
    protected void configureColumnSorting() {
        setColumnSortable(0, ProcessConstants.SORT_COL_ID_PROCESS_ARTIFACT_NAME);
        setColumnSortable(1, ProcessConstants.SORT_COL_ID_PROCESS_WORKFLOW_NAME);
        setColumnSortable(2, ProcessConstants.SORT_COL_ID_PROCESS_STATUS);
        sortBy(ProcessConstants.SORT_COL_ID_PROCESS_ARTIFACT_NAME, true);
    }

    /**
     * Adds a single row to the table.
     *
     * @param processBean
     *            the process bean
     */
    public void addRow(final ProcessBean processBean) {
        int rowIdx = this.rowElements.size();
        Anchor artifact_name = new Anchor();
        artifact_name.setText(processBean.getArtifactName());
        String url = srampUIBaseUrl;
        if (!url.endsWith("/")) { //$NON-NLS-1$
            url += "/"; //$NON-NLS-1$
        }
        url += "#details;uuid=" + processBean.getArtifactId(); //$NON-NLS-1$
        artifact_name.setHref(url);

        InlineLabel workflow = new InlineLabel(processBean.getWorkflow());
        InlineLabel status = new InlineLabel(processBean.getStatus().name());
        FlowPanel actions = new FlowPanel();

        if (processBean.getStatus().equals(ProcessStatusEnum.RUNNING)) {
            InlineLabel stopAction = new InlineLabel();
            stopAction.setStyleName("process-icon", true); //$NON-NLS-1$
            stopAction.setStyleName("process-abort-icon", true); //$NON-NLS-1$
            stopAction.setTitle(_i18n.format("abort")); //$NON-NLS-1$
            actions.add(stopAction);
            stopAction.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    StopProcessEvent.fire(ProcessesTable.this, processBean);
                }
            });
        }
        add(rowIdx, 0, artifact_name);
        add(rowIdx, 1, workflow);
        add(rowIdx, 2, status);
        Element row = add(rowIdx, 3, actions);
        setStyleName(row, "actions", true); //$NON-NLS-1$
    }

    /**
     * Gets the i18n.
     *
     * @return the i18n
     */
    public ClientMessages getI18n() {
        return _i18n;
    }

    /**
     * Sets the i18n.
     *
     * @param i18n
     *            the new i18n
     */
    public void setI18n(ClientMessages i18n) {
        this._i18n = i18n;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#
     * addValueChangeHandler
     * (com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ProcessBean>> handler) {
        return super.addHandler(handler, ValueChangeEvent.getType());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.local.events.StopProcessEvent.
     * HasStopProcessHandlers
     * #addStopProcessHandler(org.overlord.dtgov.ui.client.
     * local.events.StopProcessEvent.Handler)
     */
    @Override
    public HandlerRegistration addStopProcessHandler(org.overlord.dtgov.ui.client.local.events.StopProcessEvent.Handler handler) {
        return super.addHandler(handler, StopProcessEvent.getType());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<ProcessBean> getValue() {
        return processes;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<ProcessBean> value) {
        setValue(value, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object,
     * boolean)
     */
    @Override
    public void setValue(List<ProcessBean> value, boolean fireEvents) {
        processes = value;
        clear();
        refresh();
    }

    /**
     * Refresh the display with the current value.
     */
    public void refresh() {
        if (processes != null && !processes.isEmpty()) {
            for (ProcessBean processBean : processes) {
                addRow(processBean);
            }
        }
    }



}