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
package org.overlord.dtgov.ui.server.services.workflows;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryBean;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQueryProperty;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQuerySummaryBean;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;

/**
 * Factory class that contains all the code related conversions from S-ramp
 * Object to WorkflowQueryBean and viceversa.
 * 
 * @author David Virgil Naranjo
 */
public class WorkflowQueryFactory {

    public static final String WORKFLOW_QUERY_WORKFLOW = "workflow";
    public static final String WORKFLOW_QUERY_QUERY = "query";
    public static final String WORKFLOW_QUERY_PROPS_PREFIX = "prop.";

    /**
     * To workflow query.
     *
     * @param artifact
     *            the artifact
     * @return the workflow query bean
     */
    public static WorkflowQueryBean toWorkflowQuery(BaseArtifactType artifact) {
        WorkflowQueryBean bean = new WorkflowQueryBean();

        bean.setName(artifact.getName());
        bean.setUuid(artifact.getUuid());
        bean.setDescription(artifact.getDescription());
        bean.setWorkflow(SrampModelUtils.getCustomProperty(artifact, WORKFLOW_QUERY_WORKFLOW));
        bean.setQuery(SrampModelUtils.getCustomProperty(artifact, WORKFLOW_QUERY_QUERY));
        Map<String, String> props = SrampModelUtils.getCustomPropertiesByPrefix(artifact, WORKFLOW_QUERY_PROPS_PREFIX);
        if (props != null && !props.isEmpty()) {
            for (String key : props.keySet()) {
                bean.addWorkflowQueryProperty(key, props.get(key));
            }
        }
        return bean;
    }

    /**
     * To base artifact.
     *
     * @param workflowQuery
     *            the workflow query
     * @return the base artifact type
     */
    public static BaseArtifactType toBaseArtifact(WorkflowQueryBean workflowQuery) {
        ExtendedArtifactType toSave = new ExtendedArtifactType();
        toSave.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        toSave.setExtendedType("DtgovWorkflowQuery"); //$NON-NLS-1$
        toSave.setName(workflowQuery.getName());
        toSave.setDescription(workflowQuery.getDescription());

        SrampModelUtils.setCustomProperty(toSave, "query", workflowQuery.getQuery()); //$NON-NLS-1$
        SrampModelUtils.setCustomProperty(toSave, "workflow", workflowQuery.getWorkflow()); //$NON-NLS-1$

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(new Date());
        try {
            XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            toSave.setCreatedTimestamp(xmlCal);
        } catch (DatatypeConfigurationException ee) {
            throw new RuntimeException(ee);
        }

        for (WorkflowQueryProperty property : workflowQuery.getProperties()) {
            SrampModelUtils.setCustomProperty(toSave, "prop." + property.getKey(), property.getValue()); //$NON-NLS-1$
        }
        return toSave;
    }

    /**
     * As list.
     *
     * @param resultSet
     *            the result set
     * @return the list
     */
    public static List<WorkflowQuerySummaryBean> asList(QueryResultSet resultSet) {
        ArrayList<WorkflowQuerySummaryBean> queries = new ArrayList<WorkflowQuerySummaryBean>();
        for (ArtifactSummary artifactSummary : resultSet) {
            WorkflowQuerySummaryBean bean = new WorkflowQuerySummaryBean();
            bean.setName(artifactSummary.getName());
            bean.setUuid(artifactSummary.getUuid());
            bean.setDescription(artifactSummary.getDescription());
            bean.setQuery(artifactSummary.getCustomPropertyValue("query")); //$NON-NLS-1$
            bean.setWorkflow(artifactSummary.getCustomPropertyValue("workflow")); //$NON-NLS-1$
            queries.add(bean);
        }
        return queries;
    }
}
