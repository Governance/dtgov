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
package org.overlord.dtgov.ui.client.shared.services;

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.dtgov.ui.client.shared.beans.TaskBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;

/**
 * Provides a way to get and set Task meta data and invoke actions on a task.
 *
 * @author eric.wittmann@redhat.com
 */
@Remote
public interface ITaskService {

    /**
     * Gets the full meta data for an artifact (by UUID).
     * @param uuid
     * @throws SrampUiException
     */
    public TaskBean get(String id) throws DtgovUiException;

    /**
     * Called to update the given task.
     * @param task
     * @throws SrampUiException
     */
    public void update(TaskBean task) throws DtgovUiException;

}
