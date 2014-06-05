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

import javax.enterprise.inject.Alternative;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.jboss.seam.transaction.DefaultSeamTransaction;
import org.jboss.seam.transaction.DefaultTransaction;

/**
 * A custom seam transaction that pulls the usertransaction from a different
 * jndi location.
 * 
 * @author eric.wittmann@redhat.com
 */
@Alternative
@DefaultTransaction
public class FuseSeamTransaction extends DefaultSeamTransaction {

    /**
     * Constructor.
     */
    public FuseSeamTransaction() {
    }

    /**
     * @see org.jboss.seam.transaction.DefaultSeamTransaction#getUserTransaction()
     */
    @Override
    protected UserTransaction getUserTransaction() throws NamingException {
        InitialContext context = new InitialContext();
        try {
            return (javax.transaction.UserTransaction) context.lookup("osgi:service/javax.transaction.UserTransaction"); //$NON-NLS-1$
        } catch (NamingException ne) {
            return super.getUserTransaction();
        }
    }

}
