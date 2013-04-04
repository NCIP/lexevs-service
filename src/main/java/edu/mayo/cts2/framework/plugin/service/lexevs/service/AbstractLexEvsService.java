/*
 * Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.cts2.framework.plugin.service.lexevs.service;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.apache.log4j.Logger;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.beans.factory.annotation.Value;

import edu.mayo.cts2.framework.model.core.OpaqueData;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.service.profile.BaseService;

/**
 * The base LexEVS CTS2 Service implementation class.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractLexEvsService implements BaseService {

	protected Logger log = Logger.getLogger(this.getClass());

	@Value("#{buildProperties.buildversion}")
	private String buildVersion;

	@Value("#{buildProperties.name}")
	private String buildName;

	@Value("#{buildProperties.description}")
	private String buildDescription;

	@Resource
	private LexBIGService lexBigService;

	//TODO: Create factories for this
	private LexEVSResolvedValueSetService lexEVSResolvedService;
	
	//TODO: Create factories for this
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
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
}
