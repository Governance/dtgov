package org.overlord.sramp.governance.workflow.jbpm;

import static org.overlord.sramp.common.test.resteasy.TestPortProvider.generateURL;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.overlord.sramp.common.test.resteasy.BaseResourceTest;
import org.overlord.sramp.governance.workflow.jbpm.ProcessService;

public class ProcessResourceTest extends BaseResourceTest {

	@BeforeClass
	public static void setUpBrms() throws Exception {
		dispatcher.getRegistry().addPerRequestResource(ProcessService.class);
	}

	@Test @Ignore
	public void testCreateProcess() {
	    try {
	        URL url = new URL(generateURL("/process/start/deploymentId/processId")); //$NON-NLS-1$
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        
	        String parameters = "kurt=" + URLEncoder.encode("stam","UTF-8");
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST"); //$NON-NLS-1$
	        connection.setConnectTimeout(1000000);
	        connection.setReadTimeout(1000000);
	        PrintWriter out = new PrintWriter(connection.getOutputStream());
       	 	out.print(parameters);
       	 	out.close();
	        connection.connect();
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) {
	        	 
	             InputStream is = (InputStream) connection.getContent();
	             String reply = IOUtils.toString(is);
	             System.out.println("reply=" + reply); //$NON-NLS-1$
	        } else {
	            System.err.println("endpoint could not be reached"); //$NON-NLS-1$
	            Assert.fail();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        Assert.fail();
	    }
	}
	
	@Test  @Ignore
	public void testSignalProcess() {
	    try {
	        URL url = new URL(generateURL("/process/signal/1234/eventType/uuid")); //$NON-NLS-1$
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        
	        connection.setRequestMethod("PUT"); //$NON-NLS-1$
	        connection.setConnectTimeout(1000000);
	        connection.setReadTimeout(1000000);
	        connection.connect();
	        int responseCode = connection.getResponseCode();
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	        Assert.fail();
	    }
	}
	
}
