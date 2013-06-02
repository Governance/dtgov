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
package org.overlord.dtgov.ui.client.shared.beans;

/**
 * The owner of a human task.
 *
 * @author eric.wittmann@redhat.com
 */
public enum TaskOwnerEnum {

    any, mine, active, group;

    /**
     * Creates an owner enum given four booleans (any, mine, active, group).  The first of these
     * that is true will win out.  This method is useful in the UI, where the value is displayed
     * as four radio buttons.
     * @param any
     * @param mine
     * @param active
     * @param group
     */
    public static TaskOwnerEnum valueOf(Boolean any, Boolean mine, Boolean active, Boolean group) {
        if (any)
            return TaskOwnerEnum.any;
        if (mine)
            return TaskOwnerEnum.mine;
        if (active)
            return TaskOwnerEnum.active;
        if (group)
            return TaskOwnerEnum.group;
        // Return null here - unspecified behavior.
        return null;
    }

}
