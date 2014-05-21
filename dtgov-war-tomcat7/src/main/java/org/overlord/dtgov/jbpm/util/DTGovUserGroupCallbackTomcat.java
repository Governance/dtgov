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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Role;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.users.MemoryUser;
import org.kie.internal.task.api.UserGroupCallback;
import org.overlord.commons.auth.filters.HttpRequestThreadLocalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Loosely based on org.jbpm.task.identity.JAASUserGroupCallbackImpl
 *
 * @author kstam
 *
 */
@Alternative
public class DTGovUserGroupCallbackTomcat implements UserGroupCallback {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @see org.kie.internal.task.api.UserGroupCallback#existsUser(java.lang.String)
     */
    @Override
    public boolean existsUser(String userId) {
    	// allow everything as there is no way to ask JAAS/JACC for users in the domain
        return true;
    }

    /**
     * @see org.kie.internal.task.api.UserGroupCallback#existsGroup(java.lang.String)
     */
    @Override
    public boolean existsGroup(String groupId) {
    	// allow everything as there is no way to ask JAAS/JACC for groups in the domain
    	return true;
    }

    /**
     * @see org.kie.internal.task.api.UserGroupCallback#getGroupsForUser(java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public List<String> getGroupsForUser(String userId, List<String> groupIds,
            List<String> allExistingGroupIds) {
    	List<String> roles = null;
        try {
        	HttpServletRequest request = HttpRequestThreadLocalFilter.TL_request.get();
            Principal principal = request.getUserPrincipal();
            if (principal instanceof GenericPrincipal) {
                GenericPrincipal gp = (GenericPrincipal) principal;
                String[] gpRoles = gp.getRoles();
                roles = new ArrayList<String>(gpRoles.length);
                for (String role : gpRoles) {
                    roles.add(role);
                }
            } else if (principal instanceof MemoryUser) {
            	MemoryUser mu = (MemoryUser) principal;
            	Iterator<Role> iter = mu.getRoles();
            	roles = new ArrayList<String>();
            	while (iter.hasNext()) {
            		roles.add(iter.next().getRolename());
            	}
            }
        } catch (Exception e) {
            logger.error("ErrorGettingRoles for user " + userId, e); //$NON-NLS-1$
        }
        return roles;
    }
}
