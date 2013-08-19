/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.map.MapReadService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapReadService 
	extends AbstractLexEvsCodeSystemService<MapCatalogEntry>
	implements MapReadService {

	@Resource
	CodingSchemeToMapTransform transformer;
	
	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private MappingExtension mappingExtension;

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
	protected MapCatalogEntry transform(CodingScheme codingScheme) {
		MapCatalogEntryListEntry listEntry = this.transformer.transformFullDescription(codingScheme);
		
		return listEntry == null ? null : listEntry.getEntry();
	}

	@Override
	public MapCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		
		return this.getByVersionIdOrTag(identifier, Constants.CURRENT_LEXEVS_TAG);
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
