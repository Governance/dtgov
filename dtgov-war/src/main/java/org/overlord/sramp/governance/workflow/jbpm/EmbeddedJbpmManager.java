/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.sramp.governance.workflow.jbpm;

import java.util.Map;

import org.overlord.dtgov.jbpm.util.CDIUtil;
import org.overlord.sramp.governance.workflow.BpmManager;
import org.overlord.sramp.governance.workflow.WorkflowException;

public class EmbeddedJbpmManager implements BpmManager {
	
    @Override
    public void newProcessInstance(String processId, Map<String, Object> context) throws WorkflowException {
        
    	try {
    		ProcessService processService = CDIUtil.getContextualBeanInstance(ProcessService.class);
    		
    		@SuppressWarnings("unused")
			long processInstanceId = processService.newProcessInstance(processId, context);
    		
    	} catch (Exception e) {
    		throw new WorkflowException(e);
    	}
    }
    
}
