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
package org.overlord.dtgov.taskapi;

import java.util.Comparator;
import java.util.Date;

import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;

/**
 * A comparator used to sort and filter tasks returned from the jbpm task service.
 * @author eric.wittmann@redhat.com
 */
public class TaskSummaryComparator implements Comparator<TaskSummary> {

    private final String orderBy;
    private final boolean ascending;

    /**
     * Constructor.
     *
     * @param orderBy
     * @param ascending
     */
    public TaskSummaryComparator(String orderBy, boolean ascending) {
        this.orderBy = orderBy;
        this.ascending = ascending;
    }

    @Override
    public int compare(TaskSummary task1, TaskSummary task2) {
        if (task1.getId() == task2.getId()) {
            return 0;
        }
        int order = 0;
        if (orderBy.equals("priority")) {
            order = new Integer(task1.getPriority()).compareTo(task2.getPriority());
        } else if (orderBy.equals("name")) {
            order = task1.getName().compareTo(task2.getName());
        } else if (orderBy.equals("owner")) {
            User owner1 = task1.getActualOwner();
            User owner2 = task2.getActualOwner();
            if (owner1 == null && owner2 == null) {
                order = 0;
            } else if (owner1 == null && owner2 != null) {
                order = -1;
            } else if (owner1 != null && owner2 == null) {
                order = 1;
            } else {
                order = owner1.getId().compareTo(owner2.getId());
            }
        } else if (orderBy.equals("status")) {
            order = task1.getStatus().toString().compareTo(task2.getStatus().toString());
        } else if (orderBy.equals("expirationDate")) {
            Date date1 = task1.getExpirationTime();
            Date date2 = task2.getExpirationTime();
            if (date1 == null && date2 == null) {
                order = 0;
            } else if (date1 == null && date2 != null) {
                order = -1;
            } else if (date1 != null && date2 == null) {
                order = 1;
            } else {
                order = date1.compareTo(date2);
            }
        }
        // Task ID is always the tie breaker.
        if (order == 0) {
            order = new Long(task1.getId()).compareTo(task2.getId());
        }
        if (!ascending) {
            order *= -1;
        }
        return order;
    }
}