package org.overlord.dtgov.jbpm.util;

import static org.kie.scanner.embedder.MavenProjectLoader.loadMavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.wagon.Wagon;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.maven.wagon.AhcWagon;

/**
 * This class contains a fix in the ManualWagonProvider to register the SrampWagon
 * and once the drools team comes up with support for custom Wagons this class
 * will be removed.
 *
 * @author kstam
 *
 */
public class Aether {

    private static final String M2_REPO = System.getProperty( "user.home" ) + "/.m2/repository";
    private String localRepoDir = M2_REPO;

    public static final Aether DEFUALT_AETHER = new Aether();

    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    private RemoteRepository localRepository;

    private Aether() {
        this(loadMavenProject());
    }

    Aether(MavenProject mavenProject) {
        system = newRepositorySystem();
        session = newRepositorySystemSession( system );
        repositories = initRepositories(mavenProject);
    }

    private List<RemoteRepository> initRepositories(MavenProject mavenProject) {
        List<RemoteRepository> reps = new ArrayList<RemoteRepository>();
        if (mavenProject != null) {
            reps.addAll(mavenProject.getRemoteProjectRepositories());
        } else {
            reps.add(newCentralRepository());
        }

        RemoteRepository localRepo = newLocalRepository();
        if (localRepo != null) {
            reps.add(localRepo);
            localRepository = localRepo;
        } else {
            localRepoDir = IoUtils.getTmpDirectory().getAbsolutePath();
        }
        return reps;
    }

    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = new DefaultServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class );
        locator.addService( RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class );
        locator.setServices( WagonProvider.class, new ManualWagonProvider() );

        return locator.getService( RepositorySystem.class );
    }

    private DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system ) {
        LocalRepository localRepo = new LocalRepository(localRepoDir);
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );
        return session;
    }

    private RemoteRepository newCentralRepository() {
        return new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );
    }

    private RemoteRepository newLocalRepository() {
        File m2RepoDir = new File( M2_REPO );
        if (!m2RepoDir.exists()) {
            return null;
        }
        try {
            String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
            return new RemoteRepository( "local", "default", localRepositoryUrl );
        } catch (MalformedURLException e) { }
        return null;
    }

    public RepositorySystem getSystem() {
        return system;
    }

    public RepositorySystemSession getSession() {
        return session;
    }

    public List<RemoteRepository> getRepositories() {
        return repositories;
    }

    public RemoteRepository getLocalRepository() {
        return localRepository;
    }

    private static class ManualWagonProvider implements WagonProvider {

        @Override
        public Wagon lookup( String roleHint ) throws Exception {
            if ( "http".equals( roleHint ) ) {
                return new AhcWagon();
            }
            if ( "sramp".equals( roleHint ) ) {
                return new SrampWagonProxy();
            }
            return null;
        }

        @Override
        public void release( Wagon wagon ) { }
    }
}
