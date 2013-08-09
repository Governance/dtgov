/*
 * Copyright 2012 JBoss Inc
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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.Property;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.governance.workflow.BpmManager;
import org.overlord.sramp.governance.workflow.WorkflowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author <a href="mailto:kstam@jboss.com">Kurt T Stam</a>
 *
 */
@Named
@RequestScoped
public class QueryExecutor {

    private static String WORKFLOW_PROCESS_ID = "workflowProcessId="; //$NON-NLS-1$
    private static String WORKFLOW_PARAMETERS = "workflowParameters="; //$NON-NLS-1$
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Governance governance = new Governance();

    private BpmManager bpmManager = WorkflowFactory.newInstance();

    public synchronized void execute() throws SrampClientException, MalformedURLException, ConfigException {
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        //for all queries defined in the governance.properties file
        Iterator<Query> queryIterator = governance.getQueries().iterator();
        while (queryIterator.hasNext()) {
            Query query = queryIterator.next();
            try {
                String srampQuery = query.getSrampQuery();
                QueryResultSet queryResultSet = client.query(srampQuery);
                if (queryResultSet.size() > 0) {
                    Iterator<ArtifactSummary> queryResultIterator = queryResultSet.iterator();
                    while (queryResultIterator.hasNext()) {
                        ArtifactSummary artifactSummary = queryResultIterator.next();
                        BaseArtifactType artifact = client.getArtifactMetaData(artifactSummary.getType(), artifactSummary.getUuid());
                        List<Property> properties = artifact.getProperty();
                        String name  = WORKFLOW_PROCESS_ID + query.getWorkflowId();
                        String value = WORKFLOW_PARAMETERS + query.getParameters();
                        String propertyName = null;
                        boolean hasPropertyName = false;
                        Map<String,String> propertyMap = new HashMap<String,String>();
                        for (Property property : properties) {
                            propertyMap.put(property.getPropertyName(), property.getPropertyValue());
                            if (property.getPropertyName().startsWith(name) && property.getPropertyValue().equals(value)) {
                                hasPropertyName = true;
                            }
                        }
                        if (hasPropertyName) {
                            if (logger.isDebugEnabled())
                                logger.debug(Messages.i18n.format(
                                        "QueryExecutor.ExistingWorkflowError", //$NON-NLS-1$
                                        artifact.getUuid(), query.getWorkflowId(), query.getParameters()));
                        } else {
                            propertyName = WORKFLOW_PROCESS_ID + query.getWorkflowId() + "_"; //$NON-NLS-1$
                            // set this process as a property
                            int i=0;
                            while (propertyMap.keySet().contains(propertyName + i)) {
                                i++;
                            }
                            Property property = new Property();
                            property.setPropertyName(propertyName + i);
                            property.setPropertyValue(WORKFLOW_PARAMETERS + query.getParameters());
                            artifact.getProperty().add(property);
                            client.updateArtifactMetaData(artifact);
                            //start workflow for this artifact
                            logger.info(Messages.i18n.format("QueryExecutor.StartingWorkflow", query.getWorkflowId(), artifact.getUuid())); //$NON-NLS-1$
                            Map<String,Object> parameters = query.getParsedParameters();
                            parameters.put("ArtifactUuid", artifact.getUuid()); //$NON-NLS-1$
                            bpmManager.newProcessInstance(query.getWorkflowId(), parameters);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(Messages.i18n.format("QueryExecutor.ExceptionFor", query.getSrampQuery(), e.getMessage()), e); //$NON-NLS-1$
            }
        }
    }

}
