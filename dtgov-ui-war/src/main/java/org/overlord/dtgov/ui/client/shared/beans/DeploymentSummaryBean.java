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
 * A simple data bean for returning summary information for single deployment.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class DeploymentSummaryBean {

    private String uuid;
    private String name;
    private String model;
    private String type;
    private String rawType;
    private String stage;
    private Date initiatedDate;

    /**
     * Constructor.
     */
    public DeploymentSummaryBean() {
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public DeploymentSummaryBean setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public DeploymentSummaryBean setName(String name) {
        this.name = name;
        return this;
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
    public DeploymentSummaryBean setType(String type) {
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
    public DeploymentSummaryBean setStage(String stage) {
        this.stage = stage;
        return this;
    }

    /**
     * @return the initiatedDate
     */
    public Date getInitiatedDate() {
        return initiatedDate;
    }

    /**
     * @param initiatedDate the initiatedDate to set
     */
    public DeploymentSummaryBean setInitiatedDate(Date initiatedDate) {
        this.initiatedDate = initiatedDate;
        return this;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the rawType
     */
    public String getRawType() {
        return rawType;
    }

    /**
     * @param rawType the rawType to set
     */
    public void setRawType(String rawType) {
        this.rawType = rawType;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
        DeploymentSummaryBean other = (DeploymentSummaryBean) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}
