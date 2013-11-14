package org.overlord.dtgov.jbpm.util;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.kie.api.runtime.manager.RuntimeManager;

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
    public Collection<ProcessDesc> getProcesses() {
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
    public Collection<ProcessDesc> getProcesses(String deploymentId) {
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
        	//deploymentService.undeploy(deployedUnit.getDeploymentUnit());
        }
    }
}
