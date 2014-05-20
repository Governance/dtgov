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
package org.overlord.dtgov.ui.client.local.pages.workflowQuery;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.WorkflowQueriesRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueriesFilterBean;

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
 * The workflow queries filtersPanel sidebar. Whenever the user changes any of
 * the settings in the filter sidebar, a ValueChangeEvent will be fired.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/workflowQueries.html#queries-filter-sidebar")
@Dependent
public class WorkflowQueriesFilter extends Composite implements HasValue<WorkflowQueriesFilterBean> {

    /** The config service. */
    @Inject
    private ConfigurationService _configService;

    /** The current state. */
    private WorkflowQueriesFilterBean _currentState = new WorkflowQueriesFilterBean();

    /** The _workflow query service. */
    @Inject
    private WorkflowQueriesRpcService _workflowQueryService;

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
    @DataField("name")
    private TextBox _name;

    /** The clear filters. */
    @Inject
    @DataField("clearFilters")
    private Button _clearFilters;

    /**
     * Instantiates a new workflow queries filter.
     */
    public WorkflowQueriesFilter() {

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
                setValue(new WorkflowQueriesFilterBean(), true);
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
        _name.addValueChangeHandler(valueChangeHandler);
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
    public void setValue(WorkflowQueriesFilterBean value, boolean fireEvents) {
        _workflow.setValue(value.getWorkflow() == null ? "" : value.getWorkflow()); //$NON-NLS-1$
        _name.setValue(value.getName() == null ? "" : value.getName()); //$NON-NLS-1$
        WorkflowQueriesFilterBean oldState = this._currentState;
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

        _workflowQueryService.getWorkflowTypes(new IRpcServiceInvocationHandler<Set<String>>() {

            @Override
            public void onReturn(Set<String> workflowTypes) {
                for (String entry : workflowTypes) {
                    _workflow.addItem(entry, entry);
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
        WorkflowQueriesFilterBean newState = new WorkflowQueriesFilterBean();
        newState.setWorkflow(_workflow.getValue()).setName(_name.getValue());

        WorkflowQueriesFilterBean oldState = this._currentState;
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
    public WorkflowQueriesFilterBean getValue() {
        return this._currentState;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new filter settings
     */
    @Override
    public void setValue(WorkflowQueriesFilterBean value) {
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
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<WorkflowQueriesFilterBean> handler) {
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
    public WorkflowQueriesFilterBean getCurrentState() {
        return _currentState;
    }

    /**
     * Sets the current state.
     *
     * @param currentState
     *            the new current state
     */
    public void setCurrentState(WorkflowQueriesFilterBean currentState) {
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
     * Gets the name.
     *
     * @return the name
     */
    public TextBox getName() {
        return _name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(TextBox name) {
        this._name = name;
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
