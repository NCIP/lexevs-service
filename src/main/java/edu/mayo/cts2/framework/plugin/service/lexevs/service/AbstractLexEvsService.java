/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.beans.factory.annotation.Value;

import edu.mayo.cts2.framework.model.core.OpaqueData;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.service.profile.BaseService;

/**
 * The base LexEVS CTS2 Service implementation class.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractLexEvsService implements BaseService {

	protected Logger log = LogManager.getLogger(this.getClass());

	@Value("#{buildProperties.buildversion}")
	private String buildVersion;

	@Value("#{buildProperties.name}")
	private String buildName;

	@Value("#{buildProperties.description}")
	private String buildDescription;

	@Resource
	private LexBIGService lexBigService;

	@Resource
	private LexEVSResolvedValueSetService lexEVSResolvedService;
	
	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	@Resource
    private ValueSetNameTranslator valueSetNameTranslator;
	
	@Override
	public String getServiceName() {
		return this.buildName;
	}

	@Override
	public OpaqueData getServiceDescription() {
		return ModelUtils.createOpaqueData(this.buildDescription);
	}

	@Override
	public String getServiceVersion() {
		return this.buildVersion;
	}

	@Override
	public SourceReference getServiceProvider() {
		return new SourceReference("Mayo Clinic");
	}

	public LexBIGService getLexBigService() {
		return lexBigService;
	}

	public void setLexBigService(LexBIGService lexBigService) {
		this.lexBigService = lexBigService;
	}

	public LexEVSResolvedValueSetService getLexEVSResolvedService() {
		return lexEVSResolvedService;
	}

	public void setLexEVSResolvedService(
			LexEVSResolvedValueSetService lexEVSResolvedService) {
		this.lexEVSResolvedService = lexEVSResolvedService;
	}

	public LexEVSValueSetDefinitionServices getLexEVSValueSetDefinitionServices() {
		return lexEVSValueSetDefinitionServices;
	}

	public void setLexEVSValueSetDefinitionServices(
			LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices) {
		this.lexEVSValueSetDefinitionServices = lexEVSValueSetDefinitionServices;
	}
	
	
	public ValueSetNameTranslator getValueSetNameTranslator() {
		return valueSetNameTranslator;
	}

	public void setValueSetNameTranslator(
			ValueSetNameTranslator valueSetNameTranslator) {
		this.valueSetNameTranslator = valueSetNameTranslator;
	}
}
