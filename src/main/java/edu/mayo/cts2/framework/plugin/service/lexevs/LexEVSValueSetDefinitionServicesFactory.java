/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs;

import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEVSValueSetDefinitionServicesFactory implements
		FactoryBean<LexEVSValueSetDefinitionServices> {

	@Override
	public LexEVSValueSetDefinitionServices getObject() throws Exception {
		return LexEVSValueSetDefinitionServicesImpl.defaultInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return LexEVSValueSetDefinitionServices.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
