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
package org.overlord.dtgov.ui.server.api;

import java.lang.reflect.Constructor;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.configuration.Configuration;

/**
 * The class used whenever a request to a task server needs to be made.
 *
 * @author eric.wittmann@redhat.com
 */
@Singleton
public class TaskClientAccessor {

    private transient ITaskClient client;

	/**
	 * C'tor.
	 */
    @Inject
	public TaskClientAccessor(DtgovUIConfig config) {
		String clientClassname = (String) config.getConfiguration().getProperty("dtgov-ui.task-client.class");
		if (clientClassname != null) {
		    try {
                Class<?> clientClass = Class.forName(clientClassname);
                Constructor<?> constructor = null;
                try {
                    constructor = clientClass.getConstructor(Configuration.class);
                    client = (ITaskClient) constructor.newInstance(config.getConfiguration());
                } catch (NoSuchMethodException e) {}
                try {
                    constructor = clientClass.getConstructor();
                    client = (ITaskClient) constructor.newInstance();
                } catch (NoSuchMethodException e) {}
            } catch (Exception e) {
                throw new RuntimeException("Error creating the Task Inbox client.", e);
            }
		}
		if (client == null) {
		    throw new RuntimeException("Failed to create the Task Inbox client from: " + clientClassname);
		}
	}

	/**
	 * @return the atom api client
	 */
	public ITaskClient getClient() {
	    return client;
	}

}
