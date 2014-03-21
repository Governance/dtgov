package org.overlord.sramp.governance.services.rhq;

import static com.jayway.restassured.RestAssured.basic;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.governance.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class RHQDeployUtil {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	static final String APPLICATION_JSON = "application/json"; //$NON-NLS-1$
	static Header acceptJson = new Header("Accept", APPLICATION_JSON); //$NON-NLS-1$
	String rhqUser, rhqPassword, rhqPluginName;

	public RHQDeployUtil(String rhqUser, String rhqPassword, String rhqBaseUrl, Integer rhqPort, String rhqPluginname) {
		super();
		RestAssured.baseURI = rhqBaseUrl;
        RestAssured.port = rhqPort;
        RestAssured.basePath = "/rest/"; //$NON-NLS-1$
        this.rhqUser = rhqUser;
        this.rhqPassword = rhqPassword;
        RestAssured.authentication = basic(rhqUser,rhqPassword);
	}
	/**
	 * Looks up the group id in RHQ for the given group name.
	 *
	 * @param groupName - name of the group
	 * @return - the group id in RHQ
	 * @throws ConfigException
	 */
	public Integer getGroupIdForGroup(String groupName) throws ConfigException {
		Response response =
		given()
		    .header(acceptJson)
    		.queryParam("q", groupName) //$NON-NLS-1$
    	.expect()
    		.statusCode(200)
    	.when()
    		.get("/group"); //$NON-NLS-1$

		JsonPath jsonPath = response.jsonPath();
		if (! jsonPath.prettify().contains("\"id\"")) { //$NON-NLS-1$
			throw new ConfigException(Messages.i18n.format("RHQDeployUtil.MissingGroup", groupName, jsonPath.prettify())); //$NON-NLS-1$
		}
		int groupId = jsonPath.getInt("[0].id"); //$NON-NLS-1$
		return groupId;
	}
	/**
	 * Given a RHQ Group this will return a the resource Ids in this
	 * Group.
	 *
	 * @param groupId - the group's RHQ id.
	 * @return a list of resourceIds in this group.
	 */
	public List<Integer> getServerIdsForGroup(Integer groupId) {

		Response response =
		given()
		    .header(acceptJson)
        	.pathParam("groupId", groupId) //$NON-NLS-1$
        .expect()
        	.statusCode(200)
        .when()
        	.get("/group/{groupId}/resources"); //$NON-NLS-1$
		JsonPath jsonPath = response.jsonPath();
		List<Integer> resourceIds = jsonPath.get("resourceId"); //$NON-NLS-1$
		return resourceIds;
	}
	/**
	 * In RHQ the group needs be a grouping of JBossAS7/EAP6 SERVERs
	 * @param as7Id
	 * @param in
	 * @param artifactName
	 * @throws IOException
	 */
	public void deploy(Integer as7Id, byte[] bytes, String artifactName) throws IOException {

		String handle = null;
		try {
	        //int size = bytes.length;

	        handle =
	        given()
	            .auth().preemptive().basic(rhqUser, rhqPassword)
	            .body(bytes)
	            .contentType(ContentType.BINARY)
	            .header(acceptJson)
	        .expect()
	            .statusCode(isOneOf(200, 201))
	            .body("value", startsWith("rhq-rest-")) //$NON-NLS-1$ //$NON-NLS-2$
                .body("value",endsWith(".bin")) //$NON-NLS-1$ //$NON-NLS-2$
	        .when()
	            .post("/content/fresh") //$NON-NLS-1$
	        .jsonPath()
	            .getString("value"); //$NON-NLS-1$

            CreateCBRRequest resource = new CreateCBRRequest();
            resource.setParentId(as7Id);
            resource.setResourceName(artifactName);

            // type of the new resource
            resource.setTypeName("Deployment"); //$NON-NLS-1$
            resource.setPluginName(rhqPluginName); //$NON-NLS-1$

            // set plugin config (path) and deploy config (runtime-name)
            resource.getPluginConfig().put("path","deployment"); //$NON-NLS-1$ //$NON-NLS-2$
            resource.getResourceConfig().put("runtimeName", artifactName); //$NON-NLS-1$

            Response response =
            given()
                .body(resource) // Type of new resource
                .queryParam("handle", handle) //$NON-NLS-1$
                .contentType(ContentType.JSON)
                .header(acceptJson)
                //.log().everything()
            .expect()
                .statusCode(isOneOf(200, 201, 302))
                .log().everything()
            .when()
                .post("/resource"); //$NON-NLS-1$

            //System.out.println("after post");
            //System.out.flush();

            int status = response.getStatusCode();
            String location = response.getHeader("Location"); //$NON-NLS-1$

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

            int createdResourceId = response.jsonPath().getInt("resourceId"); //$NON-NLS-1$

            //System.out.println("\n  Deploy is done, resource Id = " + createdResourceId + " \n");
            //System.out.flush();

            assert  createdResourceId != -1;

        } finally {
            // Remove the uploaded content
            removeContent(handle, false);

            //System.out.println("\n  Content removed \n");
        }
	}

	private void removeContent(String handle, boolean validate) {
        given()
            .pathParam("handle", handle) //$NON-NLS-1$
            .header(acceptJson)
        .expect()
            .statusCode(204)
            .log().ifError()
        .when()
            .delete("/content/{handle}"); //$NON-NLS-1$
    }
	/**
	 * Deletes (undeploys) all archives from the all AS7/EAP servers in the given
	 * RHQ group.
	 * @param artifactName - the name of the archive to be deleted
	 * @param groupId - the id of the RHQ group
	 */
	public void wipeArchiveIfNecessary(String artifactName, Integer groupId) {

        @SuppressWarnings("unchecked")
        List<Resource> resources =
        given()
            .queryParam("q",artifactName) //$NON-NLS-1$
            .queryParam("category", "SERVICE") //$NON-NLS-1$ //$NON-NLS-2$
            .queryParam("group", groupId) //$NON-NLS-1$
            .header(acceptJson)
        .expect()
            //.log().everything()
        .when()
            .get("/resource") //$NON-NLS-1$
        .as(List.class);
        logger.info(Messages.i18n.format(
                "RHQDeployUtil.ManagementSummary", resources.size(), //$NON-NLS-1$
                groupId, artifactName));
    	for (int i=0; i<resources.size(); i++) {
    		try {
	            @SuppressWarnings("unchecked")
				int resourceId = (Integer) ((Map < String,Object>)resources.get(i)).get("resourceId"); //$NON-NLS-1$
	            logger.info(Messages.i18n.format("RHQDeployUtil.DeletingDeployment", artifactName, resourceId)); //$NON-NLS-1$
	            given()
	                .pathParam("id", resourceId) //$NON-NLS-1$
	                .queryParam("physical", "true") // Also remove target on the EAP instance //$NON-NLS-1$ //$NON-NLS-2$
	            .expect()
	                .statusCode(204)
	            .when()
	                .delete("/resource/{id}"); //$NON-NLS-1$
    		} catch (Throwable t) {
    			logger.error(Messages.i18n.format("RHQDeployUtil.DeleteFailed")); //$NON-NLS-1$
    			logger.error(t.getMessage());
    			//TODO do we need to send out somekind of notification about this?
    		}
    	}

    }
}
