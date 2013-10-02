
== Welcome ==
Welcome to version ${project.version} of the Overlord Design Time Governance 
Distribution, thanks for downloading!

== Quick Start ==
Here are the steps you need to follow to get everything working.  For a more 
detailed set of instructions, see the "Overview" section below.

    1) Download JBoss EAP 6.1
    2) Download the ModeShape 3.2.0.Final EAP distribution
    3) Download S-RAMP distribution
    4) Unpack S-RAMP distribution
    5) Copy the EAP download into the unpacked S-RAMP distro
    6) Copy the ModeShape distribution into the unpacked S-RAMP distro
    7) Run the S-RAMP installer
    8) Unpack the DTGov distribution
    9) Move the "target" folder from the S-RAMP distro to the unpacked DTGov distro
   10) Copy the EAP download into the unpacked DTGov distro
   11) Run the DTGov installer
   12) Start JBoss
   13) Populate the S-RAMP repository with DTGov seed data (ontology + workflow jar)

Here is some pseudo-bash that accomplishes the above:

    mkdir ~/overlord
    cd ~/overlord
    # Download JBoss EAP 6.1 (jboss-eap-6.1.0.zip)
    #    From - http://www.jboss.org/jbossas/downloads
    # Download the ModeShape EAP distro (modeshape-3.2.0.Final-jbosseap-61-dist.zip)
    #    From - http://www.jboss.org/modeshape/downloads/downloads3-2-0-final.html
    # Download S-RAMP distribution (s-ramp-${s-ramp.version}.zip)
    #    From - http://www.jboss.org/overlord/downloads/sramp
    unzip s-ramp-${s-ramp.version}.zip
    cp jboss-eap-6.1.0.zip s-ramp-${s-ramp.version}
    cp modeshape-3.2.0.Final-jbosseap-61-dist.zip s-ramp-${s-ramp.version}
    cd s-ramp-${s-ramp.version}
    ant install
    cd ..
    mv s-ramp-${s-ramp.version}/target dtgov-${project.version}
    cp jboss-eap-6.1.0.zip dtgov-${project.version}
    cd dtgov-${project.version}
    ant install
    # Start JBoss (target/jboss-eap-6.1/bin/standalone.sh) - wait for startup to complete
    ant seed
    cd dtgov-data
    mvn install
    

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
Platform (JBoss EAP 6.1).  You must download EAP and point the DTGov installer
to the downloaded .zip.  You can accomplish the latter by simply copying the
downloaded EAP .zip file into the root of this distribution, or you can 
modify the 'dtgov-build.properties' file to point to wherever you saved it.

    Download here:  http://www.jboss.org/jbossas/downloads

Overlord DTGov provides functionality that is built on top of the Overlord
S-RAMP project.  So you *must* have S-RAMP installed in order for DTGov
to function.  The easiest way to do this is to download the S-RAMP 
distribution and also install it into the same JBoss EAP 6.1.  It doesn't
matter what order you install them.

    Download here:  http://www.jboss.org/overlord/downloads/sramp

Of course, you can run S-RAMP in a separate JBoss if you like, but the 
configuration will be *slightly* more complicated (there are a few 
configuration files in standalone/configuration that you will need to update). 

Once you have downloaded and installed S-RAMP, you can install DTGov into
the resulting S-RAMP JBoss installation.  This can be accomplished by 
copying/moving the "target" folder in the S-RAMP distribution (after the 
installer has run) to the root of the DTGov distribution.

Now that you have the S-RAMP distribution's "target" folder (which contains 
jboss configured for S-RAMP) in the root of the DTGov distribution, you can
go ahead and install DTGov:

    ant install

Once the installation completes, you can start JBoss (which you should find
in the 'target' directory).

Once JBoss is running, you must seed the system with some DTGov specific
data:

    ant seed
    cd data; mvn package

The first step will install an S-RAMP ontology.  The second step will add
the DTGov deployment release process to the repository (basically just a 
JAR containing JBPM artifacts).

Finally, you can hit the UI!

    UI: http://localhost:8080/dtgov-ui

You should be able to log in with the following credentials:

    Username: gary
    Password: gary

