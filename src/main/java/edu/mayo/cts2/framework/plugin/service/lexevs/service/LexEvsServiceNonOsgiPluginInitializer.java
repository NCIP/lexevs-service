package edu.mayo.cts2.framework.plugin.service.lexevs.service;

import edu.mayo.cts2.framework.util.spring.AbstractSpringNonOsgiPluginInitializer;

public class LexEvsServiceNonOsgiPluginInitializer 
	extends AbstractSpringNonOsgiPluginInitializer {

	@Override
	protected String[] getContextConfigLocations() {
		return new String[]{
				"/META-INF/spring/lexevs-context.xml",
				"/META-INF/spring/osgi-context.xml"};
	}

}
