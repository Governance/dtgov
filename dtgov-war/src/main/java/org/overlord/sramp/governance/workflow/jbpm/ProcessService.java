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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.overlord.dtgov.jbpm.ProcessBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The JAX-RS resource that handles process specific tasks.
 *
 */
@Path("/process")
@RequestScoped
public class ProcessService {

	@Inject
	@ApplicationScoped
    private ProcessBean processBean;
	
	
	private static Logger logger = LoggerFactory.getLogger(ProcessService.class);
	
	@POST
    @Path("start/{deploymentId}/{processId}")
    public String startProcess(
    		@Context HttpServletRequest request,
    		@PathParam("deploymentId") String deploymentId, 
    		@PathParam("processId") String processId) throws Exception {
    	
		Map<String, Object> context = new HashMap<String, Object>();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String name = params.nextElement();
			context.put(name,request.getParameter(name));
		}
        if (logger.isDebugEnabled()) {
            logger.debug("Starting process %s %s with %d parameters", deploymentId, processId, context.size()); //$NON-NLS-1$
        }
        Long processInstanceId = processBean.startProcess(deploymentId, processId, context);
        return String.valueOf(processInstanceId);
	}
	
	@PUT
    @Path("signal/{processInstanceId}/{signalType}/{event}")
    public void signalProcess(
    		@PathParam("processInstanceId") Long processInstanceId,
    		@PathParam("signalType") String signalType,
    		@PathParam("event") String event) throws Exception {
		
        if (logger.isDebugEnabled()) {
            logger.debug("Signalling processInstanceId %d with signalType %s and event $s", //$NON-NLS-1$
                    processInstanceId, signalType, event);
        }
        processBean.signalProcess(processInstanceId, signalType, (Object) event);
	}
    
}
