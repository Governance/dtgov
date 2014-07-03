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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.shared.beans.MavenTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Maven Target Component that includes the injections of the maven target form
 * fields.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/targets/maven_target.html#target_content")
@Dependent
public class MavenTargetPanel extends AbstractTargetPanel {

    @Inject
    @DataField("form-target-user-input")
    private TextBox user;

    @Inject
    @DataField("form-target-password-input")
    private TextBox password;

    @Inject
    @DataField("form-target-url-input")
    private TextBox _repositoryURL;

    @Inject
    @DataField("form-target-releaseEnabled")
    private RadioButton _releaseEnabled;

    @Inject
    @DataField("form-target-releaseNotEnabled")
    private RadioButton _releaseNotEnabled;

    @Inject
    @DataField("form-target-snapshotEnabled")
    private RadioButton _snapshotEnabled;

    @Inject
    @DataField("form-target-snapshotNotEnabled")
    private RadioButton _snapshotNotEnabled;

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#getTargetBean()
     */
    @Override
    public TargetBean getTargetBean() {
        MavenTargetBean maven = new MavenTargetBean();
        maven.setUser(user.getValue());
        maven.setPassword(password.getValue());
        maven.setRepositoryUrl(_repositoryURL.getText());
        maven.setReleaseEnabled(_releaseEnabled.getValue());
        maven.setSnapshotEnabled(_snapshotEnabled.getValue());
        return maven;
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.target.panel.AbstractTargetPanel#initialize(org.overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public void initialize(TargetBean bean) {
        MavenTargetBean maven = (MavenTargetBean) bean;
        user.setValue(maven.getUser());
        password.setValue(maven.getPassword());
        _repositoryURL.setValue(maven.getRepositoryUrl());
        _releaseEnabled.setValue(maven.isReleaseEnabled());
        _snapshotEnabled.setValue(maven.isSnapshotEnabled());
        _releaseNotEnabled.setValue(!maven.isReleaseEnabled());
        _snapshotNotEnabled.setValue(!maven.isSnapshotEnabled());
    }

    /**
     * On post construct.
     */
    @PostConstruct
    public void onPostConstruct() {
        _releaseEnabled.setName("release");
        _releaseNotEnabled.setName("release");
        _snapshotEnabled.setName("snapshot");
        _snapshotNotEnabled.setName("snapshot");
    }
}
