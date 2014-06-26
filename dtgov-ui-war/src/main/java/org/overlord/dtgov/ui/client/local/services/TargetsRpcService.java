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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.overlord.dtgov.ui.client.local.services.rpc.DelegatingErrorCallback;
import org.overlord.dtgov.ui.client.local.services.rpc.DelegatingRemoteCallback;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.ITargetService;

/**
 * Client-side service for making RPC calls to the remote target service. f
 * 
 * @author David Virgil Naranjo
 */
@ApplicationScoped
public class TargetsRpcService {
    @Inject
    private Caller<ITargetService> _remoteTargetService;

    /**
     * Instantiates a new targets rpc service.
     */
    public TargetsRpcService() {

    }

    /**
     * Delete.
     *
     * @param uuid
     *            the uuid
     * @param handler
     *            the handler
     */
    public void delete(String uuid, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteTargetService.call(successCallback, errorCallback).delete(uuid);
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
     */
    public void get(String uuid, final IRpcServiceInvocationHandler<TargetBean> handler) {
        RemoteCallback<TargetBean> successCallback = new DelegatingRemoteCallback<TargetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteTargetService.call(successCallback, errorCallback).get(uuid);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Save.
     *
     * @param target
     *            the target
     * @param handler
     *            the handler
     */
    public void save(TargetBean target, final IRpcServiceInvocationHandler<String> handler) {
        RemoteCallback<String> successCallback = new DelegatingRemoteCallback<String>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteTargetService.call(successCallback, errorCallback).save(target);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * List.
     *
     * @param handler
     *            the handler
     */
    public void list(final IRpcServiceInvocationHandler<List<TargetSummaryBean>> handler) {
        // TODO only allow one search at a time. If another search comes in
        // before the previous
        // one is finished, cancel the previous one. In other words, only return
        // the results of
        // the *last* search performed.
        RemoteCallback<List<TargetSummaryBean>> successCallback = new DelegatingRemoteCallback<List<TargetSummaryBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            _remoteTargetService.call(successCallback, errorCallback).list();
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }
}
