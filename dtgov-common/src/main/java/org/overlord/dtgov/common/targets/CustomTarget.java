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
package org.overlord.dtgov.common.targets;

import java.io.Serializable;
import java.util.Map;

import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.common.model.DtgovModel;

/**
 * Custom Target Implementation
 * 
 * @author David Virgil Naranjo
 */
public class CustomTarget extends Target implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8298937126180266707L;

    private String customType;
    Map<String, String> properties;

    /**
     * Instantiates a new custom target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param customType
     *            the custom type
     * @param properties
     *            the properties
     */
    public CustomTarget(String name, String classifier, String customType, Map<String, String> properties) {
        super(name, classifier, TYPE.CUSTOM);
        this.customType = customType;
        this.properties = properties;
    }

    /**
     * Instantiates a new custom target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     */
    public CustomTarget(String name, String classifier) {
        super(name, classifier, TYPE.CUSTOM);
    }

    /**
     * Constructor a target of custom type.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param customType
     *            the custom type
     * @param properties
     *            the properties
     * @return the target
     */
    public static final Target getTarget(String name, String classifier, String customType, Map<String, String> properties) {
        CustomTarget target = new CustomTarget(name, classifier);
        target.customType = customType;
        target.properties = properties;
        return target;
    }

    /**
     * Gets the custom type.
     *
     * @return the custom type
     */
    public String getCustomType() {
        return customType;
    }

    /**
     * Sets the custom type.
     *
     * @param customType
     *            the new custom type
     */
    public void setCustomType(String customType) {
        this.customType = customType;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets the properties.
     *
     * @param properties
     *            the properties
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Gets the property.
     *
     * @param key
     *            the key
     * @return the property
     */
    public String getProperty(String key) {
        if (properties != null && !properties.isEmpty()) {
            if (properties.containsKey(key)) {
                return properties.get(key);
            } else {
                String key_prefixed = DtgovModel.PREFIX_CUSTOM_PROPERTY + key;
                if (properties.containsKey(key_prefixed)) {
                    return properties.get(key_prefixed);
                }
            }
        }
        return null;
    }
}
