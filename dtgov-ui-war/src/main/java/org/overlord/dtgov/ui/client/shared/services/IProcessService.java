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

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;


/**
 * Process Service Interface that includes all the methods to manage a process.
 *
 * @author David Virgil Naranjo
 */
@Remote
public interface IProcessService {

    /**
     * Search all the workflow processes.
     * 
     * @param filters
     *            the filters
     * @param page
     *            the page
     * @param sortColumnId
     *            the sort column id
     * @param sortAscending
     *            the sort ascending
     * @return the processes result set bean
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public ProcessesResultSetBean search(ProcessesFilterBean filters, int page, String sortColumnId, boolean sortAscending) throws DtgovUiException;

    /**
     * Aborts a workflow process.
     * 
     * @param uuid
     *            the uuid of the workflow process artifact to abort.
     * @return true, if successful
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public boolean abort(String uuid) throws DtgovUiException;
}
