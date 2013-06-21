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

import static org.kie.scanner.MavenRepository.getMavenRepository;

import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.MavenRepository;
import org.overlord.sramp.governance.ConfigException;
import org.sonatype.aether.artifact.Artifact;


/**
 * Tests the Configuration.
 *
 * @author kurt.stam@redhat.com
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KieTest {

	/**
	 * @throws ConfigException
	 * @throws ComponentLookupException
	 * @throws PlexusContainerException
	 * @throws UnknownRepositoryLayoutException
	 */
    @Test
	public void testKieJar() throws ConfigException, UnknownRepositoryLayoutException, PlexusContainerException, ComponentLookupException {

    	try {
	    	KieServices ks = KieServices.Factory.get();
	    	MavenProject srampProject = KieUtil.getSrampProject(
	    			"0.2.1-SNAPSHOT",
	    			"sramp://localhost:8080/s-ramp-server/",
	    			true,
	    			true);
	    	//MavenRepository repository = getMavenRepository();
	    	MavenRepository repo = getMavenRepository(srampProject);

	    	ReleaseId releaseId = ks.newReleaseId("org.overlord.dtgov", "dtgov-workflows", "0.1.0-SNAPSHOT");

	        String name = releaseId.toExternalForm();
	        Artifact artifact = repo.resolveArtifact(name);
	    	System.out.println("artifact=" + artifact);
	    	Assert.assertNotNull(artifact);

	    	KieContainer kieContainer = ks.newKieContainer(releaseId);
	    	Assert.assertNotNull(kieContainer);

			KieSession ksession = kieContainer.newKieSession("ksessionSRAMP");
	        Assert.assertNotNull(ksession);

	        System.out.println("KSession=" + ksession);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Assert.fail(e.getMessage());
    	}

	}

}
