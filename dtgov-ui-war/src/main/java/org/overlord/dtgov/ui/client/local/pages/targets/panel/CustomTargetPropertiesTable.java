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
package org.overlord.dtgov.ui.client.local.pages.targets.panel;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetProperty;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Table widget included in the custom target panel to include custom properties
 * 
 * @author David Virgil Naranjo
 */
@Dependent
public class CustomTargetPropertiesTable extends TemplatedWidgetTable implements HasValue<List<CustomTargetProperty>> {

    /** The config service. */
    @Inject
    private ConfigurationService configService;

    /** The i18n. */
    @Inject
    private ClientMessages i18n;

    private List<CustomTargetProperty> properties;


    /**
     * Constructor.
     */
    public CustomTargetPropertiesTable() {
        properties = new ArrayList<CustomTargetProperty>();
        this.columnCount = 3;
    }

    /**
     * Adds the new row.
     */
    public void addNewRow() {
        CustomTargetProperty property = new CustomTargetProperty();
        properties.add(property);
        addRow(property);
    }

    /**
     * Adds the row.
     *
     * @param property
     *            the property
     */
    private void addRow(final CustomTargetProperty property) {
        final int rowIdx = this.rowElements.size();
        String propValue = property.getValue();
        FlowPanel actions = new FlowPanel();
        InlineLabel deleteAction = new InlineLabel();
        deleteAction.setStyleName("workflow-icon", true); //$NON-NLS-1$
        deleteAction.setStyleName("workflow-delete-icon", true); //$NON-NLS-1$
        actions.add(deleteAction);

        deleteAction.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                deleteRow(rowIdx);
                properties.remove(rowIdx);
                setValue(properties, true);
            }
        });
        final TextBox valueBox = new TextBox();
        valueBox.setText(propValue);
        valueBox.setStyleName("input-value"); //$NON-NLS-1$
        valueBox.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String val = event.getValue();
                property.setValue(val);
            }
        });

        TextBox propertyKey = new TextBox();

        propertyKey.setStyleName("input-value"); //$NON-NLS-1$
        propertyKey.setText(property.getKey());
        propertyKey.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String val = event.getValue();
                property.setKey(val);

            }
        });

        add(rowIdx, 0, propertyKey);
        add(rowIdx, 1, valueBox);
        Element row = add(rowIdx, 2, actions);
        setStyleName(row, "actions", true); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#
     * addValueChangeHandler
     * (com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<CustomTargetProperty>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Gets the config service.
     *
     * @return the config service
     */
    public ConfigurationService getConfigService() {
        return configService;
    }

    /**
     * Gets the i18n.
     *
     * @return the i18n
     */
    public ClientMessages getI18n() {
        return i18n;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<CustomTargetProperty> getValue() {
        return properties;
    }

    /**
     * Sets the config service.
     *
     * @param configService
     *            the new config service
     */
    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * Sets the i18n.
     *
     * @param i18n
     *            the new i18n
     */
    public void setI18n(ClientMessages i18n) {
        this.i18n = i18n;
    }

    /**
     * Sets the properties.
     *
     * @param properties
     *            the new properties
     */
    public void setProperties(List<CustomTargetProperty> properties) {
        this.properties = properties;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<CustomTargetProperty> value) {
        this.setValue(value, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object,
     * boolean)
     */
    @Override
    public void setValue(List<CustomTargetProperty> props, boolean fireEvents) {
        this.properties = new ArrayList<CustomTargetProperty>(props);
        clear();
        if (props == null || props.isEmpty()) {
            // Put something here? "No Properties found..." ?
        } else {
            for (final CustomTargetProperty property : props) {
                addRow(property);
            }
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, this.properties);
        }
    }

}
