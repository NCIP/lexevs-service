/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MappingExtensionFactory implements FactoryBean<MappingExtension> {

	private static final String MAPPING_EXTENSION = "MappingExtension";	
	
	@Resource
	private LexBIGService lexBigService;
	
	@Override
	public MappingExtension getObject() throws Exception {
		return (MappingExtension) this.lexBigService.getGenericExtension(MAPPING_EXTENSION);
	}

	@Override
	public Class<?> getObjectType() {
		return MappingExtension.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
