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

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.workflow.BpmManager;
import org.overlord.sramp.governance.workflow.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedJbpmManager implements BpmManager {
	
	private static Logger logger = LoggerFactory.getLogger(EmbeddedJbpmManager.class);
	
    @Override
    public long newProcessInstance(String deploymentId, String processId, Map<String, Object> context) 
    		throws WorkflowException {
    	HttpURLConnection connection = null;
    	Governance governance = new Governance();
    	final String username = governance.getOverlordUser();
 	    final String password = governance.getOverlordPassword();
    	Authenticator.setDefault (new Authenticator() {
 		    protected PasswordAuthentication getPasswordAuthentication() {
 		        return new PasswordAuthentication (username, password.toCharArray());
 		    }
 		});
    	
    	try {
    		deploymentId = URLEncoder.encode(deploymentId,"UTF-8"); //$NON-NLS-1$
    		processId = URLEncoder.encode(processId,"UTF-8"); //$NON-NLS-1$
    		String urlStr = governance.getGovernanceUrl() + String.format("/rest/process/start/%s/%s",deploymentId, processId); //$NON-NLS-1$
    		URL url = new URL(urlStr); 
	        connection = (HttpURLConnection) url.openConnection();
	        StringBuffer params = new StringBuffer();
	        for (String key : context.keySet()) {
	        	String value = String.valueOf(context.get(key));
	        	value = URLEncoder.encode(value,"UTF-8"); //$NON-NLS-1$
	        	params.append(String.format("&%s=%s",key,value)); //$NON-NLS-1$
	        }
	        //remove leading '&'
	        if (params.length() > 0) params.delete(0, 1);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST"); //$NON-NLS-1$
	        connection.setConnectTimeout(60000);
	        connection.setReadTimeout(60000);
	        if (params.length() > 0) {
		        PrintWriter out = new PrintWriter(connection.getOutputStream());
	       	    out.print(params.toString());
	       	    out.close();
	        }
	        connection.connect();
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) {
	             InputStream is = (InputStream) connection.getContent();
	             String reply = IOUtils.toString(is);
	             System.out.println("reply=" + reply); //$NON-NLS-1$
	             return Long.parseLong(reply);
	        } else {
	        	logger.error("HTTP RESPONSE CODE=" + responseCode); //$NON-NLS-1$
	        	throw new WorkflowException("Unable to connect to " + urlStr); //$NON-NLS-1$
	        }
    	} catch (Exception e) {
    		throw new WorkflowException(e);
    	} finally {
    		if (connection!=null) connection.disconnect();
    	}
    }
    
    @Override
    public void signalProcess(long processInstanceId, String signalType, Object event) 
    		throws WorkflowException {
    	HttpURLConnection connection = null;
    	Governance governance = new Governance();
    	final String username = governance.getOverlordUser();
 	    final String password = governance.getOverlordPassword();
    	Authenticator.setDefault (new Authenticator() {
 		    protected PasswordAuthentication getPasswordAuthentication() {
 		        return new PasswordAuthentication (username, password.toCharArray());
 		    }
 		});
    	
    	try {
    		String urlStr = governance.getGovernanceUrl() + String.format("/rest/process/signal/%s/%s/%s",processInstanceId, signalType, event); //$NON-NLS-1$
    		URL url = new URL(urlStr); 
	        connection = (HttpURLConnection) url.openConnection();
	        
	        connection.setRequestMethod("PUT"); //$NON-NLS-1$
	        connection.setConnectTimeout(60000);
	        connection.setReadTimeout(60000);
	        connection.connect();
	        int responseCode = connection.getResponseCode();
	        if (responseCode!=200) {
	        	logger.error("HTTP RESPONSE CODE=" + responseCode); //$NON-NLS-1$
	        	throw new WorkflowException("Unable to connect to " + urlStr); //$NON-NLS-1$
	        }

	    } catch (Exception e) {
	    	throw new WorkflowException(e);
	    } finally {
	    	if (connection!=null) connection.disconnect();
	    }
    }
    
}
