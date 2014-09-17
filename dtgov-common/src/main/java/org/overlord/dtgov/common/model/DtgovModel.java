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

package org.overlord.dtgov.common.model;



/**
 * The data model for dtgov as found in the S-RAMP repository.
 *
 * @author eric.wittmann@redhat.com
 */
public class DtgovModel {

    public static final String DeploymentTargetType        = "DtgovDeploymentTarget"; //$NON-NLS-1$
    public static final String WorkflowInstanceType        = "DtgovWorkflowInstance"; //$NON-NLS-1$
    public static final String WorkflowQueryType           = "DtgovWorkflowQuery"; //$NON-NLS-1$
    public static final String UndeploymentInformationType = "UndeploymentInformation"; //$NON-NLS-1$
    public static final String EmailTemplateType           = "DtgovEmailTemplate"; //$NON-NLS-1$
    public static final String DataInitializedType = "DtgovDataInitialized"; //$NON-NLS-1$


    /* *****************************************************************
     * Undeployment Information Properties
     * ***************************************************************** */

    public static final String CUSTOM_PROPERTY_DEPLOY_TARGET     = "deploy.target"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_DEPLOY_TYPE       = "deploy.type"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_DEPLOY_CLASSIFIER = "deploy.classifier"; //$NON-NLS-1$
    public static final String RELATIONSHIP_DESCRIBED_DEPLOYMENT = "describesDeployment"; //$NON-NLS-1$


    /* *****************************************************************
     * Email Template Properties
     * ***************************************************************** */

    public static final String CUSTOM_PROPERTY_TEMPLATE       = "template"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_TEMPLATE_TYPE  = "template-type"; //$NON-NLS-1$


    /* *****************************************************************
     * Workflow Query Properties
     * ***************************************************************** */

    public static final String CUSTOM_PROPERTY_QUERY     = "query"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_WORKFLOW  = "workflow"; //$NON-NLS-1$


    /* *****************************************************************
     * Workflow Instance Properties
     * ***************************************************************** */

    //public static final String CUSTOM_PROPERTY_WORKFLOW = "workflow"; (shared with DtgovWorkflowQuery)
    public static final String CUSTOM_PROPERTY_PROCESS_ID     = "workflow.processId"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_NUM_PARAMS     = "workflow.numParams"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_STATUS         = "workflow.status"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_PARAM_PREFIX   = "workflow.param."; //$NON-NLS-1$
    public static final String RELATIONSHIP_ARTIFACT_GOVERNED = "governs"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_ARTIFACT_ID    = "workflow.artifactId"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_ARTIFACT_NAME  = "workflow.artifactName"; //$NON-NLS-1$


    /* DTGOV INITIALIZATION PROPERTIES */

    public static final String DataInitializedName = "DtgovInit"; //$NON-NLS-1$

    /* *****************************************************************
     * Deployment Target Properties
     * ***************************************************************** */

    // COMMON CONSTANTS
    public static final String TARGET_TYPE                 = "deployment.type"; //$NON-NLS-1$
    public static final String TARGET_CLASSIFIERS          = "deployment.classifiers"; //$NON-NLS-1$
    public static final String TARGET_CLASSIFIER_SEPARATOR = "|"; //$NON-NLS-1$

    // RHQ CONSTANTS
    public static final String RHQ_BASE_URL                = "deployment.baseUrl"; //$NON-NLS-1$
    public static final String RHQ_PLUGIN_NAME             = "deployment.pluginName"; //$NON-NLS-1$
    public static final String RHQ_USER                    = "deployment.user"; //$NON-NLS-1$
    public static final String RHQ_PASSWORD                = "deployment.password"; //$NON-NLS-1$
    public static final String RHQ_GROUP                   = "deployment.group"; //$NON-NLS-1$

    // CLI CONSTANTS
    public static final String CLI_HOST                    = "deployment.host"; //$NON-NLS-1$
    public static final String CLI_PORT                    = "deployment.port"; //$NON-NLS-1$
    public static final String CLI_USER                    = "deployment.user"; //$NON-NLS-1$
    public static final String CLI_PASSWORD                = "deployment.password"; //$NON-NLS-1$
    public static final String CLI_DOMAIN_MODE             = "deployment.domainMode"; //$NON-NLS-1$
    public static final String CLI_SERVER_GROUP            = "deployment.serverGroup"; //$NON-NLS-1$

    // MAVEN CONSTANTS
    public static final String MAVEN_REPOSITORY_URL        = "deployment.repositoryUrl"; //$NON-NLS-1$
    public static final String MAVEN_IS_RELEASE_ENABLED    = "deployment.releaseEnabled"; //$NON-NLS-1$
    public static final String MAVEN_SNAPSHOT_ENABLED      = "deployment.snapshotEnabled"; //$NON-NLS-1$
    public static final String MAVEN_USER                  = "deployment.user"; //$NON-NLS-1$
    public static final String MAVEN_PASSWORD              = "deployment.password"; //$NON-NLS-1$

    // COPY CONSTANTS
    public static final String COPY_DEPLOY_DIR             = "deployment.deployDir"; //$NON-NLS-1$

    // CUSTOM TARGET CONSTANTS
    public static final String CUSTOM_TYPE_NAME            = "deployment.custom.type"; //$NON-NLS-1$
    public static final String PREFIX_CUSTOM_PROPERTY      = "deployment.custom.property."; //$NON-NLS-1$

}
