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

package org.overlord.dtgov.jbpm.web;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kie.api.runtime.process.ProcessInstance;
import org.overlord.dtgov.jbpm.ejb.ProcessLocal;

public class ProcessServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProcessLocal processService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        String recipient = req.getParameter("recipient");
        String processId = req.getParameter("processId");
        String uuid = req.getParameter("uuid");
        		//"com.sample.rewards-basic";

        long processInstanceId = -1;
        Collection<ProcessInstance> processInstances = null;
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        String governanceUrl = "http://localhost:8080/dtgov";
        if (processId.equals("overlord.demo.SimpleReleaseProcess")) {
        	parameters.put("ArtifactUuid", uuid);
        	parameters.put("DeploymentUrl",governanceUrl + "/rest/deploy/copy/{target}/{uuid}");
        	parameters.put("NotificationUrl",governanceUrl + "/rest/notify/email/{group}/deployed/{target}/{uuid}");
        	parameters.put("UpdateMetaDataUrl",governanceUrl + "/rest/update/{name}/{value}/{uuid}");
        }
        parameters.put("recipient", recipient);
        try {
            processInstanceId = processService.startProcess(processId, parameters);
            //processInstances = processService.listProcessInstances();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        req.setAttribute("message", "process instance (id = "
                + processInstanceId + ") has been started.");
        
        req.setAttribute("processList", processInstances);

        ServletContext context = this.getServletContext();
        RequestDispatcher dispatcher = context
                .getRequestDispatcher("/index.jsp");
        dispatcher.forward(req, res);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws ServletException, IOException {
    	
    	String processId = req.getParameter("processId");
    	try {
            processService.listProcessInstanceDetail(Long.parseLong(processId));
            //processInstances = processService.listProcessInstances();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}