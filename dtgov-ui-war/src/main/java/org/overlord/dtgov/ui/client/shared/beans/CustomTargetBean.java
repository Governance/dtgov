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
package org.overlord.dtgov.ui.client.shared.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Custom Target bean.
 * 
 * @author David Virgil Naranjo
 */
@Portable
public class CustomTargetBean extends TargetBean implements Serializable {

    private static final long serialVersionUID = 8165075213299343976L;

    private String customTypeName;

    private List<CustomTargetProperty> properties;

    /**
     * Instantiates a new custom target bean.
     */
    public CustomTargetBean() {

    }

    /**
     * Instantiates a new custom target bean.
     *
     * @param uuid
     *            the uuid
     * @param classifiers
     *            the classifiers
     * @param description
     *            the description
     * @param name
     *            the name
     * @param customTypeName
     *            the custom type name
     * @param properties
     *            the properties
     */
    public CustomTargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name, String customTypeName,
            List<CustomTargetProperty> properties) {
        super(uuid, classifiers, description, name);
        this.customTypeName = customTypeName;
        this.properties = properties;
    }

    /**
     * Gets the custom type name.
     *
     * @return the custom type name
     */
    public String getCustomTypeName() {
        return customTypeName;
    }

    /**
     * Sets the custom type name.
     *
     * @param customTypeName
     *            the new custom type name
     */
    public void setCustomTypeName(String customTypeName) {
        this.customTypeName = customTypeName;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public List<CustomTargetProperty> getProperties() {
        return properties;
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

    /**
     * Adds the property.
     *
     * @param property
     *            the property
     */
    public void addProperty(CustomTargetProperty property) {
        if (properties == null) {
            properties = new ArrayList<CustomTargetProperty>();
        }
        properties.add(property);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CustomTargetBean [" + super.toString() + ", customTypeName=" + customTypeName); //$NON-NLS-1$ //$NON-NLS-2$

        if (properties != null && properties.size() > 0) {
            builder.append(", properties:{"); //$NON-NLS-1$
            int size = properties.size();
            int i = 0;
            for (CustomTargetProperty key : properties) {

                builder.append(key.getKey()).append(": ").append(key.getValue()); //$NON-NLS-1$
                if (i < size - 1) {
                    builder.append(", "); //$NON-NLS-1$
                }
                i++;
            }
            builder.append("}"); //$NON-NLS-1$
        }
        builder.append("]"); //$NON-NLS-1$

        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#getType()
     */
    @Override
    public TargetType getType() {
        return TargetType.CUSTOM;
    }

}
