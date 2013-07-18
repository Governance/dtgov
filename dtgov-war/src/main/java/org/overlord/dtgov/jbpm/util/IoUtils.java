package org.overlord.dtgov.jbpm.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Only here to satisfy the Aether class. Will be deleted once the Aether call gets deleted.
 * 
 * @author kstam
 *
 */
class IoUtils {

    public static File getTmpDirectory() {
        File tmp = new File( System.getProperty( "java.io.tmpdir" ) );
        File f = new File( tmp, "_kie_repo_" + UUID.randomUUID().toString() );
        //files.add( f );
        if ( f.exists() ) {
            if ( f.isFile() ) {
                throw new IllegalStateException( "The temp directory exists as a file. Nuke it now !" );
            }
            deleteDir( f );
            f.mkdir();
        } else {
            f.mkdir();
        }
        return f;
    }

    private static void deleteDir(File dir) {
        // Will throw RuntimeException is anything fails to delete
        String[] children = dir.list();
        for ( String child : children ) {
            File file = new File( dir,
                    child );
            if ( file.isFile() ) {
                deleteFile( file );
            } else {
                deleteDir( file );
            }
        }

        deleteFile( dir );
    }

    private static void deleteFile(File file) {
        // This will attempt to delete a file 5 times, calling GC and Sleep between each iteration
        // Sometimes windows takes a while to release a lock on a file.
        // Throws an exception if it fails to delete
        if ( !file.delete() ) {
            int count = 0;
            while ( !file.delete() && count++ < 5 ) {
                System.gc();
                try {
                    Thread.sleep( 250 );
                } catch ( InterruptedException e ) {
                    throw new RuntimeException( "This should never happen" );
                }
            }
        }

        if ( file.exists() ) {
            try {
                throw new RuntimeException( "Unable to delete file:" + file.getCanonicalPath() );
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to delete file", e);
            }
        }
    }

}
