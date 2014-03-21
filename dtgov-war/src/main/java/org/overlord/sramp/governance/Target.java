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
package org.overlord.sramp.governance;

/**
 * A configured deployment target.  These are typically configured in the DTGov configuration
 * file.
 *
 * @author eric.wittmann@redhat.com
 */
public class Target {

	public enum TYPE {COPY, RHQ, AS_CLI, MAVEN};

	/**
	 * Constructor.
     * @param name
     * @param classifier
     * @param deployDir
     */
    public Target(String name, String classifier, String deployDir) {
        super();
        this.name = name;
        this.classifier = classifier;
        this.type = TYPE.COPY;
        this.deployDir = deployDir;
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
     */
    public Target(String name, String classifier, String asUser, String asPassword, String asHost, Integer asPort) {
        super();
        this.name = name;
        this.classifier = classifier;
        this.type = TYPE.AS_CLI;
        this.user = asUser;
        this.password = asPassword;
        if (asHost!=null) {
        	this.host = asHost;
        } else {
        	this.host = "localhost"; //$NON-NLS-1$
        }
        if (port!=null && port > 0) {
        	this.port = asPort;
        } else {
        	this.port = 9999;
        }
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
    public Target(String name, String classifier, String rhqUser, String rhqPassword, String rhqBaseUrl,
    		String rhqPluginName) {
        super();
        this.name = name;
        this.classifier = classifier;
        this.type = TYPE.RHQ;
        this.user = rhqUser;
        this.password = rhqPassword;
        int secondColon = rhqBaseUrl.indexOf(":",rhqBaseUrl.indexOf(":")+1); //$NON-NLS-1$ //$NON-NLS-2$
        if (secondColon > 0) {
        	this.rhqBaseUrl = rhqBaseUrl.substring(0,secondColon);
        	String portStr = rhqBaseUrl.substring(secondColon + 1);
        	int slashPosition = portStr.indexOf("/") ; //$NON-NLS-1$
        	if (slashPosition > 0) portStr = portStr.substring(0,slashPosition);
        	this.port = Integer.valueOf(portStr);
        	
        } else {
        	this.rhqBaseUrl = rhqBaseUrl;
        	this.port = 7080;
        }
        this.rhqPluginName = rhqPluginName;
    }

    /**
     * Constructor a target of type Maven.
     * @param name
     * @param classifier
     * @param mavenUrl
     * @param isReleaseEnabled
     * @param isSnapshotEnabled
     */
    public Target(String name, String classifier, String mavenUrl, boolean isReleaseEnabled, boolean isSnapshotEnabled ) {
        super();
        this.name = name;
        this.classifier = classifier;
        this.type = TYPE.MAVEN;
        this.mavenUrl = mavenUrl;
        this.setReleaseEnabled(isReleaseEnabled);
        this.setSnapshotEnabled(isSnapshotEnabled);
    }

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	private String name;
	private String classifier;
    private TYPE type;
    private String deployDir;
    private String user;
    private String password;
    private String rhqBaseUrl;
    private String rhqPluginName;
    private String host;
    private Integer port;
    private String mavenUrl;
    private boolean isReleaseEnabled;
    private boolean isSnapshotEnabled;

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

    @Override
    public String toString() {
        return "Name=" + name + "\nDeployDir=" + deployDir; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
