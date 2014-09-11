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

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.sramp.client.SrampAtomApiClient;

/**
 * Interface tha provides the methods that a dtgov deployer should implement. A
 * deployer can deploy and undeploy an s-ramp artifact on a specific target.
 *
 * @author David Virgil Naranjo
 */
public interface Deployer<T extends Target> {

    /**
     * Deploy an artifact
     *
     * @param artifact
     *            the artifact
     * @param target
     *            the target
     * @return the string
     * @throws Exception
     *             the exception
     */
    public String deploy(BaseArtifactType artifact, T target, SrampAtomApiClient client)
            throws Exception;

    /**
     * Undeploy an artifact
     *
     * @param prevVersionArtifact
     *            the prev version artifact
     * @param undeployInfo
     *            the undeploy info
     * @param target
     *            the target
     * @throws Exception
     *             the exception
     */
    public void undeploy(BaseArtifactType prevVersionArtifact, BaseArtifactType undeployInfo, T target,
            SrampAtomApiClient client) throws Exception;
}
