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
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.shared.beans.CopyTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.user.client.ui.TextBox;

/**
 * Copy Target Component that includes the injections of the copy target form
 * fields.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets/copy_target.html#target_content")
@Dependent
public class CopyTargetPanel extends AbstractTargetPanel {

    @Inject
    @DataField("form-target-deploymentDir-input")
    private TextBox _deploymentDir;

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#getTargetBean()
     */
    @Override
    public TargetBean getTargetBean() {
        CopyTargetBean copy = new CopyTargetBean();
        copy.setDeployDirectory(_deploymentDir.getValue());
        return copy;
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        CopyTargetBean copy = (CopyTargetBean) bean;
        _deploymentDir.setValue(copy.getDeployDirectory());
    }

    /**
     * Gets the deployment dir.
     *
     * @return the deployment dir
     */
    public TextBox getDeploymentDir() {
        return _deploymentDir;
    }

    /**
     * Sets the deployment dir.
     *
     * @param deploymentDir
     *            the new deployment dir
     */
    public void setDeploymentDir(TextBox deploymentDir) {
        this._deploymentDir = deploymentDir;
    }

}
