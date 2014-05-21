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
package org.overlord.sramp.governance.services.notification;

/**
 * Enum that determines the types of notification templates
 * 
 * @author David Virgil Naranjo
 *
 */
public enum NotificationTemplateTypeEnum {
    SUBJECT("subject"),  //$NON-NLS-1$
    BODY("body"); //$NON-NLS-1$

    private final String value;

    /**
     * Instantiates a new notification template type enum.
     *
     * @param value
     *            the value
     */
    NotificationTemplateTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v
     *            the v
     * @return the notification template type enum
     */
    public static NotificationTemplateTypeEnum fromValue(String v) {
        for (NotificationTemplateTypeEnum c : NotificationTemplateTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
