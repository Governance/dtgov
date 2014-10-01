package org.overlord.dtgov.karaf.commands;

/**
 * Constants class used to store the common properties used in the s-ramp karaf
 * commands.
 *
 * @author David Virgil Naranjo
 */
public interface ConfigureConstants {
    public static final String DTGOV_WORKFLOW_USER = "dtgovworkflow"; //$NON-NLS-1$

    public static final String DTGOV_WORKFLOW_USER_GRANTS = "overlorduser,admin.sramp";

    public static final String DTGOV_PROPERTIES_FILE_NAME = "dtgov.properties";

    public static final String DTGOV_WORKFLOW_PASSWORD = "${dtgov.users.workflow.password}";
    // FABRIC CONSTANTS

    // Ui headers:

    public static final String DTGOV_HEADER_HREF = "overlord.headerui.apps.dtgov.href";
    public static final String DTGOV_HEADER_HREF_VALUE = "/dtgov-ui/";
    public static final String DTGOV_HEADER_LABEL = "overlord.headerui.apps.dtgov.label";
    public static final String DTGOV_HEADER_LABEL_VALUE = "Design Time";
    public static final String DTGOV_HEADER_PRIMARY_BRAND = "overlord.headerui.apps.dtgov.primary-brand";
    public static final String DTGOV_HEADER_PRIMARY_BRAND_VALUE = "JBoss Overlord";
    public static final String DTGOV_HEADER_SECOND_BRAND = "overlord.headerui.apps.dtgov.secondary-brand";
    public static final String DTGOV_HEADER_SECOND_BRAND_VALUE = "Governance";

}
