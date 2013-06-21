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
 * A list of all the artifacts derived from some other artifact.
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class DerivedArtifactsBean {

    private String artifactUuid;
    private String artifactName;
    private List<DerivedArtifactSummaryBean> derivedArtifacts = new ArrayList<DerivedArtifactSummaryBean>();

    /**
     * Constructor.
     */
    public DerivedArtifactsBean() {
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
     * @return the derivedArtifacts
     */
    public List<DerivedArtifactSummaryBean> getDerivedArtifacts() {
        return derivedArtifacts;
    }

    /**
     * @param derivedArtifacts the derivedArtifacts to set
     */
    public void setDerivedArtifacts(List<DerivedArtifactSummaryBean> derivedArtifacts) {
        this.derivedArtifacts = derivedArtifacts;
    }
}
