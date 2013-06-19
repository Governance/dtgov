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

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * A single event in the history of an artifact.  Summary information only.
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class HistoryEventSummaryBean {

    private String artifactUuid;
    private String id;
    private String who;
    private Date when;
    private String summary;

    /**
     * Constructor.
     */
    public HistoryEventSummaryBean() {
    }

    /**
     * @return the who
     */
    public String getWho() {
        return who;
    }

    /**
     * @param who the who to set
     */
    public void setWho(String who) {
        this.who = who;
    }

    /**
     * @return the when
     */
    public Date getWhen() {
        return when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen(Date when) {
        this.when = when;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HistoryEventSummaryBean other = (HistoryEventSummaryBean) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
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

}
