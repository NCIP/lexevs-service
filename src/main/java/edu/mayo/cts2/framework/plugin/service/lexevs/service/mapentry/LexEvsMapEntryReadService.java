/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.mapversion.MapEntryListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.MapResolvedConceptReference;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryReadService;
import edu.mayo.cts2.framework.service.profile.mapentry.name.MapEntryReadId;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapEntryReadService extends AbstractLexEvsService implements MapEntryReadService {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private MappingToMapEntryTransform mappingToMapEntryTransform;
	
	@Resource
	private MappingExtension mappingExtension;
	
	// ------ Local methods ----------------------
	private String extractMapVersion(MapEntryReadId identifier) {		
		String mapVersion = null;
		NameOrURI nameOrURI = identifier.getMapVersion();
		if (nameOrURI != null) {
			mapVersion = nameOrURI.getName() != null ? nameOrURI.getName() : nameOrURI.getUri();
		}
				
		return mapVersion;
	}

	private ResolvedConceptReferencesIterator getInteratorFromMapping(
			MappingExtension mappingExtension, 
			String mapVersion,
			String sourceEntityCode, 
			String relationsContainerName) throws LBException {
		
		NameVersionPair nameVersionPair = this.nameConverter.fromCts2VersionName(mapVersion);
		Mapping mapping = null;
		CodingSchemeVersionOrTag csvt = 
			Constructors.createCodingSchemeVersionOrTagFromVersion(nameVersionPair.getVersion());
		
		if(mappingExtension.isMappingCodingScheme(nameVersionPair.getName(), csvt)){
			mapping = mappingExtension.getMapping(nameVersionPair.getName(), csvt, relationsContainerName);
			mapping = mapping.restrictToCodes(Constructors.createConceptReferenceList(sourceEntityCode), SearchContext.SOURCE_CODES);
			return mapping.resolveMapping();
		}
		
		return null;
	}
	
	
	
	private ResolvedConceptReference getResolvedConceptReference(MapEntryReadId identifier,
			ResolvedReadContext readContext) {

		ScopedEntityName scopedEntityName = identifier.getEntityName();
		String sourceEntityCode = scopedEntityName.getName();
		String mapVersion = extractMapVersion(identifier);
		
		String relationsContainerName = null;   
		
		ResolvedConceptReferencesIterator resolvedConceptReferencesIterator;
		ResolvedConceptReference resolvedConceptReference = null;
		try {
			resolvedConceptReferencesIterator = getInteratorFromMapping(this.mappingExtension, mapVersion, sourceEntityCode, relationsContainerName); 
				
			if (resolvedConceptReferencesIterator != null && resolvedConceptReferencesIterator.numberRemaining() == 1) {
				resolvedConceptReference = resolvedConceptReferencesIterator.next();							
			}
		} catch (LBParameterException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return resolvedConceptReference;
	}


	// -------- Implemented methods ----------------	
	@Override
	public MapEntry read(
			MapEntryReadId identifier,
			ResolvedReadContext readContext) {
		
		ResolvedConceptReference resolvedConceptReference = getResolvedConceptReference(identifier, readContext);		
		
		if(resolvedConceptReference == null){
			return null;
		} else {
			MapEntryListEntry listEntry = this.mappingToMapEntryTransform.transformFullDescription(
				new MapResolvedConceptReference(
					this.nameConverter.fromCts2VersionName(identifier.getMapVersion().getName()), resolvedConceptReference));
		
			return listEntry == null ? null : listEntry.getEntry();
		}
	}

	@Override
	public boolean exists(MapEntryReadId identifier, ResolvedReadContext readContext) {	
		return getResolvedConceptReference(identifier, readContext) != null;
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
