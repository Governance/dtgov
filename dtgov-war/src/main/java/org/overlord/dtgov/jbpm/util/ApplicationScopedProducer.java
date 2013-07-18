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

import static org.overlord.dtgov.jbpm.util.MavenRepository.getMavenRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.apache.maven.project.MavenProject;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.task.api.UserGroupCallback;
import org.overlord.sramp.governance.Governance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;

@ApplicationScoped
public class ApplicationScopedProducer {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Inject
    private InjectableRegisterableItemsFactory factory;
    @Inject
    private UserGroupCallback usergroupCallback;
    
    @PersistenceUnit(unitName = "org.overlord.dtgov.jbpm")
    private EntityManagerFactory emf;

    @Produces
    public UserGroupCallback produceUserGroupCallback() {
        return usergroupCallback;
    }

    @Produces
    public EntityManagerFactory produceEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence
                    .createEntityManagerFactory("org.overlord.dtgov.jbpm");
        }
        return this.emf;
    }

    @Produces
    @Singleton
    @PerProcessInstance
    @PerRequest
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
    	
    	
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder
                .getDefault()
                .entityManagerFactory(emf)
                .userGroupCallback(usergroupCallback)
                .registerableItemsFactory(factory);
        
        KieBase kbase = getKieBase();
        if (kbase!=null) builder.knowledgeBase(getKieBase());        
        return builder.get();
    }
    
    private KieBase getKieBase() {
    	Governance governance = new Governance();
    	try {
    		String srampUrl = governance.getSrampUrl().toExternalForm();
        	srampUrl = "sramp" + srampUrl.substring(srampUrl.indexOf(":"));
        	
	    	KieServices ks = KieServices.Factory.get();
	    	MavenProject srampProject = KieUtil.getSrampProject(
	    			governance.getSrampWagonVersion(), 
	    			srampUrl, 
	    			governance.getSrampWagonSnapshots(), 
	    			governance.getSrampWagonReleases());
	    	
	    	//Setup S-RAMP as the Maven Repository
	    	org.overlord.dtgov.jbpm.util.MavenRepository repo = getMavenRepository(srampProject);
	    	
	    	ReleaseId releaseId = ks.newReleaseId(
	    			governance.getGovernanceWorkflowGroup(),
	    			governance.getGovernanceWorkflowName(),
	    			governance.getGovernanceWorkflowVersion());
	    	
	        //Resolving the workflow package in the S-RAMP repo
	        Artifact artifact = repo.resolveArtifact(releaseId.toExternalForm());
	        
	        logger.info("Creating KIE container with workflows from " + artifact);
	    	KieContainer kieContainer = ks.newKieContainer(releaseId);
	    	
	    	//Creating the KieBase for the SRAMPPackage
	    	logger.info("Looking for KieBase named " + governance.getGovernanceWorkflowPackage());
	    	KieBase kbase = kieContainer.getKieBase(governance.getGovernanceWorkflowPackage());
	    	
	    	return kbase;
    	} catch (Exception e) {
    		logger.error("Could not find or read the " + governance.getGovernanceWorkflowPackage());
    		logger.error(e.getMessage(),e);
    	}
    	return null;
    }
    
	


}
