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
package org.overlord.dtgov.jbpm.util;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;

/**
 * Main entry point for the application to interact with ProcessEngine.
 * It is capable of managing multiple RuntimeManagers that are built from kjar. Each kjar will 
 * have it's own dedicated RuntimeManager instance where each of them might be configured
 * based on different strategies (singleton, per request, per process instance)
 * 
 * @see EnvironmentProducer
 */
@ApplicationScoped
public class ProcessEngineService {
    
    @Inject
    private RuntimeDataService runtimeDataService;
    
    @Inject
    @Sramp
    private DeploymentService deploymentService;
    
    @ApplicationScoped
    @Produces  
    public DeploymentService getDeploymentService()  
    {  
    	return deploymentService;
    }  
    
    @Inject
	@Undeploy
	protected Event<DeploymentEvent> undeploymentEvent;


    /**
     * Deploys given unit into the process engine
     * @param unit unit that represents kjar and runtime strategy
     */
    public void deployUnit(KModuleDeploymentUnit unit) {
        deploymentService.deploy(unit);
    }
    
    /**
     * Undeploys given unit from the process engine
     * @param unit unit that represents kjar
     */
    public void undeployUnit(KModuleDeploymentUnit unit) {
        deploymentService.undeploy(unit);
    }
    
    /**
     * Returns all available process definitions
     * @return
     */
    public Collection<ProcessAssetDesc> getProcesses() {
        return runtimeDataService.getProcesses();
    }
    
    /**
     * Returns all available process definitions
     * @return
     */
    public ProcessInstanceDesc getProcessInstance(long processInstanceId) {
        return runtimeDataService.getProcessInstanceById(processInstanceId);
    }
    
    /**
     * Returns all available process definitions
     * @return
     */
    public Collection<ProcessInstanceDesc> getProcessInstances() {
        return runtimeDataService.getProcessInstances();
    }
    
    /**
     * Returns all process definitions for given deployment unit (kjar)
     * @param deploymentId unique identifier of unit (kjar)
     * @return
     */
    public Collection<ProcessAssetDesc> getProcesses(String deploymentId) {
        return runtimeDataService.getProcessesByDeploymentId(deploymentId);
        
    }
    
    /**
     * Returns <code>RuntimeManager</code> instance for given deployment unit (kjar)
     * @param deploymentId unique identifier of unit (kjar)
     * @return null if no RuntimeManager available for given id
     */
    public RuntimeManager getRuntimeManager(String deploymentId) {
        
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
        if (deployedUnit == null) {
            return null;
        }
        
        return deployedUnit.getRuntimeManager();
    }
    
    public void closeAllRuntimeManagers() {
        
        for (DeployedUnit deployedUnit: deploymentService.getDeployedUnits()) {
        	deployedUnit.getRuntimeManager().close();
        	undeploymentEvent.fire(new DeploymentEvent(deployedUnit.getDeploymentUnit().getIdentifier(), deployedUnit));
        }
    }
}
