/*
 * Copyright 2014 JBoss Inc
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

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;



/**
 * The Class WorkflowQuerySummaryBean.
 */
@Portable
public class WorkflowQuerySummaryBean implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7284042552981203699L;

    /** The _description. */
    private String _description;

    /** The _name. */
    private String _name;

    /** The _query. */
    private String _query;

    /** The _uuid. */
    private String _uuid;

    /** The _workflow. */
    private String _workflow;

    /**
     * Instantiates a new workflow query summary bean.
     */
    public WorkflowQuerySummaryBean() {

    }

    /*
     * (non-Javadoc)
     * 
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
        WorkflowQuerySummaryBean other = (WorkflowQuerySummaryBean) obj;
        if (_description == null) {
            if (other._description != null)
                return false;
        } else if (!_description.equals(other._description))
            return false;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        if (_query == null) {
            if (other._query != null)
                return false;
        } else if (!_query.equals(other._query))
            return false;
        if (_uuid == null) {
            if (other._uuid != null)
                return false;
        } else if (!_uuid.equals(other._uuid))
            return false;
        if (_workflow == null) {
            if (other._workflow != null)
                return false;
        } else if (!_workflow.equals(other._workflow))
            return false;
        return true;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the query.
     * 
     * @return the query
     */
    public String getQuery() {
        return _query;
    }

    /**
     * Gets the uuid.
     * 
     * @return the uuid
     */
    public String getUuid() {
        return _uuid;
    }

    /**
     * Gets the workflow.
     * 
     * @return the workflow
     */
    public String getWorkflow() {
        return _workflow;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_description == null) ? 0 : _description.hashCode());
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        result = prime * result + ((_query == null) ? 0 : _query.hashCode());
        result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
        result = prime * result + ((_workflow == null) ? 0 : _workflow.hashCode());
        return result;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the description
     * @return the workflow query summary bean
     */
    public WorkflowQuerySummaryBean setDescription(String description) {
        this._description = description;
        return this;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name
     * @return the workflow query summary bean
     */
    public WorkflowQuerySummaryBean setName(String name) {
        this._name = name;
        return this;
    }

    /**
     * Sets the query.
     * 
     * @param query
     *            the query
     * @return the workflow query summary bean
     */
    public WorkflowQuerySummaryBean setQuery(String query) {
        this._query = query;
        return this;
    }

    /**
     * Sets the uuid.
     * 
     * @param uuid
     *            the uuid
     * @return the workflow query summary bean
     */
    public WorkflowQuerySummaryBean setUuid(String uuid) {
        this._uuid = uuid;
        return this;
    }

    /**
     * Sets the workflow.
     * 
     * @param workflow
     *            the workflow
     * @return the workflow query summary bean
     */
    public WorkflowQuerySummaryBean setWorkflow(String workflow) {
        this._workflow = workflow;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WorkflowQuerySummaryBean [uuid=" + _uuid + ", name=" + _name + ", description="
                + _description + ", query=" + _query + ", workflow=" + _workflow + "]";
    }

}
