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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
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

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.overlord.dtgov.jbpm.ejb.ProcessOperationException;
import org.overlord.dtgov.taskapi.types.FindTasksRequest;
import org.overlord.dtgov.taskapi.types.FindTasksResponse;
import org.overlord.dtgov.taskapi.types.StatusType;
import org.overlord.dtgov.taskapi.types.TaskDataType;
import org.overlord.dtgov.taskapi.types.TaskDataType.Entry;
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
	
	@Inject
    TaskService taskService;
	
    @Resource
    private UserTransaction ut;

    /**
     * Constructor.
     */
    public TaskApi() {}

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
    @Consumes(MediaType.APPLICATION_XML)
    public FindTasksResponse findTasks(final FindTasksRequest findTasksRequest, @Context HttpServletRequest httpRequest) throws Exception {
        String currentUser = assertCurrentUser(httpRequest);

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
                taskSummary.setStatus(StatusType.fromValue(task.getStatus().toString()));
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
        assertCurrentUser(httpRequest);
        
        Task task = taskService.getTaskById(taskId);
        
        long docId = taskService.getTaskById(taskId).getTaskData().getDocumentContentId();
        Content content = taskService.getContentById(docId);

        @SuppressWarnings("unchecked")
		Map<String,Object> inputParams = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        
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
        rval.setType(task.getTaskType());
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
            rval.setStatus(StatusType.fromValue(taskData.getStatus().toString()));
        }
        //Set the input params
        if (inputParams!=null && inputParams.size() > 0) {
        	if (rval.getTaskData()==null) rval.setTaskData(new TaskDataType());
        	for ( String key : inputParams.keySet()) {
        		Entry entry = new Entry();
        		entry.setKey(key);
        		entry.setValue(String.valueOf(inputParams.get(key)));
				rval.getTaskData().getEntry().add(entry);
			}
        }

        return rval;
    }

    /**
     * Called to claim a task.
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @GET
    @Path("claim/{taskId}")
    @Produces(MediaType.APPLICATION_XML)
    public TaskType claimTask(@Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        String currentUser = assertCurrentUser(httpRequest);
        ut.begin();
        try {
            taskService.claim(taskId, currentUser);
            ut.commit();
        } catch (Exception e) {
            handleException(e);
        }
        return getTask(httpRequest, taskId);
    }


    /**
     * Called to release a task.
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @GET
    @Path("release/{taskId}")
    @Produces(MediaType.APPLICATION_XML)
    public TaskType releaseTask(@Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        String currentUser = assertCurrentUser(httpRequest);
        ut.begin();
        try {
            taskService.release(taskId, currentUser);
            ut.commit();
        } catch (Exception e) {
            handleException(e);
        }
        return getTask(httpRequest, taskId);
    }


    /**
     * Called to start a task.
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @GET
    @Path("start/{taskId}")
    @Produces(MediaType.APPLICATION_XML)
    public TaskType startTask(@Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        String currentUser = assertCurrentUser(httpRequest);
        ut.begin();
        try {
            taskService.start(taskId, currentUser);
            ut.commit();
        } catch (Exception e) {
            handleException(e);
        }
        return getTask(httpRequest, taskId);
    }


    /**
     * Called to stop a task.
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @GET
    @Path("stop/{taskId}")
    @Produces(MediaType.APPLICATION_XML)
    public TaskType stopTask(@Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        String currentUser = assertCurrentUser(httpRequest);
        ut.begin();
        try {
            taskService.stop(taskId, currentUser);
            ut.commit();
        } catch (Exception e) {
            handleException(e);
        }
        return getTask(httpRequest, taskId);
    }


    /**
     * Called to complete a task.
     * @param taskData
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @POST
    @Path("complete/{taskId}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public TaskType completeTask(final TaskDataType taskData, @Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        String currentUser = assertCurrentUser(httpRequest);
        ut.begin();
        try {
            Map<String, Object> data = taskDataAsMap(taskData);
            taskService.complete(taskId, currentUser, data);
            ut.commit();
        } catch (Exception e) {
            handleException(e);
        }
        return getTask(httpRequest, taskId);
    }


    /**
     * Called to fail a task.
     * @param httpRequest
     * @param taskId
     * @throws Exception
     */
    @POST
    @Path("fail/{taskId}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public TaskType failTask(final TaskDataType taskData, @Context HttpServletRequest httpRequest, @PathParam("taskId") long taskId)
            throws Exception {
        String currentUser = assertCurrentUser(httpRequest);
        ut.begin();
        try {
            Map<String, Object> data = taskDataAsMap(taskData);
            taskService.fail(taskId, currentUser, data);
            ut.commit();
        } catch (Exception e) {
            handleException(e);
        }
        return getTask(httpRequest, taskId);
    }

    /**
     * Converts the inbound task data payload into a map useable by jbpm.
     * @param taskData
     */
    private Map<String, Object> taskDataAsMap(TaskDataType taskData) {
        Map<String, Object> data = new HashMap<String, Object>();
        // TODO missing type mappings here - can we convert types in some way based on a schema or something?  right now everything is a string
        for (Entry entry : taskData.getEntry()) {
            data.put(entry.getKey(), entry.getValue());
        }
        return data;
    }

    /**
     * Handles an exception that comes out of one of the task operations.  This provides a common
     * way to handle transaction rollbacks (when necessary) and also re-throws the appropriate
     * exceptions.
     * @param error
     * @throws Exception
     */
    protected void handleException(Exception error) throws Exception {
        if (error instanceof RollbackException) {
            Throwable cause = error.getCause();
            if (cause != null && cause instanceof OptimisticLockException) {
                // Concurrent access to the same process instance
                throw new ProcessOperationException("The same task instance has likely been accessed concurrently", error);
            }
            throw error;
        }
        if (error instanceof PermissionDeniedException) {
            // Transaction might be already rolled back by TaskServiceSession
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            // Probably the task has already been started by other users
            throw new ProcessOperationException("The task has likely been claimed/started by another user.", error);
        }
        // Transaction might be already rolled back by TaskServiceSession
        if (ut.getStatus() == Status.STATUS_ACTIVE) {
            ut.rollback();
        }
        throw error;
    }

    /**
     * Asserts that a user is logged in and then returns the user's id.
     * @param httpRequest
     * @throws Exception
     */
    protected String assertCurrentUser(HttpServletRequest httpRequest) throws Exception {
        String currentUser = httpRequest.getRemoteUser();
        if (currentUser == null) {
            throw new Exception("User not authenticated.");
        }
        return currentUser;
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
