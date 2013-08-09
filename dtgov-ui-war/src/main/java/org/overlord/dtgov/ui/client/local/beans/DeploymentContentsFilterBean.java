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
package org.overlord.dtgov.ui.client.local.beans;

import org.overlord.dtgov.ui.client.shared.beans.ExpandedArtifactSummaryBean;


/**
 * All of filter settings on the deployment contents page.
 *
 * @author eric.wittmann@redhat.com
 */
public class DeploymentContentsFilterBean {

    private String name;
    private String type;

    /**
     * Constructor.
     */
    public DeploymentContentsFilterBean() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public DeploymentContentsFilterBean setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public DeploymentContentsFilterBean setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Returns true iff the given event matches the criteria in the filter.
     * @param item
     */
    public boolean accepts(ExpandedArtifactSummaryBean bean) {
        String nameCriteria = getName();
        if (nameCriteria != null && nameCriteria.trim().length() > 0) {
            String beanName = bean.getName().toLowerCase();
            if (nameCriteria.startsWith("*") && nameCriteria.endsWith("*")) { //$NON-NLS-1$ //$NON-NLS-2$
                String criteria = nameCriteria.substring(1, nameCriteria.length() - 1).toLowerCase();
                if (!beanName.contains(criteria)) {
                    return false;
                }
            } else if (nameCriteria.endsWith("*")) { //$NON-NLS-1$
                String criteria = nameCriteria.substring(0, nameCriteria.length() - 1).toLowerCase();
                if (!beanName.startsWith(criteria)) {
                    return false;
                }
            } else if (nameCriteria.startsWith("*")) { //$NON-NLS-1$
                String criteria = nameCriteria.substring(1).toLowerCase();
                if (!beanName.toLowerCase().endsWith(criteria)) {
                    return false;
                }
            } else {
                if (!bean.getName().equals(nameCriteria)) {
                    return false;
                }
            }
        }
        if (getType() != null && getType().trim().length() > 0 && !getType().equalsIgnoreCase(bean.getType())) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeploymentContentsFilterBean other = (DeploymentContentsFilterBean) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
