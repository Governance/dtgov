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
package org.overlord.dtgov.ui.server.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.workflow.WorkflowConstants;
import org.overlord.dtgov.ui.client.shared.beans.ProcessBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessStatusEnum;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.ProcessesResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IProcessService;
import org.overlord.dtgov.ui.server.services.dtgov.DtGovClientAccessor;
import org.overlord.dtgov.ui.server.services.dtgov.IDtgovClient;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;

/**
 * Concrete implementation of the process service interface.
 *
 * @author David Virgil Naranjo
 */
@Service
public class ProcessService implements IProcessService {

    private static final int PAGE_SIZE = 10;

    @Inject
    private SrampApiClientAccessor _srampClientAccessor;

    @Inject
    private DtGovClientAccessor _dtgovClientAccessor;

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IProcessService#search(org.overlord.dtgov.ui.client.shared.beans.ProcessesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ProcessesResultSetBean search(ProcessesFilterBean filters, int page, String sortColumnId, boolean sortAscending) throws DtgovUiException {
        int pageSize = PAGE_SIZE;
        int req_startIndex = (page - 1) * pageSize;
        SrampClientQuery query = null;
        query = createQuery(filters);
        SrampClientQuery scq = query.startIndex(req_startIndex).orderBy(sortColumnId);
        if (sortAscending) {
            scq = scq.ascending();
        } else {
            scq = scq.descending();
        }
        QueryResultSet resultSet = null;
        try {
            resultSet = scq.count(pageSize + 1).query();
        } catch (SrampClientException e) {
            throw new DtgovUiException(e);
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e);
        }

        ProcessesResultSetBean bean=new ProcessesResultSetBean();
        List<ProcessBean> processes = new ArrayList<ProcessBean>();
        for (ArtifactSummary summary : resultSet) {
            String status = summary.getCustomPropertyValue(WorkflowConstants.CUSTOM_PROPERTY_STATUS);
            String workflow = summary.getCustomPropertyValue(WorkflowConstants.CUSTOM_PROPERTY_WORKFLOW);
            String artifactName = summary.getCustomPropertyValue(WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_NAME);
            String artifactId = summary.getCustomPropertyValue(WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_ID);
            ProcessBean processBean = new ProcessBean(summary.getUuid(), workflow, artifactName, artifactId, ProcessStatusEnum.valueOf(status));
            processes.add(processBean);
        }

        boolean hasMorePages = false;
        if (processes.size() > pageSize) {
            processes.remove(processes.get(processes.size() - 1));
            hasMorePages = true;
        }
        // Does the server support opensearch style attributes? If so,
        // use that information. Else figure it out from the request params.
        if (resultSet.getTotalResults() != -1) {
            bean.setItemsPerPage(pageSize);
            bean.setStartIndex(resultSet.getStartIndex());
            bean.set_totalResults(resultSet.getTotalResults());
        } else {
            bean.setItemsPerPage(pageSize);
            bean.set_totalResults(hasMorePages ? pageSize + 1 : processes.size());
            bean.setStartIndex(req_startIndex);
        }

        bean.setProcesses(processes);
        return bean;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IProcessService#abort(java.lang.String)
     */
    @Override
    public boolean abort(String uuid) throws DtgovUiException {
        BaseArtifactType artifact;
        try {
            artifact = _srampClientAccessor.getClient().getArtifactMetaData(uuid);
        } catch (SrampClientException e1) {
            throw new DtgovUiException(e1);
        } catch (SrampAtomException e1) {
            throw new DtgovUiException(e1);
        }
        if (artifact != null) {
            String processId = SrampModelUtils.getCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_PROCESS_ID);
            String targetUUID = SrampModelUtils.getCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_ID);
            IDtgovClient client = _dtgovClientAccessor.getClient();
            try {
                client.stopProcess(targetUUID, new Long(processId));
            } catch (Exception e) {
                throw new DtgovUiException(e);
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * Creates the query.
     *
     * @param filters
     *            the filters
     * @return the sramp client query
     */
    private SrampClientQuery createQuery(ProcessesFilterBean filters) {
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query

        queryBuilder.append("/s-ramp/ext/" + WorkflowConstants.WORKFLOW_EXTENDED_TYPE); //$NON-NLS-1$

        List<Object> params = new ArrayList<Object>();
        if (filters != null) {
            List<String> criteria = new ArrayList<String>();
            if (filters.getArtifact() != null && filters.getArtifact().trim().length() > 0) {
                criteria.add("fn:matches(@" + WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_NAME + ", ?)"); //$NON-NLS-1$ //$NON-NLS-2$
                params.add(filters.getArtifact().replace("*", ".*")); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (StringUtils.isNotBlank(filters.getWorkflow())) {
                criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_WORKFLOW + " = ?"); //$NON-NLS-1$ //$NON-NLS-2$
                params.add(filters.getWorkflow());
            }

            if (filters.getStatus() != null) {
                criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_STATUS + " = ?"); //$NON-NLS-1$ //$NON-NLS-2$
                params.add(filters.getStatus().name());
            }

            // Now create the query predicate from the generated criteria
            if (criteria.size() > 0) {
                queryBuilder.append("["); //$NON-NLS-1$
                queryBuilder.append(StringUtils.join(criteria, " and ")); //$NON-NLS-1$
                queryBuilder.append("]"); //$NON-NLS-1$
            }
        }



        // Create the query, and parameterize it
        SrampAtomApiClient client = _srampClientAccessor.getClient();
        SrampClientQuery query = client.buildQuery(queryBuilder.toString());
        for (Object param : params) {
            if (param instanceof String) {
                query.parameter((String) param);
            }
            if (param instanceof Calendar) {
                query.parameter((Calendar) param);
            }
        }
        query.propertyName(WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_ID);
        query.propertyName(WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_NAME);
        query.propertyName(WorkflowConstants.CUSTOM_PROPERTY_WORKFLOW);
        query.propertyName(WorkflowConstants.CUSTOM_PROPERTY_STATUS);
        return query;
    }

    /**
     * Gets the sramp client accessor.
     *
     * @return the sramp client accessor
     */
    public SrampApiClientAccessor getSrampClientAccessor() {
        return _srampClientAccessor;
    }

    /**
     * Sets the sramp client accessor.
     *
     * @param srampClientAccessor
     *            the new sramp client accessor
     */
    public void setSrampClientAccessor(SrampApiClientAccessor srampClientAccessor) {
        this._srampClientAccessor = srampClientAccessor;
    }

    /**
     * Gets the dtgov client accessor.
     *
     * @return the dtgov client accessor
     */
    public DtGovClientAccessor getDtgovClientAccessor() {
        return _dtgovClientAccessor;
    }

    /**
     * Sets the dtgov client accessor.
     *
     * @param dtgovClientAccessor
     *            the new dtgov client accessor
     */
    public void setDtgovClientAccessor(DtGovClientAccessor dtgovClientAccessor) {
        this._dtgovClientAccessor = dtgovClientAccessor;
    }

}
