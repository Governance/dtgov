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

package org.overlord.dtgov.ui.client.local.services;

/**
 * Constants used when storing state in the app state service.
 *
 * @author eric.wittmann@redhat.com
 */
public final class ApplicationStateKeys {

	public static final String TASK_INBOX_FILTER = "task-inbox.filter-bean"; //$NON-NLS-1$
	public static final String TASK_INBOX_PAGE = "task-inbox.page"; //$NON-NLS-1$
    public static final String TASK_INBOX_SORT_COLUMN = "task-inbox.sort-column"; //$NON-NLS-1$

    public static final String DEPLOYMENTS_FILTER = "deployments.filter-bean"; //$NON-NLS-1$
    public static final String DEPLOYMENTS_PAGE = "deployments.page"; //$NON-NLS-1$
    public static final String DEPLOYMENTS_SEARCH_TEXT = "deployments.search-text"; //$NON-NLS-1$
    public static final String DEPLOYMENTS_SORT_COLUMN = "deployments.sort-column"; //$NON-NLS-1$
    
    public static final String WORKFLOW_QUERIES_FILTER = "workflow-queries.filter-bean"; //$NON-NLS-1$
    public static final String WORKFLOW_QUERIES_PAGE = "workflow-queries.page"; //$NON-NLS-1$
    public static final String WORKFLOW_QUERIES_SORT_COLUMN = "workflow-queries.sort-column"; //$NON-NLS-1$
	
}
