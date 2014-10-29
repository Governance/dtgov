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
package org.overlord.dtgov.jbpm.util;

/**
 * Model class that contains all the necessary attributes that defines a kie jar
 * artifact. It makes easier to treat them in dtgov instead of read the s-ramp
 * artifact properties.
 *
 * 
 * @author David Virgil Naranjo
 */
public class KieJar {
    private String uuid;
    private String groupId;
    private String artifactId;
    private String version;
    private String workflowPackage;
    private String workflowKSession;

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the group id.
     *
     * @param groupId
     *            the new group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the artifact id.
     *
     * @return the artifact id
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Sets the artifact id.
     *
     * @param artifactId
     *            the new artifact id
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the new version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the workflow package.
     *
     * @return the workflow package
     */
    public String getWorkflowPackage() {
        return workflowPackage;
    }

    /**
     * Sets the workflow package.
     *
     * @param workflowPackage
     *            the new workflow package
     */
    public void setWorkflowPackage(String workflowPackage) {
        this.workflowPackage = workflowPackage;
    }

    /**
     * Gets the workflow k session.
     *
     * @return the workflow k session
     */
    public String getWorkflowKSession() {
        return workflowKSession;
    }

    /**
     * Sets the workflow k session.
     *
     * @param workflowKSession
     *            the new workflow k session
     */
    public void setWorkflowKSession(String workflowKSession) {
        this.workflowKSession = workflowKSession;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid
     *            the new uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
