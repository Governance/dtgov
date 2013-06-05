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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kie.api.task.TaskService;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.overlord.dtgov.taskapi.types.FindTasksRequest;
import org.overlord.dtgov.taskapi.types.FindTasksResponse;
import org.overlord.dtgov.taskapi.types.StatusType;
import org.overlord.dtgov.taskapi.types.TaskSummaryType;
import org.overlord.dtgov.taskapi.types.TaskType;

/**
 *
 * @author eric.wittmann@redhat.com
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Path("/tasks")
public class TaskApi {

    @Resource
    private UserTransaction ut;
    @Inject
    private TaskService taskService;

    /**
     * Constructor.
     */
    public TaskApi() {
    }

    /**
     * Gets a list of all tasks for the authenticated user.
     * @param uri
     * @throws Exception
     */
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_XML)
    public FindTasksResponse listTasks(
            @Context HttpServletRequest httpRequest,
            @QueryParam("startIndex") Integer startIndex,
            @QueryParam("endIndex") Integer endIndex,
            @QueryParam("orderBy") String orderBy,
            @QueryParam("orderAscending") Boolean orderAscending,
            @QueryParam("status") String status,
            @QueryParam("priority") Integer priority) throws Exception {
        FindTasksRequest findTasksReq = new FindTasksRequest();
        findTasksReq.setStartIndex(0);
        if (startIndex != null) {
            findTasksReq.setStartIndex(startIndex);
        }
        findTasksReq.setEndIndex(19);
        if (endIndex != null) {
            findTasksReq.setEndIndex(endIndex);
        }
        findTasksReq.setOrderBy("priority");
        if (orderBy != null) {
            findTasksReq.setOrderBy(orderBy);
        }
        findTasksReq.setOrderAscending(false);
        if (orderAscending != null) {
            findTasksReq.setOrderAscending(orderAscending);
        }
        findTasksReq.getPriority().clear();
        if (priority != null) {
            findTasksReq.getPriority().add(priority);
        }
        findTasksReq.getStatus().clear();
        if (status != null) {
            findTasksReq.getStatus().add(StatusType.fromValue(status));
        }
        return findTasks(findTasksReq, httpRequest);
    }

    /**
     * Gets a list of all tasks for the authenticated user.  Filters the list based on the
     * criteria included in the {@link FindTasksRequest}.
     * @param findTasksRequest
     * @param httpRequest
     * @throws Exception
     */
    @POST
    @Path("find")
    @Produces(MediaType.APPLICATION_XML)
    public FindTasksResponse findTasks(final FindTasksRequest findTasksRequest, @Context HttpServletRequest httpRequest) throws Exception {
        String currentUser = httpRequest.getRemoteUser();
        if (currentUser == null) {
            throw new Exception("User not authenticated.");
        }

        FindTasksResponse response = new FindTasksResponse();
        // Get all tasks - the ones assigned as potential owner *and* the ones assigned as owner.  If
        // there is overlap we'll deal with that during the sort.
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(currentUser, "en-UK");
        list.addAll(taskService.getTasksOwned(currentUser, "en-UK"));

        final String orderBy = findTasksRequest.getOrderBy() == null ? "priority" : findTasksRequest.getOrderBy();
        final boolean ascending = findTasksRequest.isOrderAscending();
        TreeSet<TaskSummary> sortedFiltered = new TreeSet<TaskSummary>(new TaskSummaryComparator(orderBy, ascending));

        for (TaskSummary task : list) {
            if (accepts(task, findTasksRequest)) {
                sortedFiltered.add(task);
            }
        }

        int startIdx = findTasksRequest.getStartIndex();
        int endIdx = findTasksRequest.getEndIndex();
        int idx = 0;
        for (TaskSummary task : sortedFiltered) {
            if (idx >= startIdx && idx <= endIdx) {
                TaskSummaryType taskSummary = new TaskSummaryType();
                taskSummary.setId(String.valueOf(task.getId()));
                taskSummary.setName(task.getName());
                User actualOwner = task.getActualOwner();
                if (actualOwner != null) {
                    taskSummary.setOwner(actualOwner.getId());
                }
                taskSummary.setPriority(task.getPriority());
                taskSummary.setStatus(task.getStatus().toString());
                response.getTaskSummary().add(taskSummary);
            }
            idx++;
        }
        response.setTotalResults(response.getTaskSummary().size());
        return response;
    }

    /**
     * Fetches a single task by its unique ID.
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @GET
    @Path("get/{taskId}")
    @Produces(MediaType.APPLICATION_XML)
    public TaskType getTask(@Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        Task task = taskService.getTaskById(taskId);
        TaskType rval = new TaskType();
        List<I18NText> descriptions = task.getDescriptions();
        if (descriptions != null && !descriptions.isEmpty()) {
            rval.setDescription(descriptions.iterator().next().getText());
        }
        List<I18NText> names = task.getNames();
        if (names != null && !names.isEmpty()) {
            rval.setName(names.iterator().next().getText());
        }
        rval.setPriority(task.getPriority());
        rval.setId(String.valueOf(task.getId()));
        TaskData taskData = task.getTaskData();
        if (taskData != null) {
            User owner = taskData.getActualOwner();
            if (owner != null) {
                rval.setOwner(owner.getId());
            }
            Date expTime = taskData.getExpirationTime();
            if (expTime != null) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(expTime);
                DatatypeFactory dtFactory = DatatypeFactory.newInstance();
                rval.setDueDate(dtFactory.newXMLGregorianCalendar(cal));
            }
            rval.setStatus(taskData.getStatus().toString());
        }

        return rval;
    }

    /**
     * Returns true if the given task should be included in the result set based on the
     * criteria found in the request.
     * @param task
     * @param findTasksRequest
     */
    private boolean accepts(TaskSummary task, FindTasksRequest findTasksRequest) {
        Set<Integer> priorities = new HashSet<Integer>(findTasksRequest.getPriority());
        Set<StatusType> statuses = new HashSet<StatusType>(findTasksRequest.getStatus());
        if (!priorities.isEmpty() && !priorities.contains(task.getPriority())) {
            return false;
        }
        if (!statuses.isEmpty() && !statuses.contains(StatusType.fromValue(task.getStatus().toString()))) {
            return false;
        }

        XMLGregorianCalendar from = findTasksRequest.getDueOnFrom();
        if (from != null) {
            Date expirationTime = task.getExpirationTime();
            if (expirationTime == null) {
                return false;
            }
            if (expirationTime.compareTo(from.toGregorianCalendar().getTime()) < 0) {
                return false;
            }
        }
        XMLGregorianCalendar to = findTasksRequest.getDueOnTo();
        if (to != null) {
            Date expirationTime = task.getExpirationTime();
            if (expirationTime == null) {
                return false;
            }
            if (expirationTime.compareTo(to.toGregorianCalendar().getTime()) > 0) {
                return false;
            }
        }
        return true;
    }

}
