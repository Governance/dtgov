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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.configuration.Configuration;
import org.overlord.commons.config.JBossServer;
import org.overlord.dtgov.taskapi.types.FindTasksRequest;
import org.overlord.dtgov.taskapi.types.FindTasksResponse;
import org.overlord.dtgov.taskapi.types.StatusType;
import org.overlord.dtgov.taskapi.types.TaskDataType;
import org.overlord.dtgov.taskapi.types.TaskDataType.Entry;
import org.overlord.dtgov.taskapi.types.TaskSummaryType;
import org.overlord.dtgov.taskapi.types.TaskType;
import org.overlord.dtgov.taskclient.TaskApiClient;
import org.overlord.dtgov.taskclient.auth.AuthenticationProvider;
import org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskOwnerEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskSummaryBean;
import org.overlord.dtgov.ui.server.DtgovUIConfig;

/**
 * An implementation of a task client that goes against the Task API defined in DtGov.
 * @author eric.wittmann@redhat.com
 */
public class DtGovTaskApiClient implements ITaskClient {

    private TaskApiClient client;

    /**
     * Constructor.
     */
    public DtGovTaskApiClient(Configuration config) {
    	String defaultTaskApiEndpoint = JBossServer.getBaseUrl() + "/dtgov/rest/tasks";
        String endpoint = config.getString(DtgovUIConfig.TASK_API_ENDPOINT, defaultTaskApiEndpoint);
        AuthenticationProvider authProvider = null;
        String authProviderClass = config.getString(DtgovUIConfig.TASK_API_AUTH_PROVIDER);
        try {
            if (authProviderClass != null && authProviderClass.trim().length() > 0) {
                Class<?> c = Class.forName(authProviderClass);
                Constructor<?> constructor = null;
                try {
                    constructor = c.getConstructor(Configuration.class);
                    authProvider = (AuthenticationProvider) constructor.newInstance(config);
                } catch (NoSuchMethodException e) {}
                try {
                    constructor = c.getConstructor();
                    authProvider = (AuthenticationProvider) constructor.newInstance();
                } catch (NoSuchMethodException e) {}
            }
            client = new TaskApiClient(endpoint, authProvider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.server.services.tasks.ITaskClient#getTasks(org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean, int, int, java.lang.String, boolean)
     */
    @Override
    public TaskInboxResultSetBean getTasks(TaskInboxFilterBean filters, int startIndex, int endIndex,
            String sortColumnId, boolean sortAscending) throws Exception {
        FindTasksRequest request = createFindTasksRequest(filters);
        request.setStartIndex(startIndex);
        request.setEndIndex(endIndex);
        request.setOrderBy(sortColumnId);
        request.setOrderAscending(sortAscending);
        FindTasksResponse findTasksResponse = client.findTasks(request);

        TaskInboxResultSetBean result = new TaskInboxResultSetBean();
        result.setTasks(new ArrayList<TaskSummaryBean>());
        for (TaskSummaryType taskSummaryType : findTasksResponse.getTaskSummary()) {
            TaskSummaryBean taskSummaryBean = convertToBean(taskSummaryType);
            result.getTasks().add(taskSummaryBean);
        }
        result.setItemsPerPage((endIndex - startIndex) + 1);
        result.setTotalResults(findTasksResponse.getTotalResults());
        result.setStartIndex(startIndex);

        return result;
    }

    /**
     * @see org.overlord.dtgov.ui.server.services.tasks.ITaskClient#getTask(java.lang.String)
     */
    @Override
    public TaskBean getTask(String taskId) throws Exception {
        TaskType task = client.getTask(taskId);
        return convertToBean(task);
    }

    /**
     * @see org.overlord.dtgov.ui.server.services.tasks.ITaskClient#updateTask(org.overlord.dtgov.ui.client.shared.beans.TaskBean)
     */
    @Override
    public void updateTask(TaskBean task) throws Exception {
        // TODO implement updating a task!!  what are the jbpm/dtgov capabilities here?
    }

    /**
     * @see org.overlord.dtgov.ui.server.services.tasks.ITaskClient#executeAction(org.overlord.dtgov.ui.client.shared.beans.TaskBean, org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum)
     */
    @Override
    public TaskBean executeAction(TaskBean task, TaskActionEnum action) throws Exception {
        TaskType updatedTask = null;
        if (action == TaskActionEnum.claim) {
            updatedTask = client.claimTask(task.getId());
        } else if (action == TaskActionEnum.complete) {
            updatedTask = client.completeTask(task.getId(), task.getTaskData());
        } else if (action == TaskActionEnum.start) {
            updatedTask = client.startTask(task.getId());
        } else if (action == TaskActionEnum.stop) {
            updatedTask = client.stopTask(task.getId());
        } else if (action == TaskActionEnum.release) {
            updatedTask = client.releaseTask(task.getId());
        } else if (action == TaskActionEnum.fail) {
            updatedTask = client.failTask(task.getId(), task.getTaskData());
        }
        return convertToBean(updatedTask);
    }

    /**
     * @throws DatatypeConfigurationException
     */
    protected DatatypeFactory getXmlFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the given task summary API object to a UI bean.
     * @param taskSummaryType
     */
    private TaskSummaryBean convertToBean(TaskSummaryType taskSummaryType) {
        TaskSummaryBean bean = new TaskSummaryBean();
        XMLGregorianCalendar dueDate = taskSummaryType.getDueDate();
        if (dueDate != null) {
            bean.setDueDate(dueDate.toGregorianCalendar().getTime());
        }
        bean.setId(taskSummaryType.getId());
        bean.setName(taskSummaryType.getName());
        bean.setOwner(taskSummaryType.getOwner());
        bean.setPriority(taskSummaryType.getPriority());
        bean.setStatus(taskSummaryType.getStatus().value());
        return bean;
    }

    /**
     * Converts the given task API object to a UI bean.
     * @param taskSummaryType
     */
    private TaskBean convertToBean(TaskType taskType) {
        TaskBean bean = new TaskBean();
        XMLGregorianCalendar dueDate = taskType.getDueDate();
        if (dueDate != null) {
            bean.setDueDate(dueDate.toGregorianCalendar().getTime());
        }
        bean.setId(taskType.getId());
        bean.setName(taskType.getName());
        bean.setOwner(taskType.getOwner());
        bean.setPriority(taskType.getPriority());
        bean.setStatus(taskType.getStatus().value());
        bean.setType(taskType.getType());
        bean.setDescription(taskType.getDescription());
        Map<String, String> taskData = new HashMap<String, String>();
        TaskDataType data = taskType.getTaskData();
        if (data != null) {
            List<Entry> entries = data.getEntry();
            for (Entry entry : entries) {
                taskData.put(entry.getKey(), entry.getValue());
            }
        }
        bean.setTaskData(taskData);
        assignAvailableActions(bean, taskType.getStatus());
        return bean;
    }

    /**
     * Adds the available actions to the task bean based on the status of the task.
     * @param bean
     * @param status
     */
    private void assignAvailableActions(TaskBean bean, StatusType status) {
        if (status == StatusType.COMPLETED || status == StatusType.FAILED || status == StatusType.ERROR) {
            // No actions available - tasks are complete.
        } else if (status == StatusType.READY) {
            bean.addAllowedAction(TaskActionEnum.claim);
        } else if (status == StatusType.IN_PROGRESS) {
            bean.addAllowedAction(TaskActionEnum.stop);
            bean.addAllowedAction(TaskActionEnum.complete);
            bean.addAllowedAction(TaskActionEnum.fail);
        } else if (status == StatusType.RESERVED) {
            bean.addAllowedAction(TaskActionEnum.release);
            bean.addAllowedAction(TaskActionEnum.start);
        }
    }

    /**
     * Creates a {@link FindTasksRequest} from the given task inbox filter bean.
     * @param filters
     */
    protected FindTasksRequest createFindTasksRequest(TaskInboxFilterBean filters) {
        FindTasksRequest request = new FindTasksRequest();
        if (filters.getPriority() >= 0) {
            request.getPriority().add(filters.getPriority());
        }
        if (filters.getDateDueFrom() != null) {
            DatatypeFactory dtFactory = getXmlFactory();
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(filters.getDateDueFrom());
            XMLGregorianCalendar from = dtFactory.newXMLGregorianCalendar(cal);
            request.setDueOnFrom(from);
        }
        if (filters.getDateDueTo() != null) {
            DatatypeFactory dtFactory = getXmlFactory();
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(filters.getDateDueTo());
            XMLGregorianCalendar to = dtFactory.newXMLGregorianCalendar(cal);
            request.setDueOnTo(to);
        }
        TaskOwnerEnum owner = filters.getOwner();
        if (owner == TaskOwnerEnum.any) {
            request.getStatus().add(StatusType.CREATED);
            request.getStatus().add(StatusType.READY);
            request.getStatus().add(StatusType.IN_PROGRESS);
            request.getStatus().add(StatusType.RESERVED);
        } else if (owner == TaskOwnerEnum.active) {
            request.getStatus().add(StatusType.IN_PROGRESS);
        } else if (owner == TaskOwnerEnum.mine) {
            request.getStatus().add(StatusType.IN_PROGRESS);
            request.getStatus().add(StatusType.RESERVED);
        } else if (owner == TaskOwnerEnum.group) {
            request.getStatus().add(StatusType.CREATED);
            request.getStatus().add(StatusType.READY);
        }
        return request;
    }

    /**
     * @see org.overlord.dtgov.ui.server.services.tasks.ITaskClient#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale) {
        client.setLocale(locale);
    }

}
