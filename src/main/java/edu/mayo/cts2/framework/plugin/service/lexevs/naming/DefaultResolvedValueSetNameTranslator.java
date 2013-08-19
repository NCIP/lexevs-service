/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.codingSchemes.CodingScheme;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;

@Component
public class DefaultResolvedValueSetNameTranslator 
	implements ResolvedValueSetNameTranslator, LexEvsChangeEventObserver, InitializingBean {

	@Resource
	private LexEVSResolvedValueSetService lexEVSResolvedService;
	
	private Map<ResolvedValueSetNameTriple,NameVersionPair> valueSetToNameVersionMap = 
		new HashMap<ResolvedValueSetNameTriple,NameVersionPair>();
	
	private Map<String,ResolvedValueSetNameTriple> uriToValueSetToNameVersionMap = 
			new HashMap<String,ResolvedValueSetNameTriple>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.buildCaches();
	}
	
	public void buildCaches() throws Exception {
		this.valueSetToNameVersionMap.clear();
		this.uriToValueSetToNameVersionMap.clear();
		
		try {
			for(CodingScheme codingScheme : this.lexEVSResolvedService.listAllResolvedValueSets()){
				String valueSetId = codingScheme.getCodingSchemeName();
				String valueSetDefId = 
					ValueSetDefinitionUtils.getValueSetDefinitionLocalId(codingScheme.getCodingSchemeURI());
				String localId = RESOLVED_VS_LOCAL_ID;
				
				ResolvedValueSetNameTriple triple = 
					new ResolvedValueSetNameTriple(valueSetId, valueSetDefId, localId);
				
				this.valueSetToNameVersionMap.put(
					triple, 
					new NameVersionPair(codingScheme.getCodingSchemeName(), codingScheme.getRepresentsVersion()));
				
				this.uriToValueSetToNameVersionMap.put(codingScheme.getCodingSchemeURI(), triple);
			}
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public NameVersionPair getNameVersionPair(
			ResolvedValueSetNameTriple resolvedValueSetNameTriple) {
		return this.valueSetToNameVersionMap.get(resolvedValueSetNameTriple);
	}

	@Override
	public ResolvedValueSetNameTriple getResolvedValueSetNameTriple(
			String resolvedValueSetDefinitionUri) {
		return this.uriToValueSetToNameVersionMap.get(resolvedValueSetDefinitionUri);
	}

	@Override
	public void onChange() {
		try {
			this.buildCaches();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
