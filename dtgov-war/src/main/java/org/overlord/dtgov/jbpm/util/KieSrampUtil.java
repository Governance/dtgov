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
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static String SRAMP_KIE_JAR_QUERY_FORMAT="/s-ramp/ext/KieJarArchive[" //$NON-NLS-1$
			+ "@maven.groupId='%s' and " //$NON-NLS-1$
			+ "@maven.artifactId = '%s' and " //$NON-NLS-1$
			+ "@maven.version = '%s']"; //$NON-NLS-1$

	public boolean isSRAMPPackageDeployed(String groupId, String artifactId, String version) {
		try {
			SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient(); 
			String srampQuery = String.format(SRAMP_KIE_JAR_QUERY_FORMAT, groupId, artifactId, version);
			QueryResultSet results = client.query(srampQuery);
			if (results.size() > 0) return Boolean.TRUE;
			
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
		String srampQuery = String.format(SRAMP_KIE_JAR_QUERY_FORMAT,
				governance.getGovernanceWorkflowGroup(),
				governance.getGovernanceWorkflowName(),
				governance.getGovernanceWorkflowVersion());
		
		QueryResultSet results = client.query(srampQuery);
		if (results.size() > 0) {
			ArtifactSummary artifactSummery = results.get(0);
			InputStream is = client.getArtifactContent(artifactSummery);
			KieModule kModule = repo.addKieModule(ks.getResources().newInputStreamResource(is));
			logger.info(Messages.i18n.format("ApplicationScopedProducer.CreatingKieContainer", artifactSummery)); //$NON-NLS-1$
			KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
			//Creating the KieBase for the SRAMPPackage
	    	logger.info(Messages.i18n.format("ApplicationScopedProducer.FindKieBase", governance.getGovernanceWorkflowPackage())); //$NON-NLS-1$
			return kContainer;
		} else {
			return null;
		}
		
	}
	
	public RuntimeManager getRuntimeManager(ProcessEngineService processEngineService, String deploymentId) {
		String[] deploymentInfo = deploymentId.split(":");
		if (deploymentInfo.length!=5) {
			throw new IllegalStateException("The deploymentId needs to be of format groupId:artifactId:version:packageName:ksessionName");
		}
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(
				deploymentInfo[0], deploymentInfo[1], deploymentInfo[2], deploymentInfo[3], deploymentInfo[4]);
		return getRuntimeManager(processEngineService, unit);
	}
	
	public RuntimeManager getRuntimeManager(ProcessEngineService processEngineService, KModuleDeploymentUnit unit) {
		
		//First see if we have one
		RuntimeManager runtimeManager = processEngineService.getRuntimeManager(unit.getIdentifier());
		if (runtimeManager==null) {
			if (isSRAMPPackageDeployed(unit.getGroupId(), unit.getArtifactId(), unit.getVersion())) {
				unit.setStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
				processEngineService.deployUnit(unit);
				runtimeManager = processEngineService.getRuntimeManager(unit.getIdentifier());
				logger.info("Found and deployed " + unit.getIdentifier() + " to the jBPM runtime");
			} else {
				logger.error("Workflow KieJar " + unit.getIdentifier() + " not found in the S-RAMP Repository");
				throw new IllegalStateException("Workflow KieJar " + unit.getIdentifier() + " not found in the S-RAMP Repository");
			}
		}
		return runtimeManager;
		
	}
	
}
