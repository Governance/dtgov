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
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * It contains the maven target fields. It is a subtype of the TargetBean.
 *
 * @author David Virgil Naranjo
 *
 */
@Portable
public class MavenTargetBean extends TargetBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4155385145431465081L;

    private String _repositoryUrl;

    private boolean _isReleaseEnabled;

    private boolean _isSnapshotEnabled;

    private String _user;

    private String _password;

    /**
     * Instantiates a new maven target bean.
     */
    public MavenTargetBean() {
        super();
    }

    /**
     * Instantiates a new maven target bean.
     *
     * @param uuid
     *            the uuid
     * @param classifiers
     *            the classifiers
     * @param description
     *            the description
     * @param name
     *            the name
     * @param repositoryUrl
     *            the repository url
     * @param isReleaseEnabled
     *            the is release enabled
     * @param isSnapshotEnabled
     *            the is snapshot enabled
     * @param user
     *            the user
     * @param password
     *            the password
     */
    public MavenTargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name, String repositoryUrl,
            boolean isReleaseEnabled,
            boolean isSnapshotEnabled, String user, String password) {
        super(uuid, classifiers, description, name);
        this._repositoryUrl = repositoryUrl;
        this._isReleaseEnabled = isReleaseEnabled;
        this._isSnapshotEnabled = isSnapshotEnabled;
        this._user = user;
        this._password = password;
    }



    /*
     * (non-Javadoc)
     * 
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#getType()
     */
    @Override
    public TargetType getType() {
        return TargetType.MAVEN;
    }


    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return _user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(String user) {
        this._user = user;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this._password = password;
    }

    /**
     * Gets the repository url.
     *
     * @return the repository url
     */
    public String getRepositoryUrl() {
        return _repositoryUrl;
    }

    /**
     * Sets the repository url.
     *
     * @param repositoryUrl
     *            the new repository url
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this._repositoryUrl = repositoryUrl;
    }

    /**
     * Checks if is release enabled.
     *
     * @return true, if is release enabled
     */
    public boolean isReleaseEnabled() {
        return _isReleaseEnabled;
    }

    /**
     * Sets the release enabled.
     *
     * @param isReleaseEnabled
     *            the new release enabled
     */
    public void setReleaseEnabled(boolean isReleaseEnabled) {
        this._isReleaseEnabled = isReleaseEnabled;
    }

    /**
     * Checks if is snapshot enabled.
     *
     * @return true, if is snapshot enabled
     */
    public boolean isSnapshotEnabled() {
        return _isSnapshotEnabled;
    }

    /**
     * Sets the snapshot enabled.
     *
     * @param isSnapshotEnabled
     *            the new snapshot enabled
     */
    public void setSnapshotEnabled(boolean isSnapshotEnabled) {
        this._isSnapshotEnabled = isSnapshotEnabled;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#toString()
     */
    @Override
    public String toString() {
        return "MavenTargetBean [" + super.toString() + ", repositoryUrl=" + _repositoryUrl + ", isReleaseEnabled=" + _isReleaseEnabled //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + ", isSnapshotEnabled=" + _isSnapshotEnabled + ", user=" + _user + ", password=" + _password + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }




}
