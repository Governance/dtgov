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
package org.overlord.dtgov.ui.server.services.tasks;

import java.util.Locale;

import org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxResultSetBean;

/**
 * A client used to access a human task repository (e.g. a WS-HumanTask server, or a proprietary
 * task inbox backend).
 *
 * @author eric.wittmann@redhat.com
 */
public interface ITaskClient {

    /**
     * Gets tasks from a back-end task system.
     * @param filters
     * @param startIndex
     * @param endIndex
     */
    public TaskInboxResultSetBean getTasks(TaskInboxFilterBean filters, int startIndex, int endIndex) throws Exception;

    /**
     * Gets a single task by its ID.
     * @param taskId
     */
    public TaskBean getTask(String taskId) throws Exception;

    /**
     * Updates a single task's meta data (only those fields that are allowed to be updated).
     * @param task
     */
    public void updateTask(TaskBean task) throws Exception;

    /**
     * Executes the given action for the given task.  Returns a new version of the task bean
     * reflecting any changes made by the action (e.g. change of status and available actions).
     * @param task
     * @param action
     */
    public TaskBean executeAction(TaskBean task, TaskActionEnum action) throws Exception;

    /**
     * Sets the currently locale for the task client.
     * @param locale
     */
    public void setLocale(Locale locale);

}
