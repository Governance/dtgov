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
package org.overlord.dtgov.ui.server.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IDeploymentsService;
import org.overlord.dtgov.ui.server.api.SrampApiClientAccessor;

/**
 * Concrete implementation of the task inbox service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class DeploymentsService implements IDeploymentsService {

    private static final int PAGE_SIZE = 20;

    @Inject
    private SrampApiClientAccessor srampClientAccessor;

    /**
     * Constructor.
     */
    public DeploymentsService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#search(org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean, java.lang.String, int)
     */
    @Override
    public DeploymentResultSetBean search(DeploymentsFilterBean filters, String searchText, int page)
            throws DtgovUiException {
        DeploymentResultSetBean result = new DeploymentResultSetBean();
        result.setItemsPerPage(20);
        result.setStartIndex(0);
        result.setTotalResults(42);
        ArrayList<DeploymentSummaryBean> deployments = new ArrayList<DeploymentSummaryBean>();
        for (int i = 0; i < 20; i++) {
            DeploymentSummaryBean bean = new DeploymentSummaryBean();
            bean.setInitiatedDate(new Date());
            bean.setName("switchyard-application-" + i + ".jar");
            bean.setStage("DEV");
            bean.setType("SwitchYardApplication");
            bean.setUuid(UUID.randomUUID().toString());
            deployments.add(bean);
        }
        result.setDeployments(deployments);
        return result;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#get(java.lang.String)
     */
    @Override
    public DeploymentBean get(String uuid) throws DtgovUiException {
        throw new DtgovUiException("Get Deployment not yet implemented.");
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#update(org.overlord.dtgov.ui.client.shared.beans.DeploymentBean)
     */
    @Override
    public void update(DeploymentBean deployment) throws DtgovUiException {
        throw new DtgovUiException("Update Deployment not yet implemented.");
    }

}
