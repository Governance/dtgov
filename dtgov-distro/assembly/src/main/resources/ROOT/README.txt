
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
       6.x directory as in step #3
    7) Start JBoss
    8) Populate the S-RAMP repository with DTGov seed data (e.g. ontologies + workflow jar)

Here is some pseudo-bash that accomplishes the above:

    mkdir ~/overlord
    # Download JBoss EAP 6.x (jboss-eap-6.x.0.zip)
    #    From - http://www.jboss.org/jbossas/downloads
    # Download S-RAMP distribution (s-ramp-${version.org.overlord.sramp}.zip)
    #    From - http://www.jboss.org/overlord/downloads/sramp
    unzip ~/Downloads/jboss-eap-6.x.0.zip ~/overlord
    unzip ~/Downloads/s-ramp-${version.org.overlord.sramp}.zip ~/overlord
    cd ~/overlord/s-ramp-${version.org.overlord.sramp}
    ant install
    # Follow s-ramp installation instructions here
    
    unzip ~/Downloads/dtgov-${project.version}.zip ~/overlord
    cd ~/overlord/dtgov-${project.version}
    ant
    # Follow dtgov installation instructions here

    # Start JBoss (target/jboss-eap-6.x/bin/standalone.sh) - wait for startup to complete
    ant seed -Ds-ramp.shell.password=ADMIN_PASSWORD


== Overview ==
This distribution comes with the following:

    1) bin/*.war - the WARs that make up the runtime, including
       (but not limited to) the server/workflow WAR and the UI WAR
    2) src - all of the source code, in a number of "-sources" JARs.
    3) demos - some quickstarts/demos to help you get started with DTGov
    4) build.xml/build.properties - an Ant script that will install 
       and configure DTGov in JBoss EAP 6 (or Tomcat 7)
    5) docs - the DTGov documentation
    6) workflows - the source code of the out of the box DTGov workflows


== What do I do next? ==
This distribution works with the following runtime platforms:

* JBoss EAP 6.x
* Tomcat 7
* Jetty 8

You must download the runtime platform of your choice, then install S-RAMP
into it, then point the DTGov installer to the resulting valid application 
server installation.

    Download JBoss EAP here:   http://www.jboss.org/jbossas/downloads

Overlord DTGov provides functionality that is built on top of the Overlord
S-RAMP project.  So you *must* have S-RAMP installed in order for DTGov
to function.  The easiest way to do this is to download the S-RAMP 
distribution and also install it into the same application server.  It 
doesn't matter what order you install them.

    Download here:  http://www.jboss.org/overlord/downloads/sramp

Of course, you can run S-RAMP in a separate server if you like, but the 
configuration will be slightly more complicated (there are a few 
configuration files in standalone/configuration that you will need to 
update). 

Once you have downloaded and installed S-RAMP, you can install DTGov into
the resulting S-RAMP installation.  This can be accomplished by simply 
telling the DTGov installer where S-RAMP is installed.

Now that you have S-RAMP installed, you can go ahead and install DTGov:

    ant

Once the installation completes, you can start your application container.

Once the application container (e.g. JBoss EAP) is running, you must seed 
the system with some DTGov-specific data (don't forget to supply the 
S-RAMP admin password):

    ant seed -Ds-ramp.shell.password=ADMIN_PASSWORD

Note: if your application server is running on a different port, you will
need to specify that when seeding the data:

    ant seed -Ds-ramp.shell.password=ADMIN_PASSWORD -Ds-ramp.endpoint=http://localhost:8181/s-ramp-server/

Finally, you can hit the UI!

    UI: http://localhost:8080/dtgov-ui

You should be able to log in to the UI with the credentials you set up 
when you installed S-RAMP/DTGov:

    Username: admin
    Password: <pw-chosen-during-sramp-install>


== Customized Workflows ==
Included in the distribution is a folder that contains all of the source code
for the out of the box DTGov workflows.  An easy way to get started customizing
the workflows (or creating your own) is to use what's in the 'workflows' folder.

It's easy to make changes to the files in this directory and then, when you are
done, build the workflows JAR and deploy it to S-RAMP.  To do this, simply 
change the version number in the pom.xml and then run the following:

    mvn deploy -Psramp

Make sure you have the "local-sramp-repo" and "local-sramp-repo-snapshots" 
servers configured (for authentication) in your ~/.m2/settings.xml file!

== Note on Memory Configuration ==
You will most likely need to increase the default JVM memory settings for
your application server.  The typical defaults are insufficient.  For
example, on Tomcat these settings are pretty good:

set CATALINA_OPTS=-Xms1G -Xmx1G -XX:PermSize=384m -XX:MaxPermSize=384m
