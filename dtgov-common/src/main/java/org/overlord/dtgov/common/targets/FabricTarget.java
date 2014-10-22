package org.overlord.dtgov.common.targets;

import java.io.Serializable;

import org.overlord.dtgov.common.Target;

public class FabricTarget extends Target implements Serializable{

    /**
    *
    */
    private static final long serialVersionUID = -5496658839580669371L;

    public FabricTarget(String jolokiaUrl, String user, String password, String name, String classifier) {
        super(name, classifier, TYPE.FABRIC);
        this.jolokiaUrl = jolokiaUrl;
        this.user = user;
        this.password = password;

    }


    /**
     * Instantiates a new Fabric target.
     *
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     */
    public FabricTarget(String name, String classifier) {
        super(name, classifier, TYPE.FABRIC);
    }

    /**
     * Constructor a target of type Maven.
     * 
     * @param name
     *            the name
     * @param classifier
     *            the classifier
     * @param mavenUrl
     *            the maven url
     * @param isReleaseEnabled
     *            the is release enabled
     * @param isSnapshotEnabled
     *            the is snapshot enabled
     * @return the target
     */
    public static final Target getTarget(String name, String classifier, String jolokiaUrl, String user, String password) {
        FabricTarget target = new FabricTarget(name, classifier);
        target.user = user;
        target.password = password;
        target.jolokiaUrl = jolokiaUrl;
        return target;
    }


    private String jolokiaUrl;

    private String user;

    private String password;

    public String getJolokiaUrl() {
        return jolokiaUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }


}
