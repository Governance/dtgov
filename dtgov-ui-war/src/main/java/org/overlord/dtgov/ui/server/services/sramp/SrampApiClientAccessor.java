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
package org.overlord.dtgov.ui.server.services.sramp;

import java.lang.reflect.Constructor;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.configuration.Configuration;
import org.overlord.dtgov.ui.server.DtgovUIConfig;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.auth.AuthenticationProvider;

/**
 * The class used whenever an S-RAMP Atom API request for data needs to be made.
 *
 * @author eric.wittmann@redhat.com
 */
@Singleton
public class SrampApiClientAccessor {

    private transient static ThreadLocal<SrampAtomApiClient> client = new ThreadLocal<SrampAtomApiClient>();
    private transient static ThreadLocal<Locale> tlocale = new ThreadLocal<Locale>();
    public static void setLocale(Locale locale) {
        tlocale.set(locale);
    }
    public static void clearLocale() {
        tlocale.set(null);
    }

    @Inject
    protected DtgovUIConfig config;

	/**
	 * C'tor.
	 */
	public SrampApiClientAccessor() {
	}

	/**
	 * Creates a new instance of an S-RAMP client.
	 */
	protected SrampAtomApiClient createClient() {
        String endpoint = config.getConfiguration().getString(DtgovUIConfig.SRAMP_ATOM_API_ENDPOINT);
        boolean validating = "true".equals(config.getConfiguration().getString(DtgovUIConfig.SRAMP_ATOM_API_VALIDATING)); //$NON-NLS-1$
        AuthenticationProvider authProvider = null;
        String authProviderClass = config.getConfiguration().getString(DtgovUIConfig.SRAMP_ATOM_API_AUTH_PROVIDER);
        try {
            if (authProviderClass != null && authProviderClass.trim().length() > 0) {
                Class<?> c = Class.forName(authProviderClass);
                Constructor<?> constructor = null;
                try {
                    constructor = c.getConstructor(Configuration.class);
                    authProvider = (AuthenticationProvider) constructor.newInstance(config.getConfiguration());
                } catch (NoSuchMethodException e) {}
                try {
                    constructor = c.getConstructor();
                    authProvider = (AuthenticationProvider) constructor.newInstance();
                } catch (NoSuchMethodException e) {}
            }
            return new SrampAtomApiClient(endpoint, authProvider, validating);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	/**
	 * @return the atom api client
	 */
	public SrampAtomApiClient getClient() {
        if (client.get() == null) {
            client.set(createClient());
        }
        client.get().setLocale(tlocale.get());
        return client.get();
	}

}
