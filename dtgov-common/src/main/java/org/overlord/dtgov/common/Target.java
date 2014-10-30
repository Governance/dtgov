/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.dtgov.common;

import java.util.Map;

import org.overlord.dtgov.common.targets.CliTarget;
import org.overlord.dtgov.common.targets.CopyTarget;
import org.overlord.dtgov.common.targets.CustomTarget;
import org.overlord.dtgov.common.targets.FabricTarget;
import org.overlord.dtgov.common.targets.MavenTarget;
import org.overlord.dtgov.common.targets.RHQTarget;

/**
 * A configured deployment target.  These are typically configured in the DTGov UI.
 *
 * @author eric.wittmann@redhat.com
 */
public abstract class Target {

    public enum TYPE {
        COPY, RHQ, AS_CLI, MAVEN, CUSTOM, FABRIC
    };

    /**
     * Create a COPY style target.
     * @param name
     * @param classifier
     * @param deployDir
     */
    public static final Target copy(String name, String classifier, String deployDir) {
        return CopyTarget.getTarget(name, classifier, deployDir);
    }

    /**
     * Constructs a Target of Type AS_CLI 'Application Server Command Line Interface.
     *
     * @param name - name of the target
     * @param classifier
     * @param asUser - AS user with admin rights
     * @param asPassword - password of the asUser
     * @param asHost - Application Server Hostname (defaults to localhost)
     * @param asPort - Application Server Port (defaults to 9999)
     * @param asDomainMode - whether the server is in domain mode or standalone
     * @param asServerGroup - the domain mode server group (optional)
     */
    public static final Target cli(String name, String classifier, String asUser, String asPassword, String asHost,
            Integer asPort, Boolean asDomainMode, String asServerGroup) {
        return CliTarget.getTarget(name, classifier, asUser, asPassword, asHost, asPort, asDomainMode, asServerGroup);
    }

    /**
     * Constructs a Target of Type RHQ to use it (JON) to deploy archives to a RHQ server
     * group. The RHQ Server group needs to be prefined and needs to contain Application
     * Server resources only.
     *
     * @param name - name of the target - which needs to correspond to the RHQ Server Group.
     * @param classifier
     * @param rhqUser - username of the RHQ user with rights to deploy to that group.
     * @param rhqPassword - password of the rhqUser.
     * @param rhqBaseUrl - baseUrl of the RHQ Server i.e. http://localhost:7080/
     */
    public static final Target rhq(String name, String classifier, String rhqUser, String rhqPassword, String rhqBaseUrl,
    		String rhqPluginName, String rhqGroup) {
        return RHQTarget.getTarget(name, classifier, rhqUser, rhqPassword, rhqBaseUrl, rhqPluginName, rhqGroup);
    }

    /**
     * Constructs a Target of Type RHQ to use it (JON) to deploy archives to a
     * RHQ server group. The RHQ Server group needs to be prefined and needs to
     * contain Application Server resources only.
     * 
     * @param name
     *            - name of the target - which needs to correspond to the RHQ
     *            Server Group.
     * @param classifier
     * @param rhqUser
     *            - username of the RHQ user with rights to deploy to that
     *            group.
     * @param rhqPassword
     *            - password of the rhqUser.
     * @param rhqBaseUrl
     *            - baseUrl of the RHQ Server i.e. http://localhost:7080/
     */
    public static final Target fabric(String name, String classifier, String user, String password, String jolokiaUrl) {
        return FabricTarget.getTarget(name, classifier, jolokiaUrl, user, password);
    }

    /**
     * Constructor a target of type Maven.
     * @param name
     * @param classifier
     * @param mavenUrl
     * @param isReleaseEnabled
     * @param isSnapshotEnabled
     */
    public static final Target maven(String name, String classifier, String mavenUrl,
            boolean isReleaseEnabled, boolean isSnapshotEnabled) {
        return MavenTarget.getTarget(name, classifier, mavenUrl, isReleaseEnabled, isSnapshotEnabled);
    }

    /**
     * Constructor a target of custom type.
     *
     * @param name
     * @param classifier
     * @param mavenUrl
     * @param isReleaseEnabled
     * @param isSnapshotEnabled
     */
    public static final Target custom(String name, String classifier, String customType, Map<String, String> properties) {
        return CustomTarget.getTarget(name, classifier, customType, properties);
    }

    /**
     * Constructor a target of type Maven.
     *
     * @param name
     * @param classifier
     * @param mavenUrl
     * @param isReleaseEnabled
     * @param isSnapshotEnabled
     */
    public static final Target maven(String name, String classifier, String mavenUrl, String mavenUser, String mavenPassword, boolean isReleaseEnabled,
            boolean isSnapshotEnabled) {
        return MavenTarget.getTarget(name, classifier, mavenUrl, isReleaseEnabled, isSnapshotEnabled);
    }

	private String name;
	private String classifier;
    private TYPE type;
    private String description;



    public Target(String name, String classifier, TYPE type) {
        super();
        this.name = name;
        this.classifier = classifier;
        this.type = type;
    }

    public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}



	public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }









}
