/**
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
package org.overlord.dtgov.jbpm.util;

import java.io.InputStream;

import org.jbpm.kie.services.api.DeploymentUnit.RuntimeStrategy;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeManager;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieSrampUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String SRAMP_KIE_JAR_QUERY = "/s-ramp/ext/KieJarArchive[" //$NON-NLS-1$
            + "@maven.groupId=? and @maven.artifactId = ? and @maven.version = ? and xp2:not(@maven.classifier)]"; //$NON-NLS-1$

    /**
     * Returns true if the workflow JAR is deployed to the s-ramp repository.
     * @param groupId
     * @param artifactId
     * @param version
     * @return true or false
     */
	public boolean isSRAMPPackageDeployed(String groupId, String artifactId, String version) {
		try {
			SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
			QueryResultSet results = client.buildQuery(SRAMP_KIE_JAR_QUERY).parameter(groupId).parameter(artifactId).parameter(version).count(1).query();
			if (results.size() > 0) {
			    return Boolean.TRUE;
			}
		} catch (SrampClientException e) {
			logger.error(e.getMessage(),e);
		} catch (SrampAtomException e) {
			logger.error(e.getMessage(),e);
		}
		return Boolean.FALSE;
	}

	/**
	 * Creating a KieBase from the workflow GAV specified in the config.
	 *
	 * @return KieBase for package SRAMPPackage
	 *
	 * @throws SrampClientException
	 * @throws SrampAtomException
	 */
	public KieContainer getKieContainer(ReleaseId releaseId) throws SrampClientException, SrampAtomException {
		KieServices ks = KieServices.Factory.get();
    	KieRepository repo = ks.getRepository();
    	SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();

		Governance governance = new Governance();
        QueryResultSet results = client.buildQuery(SRAMP_KIE_JAR_QUERY)
                .parameter(governance.getGovernanceWorkflowGroup())
                .parameter(governance.getGovernanceWorkflowName())
                .parameter(governance.getGovernanceWorkflowVersion()).count(1).query();
		if (results.size() > 0) {
			ArtifactSummary artifactSummery = results.get(0);
			InputStream is = client.getArtifactContent(artifactSummery);
			KieModule kModule = repo.addKieModule(ks.getResources().newInputStreamResource(is));
			logger.info(Messages.i18n.format("KieSrampUtil.CreatingKieContainer", artifactSummery)); //$NON-NLS-1$
			KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
			//Creating the KieBase for the SRAMPPackage
	    	logger.info(Messages.i18n.format("KieSrampUtil.FindKieBase", governance.getGovernanceWorkflowPackage())); //$NON-NLS-1$
			return kContainer;
		} else {
			return null;
		}

	}
	/**
	 * Returns a RuntimeManager from the ProcessEngineService for the given deploymentId.
	 * Creates a RuntimeManager if it didn't already exist.
	 *
	 * @param processEngineService
	 * @param deploymentId
	 * @return RuntimeManager
	 */
	public RuntimeManager getRuntimeManager(ProcessEngineService processEngineService, String deploymentId) {
		String[] deploymentInfo = deploymentId.split(":"); //$NON-NLS-1$
		if (deploymentInfo.length!=5) {
			throw new IllegalStateException(Messages.i18n.format("KieSrampUtil.DeploymentIdFormat")); //$NON-NLS-1$
		}
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(
				deploymentInfo[0], deploymentInfo[1], deploymentInfo[2], deploymentInfo[3], deploymentInfo[4]);
		return getRuntimeManager(processEngineService, unit);
	}
	/**
	 * Returns a RuntimeManager from the ProcessEngineService for the given KModuleDeploymentUnit.
	 * Creates a RuntimeManager if it didn't already exist.
	 * @param processEngineService
	 * @param unit
	 * @return RuntimeManager
	 */
	public RuntimeManager getRuntimeManager(ProcessEngineService processEngineService, KModuleDeploymentUnit unit) {

		//First see if we have one
		RuntimeManager runtimeManager = processEngineService.getRuntimeManager(unit.getIdentifier());
		if (runtimeManager==null) {
			if (isSRAMPPackageDeployed(unit.getGroupId(), unit.getArtifactId(), unit.getVersion())) {
				unit.setStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
				processEngineService.deployUnit(unit);
				runtimeManager = processEngineService.getRuntimeManager(unit.getIdentifier());
				logger.info(Messages.i18n.format("KieSrampUtil.FoundAndDeployed", unit.getIdentifier() )); //$NON-NLS-1$
			} else {
				logger.error(Messages.i18n.format("KieSrampUtil.NotFound", unit.getIdentifier() )); //$NON-NLS-1$
				throw new IllegalStateException(Messages.i18n.format("KieSrampUtil.NotFound", unit.getIdentifier() )); //$NON-NLS-1$
			}
		}
		return runtimeManager;

	}

}
