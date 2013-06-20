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
import org.overlord.dtgov.ui.client.shared.beans.ArtifactHistoryBean;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IHistoryService;

/**
 * Client-side service for making RPC calls to the remote history service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class HistoryRpcService {

    @Inject
    private Caller<IHistoryService> remoteHistoryService;

    /**
     * Constructor.
     */
    public HistoryRpcService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IHistoryService#listEvents(String)
     */
    public void listEvents(String artifactUuid, final IRpcServiceInvocationHandler<ArtifactHistoryBean> handler) {
        RemoteCallback<ArtifactHistoryBean> successCallback = new DelegatingRemoteCallback<ArtifactHistoryBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteHistoryService.call(successCallback, errorCallback).listEvents(artifactUuid);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IHistoryService#getEventDetails(String, String)
     */
    public void getEventDetails(String artifactUuid, String eventId, final IRpcServiceInvocationHandler<HistoryEventBean> handler) {
        RemoteCallback<HistoryEventBean> successCallback = new DelegatingRemoteCallback<HistoryEventBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteHistoryService.call(successCallback, errorCallback).getEventDetails(artifactUuid, eventId);
        } catch (DtgovUiException e) {
            errorCallback.error(null, e);
        }
    }

}
