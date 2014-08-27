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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetProperty;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Custom target panel added in the TargetPage when the type "custom" is
 * selected in the type select input.
 * 
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets/custom_target.html#target_content")
@Dependent
public class CustomTargetPanel extends AbstractTargetPanel {
    @Inject
    @DataField("form-custom-target-type-input")
    private TextBox customTypeName;

    /** The _properties table. */
    @Inject
    @DataField("properties-table")
    private CustomTargetPropertiesTable _propertiesTable;

    /** The _add property. */
    @Inject
    @DataField("btn-add-property")
    private Button _addProperty;
    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel
     * #getTargetBean()
     */
    @Override
    public TargetBean getTargetBean() {
        CustomTargetBean custom = new CustomTargetBean();
        custom.setCustomTypeName(customTypeName.getValue());
        if (_propertiesTable != null && _propertiesTable.getProperties() != null && _propertiesTable.getProperties().size() > 0) {
            for (CustomTargetProperty property : _propertiesTable.getProperties()) {
                custom.addProperty(property);
            }
        }

        return custom;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel
     * #initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        CustomTargetBean custom = (CustomTargetBean) bean;
        customTypeName.setValue(custom.getCustomTypeName());
        _propertiesTable.setValue(custom.getProperties());
    }

    /**
     * Gets the custom type name.
     *
     * @return the custom type name
     */
    public TextBox getCustomTypeName() {
        return customTypeName;
    }

    /**
     * Sets the custom type name.
     *
     * @param customTypeName
     *            the new custom type name
     */
    public void setCustomTypeName(TextBox customTypeName) {
        this.customTypeName = customTypeName;
    }

    /**
     * Adds the new property.
     *
     * @param event
     *            the event
     */
    @EventHandler("btn-add-property")
    public void addNewProperty(ClickEvent event) {
        _propertiesTable.addNewRow();
    }

    /**
     * Gets the properties table.
     *
     * @return the properties table
     */
    public CustomTargetPropertiesTable getPropertiesTable() {
        return _propertiesTable;
    }

    /**
     * Sets the properties table.
     *
     * @param _propertiesTable
     *            the new properties table
     */
    public void setPropertiesTable(CustomTargetPropertiesTable _propertiesTable) {
        this._propertiesTable = _propertiesTable;
    }

}
