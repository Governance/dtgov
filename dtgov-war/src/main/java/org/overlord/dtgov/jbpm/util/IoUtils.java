package org.overlord.dtgov.jbpm.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.overlord.dtgov.server.i18n.Messages;

/**
 * Only here to satisfy the Aether class. Will be deleted once the Aether call gets deleted.
 *
 * @author kstam
 *
 */
class IoUtils {

    public static File getTmpDirectory() {
        File tmp = new File( System.getProperty( "java.io.tmpdir" ) ); //$NON-NLS-1$
        File f = new File( tmp, "_kie_repo_" + UUID.randomUUID().toString() ); //$NON-NLS-1$
        //files.add( f );
        if ( f.exists() ) {
            if ( f.isFile() ) {
                throw new IllegalStateException(Messages.i18n.format("IoUtils.TempDirExists")); //$NON-NLS-1$
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
                    throw new RuntimeException( "This should never happen" ); //$NON-NLS-1$
                }
            }
        }

        if ( file.exists() ) {
            try {
                throw new RuntimeException( Messages.i18n.format("IoUtils.FileDeleteFail", file.getCanonicalPath()) ); //$NON-NLS-1$
            } catch ( IOException e ) {
                throw new RuntimeException(e);
            }
        }
    }

}
