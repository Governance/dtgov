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
package org.overlord.dtgov.ui.client.local.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.overlord.dtgov.ui.client.local.services.rpc.DelegatingErrorCallback;
import org.overlord.dtgov.ui.client.local.services.rpc.DelegatingRemoteCallback;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.TaskActionEnum;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.ITaskInboxService;

/**
 * Client-side service for making RPC calls to the remote artifact service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class TaskInboxRpcService {

    @Inject
    private Caller<ITaskInboxService> remoteTaskInboxService;

    /**
     * Constructor.
     */
    public TaskInboxRpcService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#search(TaskInboxFilterBean, int)
     */
    public void search(TaskInboxFilterBean filters, int page,
            final IRpcServiceInvocationHandler<TaskInboxResultSetBean> handler) {
        // TODO only allow one search at a time.  If another search comes in before the previous one
        // finished, cancel the previous one.  In other words, only return the results of the *last*
        // search performed.
        RemoteCallback<TaskInboxResultSetBean> successCallback = new DelegatingRemoteCallback<TaskInboxResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteTaskInboxService.call(successCallback, errorCallback).search(filters, page);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#get(String)
     */
    public void get(String taskId, IRpcServiceInvocationHandler<TaskBean> handler) {
        RemoteCallback<TaskBean> successCallback = new DelegatingRemoteCallback<TaskBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteTaskInboxService.call(successCallback, errorCallback).get(taskId);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#update(TaskBean)
     */
    public void update(TaskBean task, IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteTaskInboxService.call(successCallback, errorCallback).update(task);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Executes the given action on the task.
     * @param task
     * @param action
     * @param handler
     */
    public void executeAction(TaskBean task, TaskActionEnum action, IRpcServiceInvocationHandler<TaskBean> handler) {
        RemoteCallback<TaskBean> successCallback = new DelegatingRemoteCallback<TaskBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteTaskInboxService.call(successCallback, errorCallback).executeAction(task, action);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

}
