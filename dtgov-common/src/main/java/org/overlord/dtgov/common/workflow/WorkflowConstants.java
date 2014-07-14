package org.overlord.dtgov.common.workflow;

/**
 * Common place where all the workflow process constants are located.
 *
 * @author David Virgil Naranjo
 */
public interface WorkflowConstants {
    public static final String WORKFLOW_EXTENDED_TYPE = "DtgovWorkflow";

    public static final String CUSTOM_PROPERTY_WORKFLOW = "workflow";
    public static final String CUSTOM_PROPERTY_PROCESS_ID = "workflow.processId";
    public static final String CUSTOM_PROPERTY_NUM_PARAMS = "workflow.numParams";
    public static final String CUSTOM_PROPERTY_STATUS = "workflow.status";
    public static final String CUSTOM_PROPERTY_PARAM_PREFIX = "workflow.param.";
    public static final String RELATIONSHIP_ARTIFACT_GOVERNED = "governs";
    public static final String CUSTOM_PROPERTY_ARTIFACT_ID = "workflow.artifactId";
    public static final String CUSTOM_PROPERTY_ARTIFACT_NAME = "workflow.artifactName";
}
