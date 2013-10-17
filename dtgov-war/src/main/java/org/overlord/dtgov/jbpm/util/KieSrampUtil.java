package org.overlord.dtgov.jbpm.util;

import java.io.InputStream;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
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
	private static String SRAMP_KIE_JAR_QUERY_FORMAT="/s-ramp/ext/KieJarArchive["
			+ "@maven.groupId='%s' and "
			+ "@maven.artifactId = '%s' and "
			+ "@maven.version = '%s']";

	public boolean isSRAMPPackageDeployed() {
		try {
			SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient(); 
			
			Governance governance = new Governance();
			String srampQuery = String.format(SRAMP_KIE_JAR_QUERY_FORMAT,
					governance.getGovernanceWorkflowGroup(),
					governance.getGovernanceWorkflowName(),
					governance.getGovernanceWorkflowVersion());
			
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
	public KieBase getKieBase() throws SrampClientException, SrampAtomException {
		
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
			KieBase kieBase = kContainer.getKieBase("SRAMPPackage"); //$NON-NLS-1$
			return kieBase;
		} else {
			return null;
		}
		
	}
	
}
