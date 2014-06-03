/*
 * Copyright 2011 JBoss Inc
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
package org.overlord.sramp.governance.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.overlord.dtgov.taskapi.TaskApi;

/**
 * The DTGov RESTEasy application.
 */
public class GovernanceApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	/**
	 * Constructor.
	 */
	public GovernanceApplication() {
		classes.add(DeploymentResource.class);
		classes.add(NotificationResource.class);
		classes.add(SystemResource.class);
		classes.add(UpdateMetaDataResource.class);
        classes.add(TaskApi.class);
	}

	/**
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	/**
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
