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

import org.overlord.dtgov.common.targets.TargetConstants;

/**
 * A configured deployment target.  These are typically configured in the DTGov UI.
 *
 * @author eric.wittmann@redhat.com
 */
public class Target {

    public enum TYPE {
        COPY, RHQ, AS_CLI, MAVEN, CUSTOM
    };

    /**
     * Create a COPY style target.
     * @param name
     * @param classifier
     * @param deployDir
     */
    public static final Target copy(String name, String classifier, String deployDir) {
        Target target = new Target();
        target.name = name;
        target.classifier = classifier;
        target.type = TYPE.COPY;
        target.deployDir = deployDir;
        return target;
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
        Target target = new Target();
        target.name = name;
        target.classifier = classifier;
        target.type = TYPE.AS_CLI;
        target.user = asUser;
        target.password = asPassword;
        target.cliDomainMode = asDomainMode == null ? false : asDomainMode;
        target.cliServerGroup = asServerGroup;
        if (asHost != null) {
            target.host = asHost;
        } else {
            target.host = "localhost"; //$NON-NLS-1$
        }
        if (asPort != null && asPort > 0) {
            target.port = asPort;
        } else {
            target.port = 9999;
        }
        return target;
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
        Target target = new Target();
        target.name = name;
        target.classifier = classifier;
        target.type = TYPE.RHQ;
        target.user = rhqUser;
        target.password = rhqPassword;
        int secondColon = rhqBaseUrl.indexOf(":", rhqBaseUrl.indexOf(":") + 1); //$NON-NLS-1$ //$NON-NLS-2$
        if (secondColon > 0) {
            target.rhqBaseUrl = rhqBaseUrl.substring(0, secondColon);
            String portStr = rhqBaseUrl.substring(secondColon + 1);
            int slashPosition = portStr.indexOf("/"); //$NON-NLS-1$
            if (slashPosition > 0) {
                portStr = portStr.substring(0, slashPosition);
            }
            target.port = Integer.valueOf(portStr);
        } else {
            target.rhqBaseUrl = rhqBaseUrl;
            target.port = 7080;
        }
        target.rhqPluginName = rhqPluginName;
        target.rhqGroup=rhqGroup;
        return target;
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
        Target target = new Target();
        target.name = name;
        target.classifier = classifier;
        target.type = TYPE.MAVEN;
        target.mavenUrl = mavenUrl;
        target.setReleaseEnabled(isReleaseEnabled);
        target.setSnapshotEnabled(isSnapshotEnabled);
        return target;
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
        Target target = new Target();
        target.name = name;
        target.classifier = classifier;
        target.type = TYPE.CUSTOM;
        target.customType = customType;
        target.properties = properties;
        return target;
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
        Target target = maven(name, classifier, mavenUrl, isReleaseEnabled, isSnapshotEnabled);
        target.user = mavenUser;
        target.password = mavenPassword;
        return target;
    }

	private String name;
	private String classifier;
    private TYPE type;
    private String deployDir;
    private String user;
    private String password;
    private String rhqBaseUrl;
    private String rhqPluginName;
    private String rhqGroup;
    private String host;
    private Integer port;
    private String mavenUrl;
    private boolean isReleaseEnabled;
    private boolean isSnapshotEnabled;
    private boolean cliDomainMode;
    private String cliServerGroup;
    private String description;
    private String customType;
    Map<String, String> properties;

    /**
     * Constructor.
     */
    public Target() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRhqBaseUrl() {
		return rhqBaseUrl;
	}

	public void setRhqBaseUrl(String rhqBaseUrl) {
		this.rhqBaseUrl = rhqBaseUrl;
	}

	public String getRhqPluginName() {
		return rhqPluginName;
	}

	public void setRhqPluginName(String rhqPluginName) {
		this.rhqPluginName = rhqPluginName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDeployDir(String deployDir) {
        this.deployDir = deployDir;
    }

    public String getDeployDir() {
        return deployDir;
    }

	public String getMavenUrl() {
		return mavenUrl;
	}

	public void setMavenUrl(String mavenUrl) {
		this.mavenUrl = mavenUrl;
	}

	public boolean isReleaseEnabled() {
		return isReleaseEnabled;
	}

	public void setReleaseEnabled(boolean isReleaseEnabled) {
		this.isReleaseEnabled = isReleaseEnabled;
	}

	public boolean isSnapshotEnabled() {
		return isSnapshotEnabled;
	}

	public void setSnapshotEnabled(boolean isSnapshotEnabled) {
		this.isSnapshotEnabled = isSnapshotEnabled;
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

    @Override
    public String toString() {
        return "Name=" + name + "\nDeployDir=" + deployDir; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @return the cliDomainMode
     */
    public boolean isCliDomainMode() {
        return cliDomainMode;
    }

    /**
     * @param cliDomainMode the cliDomainMode to set
     */
    public void setCliDomainMode(boolean cliDomainMode) {
        this.cliDomainMode = cliDomainMode;
    }

    /**
     * @return the cliServerGroup
     */
    public String getCliServerGroup() {
        return cliServerGroup;
    }

    /**
     * @param cliServerGroup the cliServerGroup to set
     */
    public void setCliServerGroup(String cliServerGroup) {
        this.cliServerGroup = cliServerGroup;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }


    public String getProperty(String key) {
        if (properties != null && !properties.isEmpty()) {
            if (properties.containsKey(key)) {
                return properties.get(key);
            } else {
                String key_prefixed = TargetConstants.PREFIX_CUSTOM_PROPERTY + key;
                if (properties.containsKey(key_prefixed)) {
                    return properties.get(key_prefixed);
                }
            }
        }
        return null;
    }


    public String getRhqGroup() {
        return rhqGroup;
    }

    public void setRhqGroup(String rhqGroup) {
        this.rhqGroup = rhqGroup;
    }


}
