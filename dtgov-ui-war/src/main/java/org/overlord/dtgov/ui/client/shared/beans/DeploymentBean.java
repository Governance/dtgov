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
package org.overlord.dtgov.ui.client.shared.beans;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Models the full details of a human task.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class DeploymentBean extends DeploymentSummaryBean implements Serializable {

    private static final long serialVersionUID = DeploymentBean.class.hashCode();

    private String version;
    private String initiatedBy;
    private String mavenGroup;
    private String mavenId;
    private String mavenVersion;
    private String description;

    /**
     * Constructor.
     */
    public DeploymentBean() {
    }

    /**
     * @return true if this deployment has maven information
     */
    public boolean hasMavenInfo() {
        return mavenGroup != null;
    }

    /**
     * @return the mavenGroup
     */
    public String getMavenGroup() {
        return mavenGroup;
    }

    /**
     * @param mavenGroup the mavenGroup to set
     */
    public void setMavenGroup(String mavenGroup) {
        this.mavenGroup = mavenGroup;
    }

    /**
     * @return the mavenId
     */
    public String getMavenId() {
        return mavenId;
    }

    /**
     * @param mavenId the mavenId to set
     */
    public void setMavenId(String mavenId) {
        this.mavenId = mavenId;
    }

    /**
     * @return the mavenVersion
     */
    public String getMavenVersion() {
        return mavenVersion;
    }

    /**
     * @param mavenVersion the mavenVersion to set
     */
    public void setMavenVersion(String mavenVersion) {
        this.mavenVersion = mavenVersion;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the initiatedBy
     */
    public String getInitiatedBy() {
        return initiatedBy;
    }

    /**
     * @param initiatedBy the initiatedBy to set
     */
    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
