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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.Property;
import org.overlord.dtgov.ui.client.shared.beans.ValidationError;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueriesFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryProperty;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQuerySummaryBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovFormValidationException;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;

// TODO: Auto-generated Javadoc
/**
 * Concrete implementation of the workflow query service.
 * 
 * @author David Virgil Naranjo
 */
@Service
public class WorkflowQueryService implements IWorkflowQueryService {

    /** The Constant PAGE_SIZE. */
    private static final int PAGE_SIZE = 10;

    /** The _query validator. */
    @Inject
    private WorkflowQueryValidator _queryValidator;

    /** The _sramp client accessor. */
    @Inject
    private SrampApiClientAccessor _srampClientAccessor;

    /**
     * Instantiates a new workflow query service.
     */
    public WorkflowQueryService() {

    }

    /**
     * Creates a query given the selected filters and search text.
     * 
     * @param filters
     *            the filters
     * @return the sramp client query
     */
    protected SrampClientQuery createQuery(WorkflowQueriesFilterBean filters) {
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query

        queryBuilder.append("/s-ramp/ext/DtgovWorkflowQuery"); //$NON-NLS-1$

        List<String> criteria = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();

        if (filters != null && filters.getName() != null && filters.getName().trim().length() > 0) {
            criteria.add("fn:matches(@name, ?)"); //$NON-NLS-1$
            params.add(filters.getName().replace("*", ".*")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (filters != null && StringUtils.isNotBlank(filters.getWorkflow())) {
            criteria.add("@workflow = ?"); //$NON-NLS-1$
            params.add(filters.getWorkflow());
        }

        // Now create the query predicate from the generated criteria
        if (criteria.size() > 0) {
            queryBuilder.append("["); //$NON-NLS-1$
            queryBuilder.append(StringUtils.join(criteria, " and ")); //$NON-NLS-1$
            queryBuilder.append("]"); //$NON-NLS-1$
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
        query.propertyName("workflow");
        query.propertyName("query");
        return query;
    }

    /*
     * Remove a WorkflowQueryBean from s-ramp passing the specific uuid
     * identifier.
     * 
     * @see
     * org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#delete
     * (java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#delete
     * (java.lang.String)
     */
    @Override
    public void delete(String uuid) throws DtgovUiException {
        try {
            _srampClientAccessor.getClient().deleteArtifact(uuid,
                    ArtifactType.ExtendedArtifactType("DtgovWorkflowQuery", false));
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }

    }

    /*
     * Get a WorkflowQueryBean from s-ramp using the uuid param.
     * 
     * @see
     * org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#get
     * (java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#get
     * (java.lang.String)
     */
    @Override
    public WorkflowQueryBean get(String uuid) throws DtgovUiException {
        try {
            BaseArtifactType artifact = _srampClientAccessor.getClient().getArtifactMetaData(uuid);
            // ArtifactType artifactType = ArtifactType.valueOf(artifact);

            WorkflowQueryBean bean = new WorkflowQueryBean();

            bean.setName(artifact.getName());
            bean.setUuid(artifact.getUuid());
            bean.setDescription(artifact.getDescription());
            for (Property prop : artifact.getProperty()) {
                if (prop.getPropertyName().equals("workflow")) {
                    bean.setWorkflow(prop.getPropertyValue());
                } else if (prop.getPropertyName().equals("query")) {
                    bean.setQuery(prop.getPropertyValue());
                } else if (prop.getPropertyName().startsWith("prop.")) {
                    String propertyName = prop.getPropertyName().substring("prop.".length());
                    bean.addWorkflowQueryProperty(propertyName, prop.getPropertyValue());
                }
            }
            return bean;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /**
     * Gets the query validator.
     * 
     * @return the query validator
     */
    public WorkflowQueryValidator getQueryValidator() {
        return _queryValidator;
    }

    /**
     * Gets the sramp client accessor.
     * 
     * @return the sramp client accessor
     */
    public SrampApiClientAccessor getSrampClientAccessor() {
        return _srampClientAccessor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#save
     * (String)
     * 
     * @return uuid
     */
    @Override
    public String save(WorkflowQueryBean workflowQuery) throws DtgovUiException {
        List<ValidationError> errors = _queryValidator.validate(workflowQuery, PAGE_SIZE);
        if (errors.size() == 0) {
            String uuid = "";
            ExtendedArtifactType toSave = new ExtendedArtifactType();
            toSave.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
            toSave.setExtendedType("DtgovWorkflowQuery");
            toSave.setName(workflowQuery.getName());
            toSave.setDescription(workflowQuery.getDescription());

            SrampModelUtils.setCustomProperty(toSave, "query", workflowQuery.getQuery());
            SrampModelUtils.setCustomProperty(toSave, "workflow", workflowQuery.getWorkflow());

            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(new Date());
            try {
                XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
                toSave.setCreatedTimestamp(xmlCal);
            } catch (DatatypeConfigurationException ee) {
                System.err.println("There is an error");
            }

            for (WorkflowQueryProperty property : workflowQuery.getProperties()) {
                SrampModelUtils.setCustomProperty(toSave, "prop." + property.getKey(), property.getValue());
            }
            SrampAtomApiClient client = _srampClientAccessor.getClient();

            if (StringUtils.isBlank(workflowQuery.getUuid())) {

                try {
                    BaseArtifactType art = client.createArtifact(toSave);
                    uuid = art.getUuid();
                } catch (Exception exc) {
                    throw new DtgovUiException("There is a problem creating the artifact", exc);
                }

            } else {
                uuid = workflowQuery.getUuid();
                toSave.setUuid(workflowQuery.getUuid());
                try {
                    client.updateArtifactMetaData(toSave);
                } catch (SrampClientException e) {
                    throw new DtgovUiException(e.getMessage());
                } catch (SrampAtomException e) {
                    throw new DtgovUiException(e.getMessage());
                }
            }
            return uuid;
        } else {
            throw new DtgovFormValidationException(errors);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.overlord.dtgov.ui.client.shared.services.IWorkflowQueryService#search
     * (org.overlord.dtgov.ui.client.shared.beans.WorkflowQueriesFilterBean,
     * int, java.lang.String, boolean)
     */
    @Override
    public WorkflowQueryResultSetBean search(WorkflowQueriesFilterBean filters, int page,
            String sortColumnId, boolean sortAscending) throws DtgovUiException {
        int pageSize = PAGE_SIZE;
        try {
            int req_startIndex = (page - 1) * pageSize;
            SrampClientQuery query = null;
            query = createQuery(filters);
            SrampClientQuery scq = query.startIndex(req_startIndex).orderBy(sortColumnId);
            if (sortAscending) {
                scq = scq.ascending();
            } else {
                scq = scq.descending();
            }
            QueryResultSet resultSet = scq.count(pageSize + 1).query();

            WorkflowQueryResultSetBean rval = new WorkflowQueryResultSetBean();
            ArrayList<WorkflowQuerySummaryBean> queries = new ArrayList<WorkflowQuerySummaryBean>();
            for (ArtifactSummary artifactSummary : resultSet) {
                WorkflowQuerySummaryBean bean = new WorkflowQuerySummaryBean();
                bean.setName(artifactSummary.getName());
                bean.setUuid(artifactSummary.getUuid());
                bean.setDescription(artifactSummary.getDescription());
                bean.setQuery(artifactSummary.getCustomPropertyValue("query"));
                bean.setWorkflow(artifactSummary.getCustomPropertyValue("workflow"));
                queries.add(bean);
            }
            boolean hasMorePages = false;
            if (queries.size() > pageSize) {
                queries.remove(queries.get(queries.size() - 1));
                hasMorePages = true;
            }
            // Does the server support opensearch style attributes? If so,
            // use that information. Else figure it out from the request params.
            if (resultSet.getTotalResults() != -1) {
                rval.setItemsPerPage(pageSize);
                rval.setStartIndex(resultSet.getStartIndex());
                rval.set_totalResults(resultSet.getTotalResults());
            } else {
                rval.setItemsPerPage(pageSize);
                rval.set_totalResults(hasMorePages ? pageSize + 1 : queries.size());
                rval.setStartIndex(req_startIndex);
            }

            rval.setQueries(queries);
            return rval;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e);
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * Sets the query validator.
     * 
     * @param queryValidator
     *            the new query validator
     */
    public void setQueryValidator(WorkflowQueryValidator queryValidator) {
        this._queryValidator = queryValidator;
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

}
