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
 * Bean that encapsulates the process data that is show to the user in the
 * processes page table.
 *
 * @author David Virgil Naranjo
 */
@Portable
public class ProcessBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6041687137363242066L;

    private String _uuid;

    private String _workflow;

    private String _artifactName;

    private String _artifactId;

    private ProcessStatusEnum _status;


    /**
     * Instantiates a new process bean.
     */
    public ProcessBean() {
        super();
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
     * Sets the uuid.
     *
     * @param uuid
     *            the new uuid
     */
    public void setUuid(String uuid) {
        this._uuid = uuid;
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
     * Sets the workflow.
     *
     * @param workflow
     *            the new workflow
     */
    public void setWorkflow(String workflow) {
        this._workflow = workflow;
    }

    /**
     * Gets the artifact name.
     *
     * @return the artifact name
     */
    public String getArtifactName() {
        return _artifactName;
    }

    /**
     * Sets the artifact name.
     *
     * @param artifactName
     *            the new artifact name
     */
    public void setArtifactName(String artifactName) {
        this._artifactName = artifactName;
    }

    /**
     * Gets the artifact id.
     *
     * @return the artifact id
     */
    public String getArtifactId() {
        return _artifactId;
    }

    /**
     * Sets the artifact id.
     *
     * @param artifactId
     *            the new artifact id
     */
    public void setArtifactId(String artifactId) {
        this._artifactId = artifactId;
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


    /**
     * Instantiates a new process bean.
     *
     * @param uuid
     *            the uuid
     * @param workflow
     *            the workflow
     * @param artifactName
     *            the artifact name
     * @param artifactId
     *            the artifact id
     * @param status
     *            the status
     */
    public ProcessBean(String uuid, String workflow, String artifactName, String artifactId, ProcessStatusEnum status) {
        super();
        this._uuid = uuid;
        this._workflow = workflow;
        this._artifactName = artifactName;
        this._artifactId = artifactId;
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
        result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
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
        ProcessBean other = (ProcessBean) obj;
        if (_uuid == null) {
            if (other._uuid != null)
                return false;
        } else if (!_uuid.equals(other._uuid))
            return false;

        return true;
    }

}
