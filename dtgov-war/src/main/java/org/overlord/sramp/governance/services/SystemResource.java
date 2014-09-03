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
package org.overlord.sramp.governance.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.overlord.dtgov.services.deploy.DeployerFactory;
import org.overlord.sramp.governance.Governance;

/**
 * The JAX-RS resource that handles system requests.
 */
@Path("/system")
public class SystemResource {

    /**
     * Constructor.
     */
    public SystemResource() {
    }

    @GET
    @Path("status")
    @Produces("application/json")
    public String deploy(@Context HttpServletRequest request) throws Exception {
    	Governance governance = new Governance();
    	String repoUrl = governance.getGovernanceUrl();
    	StringBuilder builder = new StringBuilder();
        builder.append("{"); //$NON-NLS-1$
        builder.append("  \"repository-url\" : \"" //$NON-NLS-1$
                + repoUrl + "\""); //$NON-NLS-1$
        builder.append("}"); //$NON-NLS-1$

        return builder.toString();
    }

    @GET
    @Path("/config/deployers/custom")
    @Produces("application/json")
    public List<String> getCustomDeployers(@Context HttpServletRequest request) throws Exception {
        return DeployerFactory.getCustomDeployerNames();
    }
}
