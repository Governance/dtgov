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
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 */
public class JbpmRestClientTest {
    
    @Test @Ignore//the BPM engine needs to be running for this test to pass
    public void testNewEvaluationProcessInstance() throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        JbpmRestClient jbpmClient = new JbpmRestClient(httpclient, "http://localhost:8080/gwt-console-server"); //$NON-NLS-1$
        try {
            jbpmClient.logon("krisv", "krisv"); //$NON-NLS-1$ //$NON-NLS-2$
            //parameters that will be set in the jBPM context Map
            Map<String,Object> parameters = new HashMap<String,Object>();
            parameters.put("employee", "krisv"); //$NON-NLS-1$ //$NON-NLS-2$
            parameters.put("reason", "annual review 3"); //$NON-NLS-1$ //$NON-NLS-2$
            
            jbpmClient.newProcessInstanceAndCompleteFirstTask("com.sample.evaluation",parameters); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
    
    @Test @Ignore//the BPM engine needs to be running for this test to pass
    public void testNewProcessInstance() throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        JbpmRestClient jbpmClient = new JbpmRestClient(httpclient, "http://localhost:8080/gwt-console-server"); //$NON-NLS-1$
        try {
            jbpmClient.logon("admin", "admin"); //$NON-NLS-1$ //$NON-NLS-2$
            //parameters that will be set in the jBPM context Map
            Map<String,Object> parameters = new HashMap<String,Object>();
            parameters.put("DevDeploymentUrl", "http://localhost:8080/dtgov/deploy/copy/dev/${uuid}"); //$NON-NLS-1$ //$NON-NLS-2$
            parameters.put("DevDeploymentUrlMethod", "POST"); //$NON-NLS-1$ //$NON-NLS-2$
            parameters.put("ArtifactUuid", "e67e1b09-1de7-4945-a47f-45646752437a"); //$NON-NLS-1$ //$NON-NLS-2$
            
            jbpmClient.newProcessInstanceAndCompleteFirstTask("overlord.demo.SimpleReleaseProcess",parameters); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
}
