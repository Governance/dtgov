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
 * the Task Inbox page).
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class TaskInboxFilterBean {

    private TaskOwnerEnum owner = TaskOwnerEnum.any;
    private int priority;
    private Date dateDueFrom;
    private Date dateDueTo;

    /**
     * Constructor.
     */
    public TaskInboxFilterBean() {
    }

    /**
     * @return the owner
     */
    public TaskOwnerEnum getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(TaskOwnerEnum owner) {
        this.owner = owner;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return the dateDueFrom
     */
    public Date getDateDueFrom() {
        return dateDueFrom;
    }

    /**
     * @param dateDueFrom the dateDueFrom to set
     */
    public void setDateDueFrom(Date dateDueFrom) {
        this.dateDueFrom = dateDueFrom;
    }

    /**
     * @return the dateDueTo
     */
    public Date getDateDueTo() {
        return dateDueTo;
    }

    /**
     * @param dateDueTo the dateDueTo to set
     */
    public void setDateDueTo(Date dateDueTo) {
        this.dateDueTo = dateDueTo;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateDueFrom == null) ? 0 : dateDueFrom.hashCode());
        result = prime * result + ((dateDueTo == null) ? 0 : dateDueTo.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + priority;
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
        TaskInboxFilterBean other = (TaskInboxFilterBean) obj;
        if (dateDueFrom == null) {
            if (other.dateDueFrom != null)
                return false;
        } else if (!dateDueFrom.equals(other.dateDueFrom))
            return false;
        if (dateDueTo == null) {
            if (other.dateDueTo != null)
                return false;
        } else if (!dateDueTo.equals(other.dateDueTo))
            return false;
        if (owner != other.owner)
            return false;
        if (priority != other.priority)
            return false;
        return true;
    }


}
