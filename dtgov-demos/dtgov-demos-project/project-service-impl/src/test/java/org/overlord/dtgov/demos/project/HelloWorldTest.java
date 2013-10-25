package org.overlord.dtgov.demos.project;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.junit.BeforeClass;
import org.junit.Test;

public class HelloWorldTest {

	private static final QName SERVICE_NAME 
    = new QName("http://project.demos.dtgov.overlord.org/", "HelloWorld");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Starting Server");
		HelloWorldImpl implementor = new HelloWorldImpl();
		String address = "http://localhost:9000/helloWorld";
		Endpoint.publish(address, implementor);
	}

	@Test
	public void test() throws MalformedURLException {
		Service service = Service.create(new URL("http://localhost:9000/helloWorld"), SERVICE_NAME);
        HelloWorld hw = service.getPort(HelloWorld.class);
        System.out.println(hw.sayHi("World"));
        org.junit.Assert.assertEquals("Hello World", hw.sayHi("World"));
	}

}
