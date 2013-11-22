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
package org.overlord.dtgov.jbpm.util;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;


/**
 * Tests the Configuration.
 *
 * @author kurt.stam@redhat.com
 */
public class KieTest {
    
	@SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(KieTest.class);
	
//	@Deployment()
//    public static Archive<?> createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class, "jbpm-cdi-sample.jar")
//                .addPackage("org.jboss.seam.transaction") //seam-persistence
//                .addPackage("org.jbpm.services.task")
//                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
//                .addPackage("org.jbpm.services.task.annotations")
//                .addPackage("org.jbpm.services.task.api")
//                .addPackage("org.jbpm.services.task.impl")
//                .addPackage("org.jbpm.services.task.events")
//                .addPackage("org.jbpm.services.task.exception")
//                .addPackage("org.jbpm.services.task.identity")
//                .addPackage("org.jbpm.services.task.factories")
//                .addPackage("org.jbpm.services.task.internals")
//                .addPackage("org.jbpm.services.task.internals.lifecycle")
//                .addPackage("org.jbpm.services.task.lifecycle.listeners")
//                .addPackage("org.jbpm.services.task.query")
//                .addPackage("org.jbpm.services.task.util")
//                .addPackage("org.jbpm.services.task.commands") // This should not be required here
//                .addPackage("org.jbpm.services.task.deadlines") // deadlines
//                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
//                .addPackage("org.jbpm.services.task.subtask")
//                .addPackage("org.jbpm.services.task.rule")
//                .addPackage("org.jbpm.services.task.rule.impl")
//
//                .addPackage("org.kie.api.runtime.manager")
//                .addPackage("org.kie.internal.runtime.manager")
//                .addPackage("org.kie.internal.runtime.manager.context")
//                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
//                
//                .addPackage("org.jbpm.runtime.manager.impl")
//                .addPackage("org.jbpm.runtime.manager.impl.cdi")                               
//                .addPackage("org.jbpm.runtime.manager.impl.factory")
//                .addPackage("org.jbpm.runtime.manager.impl.jpa")
//                .addPackage("org.jbpm.runtime.manager.impl.manager")
//                .addPackage("org.jbpm.runtime.manager.impl.task")
//                .addPackage("org.jbpm.runtime.manager.impl.tx")
//                
//                .addPackage("org.jbpm.shared.services.api")
//                .addPackage("org.jbpm.shared.services.impl")
//                .addPackage("org.jbpm.shared.services.impl.tx")
//                
//                .addPackage("org.jbpm.kie.services.api")
//                .addPackage("org.jbpm.kie.services.impl")
//                .addPackage("org.jbpm.kie.services.api.bpmn2")
//                .addPackage("org.jbpm.kie.services.impl.bpmn2")
//                .addPackage("org.jbpm.kie.services.impl.event.listeners")
//                .addPackage("org.jbpm.kie.services.impl.audit")
//                
//                .addPackage("org.jbpm.kie.services.impl.example")
//                .addPackage("org.kie.commons.java.nio.fs.jgit")
//                .addPackage("org.jbpm.examples.cdi.helper") 
//                .addPackage("org.jbpm.examples.cdi")
//                .addAsResource("jndi.properties", "jndi.properties")
//                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
//                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
//
//    }
//	 
//	@Inject
//	private ProcessEngineService processService;
	    
    @Test @Ignore
    public void getKieFromFile() {
    	KieServices ks = KieServices.Factory.get();
    	
    	KieRepository repo = ks.getRepository();
    	
    	InputStream is = this.getClass().getResourceAsStream("/dtgov-workflows.jar"); //$NON-NLS-1$
    	
    	KieModule kModule = repo.addKieModule(ks.getResources().newInputStreamResource(is));
    	
    	@SuppressWarnings("unused")
		ReleaseId releaseId = kModule.getReleaseId();
    	KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
    	
    	Assert.assertNotNull(kContainer);

		KieBase kieBase = kContainer.getKieBase("SRAMPPackage"); //$NON-NLS-1$
        Assert.assertNotNull(kieBase);

        System.out.println("KieBase=" + kieBase); //$NON-NLS-1$
    }
    
//    @Test
//    public void testDeployAndStartSimpleProcess() {
//        //assertNotNull(processService);
//        processService = new ProcessEngineService();
//        
//        
//        KieServices ks = KieServices.Factory.get();
//    	
//    	KieRepository repo = ks.getRepository();
//        InputStream is = this.getClass().getResourceAsStream("/dtgov-workflows.jar"); //$NON-NLS-1$
//    	
//    	KieModule kModule = repo.addKieModule(ks.getResources().newInputStreamResource(is));
//    	
//    	@SuppressWarnings("unused")
//		ReleaseId releaseId = kModule.getReleaseId();
//    	
//        
//        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(
//        		releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion());
//    	
//        processService.deployUnit(unit);
//        logger.info("TEST:Unit {} has been deployed", unit);
//        Collection<ProcessDesc> processes = processService.getProcesses();
//        assertNotNull(processes);
//        assertEquals(2, processes.size());
//    }
}
