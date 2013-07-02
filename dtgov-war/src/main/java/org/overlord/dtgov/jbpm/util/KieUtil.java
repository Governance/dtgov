package org.overlord.dtgov.jbpm.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.model.Build;
import org.apache.maven.model.Extension;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.overlord.sramp.governance.Governance;

public class KieUtil {

	/**
	 * 
	 * @param srampVersion of the s-ramp-wagon, i.e. "0.2.1-SNAPSHOT"
	 * @param srampUrl the URL to the S-RAMP repo. i.e. "sramp://localhost:8080/s-ramp-server/?artifactType=SRAMPKJar"
	 * @param isSnapshotEnabled reading snapshot artifacts enabled
	 * @param isReleaseEnabledreading release artifacts enabled
	 * @return MavenProject - pointing to an s-ramp repository
	 * @throws PlexusContainerException
	 * @throws ComponentLookupException
	 * @throws UnknownRepositoryLayoutException
	 */
	public static MavenProject getSrampProject(String wagonVersion, String srampUrl, boolean isSnapshotEnabled, boolean isReleaseEnabled) 
			throws PlexusContainerException, ComponentLookupException, UnknownRepositoryLayoutException {
		//MavenRepository.addExtraRepository(remote);
    	MavenProject project = new MavenProject();
    	Build build = new Build();
    	Extension extension = new Extension();
    	extension.setArtifactId("s-ramp-wagon");
    	extension.setGroupId("org.overlord.sramp");
    	extension.setVersion(wagonVersion);
    	build.addExtension(extension);
    	project.setBuild(build);
    	
    	PlexusContainer container = new DefaultPlexusContainer();
    	ArtifactRepositoryFactory artifactRepoFactory = container.lookup(ArtifactRepositoryFactory.class);
    			
    	ArtifactRepository srampRepo = artifactRepoFactory.createArtifactRepository("central", 
    			srampUrl,
    			"default",
    			new ArtifactRepositoryPolicy( isSnapshotEnabled, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ),
    			new ArtifactRepositoryPolicy( isReleaseEnabled,  ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ));
    	
    	Governance goverance = new Governance();
    	//governance.get
    	
    	org.apache.maven.artifact.repository.Authentication authentication 
    		= new org.apache.maven.artifact.repository.Authentication(
    				"kurt", "kurt"
    				);
    	srampRepo.setAuthentication(authentication);
    	
    	List<ArtifactRepository> remoteArtifactRepositories = new ArrayList<ArtifactRepository>();
    	remoteArtifactRepositories.add(srampRepo);
    	
    	project.setRemoteArtifactRepositories(remoteArtifactRepositories);
    	return project;
	}
	
	
}
