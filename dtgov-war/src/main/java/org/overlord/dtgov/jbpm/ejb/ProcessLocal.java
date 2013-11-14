/**
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

package org.overlord.dtgov.jbpm.ejb;

import java.util.Collection;
import java.util.Map;

import javax.ejb.Local;

import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;

@Local
public interface ProcessLocal {
	
    public long startProcess(String deploymentId, String processId, Map<String, Object> parameters) throws Exception;
    public void signalProcess(long processInstanceId, String signalType, Object event);
    public Collection<ProcessInstanceDesc> listProcessInstances() throws Exception;
    public void listProcessInstanceDetail(long processId) throws Exception;
}
