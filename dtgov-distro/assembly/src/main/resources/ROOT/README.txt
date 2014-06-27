
== Welcome ==
Welcome to version ${project.version} of the Overlord Design Time Governance 
Distribution, thanks for downloading!

== Quick Start ==
Here are the steps you need to follow to get everything working.  For a more 
detailed set of instructions, see the "Overview" section below.

    1) Download JBoss EAP 6
    2) Download S-RAMP distribution
    3) Follow the S-RAMP distribution instructions and install S-RAMP into JBoss EAP 6
    4) Unpack the DTGov distribution
    5) Run the DTGov installer
    6) Follow the DTGov installer instructions to install DTGov into the same JBoss EAP
       6.1 directory as in step #3
    7) Start JBoss
    8) Populate the S-RAMP repository with DTGov seed data (ontology + workflow jar)

Here is some pseudo-bash that accomplishes the above:

    mkdir ~/overlord
    # Download JBoss EAP 6.x (jboss-eap-6.x.0.zip)
    #    From - http://www.jboss.org/jbossas/downloads
    # Download S-RAMP distribution (s-ramp-${s-ramp.version}.zip)
    #    From - http://www.jboss.org/overlord/downloads/sramp
    unzip ~/Downloads/jboss-eap-6.x.0.zip ~/overlord
    unzip ~/Downloads/s-ramp-${s-ramp.version}.zip ~/overlord
    cd ~/overlord/s-ramp-${s-ramp.version}
    ant install
    # Follow s-ramp installation instructions here
    
    unzip ~/Downloads/dtgov-${project.version}.zip ~/overlord
    cd ~/overlord/dtgov-${project.version}
    ant install
    # Follow dtgov installation instructions here

    # Start JBoss (target/jboss-eap-6.x/bin/standalone.sh) - wait for startup to complete
    ant seed -Ds-ramp.shell.password=ADMIN_PASSWORD
    cd ~/overlord/dtgov-${project.version}/dtgov-data
    mvn deploy


== Overview ==
This distribution comes with the following:

    1) bin/*.war - the WARs that make up the runtime, including
       (but not limited to) the server/workflow WAR and the UI WAR
    2) src - all of the source code, in a number of "-sources" JARs.
    3) demos - some quickstarts/demos to help you get started with DTGov
    4) build.xml/build.properties - an Ant script that will install 
       and configure DTGov in JBoss EAP 6 (or Tomcat 7)
    5) dtgov-data - some seed data required for out of the box DTGov
       functionality
    6) docs - the DTGov documentation


== What do I do next? ==
This distribution works with version 6.1 of the JBoss Enterprise Application
Platform (JBoss EAP 6) or Apache Tomcat 7.  You must download EAP or Tomcat
and point the DTGov installer to a valid application server installation.

    Download JBoss here:   http://www.jboss.org/jbossas/downloads
    Download Tomcat here:  http://tomcat.apache.org/download-70.cgi

Overlord DTGov provides functionality that is built on top of the Overlord
S-RAMP project.  So you *must* have S-RAMP installed in order for DTGov
to function.  The easiest way to do this is to download the S-RAMP 
distribution and also install it into the same application server.  It 
doesn't matter what order you install them.

    Download here:  http://www.jboss.org/overlord/downloads/sramp

Of course, you can run S-RAMP in a separate server if you like, but the 
configuration will be slightly more complicated (there are a few 
configuration files in standalone/configuration that you will need to update). 

Once you have downloaded and installed S-RAMP, you can install DTGov into
the resulting S-RAMP installation.  This can be accomplished by simply 
telling the DTGov installer where S-RAMP is installed (either in JBoss or
Tomcat).

Now that you have S-RAMP installed, you can go ahead and install DTGov:

    ant install

Once the installation completes, you can start JBoss (which you should find
in the 'target' directory).

Once JBoss is running, you must seed the system with some DTGov specific
data (don't forget to supply the S-RAMP admin password):

    ant seed -Ds-ramp.shell.password=ADMIN_PASSWORD

Note: if you are running in JBoss Fuse the default server port is 8181
rather than 8080.  In this case you may need to specify the location of
the S-RAMP repository when seeding:

    ant seed -Ds-ramp.shell.password=ADMIN_PASSWORD -Ds-ramp.endpoint=http://localhost:8181/s-ramp-server/

The first step will install an S-RAMP ontology.  The second step will add
the DTGov deployment release process to the repository (basically just a 
JAR containing JBPM artifacts).

Finally, you can hit the UI!

    UI: http://localhost:8080/dtgov-ui

You should be able to log in to the UI with the credentials you set up 
when you installed S-RAMP/DTGov:

    Username: admin
    Password: *pwd*
