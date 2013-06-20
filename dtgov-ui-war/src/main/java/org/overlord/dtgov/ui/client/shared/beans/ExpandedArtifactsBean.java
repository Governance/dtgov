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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class ExpandedArtifactsBean {

    private String artifactUuid;
    private String artifactName;
    private String artifactType;
    private String artifactVersion;
    private List<ExpandedArtifactSummaryBean> expandedArtifacts = new ArrayList<ExpandedArtifactSummaryBean>();

    /**
     * Constructor.
     */
    public ExpandedArtifactsBean() {
    }

    /**
     * @return the artifactUuid
     */
    public String getArtifactUuid() {
        return artifactUuid;
    }

    /**
     * @param artifactUuid the artifactUuid to set
     */
    public void setArtifactUuid(String artifactUuid) {
        this.artifactUuid = artifactUuid;
    }

    /**
     * @return the artifactName
     */
    public String getArtifactName() {
        return artifactName;
    }

    /**
     * @param artifactName the artifactName to set
     */
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    /**
     * @return the expandedArtifacts
     */
    public List<ExpandedArtifactSummaryBean> getExpandedArtifacts() {
        return expandedArtifacts;
    }

    /**
     * @param expandedArtifacts the expandedArtifacts to set
     */
    public void setExpandedArtifacts(List<ExpandedArtifactSummaryBean> expandedArtifacts) {
        this.expandedArtifacts = expandedArtifacts;
    }

    /**
     * @return the artifactType
     */
    public String getArtifactType() {
        return artifactType;
    }

    /**
     * @param artifactType the artifactType to set
     */
    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    /**
     * @return the artifactVersion
     */
    public String getArtifactVersion() {
        return artifactVersion;
    }

    /**
     * @param artifactVersion the artifactVersion to set
     */
    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

}
