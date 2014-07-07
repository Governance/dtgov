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
import org.overlord.dtgov.ui.client.shared.beans.ProcessesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IProcessService;

/**
 * Client-side service for making RPC calls to the remote processes service.
 *
 * @author David Virgil Naranjo
 */
@ApplicationScoped
public class ProcessesRpcService {
    @Inject
    private Caller<IProcessService> _remoteProcessService;

    /**
     * Instantiates a new processes rpc service.
     */
    public ProcessesRpcService() {

    }

    /**
     * Search the processes.
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
     */
    public void search(ProcessesFilterBean filters, int page, String sortColumnId, boolean sortAscending,
            final IRpcServiceInvocationHandler<ProcessesResultSetBean> handler) {
        // TODO only allow one search at a time. If another search comes in
        // before the previous
        // one is finished, cancel the previous one. In other words, only return
        // the results of
        // the *last* search performed.
        RemoteCallback<ProcessesResultSetBean> successCallback = new DelegatingRemoteCallback<ProcessesResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteProcessService.call(successCallback, errorCallback).search(filters, page, sortColumnId, sortAscending);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }


    /**
     * Abort a process.
     * 
     * @param uuid
     *            the uuid
     * @param handler
     *            the handler
     */
    public void abort(String uuid, final IRpcServiceInvocationHandler<Boolean> handler) {
        RemoteCallback<Boolean> successCallback = new DelegatingRemoteCallback<Boolean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteProcessService.call(successCallback, errorCallback).abort(uuid);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Gets the remote process service.
     *
     * @return the remote process service
     */
    public Caller<IProcessService> getRemoteProcessService() {
        return _remoteProcessService;
    }

    /**
     * Sets the remote process service.
     *
     * @param remoteProcessService
     *            the new remote process service
     */
    public void setRemoteProcessService(Caller<IProcessService> remoteProcessService) {
        this._remoteProcessService = remoteProcessService;
    }

}
