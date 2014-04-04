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
package org.overlord.dtgov.ui.client.shared.services;

import java.util.Set;

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueriesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;

/**
 * Provides a way to manage workflow queries.
 *
 * @author David Virgil Naranjo
 */
@Remote
public interface IWorkflowQueryService {

    /**
     * Delete an specific uuid workflow query.
     *
     * @param uuid
     *            the uuid
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public void delete(String uuid) throws DtgovUiException;

    /**
     * Get an specific uuid workflow query.
     *
     * @param uuid
     *            the uuid
     * @return the workflow query bean
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public WorkflowQueryBean get(String uuid) throws DtgovUiException;

    /**
     * Save an specific workflow query.
     *
     * @param workflowQuery
     *            the workflow query
     * @return the string
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public String save(WorkflowQueryBean workflowQuery) throws DtgovUiException;

    /**
     * Get the Collection that contains all the workflow types loaded in the
     * system.
     * 
     * @return the workflow types
     */
    public Set<String> getWorkflowTypes() throws DtgovUiException;

    /**
     * Search for tasks using the given filters and search text.
     *
     * @param filters
     *            the filters
     * @param page
     *            the page
     * @param sortColumnId
     *            the sort column id
     * @param sortAscending
     *            the sort ascending
     * @return the workflow query result set bean
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public WorkflowQueryResultSetBean search(WorkflowQueriesFilterBean filters, int page,
            String sortColumnId, boolean sortAscending) throws DtgovUiException;

}
