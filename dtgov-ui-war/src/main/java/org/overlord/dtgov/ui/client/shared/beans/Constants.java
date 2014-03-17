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
package org.overlord.dtgov.ui.client.shared.beans;

/**
 * Contains a number of helpful static constants.
 * @author eric.wittmann@redhat.com
 */
public class Constants {

    // Sort columns for deployments
    public static final String SORT_COLID_NAME = "name"; //$NON-NLS-1$
    public static final String SORT_COLID_DATE_INITIATED = "createdTimestamp"; //$NON-NLS-1$

    // Sort columns for tasks
    public static final String SORT_COLID_PRIORITY = "priority"; //$NON-NLS-1$
    public static final String SORT_COLID_OWNER = "owner"; //$NON-NLS-1$
    public static final String SORT_COLID_STATUS = "status"; //$NON-NLS-1$
    public static final String SORT_COLID_DUE_ON = "expirationDate"; //$NON-NLS-1$
    
    // Sort columns for tasks
    public static final String SORT_COLID_WORKFLOW_QUERY= "query"; //$NON-NLS-1$
    public static final String SORT_COLID_WORKFLOW_TYPE = "workflow"; //$NON-NLS-1$
    public static final String SORT_COLID_WORKFLOW_NAME = "name"; //$NON-NLS-1$
    
}
