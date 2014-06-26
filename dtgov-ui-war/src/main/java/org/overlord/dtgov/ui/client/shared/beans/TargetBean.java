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
 * It contains the common fields of all the Targets.
 *
 * @author David Virgil Naranjo
 *
 */
@Portable
public class TargetBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8215641937953030367L;

    private String _uuid;

    private List<TargetClassifier> _classifiers;

    private String _description;

    private String _name;

    /**
     * Instantiates a new target bean.
     *
     * @param uuid
     *            the uuid
     * @param classifiers
     *            the classifiers
     * @param description
     *            the description
     * @param name
     *            the name
     */
    public TargetBean(String uuid, List<TargetClassifier> classifiers, String description, String name) {
        super();
        this._uuid = uuid;
        this._classifiers = classifiers;
        this._description = description;
        this._name = name;
    }

    /**
     * Instantiates a new target bean.
     */
    public TargetBean() {
    }


    /**
     * Gets the type.
     *
     * @return the type
     */
    public TargetType getType() {
        return null;
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
     * Gets the classifiers.
     *
     * @return the classifiers
     */
    public List<TargetClassifier> getClassifiers() {
        return _classifiers;
    }

    /**
     * Sets the classifiers.
     *
     * @param classifiers
     *            the new classifiers
     */
    public void setClassifiers(List<TargetClassifier> classifiers) {
        this._classifiers = classifiers;
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
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this._description = description;
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
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this._name = name;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String classifierToString = "";
        if (_classifiers != null) {
            for (TargetClassifier classifier : _classifiers) {
                classifierToString += classifier.getValue() + " ,";
            }
        }

        if (classifierToString != null && !classifierToString.equals("")) {
            classifierToString = classifierToString.substring(0, classifierToString.length() - 1);
        }
        return "uuid=" + _uuid + ", classifiers=" + classifierToString + ", description=" + _description + ", name=" + _name;
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
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
        TargetBean other = (TargetBean) obj;
        if (_uuid == null) {
            if (other._uuid != null)
                return false;
        } else if (!_uuid.equals(other._uuid))
            return false;

        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;

        return true;
    }

}
