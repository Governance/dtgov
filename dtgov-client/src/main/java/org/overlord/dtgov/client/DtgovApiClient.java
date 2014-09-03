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
package org.overlord.dtgov.client;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.overlord.dtgov.client.auth.AuthenticationProvider;
import org.overlord.dtgov.client.auth.BasicAuthenticationProvider;

/**
 * Class used to communicate with the DTGov REST API.
 *
 * @author David Virgil Naranjo
 */
public class DtgovApiClient {

	private String endpoint;
	private AuthenticationProvider authProvider;
    private Locale locale;

	/**
	 * Constructor.
	 * @param endpoint
	 */
	public DtgovApiClient(String endpoint) {
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
     * @throws DtgovApiClientException
     * @throws SrampAtomException
     */
    public DtgovApiClient(final String endpoint, final String username, final String password) throws DtgovApiClientException {
        this(endpoint, new BasicAuthenticationProvider(username, password));
    }

    /**
     * Constructor.
     * @param endpoint
     * @param authenticationProvider
     * @throws DtgovApiClientException
     * @throws SrampAtomException
     */
    public DtgovApiClient(final String endpoint, AuthenticationProvider authenticationProvider) throws DtgovApiClientException {
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
    public void stopProcess(String targetUUID, long processId) throws DtgovApiClientException {
        try {
            String url = String.format("%1$s/process/stop/%2$s/%3$s", this.endpoint, targetUUID, processId); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            request.put();
        } catch (Throwable e) {
            throw new DtgovApiClientException(e);
        }
	}

    public List<String> getCustomDeployers() throws DtgovApiClientException {
        try {
            String url = String.format("%1$s/system/config/deployers/custom", this.endpoint); //$NON-NLS-1$
            ClientRequest request = createClientRequest(url);
            ClientResponse<List> response = request.get(List.class);
            List<String> deployers = response.getEntity();
            return deployers;
        } catch (Throwable e) {
            throw new DtgovApiClientException(e);
        }
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
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                Locale l = getLocale();
                if (l == null) {
                    l = Locale.getDefault();
                }
                request.addHeader("Accept-Language", l.toString()); //$NON-NLS-1$
            }
        });
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

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
