LexEVS CTS2 Service
===================

A CTS2 Framework Service Plugin based on LexEVS.

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
* ```LG_CONFIG_FILE``` - The path to the LexEVS ```lbconfig.props``` file to use when running against a local LexEVS installation
* ```uriResolutionServiceUrl``` - The URL to the URI Resolution Service, must be ```https://informatics.mayo.edu/cts2/services/uriresolver/```

___Example - lexevs.properties___
```
service.pid=edu.mayo.cts2.framework.plugin.service.lexevs
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
* ```alternateHomePage``` - Home page URL to use instead of the standard framework home page.

___Example - webapp-config.properties___
```
service.pid=edu.mayo.cts2.framework.webapp.rest.config
allowHtmlRendering=false
showStackTrace=false
showHomePage=false
allowSoap=false
supportEmail=support@yourservice.org
alternateHomePage=http://alternateHomepage.com
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

Normally Tomcat will need to be started with JVM options set for increased heap size and permgen size:
CATALINA_OPTS="-Xmx2048m -XX:MaxPermSize=128m

### You will find more details in the following links:
* [LexEVS Home](https://tracker.nci.nih.gov/browse/LEXEVS)
* [LexEVS Servers and API's](https://wiki.nci.nih.gov/x/0BwhAQ)
* [Source Code](https://github.com/NCIP/lexevs-service>)

Travis Build Status

[![Build Status](https://travis-ci.org/NCIP/lexevs-service.png?branch=master)](https://travis-ci.org/NCIP/lexevs-service)
