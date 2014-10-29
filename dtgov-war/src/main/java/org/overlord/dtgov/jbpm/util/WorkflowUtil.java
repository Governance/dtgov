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
package org.overlord.dtgov.jbpm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.Relationship;
import org.overlord.dtgov.common.model.Workflow;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.SrampMavenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that has the ability of reading information about the workflows
 * from an s-ramp repository. It allows to give the kie information about an
 * specific bpmn document, return all the workflows abailable and return all the
 * kie jars.
 *
 * @author David Virgil Naranjo
 */
public class WorkflowUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String SRAMP_KIE_MODEL = "/s-ramp/ext/KieJarArchive"; //$NON-NLS-1$
    private static final String WORKFLOW_PACKAGE;
    private static final String WORKFLOW_DEFAULT_VERSION;

    private final static String SRAMP_WORKFLOW_QUERY = "/s-ramp/ext/BpmnDocument[expandedFromDocument[@maven.groupId = ? and @maven.artifactId = ? and @maven.version = ?]]"; //$NON-NLS-1$

    private static Governance governance = new Governance();
    static {
        Governance governance = new Governance();
        WORKFLOW_PACKAGE = governance.getGovernanceWorkflowPackage();
        WORKFLOW_DEFAULT_VERSION = governance.getGovernanceWorkflowVersion();
    }

    /**
     * Gets the current workflows.
     *
     * @param client
     *            the client
     * @return the current workflows
     */
    public static List<Workflow> getCurrentWorkflows(SrampAtomApiClient client) {
        List<Workflow> currentWorkflows = new ArrayList<Workflow>();
        // First we read all the defined kie jars.
        List<KieJar> currentKieJars = getCurrentKieJar(client);
        if (currentKieJars != null && currentKieJars.size() > 0) {
            for (KieJar kieJar : currentKieJars) {
                // with this query we ll obtain all the bmpn documents contained
                // in the kie jar
                String query = SRAMP_WORKFLOW_QUERY;
                SrampClientQuery queryClient = client.buildQuery(query);
                queryClient = queryClient.parameter(kieJar.getGroupId()).parameter(kieJar.getArtifactId()).parameter(kieJar.getVersion());
                QueryResultSet resultSet = null;
                try {
                    resultSet = queryClient.query();
                } catch (SrampClientException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } catch (SrampAtomException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                // Iterate in the bpmn files
                if (resultSet != null && resultSet.size() > 0) {
                    for (ArtifactSummary summary : resultSet) {
                        Workflow workflow = new Workflow();
                        workflow.setName(summary.getName());
                        workflow.setUuid(summary.getUuid());
                        currentWorkflows.add(workflow);
                    }
                }
            }
        }

        return currentWorkflows;
    }

    /**
     * Gets the current kie jars defined in s-ramp.
     *
     * @param client
     *            the client
     * @return the current kie jar
     */
    public static List<KieJar> getCurrentKieJar(SrampAtomApiClient client) {
        List<KieJar> currentKieArtifacts = new ArrayList<KieJar>();
        // this query returns all the kie artifact returning back the artifact
        // and group id
        String query = SRAMP_KIE_MODEL;
        SrampClientQuery queryClient = client.buildQuery(query);
        queryClient.propertyName("maven.artifactId");//$NON-NLS-1$
        queryClient.propertyName("maven.groupId");//$NON-NLS-1$
        QueryResultSet resultSet = null;
        try {
            resultSet = queryClient.query();
        } catch (SrampClientException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (SrampAtomException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (resultSet != null && resultSet.size() > 0) {
            // this variable will be used to check which artifacts names have
            // been processed, as the same artifact can appear as result with a
            // different version number
            Map<String, KieJar> added = new HashMap<String, KieJar>();
            // iterate in the kie jar results. We need to find the latest
            // version, or the version defined in the dtgov.properties
            for (ArtifactSummary summary : resultSet) {
                //
                String groupId = summary.getCustomPropertyValue("maven.groupId");//$NON-NLS-1$
                String artifactId = summary.getCustomPropertyValue("maven.artifactId");//$NON-NLS-1$
                String key = groupId + ":" + artifactId;
                if (!added.containsKey(key)) {
                    // if it is not added we get the last version
                    String version = null;
                    try {
                        version = SrampMavenUtil.getVersion(SRAMP_KIE_MODEL, groupId, artifactId, WORKFLOW_DEFAULT_VERSION);
                    } catch (SrampClientException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    } catch (SrampAtomException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    // Then fill the KieJar pojo and add to the map
                    if (version != null) {
                        KieJar kieJar = new KieJar();
                        kieJar.setArtifactId(artifactId);
                        kieJar.setGroupId(groupId);
                        kieJar.setVersion(version);
                        kieJar.setWorkflowKSession(Governance.DEFAULT_GOVERNANCE_WORKFLOW_KSESSION);
                        kieJar.setWorkflowPackage(WORKFLOW_PACKAGE);
                        added.put(key, kieJar);
                    }
                }
            }
            // we return the values of the map, that should be unique.
            currentKieArtifacts.addAll(added.values());
        }

        return currentKieArtifacts;
    }

    /**
     * Gets the kie jar from bmpn name.
     *
     * @param client
     *            the client
     * @param workflowId
     *            the workflow id
     * @return the kie jar from bmpn name
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    public static KieJar getKieJarFromBmpnName(SrampAtomApiClient client, String workflowId) throws SrampClientException, SrampAtomException {
        String query = "/s-ramp/ext/BpmnDocument";//$NON-NLS-1$
        query += "[@name= ?]"; //$NON-NLS-1$
        SrampClientQuery queryClient = client.buildQuery(query);
        queryClient = queryClient.parameter(workflowId + ".bpmn");//$NON-NLS-1$
        QueryResultSet resultSet = queryClient.query();
        if (resultSet.size() == 1) {
            ArtifactSummary summary = resultSet.get(0);
            BaseArtifactType bpmnArtifact = client.getArtifactMetaData(summary.getType(), summary.getUuid());
            for (Relationship relationship : bpmnArtifact.getRelationship()) {
                // Get the expanded Relationship that should be a kiejar
                // document
                if (relationship.getRelationshipType().equals("expandedFromDocument")) {//$NON-NLS-1$
                    String uuid = relationship.getRelationshipTarget().get(0).getValue();
                    // Look for the propietary kie jar from the uuid
                    String query_workflow_jar = SRAMP_KIE_MODEL + "[@uuid= ?]";//$NON-NLS-1$
                    SrampClientQuery queryWorkflowClient = client.buildQuery(query_workflow_jar);
                    queryWorkflowClient = queryWorkflowClient.parameter(uuid);
                    queryWorkflowClient.propertyName("maven.artifactId");//$NON-NLS-1$
                    queryWorkflowClient.propertyName("maven.groupId");//$NON-NLS-1$
                    queryWorkflowClient.propertyName("maven.version");//$NON-NLS-1$
                    resultSet = queryWorkflowClient.query();
                    if (resultSet.size() == 1) {
                        ArtifactSummary summaryArtf = resultSet.get(0);

                        String groupId = summaryArtf.getCustomPropertyValue("maven.groupId");//$NON-NLS-1$
                        String artifactId = summaryArtf.getCustomPropertyValue("maven.artifactId");//$NON-NLS-1$
                        String version = summaryArtf.getCustomPropertyValue("maven.version");//$NON-NLS-1$
                        String workflowVersion = getVersion(groupId, artifactId, version);
                        // Fill the KieJar information with the data readed from
                        // the query.
                        KieJar workflow = new KieJar();
                        workflow.setArtifactId(artifactId);
                        workflow.setGroupId(groupId);
                        workflow.setVersion(workflowVersion);
                        workflow.setWorkflowKSession(Governance.DEFAULT_GOVERNANCE_WORKFLOW_KSESSION);
                        workflow.setWorkflowPackage(WORKFLOW_PACKAGE);
                        workflow.setUuid(uuid);
                        return workflow;
                    }

                }
            }
        }
        return null;

    }

    /**
     * Gets the version.
     *
     * @param groupId
     *            the group id
     * @param artifactId
     *            the artifact id
     * @param version
     *            the version
     * @return the version
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    private static String getVersion(String groupId, String artifactId, String version) throws SrampClientException, SrampAtomException {
        String workflowVersionConstant = governance.getGovernanceWorkflowVersion();
        String workflowVersion = "";
        if (SrampMavenUtil.isConstantValue(workflowVersionConstant)) {
            workflowVersion = SrampMavenUtil.getVersion(SRAMP_KIE_MODEL, groupId, artifactId, workflowVersionConstant);
            if (StringUtils.isBlank(workflowVersion)) {
                workflowVersion = version;
            }
        } else {
            workflowVersion = version;
        }
        return workflowVersion;
    }
}
