/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
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
