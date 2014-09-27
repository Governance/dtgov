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

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.workflowQuery.WorkflowTypeListBox;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.WorkflowQueriesRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.ProcessStatusEnum;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.Workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;


/**
 * The processes filtersPanel sidebar. Whenever the user changes any of the
 * settings in the filter sidebar, a ValueChangeEvent will be fired.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/processes.html#processes-filter-sidebar")
@Dependent
public class ProcessesFilter extends Composite implements HasValue<ProcessesFilterBean> {

    /** The config service. */
    @Inject
    private ConfigurationService _configService;

    /** The current state. */
    private ProcessesFilterBean _currentState = new ProcessesFilterBean();


    @Inject
    private WorkflowQueriesRpcService workflowService;

    /** The _notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The workflow. */
    @Inject
    @DataField("workflow")
    private WorkflowTypeListBox _workflow;

    /** The name. */
    @Inject
    @DataField("artifact")
    private TextBox artifact;

    /** The name. */
    @Inject
    @DataField("status")
    private ProcessStatusListBox status;

    /** The clear filters. */
    @Inject
    @DataField("clearFilters")
    private Button _clearFilters;

    /**
     * Instantiates a new workflow queries filter.
     */
    public ProcessesFilter() {

    }

    /**
     * Called after construction and injection.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void postConstruct() {

        ClickHandler clearFilterHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setValue(new ProcessesFilterBean(), true);
            }
        };
        _clearFilters.addClickHandler(clearFilterHandler);

        @SuppressWarnings("rawtypes")
        ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                onFilterValueChange();
            }
        };
        _workflow.addValueChangeHandler(valueChangeHandler);
        artifact.addValueChangeHandler(valueChangeHandler);
        status.addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the value
     * @param fireEvents
     *            the fire events
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object,
     *      boolean)
     */
    @Override
    public void setValue(ProcessesFilterBean value, boolean fireEvents) {
        _workflow.setValue(value.getWorkflow() == null ? "" : value.getWorkflow()); //$NON-NLS-1$
        artifact.setValue(value.getArtifact() == null ? "" : value.getArtifact()); //$NON-NLS-1$
        status.setValue(value.getStatus() == null ? "" : value.getStatus().toString()); //$NON-NLS-1$
        ProcessesFilterBean oldState = this._currentState;
        _currentState = value;
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldState, _currentState);
        }
    }

    /**
     * Refresh any data in the filter panel.
     */
    public void refresh() {
        // Update the items in the deployment type drop-down
        this._workflow.clear();
        this.status.clear();
        workflowService.getWorkflowTypes(new IRpcServiceInvocationHandler<List<Workflow>>() {

            @Override
            public void onReturn(List<Workflow> workflowTypes) {
                for (Workflow entry : workflowTypes) {
                    _workflow.addItem(entry.getName(), entry.getName());
                }

            }

            @Override
            public void onError(Throwable error) {
                _notificationService.sendErrorNotification(
                        _i18n.format("workflowQuery.workflow.type.loading.error"), error); //$NON-NLS-1$
            }
        });
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        ProcessesFilterBean newState = new ProcessesFilterBean();
        newState.setWorkflow(_workflow.getValue());
        newState.setArtifact(artifact.getValue());
        if (status.getValue() != null) {
            newState.setStatus(ProcessStatusEnum.valueOf(status.getValue()));
        }
        ProcessesFilterBean oldState = this._currentState;
        this._currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, _currentState);
    }

    /**
     * Gets the value.
     *
     * @return the current filter settings
     */
    @Override
    public ProcessesFilterBean getValue() {
        return this._currentState;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new filter settings
     */
    @Override
    public void setValue(ProcessesFilterBean value) {
        setValue(value, false);
    }

    /**
     * Adds the value change handler.
     *
     * @param handler
     *            the handler
     * @return the handler registration
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProcessesFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Gets the config service.
     *
     * @return the config service
     */
    public ConfigurationService getConfigService() {
        return _configService;
    }

    /**
     * Sets the config service.
     *
     * @param configService
     *            the new config service
     */
    public void setConfigService(ConfigurationService configService) {
        this._configService = configService;
    }

    /**
     * Gets the current state.
     *
     * @return the current state
     */
    public ProcessesFilterBean getCurrentState() {
        return _currentState;
    }

    /**
     * Sets the current state.
     *
     * @param currentState
     *            the new current state
     */
    public void setCurrentState(ProcessesFilterBean currentState) {
        this._currentState = currentState;
    }

    /**
     * Gets the workflow.
     *
     * @return the workflow
     */
    public WorkflowTypeListBox getWorkflow() {
        return _workflow;
    }

    /**
     * Sets the workflow.
     *
     * @param workflow
     *            the new workflow
     */
    public void setWorkflow(WorkflowTypeListBox workflow) {
        this._workflow = workflow;
    }


    /**
     * Gets the clear filters.
     *
     * @return the clear filters
     */
    public Button getClearFilters() {
        return _clearFilters;
    }

    /**
     * Sets the clear filters.
     *
     * @param clearFilters
     *            the new clear filters
     */
    public void setClearFilters(Button clearFilters) {
        this._clearFilters = clearFilters;
    }

}
