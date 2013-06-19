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
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;

import org.kie.internal.task.api.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Loosely based on org.jbpm.task.identity.JAASUserGroupCallbackImpl
 * 
 * @author kstam
 *
 */
@Alternative
public class DTGovUserGroupCallback implements UserGroupCallback {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    public boolean existsUser(String userId) {
    	// allows everything as there is no way to ask JAAS/JACC for users in the domain
        return true;
    }

    public boolean existsGroup(String groupId) {
    	// allows everything as there is no way to ask JAAS/JACC for groups in the domain
    	return true;
    }

    public List<String> getGroupsForUser(String userId,
            List<String> groupIds, List<String> allExistingGroupIds) 
    {
    	List<String> roles = null;
        try {
            Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
    
            if (subject != null) {
                Set<Principal> principals = subject.getPrincipals();
    
                if (principals != null) {
                    roles = new ArrayList<String>();
                    for (Principal principal : principals) {
                        if (principal instanceof Group) { //&& rolePrincipleName.equalsIgnoreCase(principal.getName())) {
                            Enumeration<? extends Principal> groups = ((Group) principal).members();
                            
                            while (groups.hasMoreElements()) {
                                Principal groupPrincipal = (Principal) groups.nextElement();
                                roles.add(groupPrincipal.getName());
                               
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error when getting user roles, userid:" + userId, e);
        }
        if (roles==null || roles.size()==0) {
	        roles = new ArrayList<String>();
	        if (userId.equals("john"))
	        	roles.add("PM");
	        else if (userId.equals("mary"))
	        	roles.add("HR");
        }
        return roles;
    }
}
