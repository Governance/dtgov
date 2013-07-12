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
	        BeanManager beanManager = InitialContext.doLookup("java:comp/BeanManager");
	        Set<Bean<?>> beans = beanManager.getBeans(type, qualifiers);
	        Bean<?> bean = beanManager.resolve(beans);
	        CreationalContext<?> cc = beanManager.createCreationalContext(bean);
	        return (B) beanManager.getReference(bean, type, cc);
	    } catch (NamingException e) {
	        throw new RuntimeException("", e);
	    }
	}
}
