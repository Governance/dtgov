package org.overlord.sramp.governance.services.rhq;

import static com.jayway.restassured.RestAssured.basic;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.startsWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class RHQDeployUtil {

	static final String APPLICATION_JSON = "application/json";
	static Header acceptJson = new Header("Accept", APPLICATION_JSON);
	String rhqUser, rhqPassword;
	
	public RHQDeployUtil(String rhqUser, String rhqPassword, String baseURI, int port) {
		super();
		RestAssured.baseURI = baseURI;
        RestAssured.port = port;
        RestAssured.basePath = "/rest/";
        this.rhqUser = rhqUser;
        this.rhqPassword = rhqPassword;
        RestAssured.authentication = basic(rhqUser,rhqPassword);
	}
	
	public List<Integer> getServerIdsForGroup(String group) {
		Response response =
		given()
		    .header(acceptJson)
    		.queryParam("q", group)
    	.expect()
    		.statusCode(200)
    	.when()
    		.get("/group");
		
		//System.out.println(response.asString());
		JsonPath jsonPath = response.jsonPath();
		int groupId = jsonPath.getInt("[0].id");
		response =
		given()
		    .header(acceptJson)
        	.pathParam("groupId", groupId)
        .expect()
        	.statusCode(200)
        .when()
        	.get("/group/{groupId}/resources");
		jsonPath = response.jsonPath();
		
		List<Integer> resourceIds = jsonPath.get("resourceId");
		
		return resourceIds;
	}

	public void deploy(Integer as7Id, InputStream in, String artifactName) throws IOException {
        
		String handle = null;
		try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();

	        int data;
	        while ((data = in.read())!=-1) {
	            baos.write(data);
	        }

	        byte[] bytes = baos.toByteArray();
	        //int size = bytes.length;

	        handle =
	        given()
	            .auth().preemptive().basic(rhqUser, rhqPassword)
	            .body(bytes)
	            .contentType(ContentType.BINARY)
	            .header(acceptJson)
	        .expect()
	            .statusCode(isOneOf(200, 201))
	            .body("value", startsWith("rhq-rest-"))
                .body("value",endsWith(".bin"))
	        .when()
	            .post("/content/fresh")
	        .jsonPath()
	            .getString("value");
		        
            CreateCBRRequest resource = new CreateCBRRequest();
            resource.setParentId(as7Id);
            resource.setResourceName(artifactName);

            // type of the new resource
            resource.setTypeName("Deployment");
            resource.setPluginName("JBossAS7");

            // set plugin config (path) and deploy config (runtime-name)
            resource.getPluginConfig().put("path","deployment");
            resource.getResourceConfig().put("runtimeName", artifactName);

            Response response =
            given()
                .body(resource) // Type of new resource
                .queryParam("handle", handle)
                .contentType(ContentType.JSON)
                .header(acceptJson)
                //.log().everything()
            .expect()
                .statusCode(isOneOf(200, 201, 302))
                .log().everything()
            .when()
                .post("/resource");

            //System.out.println("after post");
            //System.out.flush();

            int status = response.getStatusCode();
            String location = response.getHeader("Location");

            //System.out.println("\nLocation " + location + "\n\n");
            assert location!=null;

            // We need to check what we got. A 302 means the deploy is still
            // in progress, so we need to wait a little longer
            while (status==302) {

                response =
                given()
                    .header(acceptJson)
                    //.log().everything()
                .expect()
                    .statusCode(isOneOf(200, 201, 302))
                    //.log().everything()
                .when()
                    .get(location);

                status = response.getStatusCode();
            }

            int createdResourceId = response.jsonPath().getInt("resourceId");

            //System.out.println("\n  Deploy is done, resource Id = " + createdResourceId + " \n");
            //System.out.flush();

            assert  createdResourceId != -1;

        } finally {
        	IOUtils.closeQuietly(in);
            // Remove the uploaded content
            removeContent(handle, false);

            //System.out.println("\n  Content removed \n");
        }
	}
		
	private void removeContent(String handle, boolean validate) {
        given()
            .pathParam("handle", handle)
            .header(acceptJson)
        .expect()
            .statusCode(204)
            .log().ifError()
        .when()
            .delete("/content/{handle}");
    }
	
	public void wipeWarArchiveIfNecessary(String artifactName, String groupName) {

        @SuppressWarnings("unchecked")
        List<Resource> resources =
        given()
            .queryParam("q",artifactName)
            .queryParam("category", "SERVICE")
            //.queryParam("group", "10041")
            .header(acceptJson)
        .expect()
            .log().everything()
        .when()
            .get("/resource")
        .as(List.class);

        if (resources!=null && resources.size()>0) {
            @SuppressWarnings("unchecked")
			int resourceId = (Integer) ((Map < String,Object>)resources.get(0)).get("resourceId");

            given()
                .pathParam("id", resourceId)
                .queryParam("physical", "true") // Also remove target on the EAP instance
            .expect()
                .statusCode(204)
            .when()
                .delete("/resource/{id}");
        }
    }
}
