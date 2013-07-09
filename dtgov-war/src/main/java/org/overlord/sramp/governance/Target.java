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

	public enum TYPE {COPY, RHQ, AS7};
	
    public Target(String name, String deployDir) {
        super();
        this.name = name;
        this.type = TYPE.COPY;
        this.deployDir = deployDir;
    }
    
    public Target(String name, String rhqUser, String rhqPassword, String rhqBaseUrl) {
        super();
        this.name = name;
        this.type = TYPE.RHQ;
        this.rhqUser = rhqUser;
        this.rhqPassword = rhqPassword;
        int secondColon = rhqBaseUrl.indexOf(":",rhqBaseUrl.indexOf(":")+1);
        if (secondColon > 0) {
        	this.rhqBaseUrl = rhqBaseUrl.substring(0,secondColon);
        	this.rhqPort = Integer.valueOf(rhqBaseUrl.substring(secondColon + 1));
        } else {
        	this.rhqBaseUrl = rhqBaseUrl;
        	this.rhqPort = 7080;
        }
        
             		
    }

    private String name;
    private TYPE type;
    private String deployDir;
    private String rhqUser;
    private String rhqPassword;
    private String rhqBaseUrl;
    private Integer rhqPort;
    
    public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getRhqUser() {
		return rhqUser;
	}

	public void setRhqUser(String rhqUser) {
		this.rhqUser = rhqUser;
	}

	public String getRhqPassword() {
		return rhqPassword;
	}

	public void setRhqPassword(String rhqPassword) {
		this.rhqPassword = rhqPassword;
	}

	public String getRhqBaseUrl() {
		return rhqBaseUrl;
	}

	public void setRhqBaseUrl(String rhqBaseUrl) {
		this.rhqBaseUrl = rhqBaseUrl;
	}

	public Integer getRhqPort() {
		return rhqPort;
	}

	public void setRhqPort(Integer rhqPort) {
		this.rhqPort = rhqPort;
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
