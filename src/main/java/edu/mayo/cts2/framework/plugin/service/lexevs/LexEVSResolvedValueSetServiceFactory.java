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
import org.LexGrid.util.assertedvaluesets.AssertedValueSetParameters;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexevs.system.constants.SystemVariables;
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
		// get lbconfig properties
		SystemVariables variables = LexEvsServiceLocator.getInstance().getSystemResourceService().getSystemVariables();
		//String csVersion = variables.getAssertedValueSetVersion();
		String csTag = variables.getAssertedValueSetCodingSchemeTag();
		String csName = variables.getAssertedValueSetCodingSchemeName();
		String csURI = variables.getAssertedValueSetCodingSchemeURI();
		String hierarchyVSRelation = variables.getAssertedValueSetHierarchyVSRelation();
			
		AssertedValueSetParameters params;
		
		// Set the asserted value set params
		// create parameters with no tag set
		if (csTag == null) {
			params = new AssertedValueSetParameters.Builder().
				assertedDefaultHierarchyVSRelation(hierarchyVSRelation).
				codingSchemeName(csName).
				codingSchemeURI(csURI)
				.build();
		}
		// create parameters with a tag set
		else {
			params = new AssertedValueSetParameters.Builder().
					assertedDefaultHierarchyVSRelation(hierarchyVSRelation).
					codingSchemeName(csName).
					codingSchemeURI(csURI).
					codingSchemeTag(csTag)
					.build();
		}
		
		LexEVSResolvedValueSetServiceImpl lrvssi = new LexEVSResolvedValueSetServiceImpl(lbs);
		lrvssi.initParams(params);
		
		return lrvssi;  
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
