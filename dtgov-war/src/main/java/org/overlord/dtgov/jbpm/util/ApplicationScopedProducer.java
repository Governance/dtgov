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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.kie.internal.task.api.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationScopedProducer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Inject
    private UserGroupCallback usergroupCallback;

    @PersistenceUnit(unitName = "org.overlord.dtgov.jbpm")
    private EntityManagerFactory emf;

    @ApplicationScoped
    @Produces
    public UserGroupCallback produceUserGroupCallback() {
        return usergroupCallback;
    }

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence
                    .createEntityManagerFactory("org.overlord.dtgov.jbpm"); //$NON-NLS-1$
        }
        return this.emf;
    }

    @Produces
    @ApplicationScoped
    public EntityManager getEntityManager() {
        final EntityManager em = getEntityManagerFactory().createEntityManager();
        EntityManager emProxy = (EntityManager) 
                Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{EntityManager.class}, new EmInvocationHandler(em));
        return emProxy;
    }

    @ApplicationScoped
    public void commitAndClose(@Disposes EntityManager em) {
        try {
            em.close();
        } catch (Exception e) {

        }
    }
    
    private class EmInvocationHandler implements InvocationHandler {

        private EntityManager delegate;
        
        EmInvocationHandler(EntityManager em) {
            this.delegate = em;
        }
        
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            joinTransactionIfNeeded();
            return method.invoke(delegate, args);
        }
        
        private void joinTransactionIfNeeded() {
            try {
                UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction"); //$NON-NLS-1$
                if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    delegate.joinTransaction();
                }
            } catch (NamingException e) {
                logger.debug(e.getMessage(),e);
            } catch (Exception e) {
            	logger.error(e.getMessage(),e);
            }
        }
    }

}
