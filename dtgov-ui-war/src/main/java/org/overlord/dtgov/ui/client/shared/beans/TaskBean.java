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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Models the full details of a human task.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class TaskBean extends TaskSummaryBean implements Serializable {

    private static final long serialVersionUID = TaskBean.class.hashCode();

    private String type;
    private String description;
    private Set<TaskActionEnum> allowedActions = new HashSet<TaskActionEnum>();
    private String taskForm;
    private Map<String, String> taskData;

    /**
     * Constructor.
     */
    public TaskBean() {
    }

    /**
     * Returns true if an action is allowed by this user on this task.
     * @param action
     */
    public boolean isActionAllowed(TaskActionEnum action) {
        return getAllowedActions().contains(action);
    }

    /**
     * Adds a single allowed action to this task.
     * @param action
     */
    public void addAllowedAction(TaskActionEnum action) {
        getAllowedActions().add(action);
    }

    /**
     * @return the allowedActions
     */
    public Set<TaskActionEnum> getAllowedActions() {
        return allowedActions;
    }

    /**
     * @param allowedActions the allowedActions to set
     */
    public void setAllowedActions(Set<TaskActionEnum> allowedActions) {
        this.allowedActions = allowedActions;
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

    /**
     * @return the taskForm
     */
    public String getTaskForm() {
        return taskForm;
    }

    /**
     * @param taskForm the taskForm to set
     */
    public void setTaskForm(String taskForm) {
        this.taskForm = taskForm;
    }

    /**
     * @return the taskData
     */
    public Map<String, String> getTaskData() {
        return taskData;
    }

    /**
     * @param taskData the taskData to set
     */
    public void setTaskData(Map<String, String> taskData) {
        this.taskData = taskData;
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
    public void setType(String type) {
        this.type = type;
    }

}
