WebLogic 12c REST Deployer
==========================
This project is a REST interface to the [weblogic.Deployer](http://docs.oracle.com/middleware/1212/wls/DEPGD/wldeployer.htm) utility, called by Runtime.exec()

How to install
--------------

 * Git clone the project
 * Setup the Maven WebLogic Plugin following [documentation steps](http://docs.oracle.com/middleware/1212/wls/WLPRG/maven.htm#CHEIHIEH)
 * Edit pom.xml properly to match your AdminServer configuration (user/pass/url)
 * Make sure to deploy this application only to the AdminServer
 * Make sure your target AdminServer of WebLogic 12c is running
  * If it is not running, call the following command: 
  <pre>$ mvn com.oracle.weblogic:weblogic-maven-plugin:start-server</pre>
 * Build the project and it will be automatically deployed, by calling
<pre>$ mvn package</pre>

How to deploy applications
--------------------------
This project exposes a REST service with a POST command that will take two arguments:

 - file: the deployable artifact to upload
 - args: the Deployer arguments (see [documentation](http://docs.oracle.com/middleware/1212/wls/DEPGD/wldeployer.htm))

**Example using cURL**
<pre>$ curl -F "file=@hello-world.war" \ 
   -F "args=-username weblogic -password welcome1 -deploy" \
   http://weblogic:welcome1@localhost:7001/wlsdeployer/deployer</pre>

**Know issue**
The REST interface is securely protected and must be authenticated using an account under Administrators group in the default 'myrealm'. The Deployer command-line 'args' must also include username and password of the administrator account to deploy the uploaded artifact.
