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

import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.beans.UiConfiguration;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.DateBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The deployments filtersPanel sidebar.  Whenever the user changes any of the settings in
 * the filter sidebar, a ValueChangeEvent will be fired.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deployments.html#deployments-filter-sidebar")
@Dependent
public class DeploymentFilters extends Composite implements HasValueChangeHandlers<DeploymentsFilterBean> {

    @Inject
    private ConfigurationService configService;

    private DeploymentsFilterBean currentState = new DeploymentsFilterBean();

    // Owner, type, bundle name
    @Inject @DataField
    protected DeploymentTypeListBox type;
    @Inject @DataField
    protected DeploymentStageListBox stage;
    @Inject @DataField
    protected TextBox bundleName;

    // Date Created
    @Inject @DataField
    protected DateBox dateInitiatedFrom;
    @Inject @DataField
    protected DateBox dateInitiatedTo;

    @Inject @DataField
    protected SimpleCheckBox showCompleted;

    @Inject @DataField
    protected Anchor clearFilters;

    /**
     * Constructor.
     */
    public DeploymentFilters() {
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
                setValue(new DeploymentsFilterBean());
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
        type.addValueChangeHandler(valueChangeHandler);
        stage.addValueChangeHandler(valueChangeHandler);
        bundleName.addValueChangeHandler(valueChangeHandler);
        dateInitiatedFrom.addValueChangeHandler(valueChangeHandler);
        dateInitiatedTo.addValueChangeHandler(valueChangeHandler);
        showCompleted.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onFilterValueChange();
            }
        });
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        DeploymentsFilterBean newState = new DeploymentsFilterBean();
        newState.setType(type.getValue())
            .setStage(stage.getValue())
            .setBundleName(bundleName.getValue())
            .setDateInitiatedFrom(dateInitiatedFrom.getDateValue())
            .setDateInitiatedTo(dateInitiatedTo.getDateValue())
            .setShowCompleted(showCompleted.getValue());

        DeploymentsFilterBean oldState = this.currentState;
        this.currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
    }

    /**
     * @return the current filter settings
     */
    public DeploymentsFilterBean getValue() {
        return this.currentState;
    }

    /**
     * @param value the new filter settings
     */
    public void setValue(DeploymentsFilterBean value) {
        type.setValue(value.getType() == null ? "" : value.getType());
        stage.setValue(value.getType() == null ? "" : value.getStage());
        bundleName.setValue(value.getType() == null ? "" : value.getBundleName());
        dateInitiatedFrom.setDateValue(value.getDateInitiatedFrom() == null ? null : value.getDateInitiatedFrom());
        dateInitiatedTo.setDateValue(value.getDateInitiatedTo() == null ? null : value.getDateInitiatedTo());
        showCompleted.setValue(value.isShowCompleted());
        onFilterValueChange();
    }

    /**
     * Refresh any data in the filter panel.
     */
    public void refresh() {
        UiConfiguration uiConfig = configService.getUiConfig();

        // Update the items in the deployment type drop-down
        this.type.clear();
        Map<String, String> deploymentTypes = uiConfig.getDeploymentTypes();
        for (Entry<String, String> entry : deploymentTypes.entrySet()) {
            this.type.addItem(entry.getKey(), entry.getValue());
        }

        // Update the items in the deployment stage drop-down
        this.stage.clear();
        Map<String, String> deploymentStages = uiConfig.getDeploymentStages();
        for (Entry<String, String> entry : deploymentStages.entrySet()) {
            this.stage.addItem(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DeploymentsFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
