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

import static org.overlord.dtgov.jbpm.util.MavenRepository.getMavenRepository;

import java.net.MalformedURLException;

import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.overlord.sramp.governance.ConfigException;
import org.overlord.sramp.governance.Governance;
import org.sonatype.aether.artifact.Artifact;


/**
 * Tests the Configuration.
 *
 * @author kurt.stam@redhat.com
 */
public class KieTest {

	/**
	 * @throws ConfigException
	 * @throws ComponentLookupException
	 * @throws PlexusContainerException
	 * @throws UnknownRepositoryLayoutException
	 * @throws MalformedURLException
	 */
    @Test @Ignore
	public void testKieJar() throws ConfigException, UnknownRepositoryLayoutException, PlexusContainerException, ComponentLookupException, MalformedURLException {

    	boolean isSrampRepo = false; // true reads from S-RAMP, false from m2
    	Governance governance = new Governance();

    	String srampUrl = governance.getSrampUrl().toExternalForm();
    	srampUrl = "sramp" + srampUrl.substring(srampUrl.indexOf(":")); //$NON-NLS-1$ //$NON-NLS-2$
    	try {
    		org.overlord.dtgov.jbpm.util.MavenRepository mavenRepo = null;
	    	KieServices ks = KieServices.Factory.get();
	    	if (isSrampRepo) {
	    		System.out.println("Reading your S-RAMP repo"); //$NON-NLS-1$
	    		MavenProject srampProject = KieUtil.getSrampProject(
	    			governance.getSrampWagonVersion(),
	    			srampUrl,
	    			governance.getSrampWagonSnapshots(),
	    			governance.getSrampWagonReleases());

	    		mavenRepo = getMavenRepository(srampProject);
	    	} else {
	    		System.out.println("Reading your .m2 repo"); //$NON-NLS-1$
	    		mavenRepo = getMavenRepository();
	    	}

	    	ReleaseId releaseId = ks.newReleaseId(
	    			governance.getGovernanceWorkflowGroup(),
	    			governance.getGovernanceWorkflowName(),
	    			governance.getGovernanceWorkflowVersion());

	        String name = releaseId.toExternalForm();
	        Artifact artifact = mavenRepo.resolveArtifact(name);
	    	System.out.println("artifact=" + artifact); //$NON-NLS-1$
	    	Assert.assertNotNull(artifact);

	    	KieContainer kieContainer = ks.newKieContainer(releaseId);
	    	Assert.assertNotNull(kieContainer);

			KieBase kieBase = kieContainer.getKieBase("SRAMPPackage"); //$NON-NLS-1$
	        Assert.assertNotNull(kieBase);

	        System.out.println("KieBase=" + kieBase); //$NON-NLS-1$
    	} catch (Exception e) {
    		e.printStackTrace();
    		Assert.fail(e.getMessage());
    	}

	}

}
