package org.overlord.dtgov.common.workflow;

/**
 * Common place where all the workflow process constants are located.
 *
 * @author David Virgil Naranjo
 */
public interface WorkflowConstants {
    public static final String WORKFLOW_EXTENDED_TYPE = "DtgovWorkflow"; //$NON-NLS-1$

    public static final String CUSTOM_PROPERTY_WORKFLOW = "workflow"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_PROCESS_ID = "workflow.processId"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_NUM_PARAMS = "workflow.numParams"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_STATUS = "workflow.status"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_PARAM_PREFIX = "workflow.param."; //$NON-NLS-1$
    public static final String RELATIONSHIP_ARTIFACT_GOVERNED = "governs"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_ARTIFACT_ID = "workflow.artifactId"; //$NON-NLS-1$
    public static final String CUSTOM_PROPERTY_ARTIFACT_NAME = "workflow.artifactName"; //$NON-NLS-1$
}
