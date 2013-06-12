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

/**
 * All of the user's filter settings (configured on the left-hand sidebar of
 * the Deployments page).
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class DeploymentsFilterBean {

    private String type;
    private String stage;
    private String bundleName;
    private Date dateInitiatedFrom;
    private Date dateInitiatedTo;
    private boolean showCompleted;

    /**
     * Constructor.
     */
    public DeploymentsFilterBean() {
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public DeploymentsFilterBean setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * @return the stage
     */
    public String getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public DeploymentsFilterBean setStage(String stage) {
        this.stage = stage;
        return this;
    }

    /**
     * @return the bundleName
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * @param bundleName the bundleName to set
     */
    public DeploymentsFilterBean setBundleName(String bundleName) {
        this.bundleName = bundleName;
        return this;
    }

    /**
     * @return the dateInitiatedFrom
     */
    public Date getDateInitiatedFrom() {
        return dateInitiatedFrom;
    }

    /**
     * @param dateInitiatedFrom the dateInitiatedFrom to set
     */
    public DeploymentsFilterBean setDateInitiatedFrom(Date dateInitiatedFrom) {
        this.dateInitiatedFrom = dateInitiatedFrom;
        return this;
    }

    /**
     * @return the dateInitiatedTo
     */
    public Date getDateInitiatedTo() {
        return dateInitiatedTo;
    }

    /**
     * @param dateInitiatedTo the dateInitiatedTo to set
     */
    public DeploymentsFilterBean setDateInitiatedTo(Date dateInitiatedTo) {
        this.dateInitiatedTo = dateInitiatedTo;
        return this;
    }

    /**
     * @return the showCompleted
     */
    public boolean isShowCompleted() {
        return showCompleted;
    }

    /**
     * @param showCompleted the showCompleted to set
     */
    public DeploymentsFilterBean setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
        return this;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bundleName == null) ? 0 : bundleName.hashCode());
        result = prime * result + ((dateInitiatedFrom == null) ? 0 : dateInitiatedFrom.hashCode());
        result = prime * result + ((dateInitiatedTo == null) ? 0 : dateInitiatedTo.hashCode());
        result = prime * result + (showCompleted ? 1231 : 1237);
        result = prime * result + ((stage == null) ? 0 : stage.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        DeploymentsFilterBean other = (DeploymentsFilterBean) obj;
        if (bundleName == null) {
            if (other.bundleName != null)
                return false;
        } else if (!bundleName.equals(other.bundleName))
            return false;
        if (dateInitiatedFrom == null) {
            if (other.dateInitiatedFrom != null)
                return false;
        } else if (!dateInitiatedFrom.equals(other.dateInitiatedFrom))
            return false;
        if (dateInitiatedTo == null) {
            if (other.dateInitiatedTo != null)
                return false;
        } else if (!dateInitiatedTo.equals(other.dateInitiatedTo))
            return false;
        if (showCompleted != other.showCompleted)
            return false;
        if (stage == null) {
            if (other.stage != null)
                return false;
        } else if (!stage.equals(other.stage))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
