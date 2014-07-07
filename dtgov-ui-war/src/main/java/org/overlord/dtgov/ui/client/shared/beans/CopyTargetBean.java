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
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * It contains the copy target fields. It is a subtype of the TargetBean.
 *
 * @author David Virgil Naranjo
 *
 */
@Portable
public class CopyTargetBean extends TargetBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5726878635275230215L;

    private String _deployDirectory;

    /**
     * Instantiates a new copy target bean.
     *
     * @param uuid
     *            the uuid
     * @param classifiers
     *            the classifiers
     * @param description
     *            the description
     * @param name
     *            the name
     * @param deployDirectory
     *            the deploy directory
     */
    public CopyTargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name, String deployDirectory) {
        super(uuid, classifiers, description, name);
        this._deployDirectory = deployDirectory;
    }

    /**
     * Instantiates a new copy target bean.
     */
    public CopyTargetBean() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#getType()
     */
    @Override
    public TargetType getType() {
        return TargetType.COPY;
    }

    /**
     * Gets the deploy directory.
     *
     * @return the deploy directory
     */
    public String getDeployDirectory() {
        return _deployDirectory;
    }

    /**
     * Sets the deploy directory.
     *
     * @param deployDirectory
     *            the new deploy directory
     */
    public void setDeployDirectory(String deployDirectory) {
        this._deployDirectory = deployDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.overlord.dtgov.ui.client.shared.beans.TargetBean#toString()
     */
    @Override
    public String toString() {
        return "CopyTargetBean [" + super.toString() + ", deployDirectory=" + _deployDirectory + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }



}
