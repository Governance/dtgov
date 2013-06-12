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
import org.overlord.dtgov.ui.client.shared.beans.DeploymentBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IDeploymentsService;

/**
 * Client-side service for making RPC calls to the remote deployments service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class DeploymentsRpcService {

    @Inject
    private Caller<IDeploymentsService> remoteDeploymentsService;

    /**
     * Constructor.
     */
    public DeploymentsRpcService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#search(org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean, String, int)
     */
    public void search(DeploymentsFilterBean filters, String searchText, int page,
            final IRpcServiceInvocationHandler<DeploymentResultSetBean> handler) {
        // TODO only allow one search at a time.  If another search comes in before the previous one
        // finished, cancel the previous one.  In other words, only return the results of the *last*
        // search performed.
        RemoteCallback<DeploymentResultSetBean> successCallback = new DelegatingRemoteCallback<DeploymentResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteDeploymentsService.call(successCallback, errorCallback).search(filters, searchText, page);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#get(String)
     */
    public void get(String uuid, IRpcServiceInvocationHandler<DeploymentBean> handler) {
        RemoteCallback<DeploymentBean> successCallback = new DelegatingRemoteCallback<DeploymentBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteDeploymentsService.call(successCallback, errorCallback).get(uuid);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#update(DeploymentBean)
     */
    public void update(DeploymentBean deployment, IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteDeploymentsService.call(successCallback, errorCallback).update(deployment);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

}
