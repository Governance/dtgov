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
package org.overlord.dtgov.ui.client.local.pages.workflowQuery;

import com.google.gwt.event.shared.EventHandler;


/**
 * Custom GWT Handler created to pass a signal from the modal box to the main
 * page that the Workflow Query has been removed and the list of items need to
 * be refreshed.
 * 
 * @author David Virgil Naranjo
 * 
 */
public interface DeleteWorkflowQueryHandler extends EventHandler {

    /**
     * On workflow query deleted.
     * 
     * @param event
     *            the event
     */
    void onWorkflowQueryDeleted(DeleteWorkflowQueryEvent event);

}
