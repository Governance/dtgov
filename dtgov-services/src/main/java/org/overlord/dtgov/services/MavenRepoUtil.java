package org.overlord.dtgov.services;

import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.InputStream;

import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.project.MavenProject;
import org.kie.scanner.MavenRepository;
import org.kie.scanner.embedder.MavenProjectLoader;

public class MavenRepoUtil {


	public MavenRepository getMavenReleaseRepo(
			String repoUrl, boolean isReleaseEnabled, boolean isSnapshotEnabled,
			InputStream pomStream
			) throws Exception {

		//PlexusContainer container = new DefaultPlexusContainer();
		//ArtifactRepositoryFactory artifactRepoFactory = container.lookup(ArtifactRepositoryFactory.class);

		DeploymentRepository releaseToRepo = new DeploymentRepository();
		releaseToRepo.setId("central"); //$NON-NLS-1$
		releaseToRepo.setLayout("default"); //$NON-NLS-1$
		releaseToRepo.setUrl(repoUrl);
		RepositoryPolicy pol = new RepositoryPolicy();
		pol.setEnabled(isReleaseEnabled);
		pol.setUpdatePolicy(ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS);
		pol.setChecksumPolicy(ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE);
		releaseToRepo.setReleases(pol);
		RepositoryPolicy pol2 = new RepositoryPolicy();
		pol2.setEnabled(isSnapshotEnabled);
		pol2.setUpdatePolicy(ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS);
		pol2.setChecksumPolicy(ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE);
		releaseToRepo.setSnapshots(pol2);

		MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pomStream);

		DistributionManagement dm = new DistributionManagement();
		dm.setSnapshotRepository(releaseToRepo);
		mavenProject.setDistributionManagement(dm);

		//List<ArtifactRepository> remoteArtifactRepositories = new ArrayList<ArtifactRepository>();
		//remoteArtifactRepositories.add(releaseToRepo);
		//mavenProject.setRemoteArtifactRepositories(remoteArtifactRepositories);

		//org.apache.maven.artifact.repository.Authentication authentication
		//	= new org.apache.maven.artifact.repository.Authentication("user","pw");
		//mavenRepo.setAuthentication(authentication);

		MavenRepository repo = getMavenRepository(mavenProject);
		return repo;
	}
}
