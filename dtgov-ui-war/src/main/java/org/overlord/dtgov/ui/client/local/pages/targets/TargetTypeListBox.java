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
package org.overlord.dtgov.ui.client.local.pages.targets;

import javax.inject.Inject;

import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.widgets.common.AbstractFilterListBox;

/**
 * Custom GWT ListBox object that includeds the i18 component and stores an
 * initial component.
 *
 * @author David Virgil Naranjo
 */
public class TargetTypeListBox extends AbstractFilterListBox {
    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /**
     * Constructor.
     */
    public TargetTypeListBox() {
    }

    /**
     * Configure items.
     *
     * @see org.overlord.dtgov.ui.client.local.widgets.common.AbstractFilterListBox#configureItems()
     */
    @Override
    protected void configureItems() {
        this.addItem(_i18n.format("any"), ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Clear.
     *
     * @see com.google.gwt.user.client.ui.ListBox#clear()
     */
    @Override
    public void clear() {
        super.clear();
        this.addItem(_i18n.format("any"), ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Clears all items - doesn't add the "Any" item back in.
     */
    public void clearAll() {
        super.clear();
    }

    /**
     * Gets the i18n.
     *
     * @return the i18n
     */
    public ClientMessages getI18n() {
        return _i18n;
    }

    /**
     * Sets the i18n.
     *
     * @param i18n
     *            the new i18n
     */
    public void setI18n(ClientMessages i18n) {
        this._i18n = i18n;
    }
}
