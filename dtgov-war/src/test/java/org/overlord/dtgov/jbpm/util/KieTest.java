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


/**
 * Tests the Configuration.
 *
 * @author kurt.stam@redhat.com
 */
public class KieTest {
    
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

}
