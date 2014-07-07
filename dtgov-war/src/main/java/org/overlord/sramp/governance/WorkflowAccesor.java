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
package org.overlord.sramp.governance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.overlord.dtgov.common.workflow.WorkflowConstants;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clas with the necessary methods to access to the s-ramp workflow artifacts
 *
 * @author David Virgil Naranjo
 */
public class WorkflowAccesor {

    private static Logger _logger = LoggerFactory.getLogger(WorkflowAccesor.class);

    private final SrampAtomApiClient _client;

    /**
     * Instantiates a new workflow accesor.
     */
    public WorkflowAccesor() {
        _client = SrampAtomApiClientFactory.createAtomApiClient();
    }
    public enum WorkflowStatusEnum implements Serializable {
        RUNNING, ABORTED, COMPLETED
    }

    /**
     * Converts the values passed as params in sramp workflow artifact.
     *
     * @param targetUUID
     *            the target uuid
     * @param targetName
     *            the target name
     * @param workflow
     *            the workflow
     * @param processInstanceId
     *            the process instance id
     * @param status
     *            the status
     * @param parameters
     *            the parameters
     * @return the base artifact type
     */
    private BaseArtifactType toWorkflowArtifact(String targetUUID, String targetName, String workflow, String processInstanceId,
            WorkflowStatusEnum status,
            Map<String, String> parameters) {
        ExtendedArtifactType artifact = new ExtendedArtifactType();
        artifact.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        artifact.setExtendedType(WorkflowConstants.WORKFLOW_EXTENDED_TYPE);
        artifact.setName(buildArtifactName(targetName));

        SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_WORKFLOW, workflow);
        SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_PROCESS_ID, processInstanceId);
        SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_STATUS, status.name());
        SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_ID, targetUUID);
        SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_NAME, targetName);
        if (parameters != null && parameters.size() > 0) {
            SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_NUM_PARAMS, parameters.size() + "");
            for (String param_key : parameters.keySet()) {
                SrampModelUtils.setCustomProperty(artifact, WorkflowConstants.CUSTOM_PROPERTY_PARAM_PREFIX + param_key, parameters.get(param_key));
            }
        }

        SrampModelUtils.addGenericRelationship(artifact, WorkflowConstants.RELATIONSHIP_ARTIFACT_GOVERNED, targetUUID);
        return artifact;
    }

    /**
     * Save a new workflow type artifact.
     * 
     * @param targetUUID
     *            the target uuid
     * @param targetName
     *            the target name
     * @param workflow
     *            the workflow
     * @param processInstanceId
     *            the process instance id
     * @param parameters
     *            the parameters
     * @return the string
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    public String save(String targetUUID, String targetName, String workflow, String processInstanceId, Map<String, String> parameters)
            throws SrampClientException, SrampAtomException {
        BaseArtifactType artifact = toWorkflowArtifact(targetUUID, targetName, workflow, processInstanceId, WorkflowStatusEnum.RUNNING, parameters);
        artifact = _client.createArtifact(artifact);
        if (artifact != null) {
            return artifact.getUuid();
        }
        return null;
    }

    /**
     * Gets the process ids.
     *
     * @param targetUUID
     *            the target uuid
     * @return the process ids
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    public List<Long> getProcessIds(String targetUUID) throws SrampClientException, SrampAtomException {
        List<Long> processes = new ArrayList<Long>();
        String query = "/s-ramp/ext/" + WorkflowConstants.WORKFLOW_EXTENDED_TYPE;
        query += "[@" + WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_ID + "= ?]";
        SrampClientQuery queryClient = _client.buildQuery(query);
        queryClient = queryClient.propertyName(WorkflowConstants.CUSTOM_PROPERTY_PROCESS_ID);
        queryClient=queryClient.parameter(targetUUID);
        QueryResultSet resultSet = queryClient.query();
        for (ArtifactSummary summary : resultSet) {
            String processId = summary.getCustomPropertyValue(WorkflowConstants.CUSTOM_PROPERTY_PROCESS_ID);
            processes.add(new Long(processId));
        }
        return processes;

    }

    /**
     * Exist running workflow.
     *
     * @param targetUUID
     *            the target uuid
     * @param targetName
     *            the target name
     * @param workflow
     *            the workflow
     * @param processInstanceId
     *            the process instance id
     * @param parameters
     *            the parameters
     * @return true, if successful
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    public boolean existRunningWorkflow(String targetUUID, String targetName, String workflow, String processInstanceId,
            Map<String, String> parameters) throws SrampClientException, SrampAtomException {
        SrampClientQuery query = buildQuery(targetUUID, targetName, workflow, processInstanceId, null, parameters);

        query = query.startIndex(0); //$NON-NLS-1$
        QueryResultSet resultSet = query.query();
        if (resultSet.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Builds the artifact name.
     *
     * @param targetName
     *            the target name
     * @return the string
     */
    private String buildArtifactName(String targetName) {
        return targetName + "_workflow";
    }

    /**
     * Builds the query.
     *
     * @param targetUUID
     *            the target uuid
     * @param targetName
     *            the target name
     * @param workflow
     *            the workflow
     * @param processInstanceId
     *            the process instance id
     * @param status
     *            the status
     * @param parameters
     *            the parameters
     * @return the sramp client query
     */
    private SrampClientQuery buildQuery(String targetUUID, String targetName, String workflow, String processInstanceId, WorkflowStatusEnum status,
            Map<String, String> parameters) {
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query

        queryBuilder.append("/s-ramp/ext/" + WorkflowConstants.WORKFLOW_EXTENDED_TYPE); //$NON-NLS-1$

        List<String> criteria = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();
        criteria.add("fn:matches(@name, ?)"); //$NON-NLS-1$
        params.add(buildArtifactName(targetName));
        criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_ID + "= ?"); //$NON-NLS-1$
        params.add(targetUUID);
        criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_ARTIFACT_NAME + "= ?"); //$NON-NLS-1$
        params.add(targetName);
        if (StringUtils.isNotBlank(workflow)) {
            criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_WORKFLOW + "= ?"); //$NON-NLS-1$
            params.add(workflow);
        }
        if (StringUtils.isNotBlank(processInstanceId)) {
            criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_PROCESS_ID + "= ?"); //$NON-NLS-1$
            params.add(processInstanceId);
        }
        if (status != null) {
            criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_STATUS + "= ?"); //$NON-NLS-1$
            params.add(status.name());
        }
        if (parameters != null && parameters.size() > 0) {
            criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_NUM_PARAMS + "= ?"); //$NON-NLS-1$
            params.add(parameters.size() + "");

            for (String param_key : parameters.keySet()) {
                criteria.add("@" + WorkflowConstants.CUSTOM_PROPERTY_PARAM_PREFIX+param_key + "= ?"); //$NON-NLS-1$
                params.add(parameters.get(param_key));
            }
        }
        // Now create the query predicate from the generated criteria
        if (criteria.size() > 0) {
            queryBuilder.append("["); //$NON-NLS-1$
            queryBuilder.append(StringUtils.join(criteria, " and ")); //$NON-NLS-1$
            queryBuilder.append("]"); //$NON-NLS-1$
        }

        SrampClientQuery query = _client.buildQuery(queryBuilder.toString());
        for (Object param : params) {
            if (param instanceof String) {
                query.parameter((String) param);
            }
            if (param instanceof Calendar) {
                query.parameter((Calendar) param);
            }
        }
        return query;
    }

    /**
     * Gets the client.
     *
     * @return the client
     */
    public SrampAtomApiClient getClient() {
        return _client;
    }

}
