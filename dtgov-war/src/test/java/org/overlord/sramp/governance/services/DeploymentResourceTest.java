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
package org.overlord.sramp.governance.services;

import static org.overlord.sramp.common.test.resteasy.TestPortProvider.generateURL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.overlord.sramp.common.test.resteasy.BaseResourceTest;
import org.overlord.sramp.governance.ConfigException;
import org.overlord.sramp.governance.Target;
import org.overlord.sramp.governance.services.rhq.RHQDeployUtil;


/**
 * Tests the Deployment API.
 *
 * @author kurt.stam@redhat.com
 */
public class DeploymentResourceTest extends BaseResourceTest {

	@BeforeClass
	public static void setUpBrms() throws Exception {
		dispatcher.getRegistry().addPerRequestResource(DeploymentResource.class);
	}
	
	/**
	 * This is an integration test, and only works if artifact 'e67e1b09-1de7-4945-a47f-45646752437a'
     * exists in the repo; check the following urls to find out:
     * 
	 * http://localhost:8080/s-ramp-server/s-ramp?query=/s-ramp[@uuid%3D'e67e1b09-1de7-4945-a47f-45646752437a']
	 * http://localhost:8080/s-ramp-server/s-ramp/user/BpmnDocument/e67e1b09-1de7-4945-a47f-45646752437a
	 * 
	 * @throws Exception
	 */
	@Test @Ignore
	public void testDeployCopy() {
	    try {
	        
	        URL url = new URL(generateURL("/deploy/copy/dev/e67e1b09-1de7-4945-a47f-45646752437a"));
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setConnectTimeout(10000);
	        connection.setReadTimeout(10000);
	        connection.connect();
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) {
	             InputStream is = (InputStream) connection.getContent();
	             String reply = IOUtils.toString(is);
	             System.out.println("reply=" + reply);
	        } else {
	            System.err.println("endpoint could not be reached");
	            Assert.fail();
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        Assert.fail();
	    } 
	}
	
	@Test @Ignore
	public void testDeployMaven() {
		
		
	}
	
	@Test @Ignore
	/**
	 * Only works with a running RHQ Server.
	 * 
	 * @throws IOException
	 * @throws ConfigException
	 */
	public void testDeployRHQ() throws IOException, ConfigException {
		
		
		Target target = new Target("stage", "rhqadmin", "rhqadmin", "http://localhost:7080");
		
		RHQDeployUtil rhqDeployUtil = new RHQDeployUtil(target.getRhqUser(), target.getRhqPassword(),
				target.getRhqBaseUrl(), target.getRhqPort());
		String groupName = target.getName();
		
		String artifactName = "test-simple2.war";
		//String artifactName = "s-ramp-server.war";
		//String artifactName = "dtgov-workflows2.jar"; 
		
		Integer groupId = rhqDeployUtil.getGroupIdForGroup(groupName);
		
		rhqDeployUtil.wipeArchiveIfNecessary(artifactName, groupId);
		List<Integer> resourceIds = rhqDeployUtil.getServerIdsForGroup(groupId);
		InputStream is = getClass().getClassLoader().getResourceAsStream(artifactName);
		byte[] fileContent = IOUtils.toByteArray(is);
		
		IOUtils.closeQuietly(is);
		for (Integer resourceId : resourceIds) {
		    System.out.println(String.format("Deploying %1$s to RHQ Server %2$s", artifactName, resourceId));
			rhqDeployUtil.deploy(resourceId, fileContent, artifactName);
		}
		
		System.out.println("complete");
	}
}
