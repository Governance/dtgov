
1. Installing DTGov to S-RAMP on JBoss EAP-6

    If you don't have S-RAMP already installed then do that first. Once you have
    that deployed copy the target/jboss-eap-6 to a shared folder (eg, /opt)
    and from the dtgov-installer module run
     
    mvn -Pinstall-eap6 -Ddtgov.install.dir=/opt install
     
    This will install dtgov and dtgov-ui as well as the needed configuration.
    You can start the server by going into the /opt/jboss-eap-6/bin directory
    and by issuing
     
    ./standalone.sh
     
    Once the server is started you need to upload the workflows and the
    deployment-status.owl. TODO add a script for this.

2. Installing DTGov to Tomcat-7.x

    If you don't have S-RAMP already installed then do that first. Once you have
    that deployed copy the target/apache-tomcat-<version> to a shared folder (eg, /opt)
    and from the dtgov-installer module run
     
    mvn -Pinstall-tomcat7 -Ddtgov.install.dir=/opt install
     
    This will install dtgov and dtgov-ui as well as the needed configuration.
    You can start the server by going into the /opt/apache-tomcat-7/bin directory
    and by issuing
     
    ./standalone.sh
    
3. Installing DTGov to Jetty-8.x

    If you don't have S-RAMP already installed then do that first. Once you have
    that deployed copy the target/jetty-distribution-<version> to a shared folder (eg, /opt)
    and from the dtgov-installer module run
     
    mvn -Pinstall-jetty8 -Ddtgov.install.dir=/opt install
     
    This will install dtgov and dtgov-ui as well as the needed configuration.
    You can start the server by going into the /opt/jetty-distribtion-<version>/bin directory
    and by issuing
     
    ./jetty.sh start

    
 4. Installing DTGov to Fuse-6.1

    If you don't have S-RAMP already installed then do that first. Once you have
    that deployed copy the target/jetty-distribution-<version> to a shared folder (eg, /opt)
    and from the dtgov-installer module run
     
    mvn -Pinstall-fuse61 -Ddtgov.install.dir=/opt install
     
    You can start the server by going into the /opt/fuse-distribution-<version>/bin directory
    and by issuing
     
    ./fuse
    
    Then, to deploy the dtgov war, at the karaf prompt issue
    
    features:addurl mvn:org.overlord.dtgov/dtgov-distro-fuse61/<dtgov.version>/xml/features
    features:install -v dtgov
 
