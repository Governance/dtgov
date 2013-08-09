/*
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
package org.overlord.dtgov.taskclient;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.overlord.dtgov.taskapi.types.FindTasksRequest;
import org.overlord.dtgov.taskapi.types.FindTasksResponse;
import org.overlord.dtgov.taskapi.types.TaskDataType;
import org.overlord.dtgov.taskapi.types.TaskDataType.Entry;
import org.overlord.dtgov.taskapi.types.TaskType;
import org.overlord.dtgov.taskclient.auth.AuthenticationProvider;
import org.overlord.dtgov.taskclient.auth.BasicAuthenticationProvider;

/**
 * Class used to communicate with the S-RAMP server via the S-RAMP Atom API.
 *
 * @author eric.wittmann@redhat.com
 */
public class TaskApiClient {

	private String endpoint;
	private AuthenticationProvider authProvider;

	/**
	 * Constructor.
	 * @param endpoint
	 */
	public TaskApiClient(String endpoint) {
		this.endpoint = endpoint;
		if (this.endpoint.endsWith("/")) { //$NON-NLS-1$
			this.endpoint = this.endpoint.substring(0, this.endpoint.length()-1);
		}
	}

    /**
     * Constructor.
     * @param endpoint
     * @param username
     * @param password
     * @throws TaskApiClientException
     * @throws SrampAtomException
     */
    public TaskApiClient(final String endpoint, final String username, final String password) throws TaskApiClientException {
        this(endpoint, new BasicAuthenticationProvider(username, password));
    }

    /**
     * Constructor.
     * @param endpoint
     * @param authenticationProvider
     * @throws TaskApiClientException
     * @throws SrampAtomException
     */
    public TaskApiClient(final String endpoint, AuthenticationProvider authenticationProvider) throws TaskApiClientException {
        this(endpoint);
        this.authProvider = authenticationProvider;
    }

	/**
	 * @return the s-ramp endpoint
	 */
	public String getEndpoint() {
		return this.endpoint;
	}

	/**
	 * Finds a list of tasks based on criteria provided in {@link FindTasksRequest}.
	 * @param findTasksRequest
	 */
	public FindTasksResponse findTasks(FindTasksRequest findTasksRequest) throws TaskApiClientException {
        try {
            String url = String.format("%1$s/find", this.endpoint); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            request.body(MediaType.APPLICATION_XML_TYPE, findTasksRequest);
            ClientResponse<FindTasksResponse> response = request.post(FindTasksResponse.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
	}

	/**
	 * Gets a task by its unique id.
	 * @throws TaskApiClientException
	 * @throws SrampAtomException
	 */
	public TaskType getTask(String taskId) throws TaskApiClientException {
		try {
			String url = String.format("%1$s/get/%2$s", this.endpoint, taskId); //$NON-NLS-1$
			ClientRequest request = createClientRequest(url);
			ClientResponse<TaskType> response = request.get(TaskType.class);
			return response.getEntity();
		} catch (Throwable e) {
			throw new TaskApiClientException(e);
		}
	}

    /**
     * Claims a task and returns the latest (updated) task instance.
     * @param taskId
     */
    public TaskType claimTask(String taskId) throws TaskApiClientException {
        try {
            String url = String.format("%1$s/claim/%2$s", this.endpoint, taskId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            ClientResponse<TaskType> response = request.get(TaskType.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
    }

    /**
     * Releases a task and returns the latest (updated) task instance.
     * @param taskid
     */
    public TaskType releaseTask(String taskId) throws TaskApiClientException {
        try {
            String url = String.format("%1$s/release/%2$s", this.endpoint, taskId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            ClientResponse<TaskType> response = request.get(TaskType.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
    }

    /**
     * Starts a task and returns the latest (updated) task instance.
     * @param taskid
     */
    public TaskType startTask(String taskId) throws TaskApiClientException {
        try {
            String url = String.format("%1$s/start/%2$s", this.endpoint, taskId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            ClientResponse<TaskType> response = request.get(TaskType.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
    }

    /**
     * Stops a task and returns the latest (updated) task instance.
     * @param taskid
     */
    public TaskType stopTask(String taskId) throws TaskApiClientException {
        try {
            String url = String.format("%1$s/stop/%2$s", this.endpoint, taskId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            ClientResponse<TaskType> response = request.get(TaskType.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
    }

    /**
     * Completes a task and returns the latest (updated) task instance.
     * @param taskid
     */
    public TaskType completeTask(String taskId, Map<String, String> taskData) throws TaskApiClientException {
        try {
            TaskDataType data = convertMapToTaskData(taskData);
            String url = String.format("%1$s/complete/%2$s", this.endpoint, taskId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            request.body(MediaType.APPLICATION_XML_TYPE, data);
            ClientResponse<TaskType> response = request.post(TaskType.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
    }

    /**
     * Fails a task and returns the latest (updated) task instance.
     * @param taskid
     */
    public TaskType failTask(String taskId, Map<String, String> taskData) throws TaskApiClientException {
        try {
            TaskDataType data = convertMapToTaskData(taskData);
            String url = String.format("%1$s/fail/%2$s", this.endpoint, taskId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            request.body(MediaType.APPLICATION_XML_TYPE, data);
            ClientResponse<TaskType> response = request.post(TaskType.class);
            return response.getEntity();
        } catch (Throwable e) {
            throw new TaskApiClientException(e);
        }
    }

    /**
     * Converts the given map of task data into a structure useful for sending to the remote task api.
     * @param taskData
     */
    private TaskDataType convertMapToTaskData(Map<String, String> taskData) {
        TaskDataType data = new TaskDataType();
        for (Map.Entry<String, String> entry : taskData.entrySet()) {
            Entry e = new Entry();
            e.setKey(entry.getKey());
            e.setValue(entry.getValue());
            data.getEntry().add(e);
        }
        return data;
    }

    /**
     * Creates the RESTEasy client request object, configured appropriately.
     * @param atomUrl
     */
    protected ClientRequest createClientRequest(String atomUrl) {
        ClientExecutor executor = createClientExecutor();
        ClientRequest request = new ClientRequest(atomUrl, executor);
        return request;
    }

    /**
     * Creates the client executor that will be used by RESTEasy when
     * making the request.
     */
    private ClientExecutor createClientExecutor() {
        // TODO I think the http client is thread safe - so let's try to create just one of these
        DefaultHttpClient httpClient = new DefaultHttpClient();
        if (this.authProvider != null) {
            httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
                @Override
                public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                    authProvider.provideAuthentication(request);
                }
            });
        }
        return new ApacheHttpClient4Executor(httpClient);
    }
}
