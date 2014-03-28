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
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;


/**
 * The Class WorkflowQueryBean.
 */
/**
 * @author David Virgil Naranjo
 * 
 */
@Portable
public class WorkflowQueryBean implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7470736189849298695L;

    /** The _uuid. */
    private String _uuid;

    /** The _name. */
    private String _name;

    /** The _description. */
    private String _description;

    /** The _query. */
    private String _query;

    /** The _workflow. */
    private String _workflow;

    // private Map<String,String> properties;

    /** The properties. */
    private List<WorkflowQueryProperty> properties;

    public WorkflowQueryBean() {
        // properties=new HashMap<String, String>();
        properties = new ArrayList<WorkflowQueryProperty>();
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
     * @param uuid
     * @return
     */
    public WorkflowQueryBean setUuid(String uuid) {
        this._uuid = uuid;
        return this;
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
     * @param name
     * @return
     */
    public WorkflowQueryBean setName(String name) {
        this._name = name;
        return this;
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
     * @param description
     * @return
     */
    public WorkflowQueryBean setDescription(String description) {
        this._description = description;
        return this;
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
     * @param query
     * @return
     */
    public WorkflowQueryBean setQuery(String query) {
        this._query = query;
        return this;
    }

    /**
     * Gets the workflow.
     * 
     * @return the workflow
     */
    public String getWorkflow() {
        return _workflow;
    }

    /**
     * @param workflow
     * @return
     */
    public WorkflowQueryBean setWorkflow(String workflow) {
        this._workflow = workflow;
        return this;
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
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((_query == null) ? 0 : _query.hashCode());
        result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
        result = prime * result + ((_workflow == null) ? 0 : _workflow.hashCode());
        return result;
    }

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public List<WorkflowQueryProperty> getProperties() {
        return properties;
    }

    /**
     * @param properties
     * @return
     */
    public WorkflowQueryBean setProperties(List<WorkflowQueryProperty> properties) {
        this.properties = properties;
        return this;
    }

    /**
     * @param key
     * @param value
     */
    public void addWorkflowQueryProperty(String key, String value) {
        if (this.properties == null) {
            this.properties = new ArrayList<WorkflowQueryProperty>();
        }
        this.properties.add((new WorkflowQueryProperty()).setKey(key).setValue(value));
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
        WorkflowQueryBean other = (WorkflowQueryBean) obj;
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
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WorkflowQueryBean [uuid=" + _uuid + ", name=" + _name + ", description=" + _description
                + ", query=" + _query + ", workflow=" + _workflow + ", properties=" + properties + "]";
    }

}
