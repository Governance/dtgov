
== Welcome ==
Welcome to version ${project.version} of the Overlord Design Time Governance 
Distribution, thanks for downloading!


== Overview ==
This distribution comes with the following:

    1) bin/*.war - the WARs that make up the runtime, including
       (but not limited to) the server/workflow WAR and the UI WAR
    2) src - all of the source code, in a number of "-sources" JARs.
    3) updates - bootstrapping configuration for JBoss and jBPM.
    4) build.xml/dtgov-build.properties - an Ant script that will install 
       and configure DTGov in JBoss EAP 6.1.

== What do I do next? ==
This distribution works with version 6.1 of the JBoss Enterprise Application
Platform (JBoss EAP 6.1).  You must download EAP and point the S-RAMP installer
to the downloaded .zip.  You can accomplish the latter by simply copying the
downloaded EAP .zip file into the root of this distribution, or you can 
modify the 's-ramp-build.properties' file to point to wherever you saved it.

    Download here:  http://www.jboss.org/jbossas/downloads

Overlord DTGov provides functionality that is built on top of the Overlord
S-RAMP project.  So you *must* have S-RAMP installed in order for DTGov
to function.  The easiest way to do this is to download the S-RAMP 
distribution and also install it into the same JBoss EAP 6.1.  It doesn't
matter what order you install them.

    Download here:  http://www.jboss.org/overlord/downloads/sramp

Of course, you can run S-RAMP in a separate JBoss if you like, but the 
configuration will be *slightly* more complicated (there are a few 
configuration files in standalone/configuration that you will want to update). 

Once these two additional dependencies have been downloaded, the installer
should do everything else for you.  From the root of this distribution, simply
run:

    ant

Once the installation completes, you can start JBoss (which you should find
in the 'target' directory) and you should be ready to get going!

    UI: http://localhost:8080/dtgov-ui

You should be able to log in with the following credentials:

    Username: gary
    Password: gary
