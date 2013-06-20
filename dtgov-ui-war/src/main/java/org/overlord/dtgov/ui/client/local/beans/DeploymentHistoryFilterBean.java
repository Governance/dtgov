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
package org.overlord.dtgov.ui.client.local.beans;

import java.util.Date;

import org.overlord.dtgov.ui.client.shared.beans.HistoryEventSummaryBean;

/**
 * All of filter settings on the deployment history page.
 *
 * @author eric.wittmann@redhat.com
 */
public class DeploymentHistoryFilterBean {

    private String user;
    private Date dateFrom;
    private Date dateTo;

    /**
     * Constructor.
     */
    public DeploymentHistoryFilterBean() {
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the dateFrom
     */
    public Date getDateFrom() {
        return dateFrom;
    }

    /**
     * @return the dateTo
     */
    public Date getDateTo() {
        return dateTo;
    }

    /**
     * @param user the user to set
     */
    public DeploymentHistoryFilterBean setUser(String user) {
        this.user = user;
        return this;
    }

    /**
     * @param dateFrom the dateFrom to set
     */
    public DeploymentHistoryFilterBean setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    /**
     * @param dateTo the dateTo to set
     */
    public DeploymentHistoryFilterBean setDateTo(Date dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    /**
     * Returns true iff the given event matches the criteria in the filter.
     * @param event
     */
    public boolean accepts(HistoryEventSummaryBean event) {
        if (getUser() != null && getUser().trim().length() > 0 && !getUser().equals(event.getWho())) {
            return false;
        }
        if (getDateFrom() != null && getDateFrom().compareTo(event.getWhen()) > 0) {
            return false;
        }
        // Add a day to make it inclusive.
        if (getDateTo() != null) {
            Date to = new Date(getDateTo().getTime() + 86400000);
            if (to.compareTo(event.getWhen()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateFrom == null) ? 0 : dateFrom.hashCode());
        result = prime * result + ((dateTo == null) ? 0 : dateTo.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        DeploymentHistoryFilterBean other = (DeploymentHistoryFilterBean) obj;
        if (dateFrom == null) {
            if (other.dateFrom != null)
                return false;
        } else if (!dateFrom.equals(other.dateFrom))
            return false;
        if (dateTo == null) {
            if (other.dateTo != null)
                return false;
        } else if (!dateTo.equals(other.dateTo))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}
