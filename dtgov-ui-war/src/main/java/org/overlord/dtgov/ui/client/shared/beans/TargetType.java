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
 * The supported dtgov target types
 *
 * @author David Virgil Naranjo
 */
@Portable
public enum TargetType implements Serializable {
    RHQ("rhq"), MAVEN("maven"), COPY("copy"), CLI("cli"), FABRIC("fabric"), CUSTOM("custom"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    private String _value;

    /**
     * Instantiates a new target type.
     *
     * @param value
     *            the value
     */
    private TargetType(String value) {
        this._value = value;
    }

    /**
     * Value.
     *
     * @param value
     *            the value
     * @return the target type
     */
    public static TargetType value(String value) {
        if (value != null && !value.equals("")) { //$NON-NLS-1$
            TargetType[] values = TargetType.values();
            for (int i = 0; i < values.length; i++) {
                if (values[i]._value.equals(value)) {
                    return values[i];
                }
            }
        }

        return null;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return _value;
    }



}