
1. Installing DTGov to S-RAMP on JBoss EAP-6.1

    If you don't have S-RAMP already installed then do that first. Once you have
    that deployed copy the target/jboss-eap-6.1 to a shared folder (eg, /opt)
    and from the dtgov-installer module run
     
    mvn -Peap61 -Ddtgov.install.dir=/opt install
     
    This will install dtgov and dtgov-ui as well as the needed configuration.
    You can start the server by going into the /opt/jboss-eap-6.1/bin directory
    and by issuing
     
    ./standalone.sh
     
    Once the server is started you need to upload the workflows and the
    deployment-status.owl. TODO add a script for this.

2. Installing S-RAMP to Tomcat-7.x

	Not yet implementented.
