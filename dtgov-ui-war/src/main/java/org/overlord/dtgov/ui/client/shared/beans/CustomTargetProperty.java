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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Custom Target Property class.
 * 
 * @author David Virgil Naranjo
 */
@Portable
public class CustomTargetProperty implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** The _key. */
    private String key;

    /** The _value. */
    private String value;

    /**
     * Instantiates a new custom target property.
     */
    public CustomTargetProperty() {
    }

    /**
     * Instantiates a new custom target property.
     *
     * @param _key
     *            the _key
     * @param _value
     *            the _value
     */
    public CustomTargetProperty(String _key, String _value) {
        super();
        this.key = _key;
        this.value = _value;
    }

    /**
     * Gets the _key.
     *
     * @return the _key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the _key.
     *
     * @param key
     *            the new _key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the _value.
     *
     * @return the _value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the _value.
     *
     * @param value
     *            the new _value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
