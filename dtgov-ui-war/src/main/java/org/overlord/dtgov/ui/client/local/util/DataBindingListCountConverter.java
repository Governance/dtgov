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
package org.overlord.dtgov.ui.client.local.util;

import java.util.List;

import org.jboss.errai.databinding.client.api.Converter;

/**
 * Counts the number of items in a list.
 *
 * @author eric.wittmann@redhat.com
 */
public class DataBindingListCountConverter implements Converter<List<?>, String> {

    /**
     * Constructor.
     */
    public DataBindingListCountConverter() {
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toModelValue(java.lang.Object)
     */
    @Override
    public List<?> toModelValue(String widgetValue) {
        return null;
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toWidgetValue(java.lang.Object)
     */
    @Override
    public String toWidgetValue(List<?> modelValue) {
        if (modelValue == null)
            return "0"; //$NON-NLS-1$
        else
            return String.valueOf(modelValue.size());
    }

}
