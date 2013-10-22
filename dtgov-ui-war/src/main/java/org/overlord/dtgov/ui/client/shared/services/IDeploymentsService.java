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
package org.overlord.dtgov.ui.client.shared.services;

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactsBean;
import org.overlord.dtgov.ui.client.shared.beans.ExpandedArtifactsBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;

/**
 * Provides a way to manage deployments.
 *
 * @author eric.wittmann@redhat.com
 */
@Remote
public interface IDeploymentsService {

    /**
     * Search for deployments using the given filters and search text.
     * @param filters
     * @param searchText
     * @param page
     * @param sortColumnId
     * @param sortAscending
     * @throws DtgovUiException
     */
    public DeploymentResultSetBean search(DeploymentsFilterBean filters, String searchText, int page,
            String sortColumnId, boolean sortAscending) throws DtgovUiException;

    /**
     * Fetches a full deployment by its ID.
     * @param uuid
     * @throws DtgovUiException
     */
    public DeploymentBean get(String uuid) throws DtgovUiException;

    /**
     * Updates the meta-data of a deployment.
     * @param deployment
     * @throws DtgovUiException
     */
    public void update(DeploymentBean deployment) throws DtgovUiException;

    /**
     * Returns all artifacts expanded from the given deployment.
     * @param uuid
     * @throws DtgovUiException
     */
    public ExpandedArtifactsBean listExpandedArtifacts(String uuid) throws DtgovUiException;

    /**
     * Returns all artifacts expanded from the given deployment.
     * @param uuid
     * @throws DtgovUiException
     */
    public DerivedArtifactsBean listDerivedArtifacts(String uuid) throws DtgovUiException;

}
