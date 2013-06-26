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

package org.overlord.dtgov.jbpm.ejb;

import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.PostActivate;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.maven.project.MavenProject;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.scanner.MavenRepository;
import org.overlord.dtgov.jbpm.util.KieUtil;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.GovernanceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessBean implements ProcessLocal {

	private String wagonVersion = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private KieContainer kieContainer = null;
	Governance governance = null;
	
	public String getSrampWagonVersion() {
		if (wagonVersion==null) {
			wagonVersion = org.overlord.sramp.governance.Release.getVersionFromManifest(GovernanceConstants.SRAMP_WAGON_JAR);
		}
		return wagonVersion;
	}
	
	@PostActivate
	void startContainer() throws WorkflowConfigurationException {
		
		governance = new Governance();
    	try {
    		String srampUrl = governance.getSrampUrl().toExternalForm();
        	srampUrl = "sramp" + srampUrl.substring(srampUrl.indexOf(":"));
        	
	    	KieServices ks = KieServices.Factory.get();
	    	MavenProject srampProject = KieUtil.getSrampProject(
	    			governance.getSrampWagonVersion(), 
	    			srampUrl, 
	    			governance.getSrampWagonSnapshots(), 
	    			governance.getSrampWagonReleases());
	    	
	    	MavenRepository repo = getMavenRepository(srampProject);
	    	ReleaseId releaseId = ks.newReleaseId(
	    			governance.getGovernanceWorkflowGroup(),
	    			governance.getGovernanceWorkflowName(),
	    			governance.getGovernanceWorkflowVersion());
	    	
	        String name = releaseId.toExternalForm();
	        Artifact artifact = repo.resolveArtifact(name);
	        logger.info("Creating KIE container with workflows from " + artifact);
	    	kieContainer = ks.newKieContainer(releaseId);
    	} catch (Exception e) {
    		throw new WorkflowConfigurationException(e.getMessage(), e.getCause());
    	}
	}
	
    @Resource
    private UserTransaction ut;

    @Inject
    @Singleton
    RuntimeManager singletonManager;
    
    public long startProcess(String processId, Map<String, Object> parameters) throws Exception {

        KieSession ksession = kieContainer.newKieSession(governance.getGovernanceWorkflowSession());
        long processInstanceId = -1;
        ut.begin();
        try {
            // start a new process instance
            ProcessInstance processInstance = ksession.startProcess(processId, parameters);
            processInstanceId = processInstance.getId();
            logger.info("Process started ... : processInstanceId = " + processInstanceId);
            ut.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            throw e;
        }
        return processInstanceId;
    }
    
    public Collection<ProcessInstance> listProcessInstances() throws Exception {
    	
    	RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        
        Collection<ProcessInstance> processInstances = null;
        ut.begin();

        try {
	        processInstances = ksession.getProcessInstances();
	        for (ProcessInstance processInstance : processInstances) {
				System.out.println(processInstance.getProcess().getName());
				System.out.println("..");
			}
	        ut.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            throw e;
        }
        return processInstances;
    	
    }
    
}
