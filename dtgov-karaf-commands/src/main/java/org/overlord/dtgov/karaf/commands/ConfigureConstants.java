package org.overlord.dtgov.karaf.commands;

/**
 * Constants class used to store the common properties used in the s-ramp karaf
 * commands.
 *
 * @author David Virgil Naranjo
 */
public interface ConfigureConstants {
    public static final String DTGOV_WORKFLOW_USER = "dtgovworkflow"; //$NON-NLS-1$

    public static final String DTGOV_WORKFLOW_USER_GRANTS = "overlorduser,admin.sramp"; //$NON-NLS-1$

    public static final String DTGOV_PROPERTIES_FILE_NAME = "dtgov.properties"; //$NON-NLS-1$

    public static final String DTGOV_WORKFLOW_PASSWORD = "${dtgov.users.workflow.password}"; //$NON-NLS-1$
    // FABRIC CONSTANTS

    // Ui headers:

    public static final String DTGOV_HEADER_HREF = "overlord.headerui.apps.dtgov.href"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_HREF_VALUE = "/dtgov-ui/"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_LABEL = "overlord.headerui.apps.dtgov.label"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_LABEL_VALUE = "Design Time"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_PRIMARY_BRAND = "overlord.headerui.apps.dtgov.primary-brand"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_PRIMARY_BRAND_VALUE = "JBoss Overlord"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_SECOND_BRAND = "overlord.headerui.apps.dtgov.secondary-brand"; //$NON-NLS-1$
    public static final String DTGOV_HEADER_SECOND_BRAND_VALUE = "Governance"; //$NON-NLS-1$

}
