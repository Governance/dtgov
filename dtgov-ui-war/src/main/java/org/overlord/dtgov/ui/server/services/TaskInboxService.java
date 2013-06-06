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
package org.overlord.dtgov.ui.server.services;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.ITaskInboxService;
import org.overlord.dtgov.ui.server.services.tasks.ITaskClient;
import org.overlord.dtgov.ui.server.services.tasks.TaskClientAccessor;

/**
 * Concrete implementation of the task inbox service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class TaskInboxService implements ITaskInboxService {

    private static final int PAGE_SIZE = 20;

    @Inject
    private TaskClientAccessor taskClientAccessor;

    /**
     * Constructor.
     */
    public TaskInboxService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#search(org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean, int)
     */
    @Override
    public TaskInboxResultSetBean search(TaskInboxFilterBean filters, int page)
            throws DtgovUiException {
        ITaskClient client = taskClientAccessor.getClient();

        int startIndex = (page-1) * PAGE_SIZE;
        int endIndex = (startIndex + PAGE_SIZE) - 1;

        try {
            return client.getTasks(filters, startIndex, endIndex);
        } catch (Exception e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#get(java.lang.String)
     */
    @Override
    public TaskBean get(String taskId) throws DtgovUiException {
        ITaskClient client = taskClientAccessor.getClient();
        try {
            return client.getTask(taskId);
        } catch (Exception e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#update(org.overlord.dtgov.ui.client.shared.beans.TaskBean)
     */
    @Override
    public void update(TaskBean task) throws DtgovUiException {
        ITaskClient client = taskClientAccessor.getClient();
        try {
            client.updateTask(task);
        } catch (Exception e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#executeAction(org.overlord.dtgov.ui.client.shared.beans.TaskBean, org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum)
     */
    @Override
    public TaskBean executeAction(TaskBean task, TaskActionEnum action) throws DtgovUiException {
        ITaskClient client = taskClientAccessor.getClient();
        try {
            return client.executeAction(task, action);
        } catch (Exception e) {
            throw new DtgovUiException(e);
        }
    }

}
