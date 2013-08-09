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
package org.overlord.dtgov.taskclient;

import org.overlord.dtgov.taskapi.types.FindTasksRequest;
import org.overlord.dtgov.taskapi.types.FindTasksResponse;
import org.overlord.dtgov.taskapi.types.TaskSummaryType;

/**
 * Simple main to test the client.
 * @author eric.wittmann@redhat.com
 */
public class TaskApiClientMain {

    public static void main(String [] args) throws Exception {
        TaskApiClient client = new TaskApiClient("http://localhost:8080/dtgov/rest/tasks", "eric", "eric"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        FindTasksRequest ftReq = new FindTasksRequest();
        ftReq.setStartIndex(0);
        ftReq.setEndIndex(19);
        ftReq.setOrderBy("taskId"); //$NON-NLS-1$
        ftReq.setOrderAscending(true);
        FindTasksResponse tasksResponse = client.findTasks(ftReq);
        for (TaskSummaryType task : tasksResponse.getTaskSummary()) {
            System.out.println(task.getId() + " - Task: " + task.getName() + " (" + task.getStatus() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if (task.getStatus().equals("Reserved")) { //$NON-NLS-1$
                client.startTask(task.getId());
            }
        }
    }

}
