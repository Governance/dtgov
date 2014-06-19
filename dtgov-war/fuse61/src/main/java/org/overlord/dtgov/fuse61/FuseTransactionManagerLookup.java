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

package org.overlord.dtgov.fuse61;

import java.util.Properties;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * A hibernate transaction manager lookup that works in Fuse.
 *
 * @author eric.wittmann@redhat.com
 */
public class FuseTransactionManagerLookup implements TransactionManagerLookup {
    
    private static TransactionManager tm;
    
    /**
     * Constructor.
     */
    public FuseTransactionManagerLookup() {
    }

    /**
     * @see org.hibernate.transaction.TransactionManagerLookup#getTransactionManager(java.util.Properties)
     */
    @Override
    public TransactionManager getTransactionManager(Properties props) throws HibernateException {
        if (tm == null) {
            try {
                Bundle bundle = FrameworkUtil.getBundle(getClass());
                BundleContext context = bundle.getBundleContext();
                ServiceReference[] serviceReferences = context.getServiceReferences(TransactionManager.class.getName(), null);
                if (serviceReferences != null) {
                    if (serviceReferences.length == 1) {
                        tm = (TransactionManager) context.getService(serviceReferences[0]);
                    } else if (serviceReferences.length == 0) {
                        throw new IllegalStateException("No Tx manager found!"); //$NON-NLS-1$
                    } else {
                        throw new IllegalStateException("Too many Tx managers found!"); //$NON-NLS-1$
                    }
                }
            } catch (InvalidSyntaxException e) {
                throw new IllegalStateException("No Tx manager found!"); //$NON-NLS-1$
            }
        }
        return tm;
    }

    /**
     * @see org.hibernate.transaction.TransactionManagerLookup#getUserTransactionName()
     */
    @Override
    public String getUserTransactionName() {
        return "osgi:service/javax.transaction.UserTransaction"; //$NON-NLS-1$
    }

    /**
     * @see org.hibernate.transaction.TransactionManagerLookup#getTransactionIdentifier(javax.transaction.Transaction)
     */
    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }

}
