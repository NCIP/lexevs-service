LexEVS CTS2 Service
===================

A CTS2 Framework Service Plugin based on LexEVS.

### Installation
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
