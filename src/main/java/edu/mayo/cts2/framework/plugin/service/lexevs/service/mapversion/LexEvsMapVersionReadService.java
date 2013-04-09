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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonMapUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionReadService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapVersionReadService
	extends AbstractLexEvsCodeSystemService<MapVersion>
	implements MapVersionReadService, InitializingBean {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeToMapVersionTransform transformer;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";
	
	// ------ Local methods ----------------------
	
	// -------- Implemented methods ----------------	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}

	@Override
	protected MapVersion transform(CodingScheme codingScheme) {
		if(! CommonMapUtils.validateMappingCodingScheme(
				codingScheme.getCodingSchemeURI(), 
				codingScheme.getRepresentsVersion(),
				mappingExtension)){
			return null;
		} else {
			return this.transformer.transformDescription(codingScheme);
		}
	}
	
	@Override
	public MapVersion readByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		
		return this.getByVersionIdOrTag(parentIdentifier, 
				this.convertTag(tag));
	}

	@Override
	public boolean existsByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		return this.readByTag(parentIdentifier, tag, readContext) != null;
	}

	@Override
	public List<VersionTagReference> getSupportedTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	@Override
	public MapVersion read(NameOrURI identifier, ResolvedReadContext readContext) {
		
		String name;
		if(identifier.getName() != null){
			name = identifier.getName();
			if(!this.nameConverter.isValidVersionName(name)){
				return null;
			}
		} else {
			throw new UnsupportedOperationException("Cannot resolve by DocumentURI yet.");
		}
		
		NameVersionPair namePair = this.nameConverter.fromCts2VersionName(name);
		CodingSchemeVersionOrTag version = 
			Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion());
		
		return this.getByVersionIdOrTag
				(ModelUtils.nameOrUriFromName(
						namePair.getName()), 
						version);
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		return this.read(identifier, readContext) != null;
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
