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
 * The Class TargetSummaryBean. It contains the fields used in the TargetsPage. Just stored the target fields used in that table.
 * @author David Virgil Naranjo
 *
 */
@Portable
public class TargetSummaryBean implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 2078557245936684428L;

    private String _uuid;

    private String _description;

    private String _name;

    private TargetType _type;

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

    /**
     * Gets the type.
     *
     * @return the type
     */
    public TargetType getType() {
        return _type;
    }

    /**
     * Sets the type.
     *
     * @param type
     *            the new type
     */
    public void setType(TargetType type) {
        this._type = type;
    }

    /**
     * Instantiates a new target summary bean.
     *
     * @param uuid
     *            the uuid
     * @param description
     *            the description
     * @param name
     *            the name
     * @param type
     *            the type
     */
    public TargetSummaryBean(String uuid, String description, String name, TargetType type) {
        super();
        this._uuid = uuid;
        this._description = description;
        this._name = name;
        this._type = type;
    }

    /**
     * Instantiates a new target summary bean.
     */
    public TargetSummaryBean() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TargetSummaryBean [uuid=" + _uuid + ", description=" + _description + ", name=" + _name + ", type=" + _type + "]";
    }

}
