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
package org.overlord.dtgov.common.targets;

/**
 * Common place where all the target service constants are located.
 *
 * @author David Virgil Naranjo
 */
public interface TargetConstants {
    // COMMON CONSTANTS
    public static final String TARGET_TYPE = "deployment.type"; //$NON-NLS-1$
    public static final String TARGET_CLASSIFIERS = "deployment.classifiers"; //$NON-NLS-1$
    public static final String TARGET_CLASSIFIER_SEPARATOR = "|"; //$NON-NLS-1$
    public static final String TARGET_EXTENDED_TYPE = "DeploymentTarget"; //$NON-NLS-1$

    // RHQ CONSTANTS
    public static final String RHQ_BASE_URL = "deployment.baseUrl"; //$NON-NLS-1$
    public static final String RHQ_PLUGIN_NAME = "deployment.pluginName"; //$NON-NLS-1$
    public static final String RHQ_USER = "deployment.user"; //$NON-NLS-1$
    public static final String RHQ_PASSWORD = "deployment.password"; //$NON-NLS-1$

    // CLI CONSTANTS
    public static final String CLI_HOST = "deployment.host"; //$NON-NLS-1$
    public static final String CLI_PORT = "deployment.port"; //$NON-NLS-1$
    public static final String CLI_USER = "deployment.user"; //$NON-NLS-1$
    public static final String CLI_PASSWORD = "deployment.password"; //$NON-NLS-1$
    public static final String CLI_DOMAIN_MODE = "deployment.domainMode"; //$NON-NLS-1$
    public static final String CLI_SERVER_GROUP = "deployment.serverGroup"; //$NON-NLS-1$

    // MAVEN CONSTANTS
    public static final String MAVEN_REPOSITORY_URL = "deployment.repositoryUrl"; //$NON-NLS-1$
    public static final String MAVEN_IS_RELEASE_ENABLED = "deployment.releaseEnabled"; //$NON-NLS-1$
    public static final String MAVEN_SNAPSHOT_ENABLED = "deployment.snapshotEnabled"; //$NON-NLS-1$
    public static final String MAVEN_USER = "deployment.user"; //$NON-NLS-1$
    public static final String MAVEN_PASSWORD = "deployment.password"; //$NON-NLS-1$

    // COPY CONSTANTS
    public static final String COPY_DEPLOY_DIR = "deployment.deployDir"; //$NON-NLS-1$
}
