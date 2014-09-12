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
package org.overlord.dtgov.common.targets;

import java.io.Serializable;

import org.overlord.dtgov.common.Target;

/**
 * Maven Target Implementation
 * 
 * @author David Virgil Naranjo
 */
public class MavenTarget extends Target implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -8043540607465735263L;
    private String user;
    private String password;

    private String mavenUrl;
    private boolean isReleaseEnabled;
    private boolean isSnapshotEnabled;


    /**
     * Instantiates a new maven target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param user
     *            the user
     * @param password
     *            the password
     * @param mavenUrl
     *            the maven url
     * @param isReleaseEnabled
     *            the is release enabled
     * @param isSnapshotEnabled
     *            the is snapshot enabled
     */
    public MavenTarget(String name, String classifier, String user, String password, String mavenUrl, boolean isReleaseEnabled,
            boolean isSnapshotEnabled) {
        super(name, classifier, TYPE.MAVEN);
        this.user = user;
        this.password = password;
        this.mavenUrl = mavenUrl;
        this.isReleaseEnabled = isReleaseEnabled;
        this.isSnapshotEnabled = isSnapshotEnabled;
    }

    /**
     * Instantiates a new maven target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     */
    public MavenTarget(String name, String classifier) {
        super(name, classifier, TYPE.MAVEN);

    }

    /**
     * Constructor a target of type Maven.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param mavenUrl
     *            the maven url
     * @param isReleaseEnabled
     *            the is release enabled
     * @param isSnapshotEnabled
     *            the is snapshot enabled
     * @return the target
     */
    public static final Target getTarget(String name, String classifier, String mavenUrl, boolean isReleaseEnabled, boolean isSnapshotEnabled) {
        MavenTarget target = new MavenTarget(name, classifier);
        target.mavenUrl = mavenUrl;
        target.setReleaseEnabled(isReleaseEnabled);
        target.setSnapshotEnabled(isSnapshotEnabled);
        return target;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the maven url.
     *
     * @return the maven url
     */
    public String getMavenUrl() {
        return mavenUrl;
    }

    /**
     * Sets the maven url.
     *
     * @param mavenUrl
     *            the new maven url
     */
    public void setMavenUrl(String mavenUrl) {
        this.mavenUrl = mavenUrl;
    }

    /**
     * Checks if is release enabled.
     *
     * @return true, if is release enabled
     */
    public boolean isReleaseEnabled() {
        return isReleaseEnabled;
    }

    /**
     * Sets the release enabled.
     *
     * @param isReleaseEnabled
     *            the new release enabled
     */
    public void setReleaseEnabled(boolean isReleaseEnabled) {
        this.isReleaseEnabled = isReleaseEnabled;
    }

    /**
     * Checks if is snapshot enabled.
     *
     * @return true, if is snapshot enabled
     */
    public boolean isSnapshotEnabled() {
        return isSnapshotEnabled;
    }

    /**
     * Sets the snapshot enabled.
     *
     * @param isSnapshotEnabled
     *            the new snapshot enabled
     */
    public void setSnapshotEnabled(boolean isSnapshotEnabled) {
        this.isSnapshotEnabled = isSnapshotEnabled;
    }

}
