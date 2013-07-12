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

public class Target {

	public enum TYPE {COPY, RHQ, AS_CLI};
	
    public Target(String name, String deployDir) {
        super();
        this.name = name;
        this.type = TYPE.COPY;
        this.deployDir = deployDir;
    }
    
    /**
     * Constructs a Target of Type AS_CLI 'Application Server Command Line Interface.
     * 
     * @param name - name of the target
     * @param asUser - AS user with admin rights
     * @param asPassword - password of the asUser
     * @param asHost - Application Server Hostname (defaults to localhost)
     * @param asPort - Application Server Port (defaults to 9999)
     */
    public Target(String name, String asUser, String asPassword, String asHost, Integer asPort) {
        super();
        this.name = name;
        this.type = TYPE.AS_CLI;
        this.user = asUser;
        this.password = asPassword;
        if (asHost!=null) {
        	this.host = asHost;
        } else {
        	this.host = "localhost";
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
     * @param rhqUser - username of the RHQ user with rights to deploy to that group.
     * @param rhqPassword - password of the rhqUser.
     * @param rhqBaseUrl - baseUrl of the RHQ Server i.e. http://localhost:7080/
     */
    public Target(String name, String rhqUser, String rhqPassword, String rhqBaseUrl) {
        super();
        this.name = name;
        this.type = TYPE.RHQ;
        this.user = rhqUser;
        this.password = rhqPassword;
        int secondColon = rhqBaseUrl.indexOf(":",rhqBaseUrl.indexOf(":")+1);
        if (secondColon > 0) {
        	this.rhqBaseUrl = rhqBaseUrl.substring(0,secondColon);
        	this.port = Integer.valueOf(rhqBaseUrl.substring(secondColon + 1));
        } else {
        	this.rhqBaseUrl = rhqBaseUrl;
        	this.port = 7080;
        }
        
             		
    }

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	private String name;
    private TYPE type;
    private String deployDir;
    private String user;
    private String password;
    private String rhqBaseUrl;
    private String host;
    private Integer port;
    
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

    @Override
    public String toString() {
        return "Name=" + name + "\nDeployDir=" + deployDir;
    }
}
