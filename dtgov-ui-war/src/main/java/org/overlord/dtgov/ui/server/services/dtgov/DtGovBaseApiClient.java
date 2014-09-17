/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.dtgov.ui.server.services.dtgov;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.overlord.commons.config.JBossServer;
import org.overlord.dtgov.client.DtgovApiClient;
import org.overlord.dtgov.client.auth.AuthenticationProvider;
import org.overlord.dtgov.common.model.Deployer;
import org.overlord.dtgov.ui.server.DtgovUIConfig;

/**
 * Concrete implementation of the Dtgov client interfeace
 *
 * @author David Virgil Naranjo
 */
public class DtGovBaseApiClient implements IDtgovClient {

    private DtgovApiClient client;

    /**
     * Instantiates a new dt gov api client.
     *
     * @param config
     *            the config
     */
    public DtGovBaseApiClient(Configuration config) {
        String defaultApiEndpoint = JBossServer.getBaseUrl() + "/dtgov/rest/"; //$NON-NLS-1$
        String endpoint = config.getString(DtgovUIConfig.DTGOV_API_ENDPOINT, defaultApiEndpoint);
        AuthenticationProvider authProvider = null;
        String authProviderClass = config.getString(DtgovUIConfig.DTGOV_API_AUTH_PROVIDER);
        try {
            if (authProviderClass != null && authProviderClass.trim().length() > 0) {
                Class<?> c = Class.forName(authProviderClass);
                Constructor<?> constructor = null;
                try {
                    constructor = c.getConstructor(Configuration.class);
                    authProvider = (AuthenticationProvider) constructor.newInstance(config);
                } catch (NoSuchMethodException e) {
                }
                try {
                    constructor = c.getConstructor();
                    authProvider = (AuthenticationProvider) constructor.newInstance();
                } catch (NoSuchMethodException e) {
                }
            }
            client = new DtgovApiClient(endpoint, authProvider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.server.services.dtgov.IDtgovClient#stopProcess(
     * java.lang.String, long)
     */
    @Override
    public void stopProcess(String targetUUID, long processId) throws Exception {
        client.stopProcess(targetUUID, processId);
    }

    @Override
    public List<Deployer> getCustomDeployerNames() throws Exception {
        return client.getCustomDeployers();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.server.services.dtgov.IDtgovClient#setLocale(java
     * .util.Locale)
     */
    @Override
    public void setLocale(Locale locale) {
        client.setLocale(locale);
    }
}
