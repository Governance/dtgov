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

package org.overlord.dtgov.jbpm.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.kie.api.KieBase;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.task.api.UserGroupCallback;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.governance.Governance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationScopedProducer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private InjectableRegisterableItemsFactory factory;
    @Inject
    private UserGroupCallback usergroupCallback;

    @PersistenceUnit(unitName = "org.overlord.dtgov.jbpm")
    private EntityManagerFactory emf;

    @Produces
    public UserGroupCallback produceUserGroupCallback() {
        return usergroupCallback;
    }

    @Produces
    public EntityManagerFactory produceEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence
                    .createEntityManagerFactory("org.overlord.dtgov.jbpm"); //$NON-NLS-1$
        }
        return this.emf;
    }

    @Produces
    @Singleton
    @PerProcessInstance
    @PerRequest
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {


        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder
                .getDefault()
                .entityManagerFactory(emf)
                .userGroupCallback(usergroupCallback)
                .registerableItemsFactory(factory);

        KieBase kbase = getKieBase();
        if (kbase!=null) builder.knowledgeBase(getKieBase());
        return builder.get();
    }

    private KieBase getKieBase() {
    	Governance governance = new Governance();
    	try {
    		KieSrampUtil kieSrampUtil = new KieSrampUtil();
	    	return kieSrampUtil.getKieBase();
    	} catch (Exception e) {
    		logger.error(Messages.i18n.format("ApplicationScopedProducer.ErrorNotFound", governance.getGovernanceWorkflowPackage())); //$NON-NLS-1$
    		logger.error(e.getMessage(),e);
    	}
    	return null;
    }




}
