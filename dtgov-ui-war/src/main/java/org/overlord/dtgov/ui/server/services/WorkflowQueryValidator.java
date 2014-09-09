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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.overlord.dtgov.common.model.DtgovModel;
import org.overlord.dtgov.ui.client.shared.beans.ValidationError;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryProperty;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.QueryResultSet;


/**
 * Validator class that makes the validation of the workflow query bean that is
 * gonna be saved in s-ramp.
 *
 * @author David Virgil Naranjo
 *
 */
@Dependent
public class WorkflowQueryValidator {

    /** The _sramp client accessor. */
    @Inject
    private SrampApiClientAccessor _srampClientAccessor;

    /** The Constant NOT_UNIQUE_LABEL. */
    private final static String NOT_UNIQUE_LABEL = "workflowQuery.validation.error.not.unique.name"; //$NON-NLS-1$

    /** The Constant INCORRECT_QUERY_LABEL. */
    private final static String INCORRECT_QUERY_LABEL = "workflowQuery.validation.error.not.correct.query"; //$NON-NLS-1$

    /** The Constant NAME_REQUIRED_LABEL. */
    private final static String NAME_REQUIRED_LABEL = "workflowQuery.validation.error.name.required"; //$NON-NLS-1$

    /** The Constant QUERY_REQUIRED_LABEL. */
    private final static String QUERY_REQUIRED_LABEL = "workflowQuery.validation.error.query.required"; //$NON-NLS-1$

    /** The Constant WORKFLOW_REQUIRED_LABEL. */
    private final static String WORKFLOW_REQUIRED_LABEL = "workflowQuery.validation.error.workflow.required"; //$NON-NLS-1$

    /** The Constant PROPERTIES_EMPTY_LABEL. */
    private final static String PROPERTIES_EMPTY_LABEL = "workflowQuery.validation.error.properties.empty"; //$NON-NLS-1$

    /** The Constant PROPERTIES_REPEATED_LABEL. */
    private final static String PROPERTIES_REPEATED_LABEL = "workflowQuery.validation.error.repeated.properties"; //$NON-NLS-1$

    /**
     * Instantiates a new workflow query validator.
     */
    public WorkflowQueryValidator() {

    }

    /*
     * Pass the different validation rules to the WorkflowQueryBean and returns
     * a List of validation errors. In case of no error, the list is empty.
     */
    /**
     * Validate.
     *
     * @param query
     *            the query
     * @param page_size
     *            the page_size
     * @return the list
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public List<ValidationError> validate(WorkflowQueryBean query, int page_size) throws DtgovUiException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!isUniqueName(query, page_size)) {
            errors.add(new ValidationError(NOT_UNIQUE_LABEL));
        }
        if (StringUtils.isBlank(query.getName())) {
            errors.add(new ValidationError(NAME_REQUIRED_LABEL));
        }
        if (StringUtils.isBlank(query.getQuery())) {
            errors.add(new ValidationError(QUERY_REQUIRED_LABEL));
        } else if (!isSRampQueryCorrect(query)) {
            errors.add(new ValidationError(INCORRECT_QUERY_LABEL));
        }
        if (StringUtils.isBlank(query.getWorkflow())) {
            errors.add(new ValidationError(WORKFLOW_REQUIRED_LABEL));
        }

        List<String> query_names = new ArrayList<String>();
        for (WorkflowQueryProperty property : query.getProperties()) {
            if (StringUtils.isBlank(property.getKey()) || StringUtils.isBlank(property.getValue())) {
                errors.add(new ValidationError(PROPERTIES_EMPTY_LABEL));
                break;
            }
            if (query_names.contains(property.getKey())) {
                errors.add(new ValidationError(PROPERTIES_REPEATED_LABEL));
                break;
            }
        }
        return errors;
    }

    /*
     * Validate if the s-ramp query has a correct format or not.
     */
    /**
     * Checks if is s ramp query correct.
     *
     * @param workflowQuery
     *            the workflow query
     * @return true, if is s ramp query correct
     */
    private boolean isSRampQueryCorrect(WorkflowQueryBean workflowQuery) {
        boolean validQuery = true;
        SrampAtomApiClient client = _srampClientAccessor.getClient();
        SrampClientQuery query = client.buildQuery(workflowQuery.getQuery());
        try {
            query.query();
        } catch (SrampClientException e) {
            validQuery = false;
        } catch (SrampAtomException e) {
            validQuery = false;
        }
        return validQuery;
    }

    /*
     * Validate the uniqueness of the query name in s-ramp. It is mandatory to
     * have an unique name to be saved in s-ramp.
     */
    /**
     * Checks if is unique name.
     *
     * @param workflowQuery
     *            the workflow query
     * @param page_size
     *            the page_size
     * @return true, if is unique name
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    private boolean isUniqueName(WorkflowQueryBean workflowQuery, int page_size) throws DtgovUiException {
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query

        queryBuilder.append("/s-ramp/ext/" + DtgovModel.WorkflowQueryType); //$NON-NLS-1$

        List<String> criteria = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();

        criteria.add("fn:matches(@name, ?)"); //$NON-NLS-1$
        params.add(workflowQuery.getName().replace("*", ".*")); //$NON-NLS-1$ //$NON-NLS-2$

        queryBuilder.append("["); //$NON-NLS-1$
        queryBuilder.append(StringUtils.join(criteria, " and ")); //$NON-NLS-1$
        queryBuilder.append("]"); //$NON-NLS-1$

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
        QueryResultSet resultSet = null;
        try {
            resultSet = query.count(page_size + 1).query();
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
        if (StringUtils.isNotBlank(workflowQuery.getUuid())) {
            if (resultSet.size() == 1 && resultSet.get(0).getUuid().equals(workflowQuery.getUuid())) {
                return true;
            }
        } else {
            if (resultSet.size() == 0) {
                return true;
            }
        }
        return false;

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

}
