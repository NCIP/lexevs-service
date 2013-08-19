LexEVS CTS2 Service
===================

A CTS2 Framework Service Plugin based on LexEVS.

## Installation (OSGi Plugin)
Install the [CTS2 Development Framework](http://informatics.mayo.edu/cts2/framework/installation/)

#### Install the LexEVS Service Plugin and dependencies
Download and [install](http://informatics.mayo.edu/cts2/framework/installing-a-service-plugin/) in this order:
* [CGLIB](http://ebr.springsource.com/repository/app/bundle/version/download?name=com.springsource.net.sf.cglib&version=2.2.0&type=binary)
* [AspectJ Runtime](http://ebr.springsource.com/repository/app/bundle/version/download?name=com.springsource.org.aspectj.runtime&version=1.7.1.RELEASE&type=binary)
* [AspectJ Weaver](http://ebr.springsource.com/repository/app/bundle/version/download?name=com.springsource.org.aspectj.weaver&version=1.7.1.RELEASE&type=binary)
* [LexEVS Service Plugin](http://informatics.mayo.edu/maven/content/repositories/snapshots/edu/mayo/cts2/framework/lexevs-service/0.1.0-SNAPSHOT/lexevs-service-0.1.0-20130506.181320-2.jar)

__!!IMPORTANT!! For the LexEVS Service Plugin, when installing via the "Install/Update" button, leave the "Start Bundle” checkbox button unchecked. For all others, ensure it is checked.__

After the plugin has been installed, navigate to the ```Configuration``` tab of the Admin Console. 

![configure](http://informatics.mayo.edu/cts2/framework/wp-content/uploads/2013/05/config-lexevs-plugin.png)

Verify the configuration information is correct. It is set up to use the NCI 6.0 Remote LexEVS Service.

Click 'Save,' and then start the LexEVS Service Plugin

![start](http://informatics.mayo.edu/cts2/framework/wp-content/uploads/2013/05/start-lexevs-plugin.png)

### Usage
__CodeSystemVersion__ 
* Query [http://localhost:8080/codesystemversions](http://localhost:8080/codesystemversions)

## Installation (Non-OSGi)
Clone the LexEVS Service Plugin: ```git clone https://github.com/cts2/lexevs-service.git```

Set MAVEN_OPTS to insure there are no out of memory errors: ```export MAVEN_OPTS="-Xmx1000m -XX:MaxPermSize=500m"```

Build the Plugin: ```mvn clean install```

Create a ```$USER_HOME/.cts2/[context]/config``` directory.
The ```context``` should be the name of your WAR file.

For example, if you install the Plugin into Tomcat as ```lexevs.war```, the configuration directory for that service will be ```$USER_HOME/.cts2/lexevs/config```.

### Configuration Files
There are several configuration files used to customize the service.
These will all be placed in the ```$USER_HOME/.cts2/[context]/config``` directory.

#### _cts2-deployment.properties_ (Required)
Parameters:
* ```osgi.suppress``` - (true/false) Whether or not to suppress the the OSGi framework. Must be ```true```.

___Example - cts2-deployment.properties___
```
osgi.suppress=true
```

#### _lexevs.properties_ (Required)
Parameters:
* ```service.pid``` - The identifier of the LexEVS Service Plugin. Must be ```edu.mayo.cts2.framework.plugin.service.lexevs```
* ```lexevsRemoteApiUrl``` - The URL of the Remote LexEVS API.
* ```useRemoteApi``` - (true/false) ```true``` to use the Remote LexEVS API, ```false``` to use a local LexEVS installation.
* ```LG_CONFIG_FILE``` - The path to the LexEVS ```lbconfig.props``` file to use when running against a local LexEVS installation
* ```uriResolutionServiceUrl``` - The URL to the URI Resolution Service, must be ```https://informatics.mayo.edu/cts2/services/uriresolver/```

___Example - lexevs.properties___
```
service.pid=edu.mayo.cts2.framework.plugin.service.lexevs
lexevsRemoteApiUrl=
useRemoteApi=false
LG_CONFIG_FILE=/Applications/LexEVS/resources/config/lbconfig.props
uriResolutionServiceUrl=https://informatics.mayo.edu/cts2/services/uriresolver/
```

#### _webapp-config.properties_ (Optional)
* ```service.pid``` - Must be ```edu.mayo.cts2.framework.webapp.rest.config```
* ```allowHtmlRendering``` - (true/false) Allow an HTML rendering of content.
* ```showStackTrace``` - (true/false) Show the Java Stack Trace in the browser on Exception. This should be ```false```, unless during development.
* ```showHomePage``` - (true/false) Show a Home/Welcome page at the root context ("/").
* ```allowSoap``` - (true/false) Turn on/off the SOAP API.
* ```supportEmail``` - The email address to direct unexpected user encountered errors.

___Example - webapp-config.properties___
```
service.pid=edu.mayo.cts2.framework.webapp.rest.config
allowHtmlRendering=false
showStackTrace=false
showHomePage=false
allowSoap=false
supportEmail=support@yourservice.org
```

#### _server-context.properties_ (Optional)
* ```service.pid``` - Must be ```ServerContext```
* ```server.root``` - The base URL of the service as deployed (defaults to ```http://localhost/8080```.

___Example - server-context.properties___
```
service.pid=ServerContext
server.root=http://myservice.org/lexevs
```

The resulting directory structure should resemble:

```
$USER_HOME
  |_ .cts2
       |_ [context]
              |_ config
                    |_ cts2-deployment.properties (required)
                    |_ lexevs.properties (required)
                    |_ webapp-config.properties (optional)
                    |_ server-context.properties (optional)


```

Install the resulting WAR file in ```target/``` to the ```webapps/``` directory of a Tomcat installation.


### You will find more details in the following links:
*  https://tracker.nci.nih.gov/browse/LEXEVS
*  https://wiki.nci.nih.gov/display/EVS/LexEVS+Servers+and+APIs+Summary
*  https://github.com/NCIP/lexevs-service>


