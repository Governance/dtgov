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

import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.user.client.ui.Composite;

/**
 * Abstract class that specify the methods that have to be implemented by the
 * Target Panel Specific Implementations
 * 
 * @author David Virgil Naranjo
 */
public abstract class AbstractTargetPanel extends Composite {

    /**
     * Gets the target bean.
     *
     * @return the target bean
     */
    public abstract TargetBean getTargetBean();

    /**
     * Initialize.
     *
     * @param bean
     *            the bean
     */
    public abstract void initialize(TargetBean bean);
}
