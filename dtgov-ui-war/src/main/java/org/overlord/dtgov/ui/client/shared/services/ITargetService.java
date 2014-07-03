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
package org.overlord.dtgov.ui.client.shared.services;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;

/**
 * Target Service Interface that includes all the methods to manage a Target
 *
 * @author David Virgil Naranjo
 */
@Remote
public interface ITargetService {


    /**
     * Delete the target from the system.
     *
     * @param uuid
     *            the uuid
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public void delete(String uuid) throws DtgovUiException;


    /**
     * Gets the Target with the uuid param.
     *
     * @param uuid
     *            the uuid
     * @return the target bean
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public TargetBean get(String uuid) throws DtgovUiException;


    /**
     * Save the target in the system. It updates or creates a new Target,
     * depends on the value of the uuid.
     *
     * @param target
     *            the target bean
     * @return the string
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public String save(TargetBean target) throws DtgovUiException;

    /**
     * Lists all the Target stored in the system.
     * 
     * @return the list
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public List<TargetSummaryBean> list() throws DtgovUiException;
}
