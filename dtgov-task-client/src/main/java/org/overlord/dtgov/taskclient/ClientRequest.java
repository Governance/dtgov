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
package org.overlord.dtgov.taskclient;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.overlord.dtgov.taskclient.i18n.Messages;

/**
 * Extends the RESTEasy {@link org.jboss.resteasy.client.ClientRequest} class in order to provide a
 * {@link ClientExecutor} and {@link ResteasyProviderFactory} without requiring clients to pass them in.
 *
 * Additionally, this class overrides the various http methods (post, get, put) in order to implement
 * some error handling.  These methods will throw an appropriate exception now (when possible), rather
 * than a less meaningful RESTEasy generic exception.
 *
 * @author eric.wittmann@redhat.com
 */
public class ClientRequest extends org.jboss.resteasy.client.ClientRequest {

	private static final ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
	static {
		RegisterBuiltin.register(providerFactory);
	}

	/**
	 * Creates a {@link UriBuilder} for the given URI template.
	 * @param uriTemplate
	 */
	private static UriBuilder getBuilder(String uriTemplate) {
		return new UriBuilderImpl().uriTemplate(uriTemplate);
	}

    /**
     * Constructor.
     * @param uriTemplate
     */
    public ClientRequest(String uriTemplate) {
        super(getBuilder(uriTemplate), getDefaultExecutor(), providerFactory);
    }

    /**
     * Constructor.
     * @param uriTemplate
     * @param clientExecutor
     */
    public ClientRequest(String uriTemplate, ClientExecutor clientExecutor) {
        super(getBuilder(uriTemplate), clientExecutor, providerFactory);
    }

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#post(java.lang.Class)
	 */
	@Override
	public <T> ClientResponse<T> post(Class<T> returnType) throws Exception {
		ClientResponse<T> response = super.post(returnType);
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#post()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ClientResponse post() throws Exception {
		ClientResponse response = super.post();
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#get(java.lang.Class)
	 */
	@Override
	public <T> ClientResponse<T> get(Class<T> returnType) throws Exception {
		ClientResponse<T> response = super.get(returnType);
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#get()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ClientResponse get() throws Exception {
		ClientResponse response = super.get();
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#put(java.lang.Class)
	 */
	@Override
	public <T> ClientResponse<T> put(Class<T> returnType) throws Exception {
		ClientResponse<T> response = super.put(returnType);
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#put()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ClientResponse put() throws Exception {
		ClientResponse response = super.put();
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#delete(java.lang.Class)
	 */
	@Override
	public <T> ClientResponse<T> delete(Class<T> returnType) throws Exception {
		ClientResponse<T> response = super.delete(returnType);
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * @see org.jboss.resteasy.client.ClientRequest#delete()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ClientResponse delete() throws Exception {
		ClientResponse response = super.delete();
		handlePotentialServerError(response);
		return response;
	}

	/**
	 * Handles the possibility of an error found in the response.
	 * @param response
	 * @throws Exception
	 */
	private <T> void handlePotentialServerError(ClientResponse<T> response) throws Exception {
		if (response.getStatus() == 500) {
			Exception error = new TaskApiClientException(Messages.i18n.format("ClientRequest.UnexpectedError")); //$NON-NLS-1$
			throw error;
		}
		if (response.getStatus() == 404) {
		    Exception error = new TaskApiClientException(Messages.i18n.format("ClientRequest.NotFound")); //$NON-NLS-1$
			throw error;
		}
		if (response.getStatus() == 403) {
		    Exception error = new TaskApiClientException(Messages.i18n.format("ClientRequest.AuthorizationError")); //$NON-NLS-1$
			throw error;
		}
		if (response.getStatus() == 401) {
		    Exception error = new TaskApiClientException(Messages.i18n.format("ClientRequest.AuthenticationError")); //$NON-NLS-1$
            throw error;
		}
	}

}
