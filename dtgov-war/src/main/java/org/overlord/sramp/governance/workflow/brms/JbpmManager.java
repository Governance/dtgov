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
package org.overlord.sramp.governance.workflow.brms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.workflow.BpmManager;
import org.overlord.sramp.governance.workflow.WorkflowException;

public class JbpmManager implements BpmManager {

    Governance governance = new Governance();
    @Override
    public long newProcessInstance(String deploymentId, String processId, Map<String, Object> context) throws WorkflowException {
        try {
	    	HttpClient httpclient = new DefaultHttpClient();
	        JbpmRestClient jbpmClient = new JbpmRestClient(httpclient, governance.getBpmUrl().toExternalForm());
	        jbpmClient.logon(governance.getBpmUser(), governance.getBpmPassword());
	        jbpmClient.newProcessInstanceAndCompleteFirstTask(processId, context);
	        httpclient.getConnectionManager().shutdown();
	        return 0;
        } catch (IOException e) {
        	throw new WorkflowException(e);
        } catch (URISyntaxException e) {
        	throw new WorkflowException(e);
		}
    }
    
	@Override
	public void signalProcess(long processId, String signalType, Object event) {
		//Not implemented
		
	}
    
}
