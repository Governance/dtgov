/**
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

package org.overlord.dtgov.jbpm.ejb;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.overlord.dtgov.server.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@javax.ejb.Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessBean implements ProcessLocal {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private UserTransaction ut;

	@Inject
	@Singleton
	private RuntimeManager singletonManager;
  
	@PostConstruct
	public void configure() {
	// use toString to make sure CDI initializes the bean
		singletonManager.toString();
	} 
	
	@Inject
	TaskService taskService;

	/**
	 * Starts up a new ProcessInstance with the given ProcessId. The
	 * parameters Map is set into the context of the workflow.
	 *
	 */
	@Override
    public long startProcess(String processId, Map<String, Object> parameters)
			throws Exception {

		RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext.get());
		KieSession ksession = runtime.getKieSession();

		long processInstanceId = -1;
		ut.begin();
		try {
			// start a new process instance
			ProcessInstance processInstance = ksession.startProcess(processId,
					parameters);
			processInstanceId = processInstance.getId();
			logger.info(Messages.i18n.format("ProcessBean.Started", processInstanceId)); //$NON-NLS-1$
			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (ut.getStatus() == Status.STATUS_ACTIVE) {
				ut.rollback();
			}
			throw e;
		}
		return processInstanceId;
	}

	@Override
    public Collection<ProcessInstance> listProcessInstances() throws Exception {

		RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext
				.get());

		KieSession ksession = runtime.getKieSession();

		Collection<ProcessInstance> processInstances = null;
		ut.begin();

		try {
			processInstances = ksession.getProcessInstances();
			for (ProcessInstance processInstance : processInstances) {
				logger.info(processInstance.getProcess().getName());

				System.out.println(".."); //$NON-NLS-1$
			}
			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (ut.getStatus() == Status.STATUS_ACTIVE) {
				ut.rollback();
			}
			throw e;
		} finally {
			ksession.dispose();
		}
		return processInstances;

	}

	@Override
    public void listProcessInstanceDetail(long processId) throws Exception {

		RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext
				.get());
		KieSession ksession = runtime.getKieSession();
		logger.info("ksession=" + ksession); //$NON-NLS-1$

		ut.begin();

		try {
			ProcessInstance processInstance = ksession
					.getProcessInstance(processId);
			if (processInstance != null) {
				System.out.println(processInstance.getProcess().getName());
				System.out.println(processInstance.getState());

				System.out.println(".."); //$NON-NLS-1$
			}
			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (ut.getStatus() == Status.STATUS_ACTIVE) {
				ut.rollback();
			}
			throw e;
		}

	}

}
