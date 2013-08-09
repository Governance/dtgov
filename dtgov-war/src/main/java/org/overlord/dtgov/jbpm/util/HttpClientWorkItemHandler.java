package org.overlord.dtgov.jbpm.util;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.governance.Governance;
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

        try {
            // extract required parameters
            String urlStr = (String) workItem.getParameter("Url"); //$NON-NLS-1$
            String method = (String) workItem.getParameter("Method"); //$NON-NLS-1$
            if (urlStr==null || method==null) {
                throw new Exception(Messages.i18n.format("HttpClientWorkItemHandler.MissingParams")); //$NON-NLS-1$
            }
            urlStr = urlStr.toLowerCase();
            Map<String,Object> params = workItem.getParameters();

            // optional timeout config parameters, defaulted to 60 seconds
            Integer connectTimeout = (Integer) params.get("ConnectTimeout"); //$NON-NLS-1$
            if (connectTimeout==null) connectTimeout = 60000;
            Integer readTimeout = (Integer) params.get("ReadTimeout"); //$NON-NLS-1$
            if (readTimeout==null) readTimeout = 60000;

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
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            addAuthorization(connection);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                InputStream is = (InputStream) connection.getContent();
                String reply = IOUtils.toString(is);
                log.info("reply=" + reply); //$NON-NLS-1$
            } else {
                workItem.getParameters().put("Status", "ERROR " + responseCode); //$NON-NLS-1$ //$NON-NLS-2$
                workItem.getParameters().put("StatusMsg", Messages.i18n.format("HttpClientWorkItemHandler.UnreachableEndpoint", urlStr)); //$NON-NLS-1$ //$NON-NLS-2$
                log.error(Messages.i18n.format("HttpClientWorkItemHandler.UnreachableEndpoint", urlStr)); //$NON-NLS-1$
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        // notify manager that work item has been completed
        manager.completeWorkItem(workItem.getId(), null);
    }


    /**
     * Adds Authorization config to the connection prior to the request
     * being sent to the server.
     * @param connection
     */
    private void addAuthorization(HttpURLConnection connection) {
    	Governance governance = new Governance();
    	String username = governance.getOverlordUser();
    	String password = governance.getOverlordPassword();

        if (username != null && password != null) {
            String b64Auth = Base64.encodeBase64String((username + ":" + password).getBytes()).trim(); //$NON-NLS-1$
            connection.setRequestProperty("Authorization", "Basic " + b64Auth); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            log.warn(Messages.i18n.format("HttpClientWorkItemHandler.MissingCreds")); //$NON-NLS-1$
        }
    }


    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

        // Do nothing, notifications cannot be aborted

    }


}
