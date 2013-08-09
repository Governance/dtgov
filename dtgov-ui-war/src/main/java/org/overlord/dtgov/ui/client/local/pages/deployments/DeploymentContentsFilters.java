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
import org.overlord.dtgov.ui.client.local.beans.DeploymentContentsFilterBean;

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
 * The filter sidebar on the deployment contents page.
 *
 * TODO this should be using auto-binding
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentContents.html#deployment-contents-filters")
@Dependent
public class DeploymentContentsFilters extends Composite implements HasValueChangeHandlers<DeploymentContentsFilterBean> {

    private DeploymentContentsFilterBean currentState = new DeploymentContentsFilterBean();

    @Inject @DataField
    protected TextBox name;
    @Inject @DataField
    protected TextBox type;

    @Inject @DataField
    protected Button clearFilters;

    /**
     * Constructor.
     */
    public DeploymentContentsFilters() {
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
                setValue(new DeploymentContentsFilterBean());
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
        name.addValueChangeHandler(valueChangeHandler);
        type.addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        DeploymentContentsFilterBean newState = new DeploymentContentsFilterBean();
        newState.setName(name.getValue())
            .setType(type.getValue());

        DeploymentContentsFilterBean oldState = this.currentState;
        this.currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
    }

    /**
     * @return the current filter settings
     */
    public DeploymentContentsFilterBean getValue() {
        return this.currentState;
    }

    /**
     * @param value the new filter settings
     */
    public void setValue(DeploymentContentsFilterBean value) {
        name.setValue(value.getName() == null ? "" : value.getName()); //$NON-NLS-1$
        type.setValue(value.getType() == null ? "" : value.getType()); //$NON-NLS-1$
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
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DeploymentContentsFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
