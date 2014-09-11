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
package org.overlord.dtgov.common.targets;

import java.io.Serializable;

import org.overlord.dtgov.common.Target;

/**
 * Copy Target Implementation
 * 
 * @author David Virgil Naranjo
 */
public class CopyTarget extends Target implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1048493803123298274L;

    private String deployDir;

    /**
     * Instantiates a new copy target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param deployDir
     *            the deploy dir
     */
    public CopyTarget(String name, String classifier, String deployDir) {
        super(name, classifier, TYPE.COPY);
        this.deployDir = deployDir;
    }

    /**
     * Instantiates a new copy target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     */
    public CopyTarget(String name, String classifier) {
        super(name, classifier, TYPE.COPY);
    }

    /**
     * Create a COPY style target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param deployDir
     *            the deploy dir
     * @return the target
     */
    public static final Target getTarget(String name, String classifier, String deployDir) {
        return new CopyTarget(name, classifier, deployDir);
    }

    /**
     * Gets the deploy dir.
     *
     * @return the deploy dir
     */
    public String getDeployDir() {
        return deployDir;
    }


    /**
     * Sets the deploy dir.
     *
     * @param deployDir
     *            the new deploy dir
     */
    public void setDeployDir(String deployDir) {
        this.deployDir = deployDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "\nDeployDir=" + deployDir; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
