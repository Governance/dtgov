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
package org.overlord.dtgov.services.deploy;

import java.util.HashMap;
import java.util.Map;

import org.overlord.dtgov.common.Target.TYPE;
import org.overlord.dtgov.services.deploy.deployers.CliDeployer;
import org.overlord.dtgov.services.deploy.deployers.CopyFileDeployer;
import org.overlord.dtgov.services.deploy.deployers.MavenDeployer;
import org.overlord.dtgov.services.deploy.deployers.RHQDeployer;

/**
 * Main Dtgov deployer provider implementation. It provides the main supported
 * dtgov deployers.
 *
 * @author David Virgil Naranjo
 */
public class DTGovDeployerProvider implements DeployerProvider {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.services.deploy.DeployerProvider#createDeployers()
     */
    @Override
    public Map<String, Deployer> createDeployers() {
        Map<String, Deployer> deployers = new HashMap<String, Deployer>();
        deployers.put(TYPE.AS_CLI.name(), new CliDeployer());
        deployers.put(TYPE.COPY.name(), new CopyFileDeployer());
        deployers.put(TYPE.MAVEN.name(), new MavenDeployer());
        deployers.put(TYPE.RHQ.name(), new RHQDeployer());
        return deployers;
    }


}
