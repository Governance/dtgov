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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Alternative;

import org.kie.internal.task.api.UserGroupCallback;

@Alternative
public class RewardsUserGroupCallback implements UserGroupCallback {

    @Override
    public boolean existsUser(String userId) {
        return userId.equals("eric") || userId.equals("kurt") || userId.equals("Administrator");
    }

    @Override
    public boolean existsGroup(String groupId) {
        return groupId.equals("PM") || groupId.equals("HR");
    }

    @Override
    public List<String> getGroupsForUser(String userId,
            List<String> groupIds, List<String> allExistingGroupIds) {
        List<String> groups = new ArrayList<String>();
        if (userId.equals("eric"))
            groups.add("PM");
        else if (userId.equals("kurt"))
            groups.add("HR");
        return groups;
    }
}
