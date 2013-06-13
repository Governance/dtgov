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
package org.overlord.dtgov.ui.client.local.pages.deployments;

import org.overlord.dtgov.ui.client.local.widgets.common.AbstractFilterListBox;

/**
 * Injectable list box for the deployment type filter.
 * @author eric.wittmann@redhat.com
 */
public class DeploymentTypeListBox extends AbstractFilterListBox {

    /**
     * Constructor.
     */
    public DeploymentTypeListBox() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.local.widgets.common.AbstractFilterListBox#configureItems()
     */
    @Override
    protected void configureItems() {
        this.addItem("Any", "");
    }

    /**
     * @see com.google.gwt.user.client.ui.ListBox#clear()
     */
    @Override
    public void clear() {
        super.clear();
        this.addItem("Any", "");
    }

}
