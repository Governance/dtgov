/**
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

package org.overlord.dtgov.jbpm.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TaskBean implements TaskLocal {


    @Resource
    private UserTransaction ut;

    @Inject
    TaskService taskService;
    
    public List<TaskSummary> retrieveTaskList(String actorId) throws Exception {

        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");

        System.out.println("retrieveTaskList by " + actorId);
        for (TaskSummary task : list) {
            System.out.println(" task.getId() = " + task.getId());
            
        }

        return list;
    }
    
    public Task retrieveTask(long taskId) throws Exception {
    	
    	Task task = taskService.getTaskById(taskId);
    	
    	long processInstanceId = task.getTaskData().getProcessInstanceId();
    	
    	
    	
    	
    	long docId = taskService.getTaskById(taskId).getTaskData().getDocumentContentId();
        Content content = taskService.getContentById(docId);
        
        @SuppressWarnings("unchecked")
		Map<String,Object> inputVars = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        return task;
    	
    	
    }

    public void approveTask(String actorId, long taskId) throws Exception {

        ut.begin();

        try {
            System.out.println("approveTask (taskId = " + taskId + ") by " + actorId);
            
            Task task = taskService.getTaskById(taskId);
        	task.getTaskData().getProcessInstanceId();
            
            taskService.start(taskId, actorId);
            
            //set outputdata in the 3rd argument
            taskService.complete(taskId, actorId, null);
            
            //long docId = taskService.getTaskById(1l).getTaskData().getDocumentContentId();
            //Content content = taskService.getContentById(docId);
            
           // Map<String,Object> inputVars = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
            
            //Thread.sleep(10000); // To test OptimisticLockException

            ut.commit();
        } catch (RollbackException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof OptimisticLockException) {
                // Concurrent access to the same process instance
                throw new ProcessOperationException("The same process instance has likely been accessed concurrently",
                        e);
            }
            throw new RuntimeException(e);
        } catch (PermissionDeniedException e) {
            e.printStackTrace();
            // Transaction might be already rolled back by TaskServiceSession
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            // Probably the task has already been started by other users
            throw new ProcessOperationException("The task (id = " + taskId
                    + ") has likely been started by other users ", e);
        } catch (Exception e) {
            e.printStackTrace();
            // Transaction might be already rolled back by TaskServiceSession
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            throw new RuntimeException(e);
        } 
    }

}
