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

/**
 * Some Governance constants.
 *
 */
public interface GovernanceConstants {

    //Configuration constants
    public static final String GOVERNANCE_FILE_NAME     = "governance.file.name"; //$NON-NLS-1$
    public static final String GOVERNANCE_FILE_REFRESH  = "governance.file.refresh"; //$NON-NLS-1$
    public static final String SRAMP_REPO_URL           = "sramp.repo.url"; //$NON-NLS-1$
    public static final String SRAMP_REPO_AUTH_PROVIDER = "sramp.repo.auth.provider"; //$NON-NLS-1$
    public static final String SRAMP_REPO_USER          = "sramp.repo.user"; //$NON-NLS-1$
    public static final String SRAMP_REPO_PASSWORD      = "sramp.repo.password"; //$NON-NLS-1$
    public static final String SRAMP_REPO_VALIDATING    = "sramp.repo.validating"; //$NON-NLS-1$

    public static final String GOVERNANCE_URL           = "governance.url"; //$NON-NLS-1$
    public static final String GOVERNANCE_QUERY_INTERVAL= "governance.query.interval"; //$NON-NLS-1$
    public static final String GOVERNANCE_ACCEPTABLE_LAG= "governance.acceptable.lagtime";  //$NON-NLS-1$
    public static final String GOVERNANCE_JNDI_EMAIL_REF= "governance.jndi.email.reference"; //$NON-NLS-1$
    public static final String GOVERNANCE_EMAIL_DOMAIN  = "governance.email.domain"; //$NON-NLS-1$
    public static final String GOVERNANCE_EMAIL_FROM    = "governance.email.from"; //$NON-NLS-1$


    //RHQ connection info
    public static final String GOVERNANCE_RHQ_USER     = "rhq.rest.user"; //$NON-NLS-1$
    public static final String GOVERNANCE_RHQ_PASSWORD = "rhq.rest.password"; //$NON-NLS-1$
    public static final String GOVERNANCE_RHQ_URL      = "rhq.base.url"; //$NON-NLS-1$

    //BPM connection info
    public static final String GOVERNANCE_BPM_USER     = "governance.bpm.user"; //$NON-NLS-1$
    public static final String GOVERNANCE_BPM_PASSWORD = "governance.bpm.password"; //$NON-NLS-1$
    public static final String GOVERNANCE_BPM_URL      = "governance.bpm.url"; //$NON-NLS-1$

    //governance resource configuration
    public static final String GOVERNANCE_USER          = "governance.user"; //$NON-NLS-1$
    public static final String GOVERNANCE_PASSWORD      = "governance.password"; //$NON-NLS-1$
    public static final String GOVERNANCE_TARGETS       = "governance.targets"; //$NON-NLS-1$
    public static final String GOVERNANCE_QUERIES       = "governance.queries"; //$NON-NLS-1$
    public static final String GOVERNANCE               = "governance."; //$NON-NLS-1$
    public static final String GOVERNANCE_UI            = "dtgov.ui.url"; //$NON-NLS-1$

    //S-RAMP
    public static final String SRAMP_WAGON_JAR          = "s-ramp-wagon"; //$NON-NLS-1$
    public static final String SRAMP_WAGON_SNAPSHOTS    = "dtgov.s-ramp-wagon.snapshots"; //$NON-NLS-1$
    public static final String SRAMP_WAGON_RELEASES     = "dtgov.s-ramp-wagon.releases"; //$NON-NLS-1$

    //Workflow config
    public static final String GOVERNANCE_DTGOV            = "dtgov"; //$NON-NLS-1$
    public static final String GOVERNANCE_WORKFLOW_GROUP   = "dtgov.workflows.group"; //$NON-NLS-1$
    public static final String GOVERNANCE_WORKFLOW_NAME    = "dtgov.workflows.name"; //$NON-NLS-1$
    public static final String GOVERNANCE_WORKFLOW_VERSION = "dtgov.workflows.version"; //$NON-NLS-1$
    public static final String GOVERNANCE_WORKFLOW_PACKAGE = "dtgov.workflows.package"; //$NON-NLS-1$
    
    //ArtifactInfo
    public static final String STATUS                   = "status"; //$NON-NLS-1$
    public static final String MESSAGE                  = "message"; //$NON-NLS-1$
    public static final String TARGET                   = "target"; //$NON-NLS-1$
    public static final String ARTIFACT_NAME            = "artifactName"; //$NON-NLS-1$
    public static final String ARTIFACT_DESCRIPTION     = "artifactDescription"; //$NON-NLS-1$
    public static final String ARTIFACT_CREATED_BY      = "artifactCreatedBy"; //$NON-NLS-1$
    
    
}
