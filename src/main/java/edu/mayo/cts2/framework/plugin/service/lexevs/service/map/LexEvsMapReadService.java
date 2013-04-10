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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.service.profile.map.MapReadService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
public class LexEvsMapReadService 
	extends AbstractLexEvsCodeSystemService<MapCatalogEntry>
	implements MapReadService, InitializingBean {

	@Resource
	CodingSchemeToMapTransform transformer;
	
	@Resource
	private VersionNameConverter nameConverter;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";	


	// ------ Local methods ----------------------
	public void setCodingSchemeToMapTransform(
			CodingSchemeToMapTransform codingSchemeToMapTransform) {
		this.transformer = codingSchemeToMapTransform;
	}

	public void setCodeSystemVersionNameConverter(VersionNameConverter converter){
		this.nameConverter = converter;
	}
	
	public void setMappingExtension(MappingExtension mappingExtension){
		this.mappingExtension = mappingExtension;
	}

	@Override
	protected boolean isValidCodingScheme(CodingScheme codingScheme) {
		try {
			return this.mappingExtension.isMappingCodingScheme(
				codingScheme.getCodingSchemeURI(),
				Constructors.createCodingSchemeVersionOrTagFromVersion(codingScheme.getRepresentsVersion()));
		} catch (LBParameterException e) {
			return false;
		}
	}

	// -------- Implemented methods ----------------	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}
	
	@Override
	protected MapCatalogEntry transform(CodingScheme codingScheme) {
		return this.transformer.transformDescription(codingScheme);
	}

	@Override
	public MapCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		NameVersionPair namePair = CommonUtils.getNamePair(nameConverter, identifier, readContext);
		NameOrURI name = ModelUtils.nameOrUriFromName(namePair.getName());
		CodingSchemeVersionOrTag versionOrTag = Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion());
		return this.getByVersionIdOrTag(name, versionOrTag);
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
