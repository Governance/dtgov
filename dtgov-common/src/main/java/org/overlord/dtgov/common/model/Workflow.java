package org.overlord.dtgov.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "workflow")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Workflow")
public class Workflow {

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Uuid")
    private String uuid;

    public Workflow() {

    }

    public Workflow(String name, String uuid) {
        super();
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
