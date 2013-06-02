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
package org.overlord.dtgov.ui.server.services;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxResultSetBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.ITaskInboxService;
import org.overlord.dtgov.ui.server.api.TaskClientAccessor;

/**
 * Concrete implementation of the task inbox service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class TaskInboxService implements ITaskInboxService {

    @Inject
    private TaskClientAccessor taskClientAccessor;

    /**
     * Constructor.
     */
    public TaskInboxService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.ITaskInboxService#search(org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean, java.lang.String, int)
     */
    @Override
    public TaskInboxResultSetBean search(TaskInboxFilterBean filters, String searchText, int page)
            throws DtgovUiException {
        // TODO Auto-generated method stub
        return null;
    }

}
