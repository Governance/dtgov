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

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CDIUtil {
	/**
	 * Provides a simple API to programatically lookup a CDI bean (possibly from a non-CDI context)
	 *
	 * @param type
	 * @param qualifiers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <B> B getContextualBeanInstance(Class<B> type, Annotation... qualifiers) {
	    try {
	        BeanManager beanManager = InitialContext.doLookup("java:comp/BeanManager"); //$NON-NLS-1$
	        Set<Bean<?>> beans = beanManager.getBeans(type, qualifiers);
	        Bean<?> bean = beanManager.resolve(beans);
	        CreationalContext<?> cc = beanManager.createCreationalContext(bean);
	        return (B) beanManager.getReference(bean, type, cc);
	    } catch (NamingException e) {
	        throw new RuntimeException("", e); //$NON-NLS-1$
	    }
	}
}
