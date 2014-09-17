
== Welcome ==
Welcome to version ${project.version} of the Overlord Design Time Governance 
Distribution, thanks for downloading!

== Running DTGov on Fuse 6.x ==
This version of Design Time Governance supports running on the JBoss Fuse 
platform.  The procedure for doing this is different than running on other
platforms we support.  Except for the initial Seeding of DTGov data into 
the S-RAMP repository, all installation and configuration of DTGov is done
through the Fuse/Karaf console.

== Quickstart ==
Follow these steps to get up and running with DTGov (and S-RAMP) on Fuse:

1) Download Fuse 6.x:  http://www.jboss.org/download-manager/file/jboss-fuse-6.1.0.GA-full_zip.zip

   NOTE: this version of DTGov was tested with JBoss Fuse 6.1.0.GA - other 
         versions may not work or may require additional steps.

2) Unpack Fuse 6.x

3) Download a patch for Fuse 6.x - this patch fixes some problems with the
   Fuse package itself:  https://developer.jboss.org/servlet/JiveServlet/download/52861-11-125645/patches.zip

4) Unpack the patches.zip into the Fuse 6.x installation - it should prompt
   you to replace a few files.

5) Launch Fuse 6.x (typically via the fuse or fuse.bat script).  You should 
   probably also beef up your memory settings on Fuse.  For example:
   
   -Xms512M -Xmx1G -XX:PermSize=384m -XX:MaxPermSize=384m

6) Run the following commands on the Fuse console:

   features:addurl mvn:org.overlord.sramp/s-ramp-distro-fuse61/0.6.0-SNAPSHOT/xml/features
   features:addurl mvn:org.overlord.dtgov/dtgov-distro-fuse61/1.4.0-SNAPSHOT/xml/features
   features:install -v s-ramp-karaf-commands
   overlord:s-ramp:configure <ADMIN-USER-PASSWORD>
   features:install -v dtgov-karaf-commands
   overlord:dtgov:configure <ADMIN-USER-PASSWORD>
   features:install -v s-ramp
   features:install -v dtgov

7) The dtgov initialization is done automatically during the dtgov deployment.
   This behaviour can be changed modifying the dtgov.automatic.data.initialization.enabled 
   property from the dtgov.properties.
       
In case you change to false, then you must populate S-ramp. Once everything has installed, 
you must seed the initial DTGov data into the S-RAMP repository.  This can be done using 
the following command at the root of this distribution:
   
   ant seed -Ds-ramp.shell.password=<ADMIN-USER-PASSWORD> -Ds-ramp.endpoint=http://localhost:8181/s-ramp-server/

8) Go ahead and get started using DTGov and S-RAMP!  You can find the UI here:

   http://localhost:8181/dtgov-ui/
   Username:  admin
   Password:  <ADMIN-USER-PASSWORD>
