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

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:kanjamala.pradip@mayo.edu">Pradip Kanjamala</a>
 *
 */
@Component
public class LexEVSResolvedValueSetServiceFactory implements
		FactoryBean<LexEVSResolvedValueSetService> {
	
	@Resource
	private LexBIGService lbs;

	@Override
	public LexEVSResolvedValueSetService getObject() throws Exception {
		return new LexEVSResolvedValueSetServiceImpl(lbs);	
	}

	@Override
	public Class<?> getObjectType() {
		return LexEVSResolvedValueSetService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
