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
 * Bean that encapsulates the data that is sent by the Processes Filter side
 * bar.
 * 
 * @author David Virgil Naranjo
 */
@Portable
public class ProcessesFilterBean implements Serializable {

    private static final long serialVersionUID = 5238894183125636757L;

    /** The _workflow. */
    private String _workflow;

    /** The _name. */
    private String artifact;

    private ProcessStatusEnum _status;

    /**
     * Instantiates a new workflow queries filter bean.
     */
    public ProcessesFilterBean() {

    }

    /**
     * Instantiates a new processes filter bean.
     *
     * @param workflow
     *            the workflow
     * @param artifact
     *            the artifact
     * @param status
     *            the status
     */
    public ProcessesFilterBean(String workflow, String artifact, ProcessStatusEnum status) {
        super();
        this._workflow = workflow;
        this.artifact = artifact;
        this._status = status;
    }

    /**
     * Gets the _workflow.
     *
     * @return the _workflow
     */
    public String getWorkflow() {
        return _workflow;
    }

    /**
     * Sets the _workflow.
     *
     * @param workflow
     *            the new _workflow
     */
    public void setWorkflow(String workflow) {
        this._workflow = workflow;
    }

    /**
     * Gets the _name.
     *
     * @return the _name
     */
    public String getArtifact() {
        return artifact;
    }

    /**
     * Sets the _name.
     *
     * @param artifact
     *            the new _name
     */
    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public ProcessStatusEnum getStatus() {
        return _status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus(ProcessStatusEnum status) {
        this._status = status;
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
        result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
        result = prime * result + ((_status == null) ? 0 : _status.hashCode());
        result = prime * result + ((_workflow == null) ? 0 : _workflow.hashCode());
        return result;
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
        ProcessesFilterBean other = (ProcessesFilterBean) obj;
        if (artifact == null) {
            if (other.artifact != null)
                return false;
        } else if (!artifact.equals(other.artifact))
            return false;
        if (_status != other._status)
            return false;
        if (_workflow == null) {
            if (other._workflow != null)
                return false;
        } else if (!_workflow.equals(other._workflow))
            return false;
        return true;
    }





}
