/*
 * Copyright 2014 JBoss Inc
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
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueriesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService;


/**
 * Client-side service for making RPC calls to the remote workflow queries
 * service.
 * 
 * @author David Virgil Naranjo
 */
@ApplicationScoped
public class WorkflowQueriesRpcService {

    /** The _remote workflow query service. */
    @Inject
    private Caller<IWorkflowQueryService> _remoteWorkflowQueryService;

    /**
     * Constructor.
     */
    public WorkflowQueriesRpcService() {
    }

    /**
     * Delete.
     * 
     * @param uuid
     *            the uuid
     * @param handler
     *            the handler
     * @see org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#delete(String)
     */
    public void delete(String uuid, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteWorkflowQueryService.call(successCallback, errorCallback).delete(uuid);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Gets the.
     * 
     * @param uuid
     *            the uuid
     * @param handler
     *            the handler
     * @see org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#get(String)
     */
    public void get(String uuid, final IRpcServiceInvocationHandler<WorkflowQueryBean> handler) {
        RemoteCallback<WorkflowQueryBean> successCallback = new DelegatingRemoteCallback<WorkflowQueryBean>(
                handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteWorkflowQueryService.call(successCallback, errorCallback).get(uuid);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Gets the remote workflow query service.
     * 
     * @return the remote workflow query service
     */
    public Caller<IWorkflowQueryService> getRemoteWorkflowQueryService() {
        return _remoteWorkflowQueryService;
    }

    /**
     * Save.
     * 
     * @param query
     *            the query
     * @param handler
     *            the handler
     * @see org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#save(WorkflowQueryBean)
     */
    public void save(WorkflowQueryBean query, final IRpcServiceInvocationHandler<String> handler) {
        RemoteCallback<String> successCallback = new DelegatingRemoteCallback<String>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteWorkflowQueryService.call(successCallback, errorCallback).save(query);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Search.
     * 
     * @param filters
     *            the filters
     * @param page
     *            the page
     * @param sortColumnId
     *            the sort column id
     * @param sortAscending
     *            the sort ascending
     * @param handler
     *            the handler
     * @see org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#search(WorkflowQueriesFilterBean,int,String,boolean)
     */
    public void search(WorkflowQueriesFilterBean filters, int page, String sortColumnId,
            boolean sortAscending, final IRpcServiceInvocationHandler<WorkflowQueryResultSetBean> handler) {
        // TODO only allow one search at a time. If another search comes in
        // before the previous one
        // finished, cancel the previous one. In other words, only return the
        // results of the *last*
        // search performed.
        RemoteCallback<WorkflowQueryResultSetBean> successCallback = new DelegatingRemoteCallback<WorkflowQueryResultSetBean>(
                handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteWorkflowQueryService.call(successCallback, errorCallback).search(filters, page,
                    sortColumnId, sortAscending);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Sets the remote workflow query service.
     * 
     * @param remoteWorkflowQueryService
     *            the new remote workflow query service
     */
    public void setRemoteWorkflowQueryService(Caller<IWorkflowQueryService> remoteWorkflowQueryService) {
        this._remoteWorkflowQueryService = remoteWorkflowQueryService;
    }

}
