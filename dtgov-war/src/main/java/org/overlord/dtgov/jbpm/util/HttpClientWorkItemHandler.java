package org.overlord.dtgov.jbpm.util;


import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.ValueEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientWorkItemHandler implements WorkItemHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor.
     */
    public HttpClientWorkItemHandler() {
    }

    /**
     * Calls an HTTP endpoint. The address of the endpoint should be set in the
     * parameter map passed into the workItem by the BPMN workflow. Both
     * this parameters 'Url' as well as the method 'Method' are required
     * parameters.
     */
	@Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

    	ClientResponse<?> response = null;
    	Map<String,Object> results = new HashMap<String,Object>();
        try {
            // extract required parameters
            String urlStr = (String) workItem.getParameter("Url"); //$NON-NLS-1$
            String method = (String) workItem.getParameter("Method"); //$NON-NLS-1$
            if (urlStr==null || method==null) {
                throw new Exception(Messages.i18n.format("HttpClientWorkItemHandler.MissingParams")); //$NON-NLS-1$
            }
            urlStr = urlStr.toLowerCase();
            Map<String,Object> params = workItem.getParameters();

            // replace tokens in the urlStr, the replacement value of the token
            // should be set in the parameters Map
            for (String key : params.keySet()) {
                // break out if there are no (more) tokens in the urlStr
                if (! urlStr.contains("{")) break; //$NON-NLS-1$
                // replace the token if it is referenced in the urlStr
                String variable = "{" + key.toLowerCase() + "}"; //$NON-NLS-1$ //$NON-NLS-2$
                if (urlStr.contains(variable)) {
                    String escapedVariable = "\\{" + key.toLowerCase() + "\\}"; //$NON-NLS-1$ //$NON-NLS-2$
                    String urlEncodedParam = URLEncoder.encode((String) params.get(key), "UTF-8").replaceAll("%2F","*2F"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    urlStr = urlStr.replaceAll(escapedVariable, urlEncodedParam);
                }
            }
            if (urlStr.contains("{"))  //$NON-NLS-1$
                throw new Exception(Messages.i18n.format("HttpClientWorkItemHandler.IncorrectParams", urlStr)); //$NON-NLS-1$

            // call http endpoint
            log.info(Messages.i18n.format("HttpClientWorkItemHandler.CallingTo", method, urlStr)); //$NON-NLS-1$
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Governance governance = new Governance();
        	String username = governance.getOverlordUser();
        	String password = governance.getOverlordPassword();
        	httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, 
            		new UsernamePasswordCredentials(username, password));
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
            
            ClientRequest request = new ClientRequest(urlStr, executor);
            request.setHttpMethod(method);
            response = request.execute();
            int responseCode = response.getResponseStatus().getStatusCode();
            if (responseCode >= 200 && responseCode < 300) {
            	Map<String,ValueEntity> map = (Map<String, ValueEntity>) response.getEntity(new
        				GenericType<HashMap<String,ValueEntity>>() {});
        		for (String key : map.keySet()) {
        			if (map.get(key).getValue()!=null) {
        				results.put(key, map.get(key).getValue());
        			}
    			}
        		log.info("reply=" + results); //$NON-NLS-1$
            } else {
            	results.put("Status", "ERROR " + responseCode); //$NON-NLS-1$ //$NON-NLS-2$
            	results.put("StatusMsg", Messages.i18n.format("HttpClientWorkItemHandler.UnreachableEndpoint", urlStr)); //$NON-NLS-1$ //$NON-NLS-2$
                log.error(Messages.i18n.format("HttpClientWorkItemHandler.UnreachableEndpoint", urlStr)); //$NON-NLS-1$
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
        	if (response !=null) response.releaseConnection();
        }

        // notify manager that work item has been completed
        manager.completeWorkItem(workItem.getId(), results);
    }


    /**
     * Adds Authorization config to the connection prior to the request
     * being sent to the server.
     * @param connection
     */
    @SuppressWarnings("unused")
    private KeyValue getAuthProperty() {
    	Governance governance = new Governance();
    	String username = governance.getOverlordUser();
    	String password = governance.getOverlordPassword();

        if (username != null && password != null) {
            String b64Auth = Base64.encodeBase64String((username + ":" + password).getBytes()).trim(); //$NON-NLS-1$
            KeyValue keyValue = new DefaultKeyValue("Authorization", "Basic " + b64Auth); //$NON-NLS-1$ //$NON-NLS-2$
            return keyValue;
        } else {
            log.warn(Messages.i18n.format("HttpClientWorkItemHandler.MissingCreds")); //$NON-NLS-1$
            return null;
        }
    }


    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

        // Do nothing, notifications cannot be aborted

    }


}
