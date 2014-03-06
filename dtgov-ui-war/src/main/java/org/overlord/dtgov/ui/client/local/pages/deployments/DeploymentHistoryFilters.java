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

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.DateBox;
import org.overlord.dtgov.ui.client.local.beans.DeploymentHistoryFilterBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The filter sidebar on the deployment history page.
 *
 * TODO this should be using auto-binding
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentHistory.html#deployment-history-filters")
@Dependent
public class DeploymentHistoryFilters extends Composite implements HasValueChangeHandlers<DeploymentHistoryFilterBean> {

    private DeploymentHistoryFilterBean currentState = new DeploymentHistoryFilterBean();

    @Inject @DataField
    protected TextBox user;
    @Inject @DataField
    protected DateBox dateFrom;
    @Inject @DataField
    protected DateBox dateTo;

    @Inject @DataField
    protected Button clearFilters;

    /**
     * Constructor.
     */
    public DeploymentHistoryFilters() {
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
                setValue(new DeploymentHistoryFilterBean());
                onFilterValueChange();
            }
        };
        clearFilters.addClickHandler(clearFilterHandler);
        @SuppressWarnings("rawtypes")
        ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                onFilterValueChange();
            }
        };
        user.addValueChangeHandler(valueChangeHandler);
        dateFrom.addValueChangeHandler(valueChangeHandler);
        dateTo.addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        DeploymentHistoryFilterBean newState = new DeploymentHistoryFilterBean();
        newState.setUser(user.getValue())
            .setDateFrom(dateFrom.getDateValue())
            .setDateTo(dateTo.getDateValue());

        DeploymentHistoryFilterBean oldState = this.currentState;
        this.currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
    }

    /**
     * @return the current filter settings
     */
    public DeploymentHistoryFilterBean getValue() {
        return this.currentState;
    }

    /**
     * @param value the new filter settings
     */
    public void setValue(DeploymentHistoryFilterBean value) {
        user.setValue(value.getUser() == null ? "" : value.getUser()); //$NON-NLS-1$
        dateFrom.setDateValue(value.getDateFrom() == null ? null : value.getDateFrom());
        dateTo.setDateValue(value.getDateTo() == null ? null : value.getDateTo());
        onFilterValueChange();
    }

    /**
     * Refresh any data in the filter panel.
     */
    public void refresh() {
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DeploymentHistoryFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
