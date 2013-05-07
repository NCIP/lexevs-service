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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.RestrictionType;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonCodingSchemeUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions.EntitiesRestriction;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQueryService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapVersionQueryService extends AbstractLexEvsService
		implements MapVersionQueryService, InitializingBean {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeToMapVersionTransform transformer;
	
	private MappingExtension mappingExtension;
	
	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(VersionNameConverter converter){
		this.nameConverter = converter;
	}
	
	public void setCodingSchemeToMapVersionTransform(CodingSchemeToMapVersionTransform transformer){
			this.transformer = transformer;
	}
	
	public void setMappingExtension(MappingExtension extension){
		this.mappingExtension = extension;
	}	

	protected CodingSchemeRendering[] filterByMapVersionQueryRestrictions(CodingSchemeRendering[] lexCodingSchemeRenderingArray, 
			QueryData<MapVersionQuery> queryData) {
		
		// Assuming the passed in CodingSchemeRendering[] object is not null
		if (lexCodingSchemeRenderingArray.length == 0) {
			return lexCodingSchemeRenderingArray;
		}
		
		List<CodingSchemeRendering> lexCodingSchemeRenderingList = new ArrayList<CodingSchemeRendering>();
		
		CodeSystemRestriction cts2CodeSystemRestriction = queryData.getCts2CodeSystemRestriction();
		EntitiesRestriction cts2EntitiesRestriction = queryData.getCts2EntitiesRestriction();
		String mapName = queryData.getCts2SystemName();
		
		if (cts2CodeSystemRestriction == null && cts2EntitiesRestriction == null && mapName == null) {
			// There are no restrictions to process so return back passed in array
			return lexCodingSchemeRenderingArray;			
		}
		
		//  Initialize items for CTS2 CodeSystemRestriction
		Set<NameOrURI> cts2CodeSystemSet = null;
		MapRole cts2CodeSystemRestrictionMapRole = null;
		if(cts2CodeSystemRestriction != null){
			cts2CodeSystemSet = cts2CodeSystemRestriction.getCodeSystems();
			cts2CodeSystemRestrictionMapRole = cts2CodeSystemRestriction.getMapRole();
		}
		

		CodingScheme lexCodingScheme;
		for (CodingSchemeRendering lexCodingSchemeRendering : lexCodingSchemeRenderingArray) {
			
			boolean addToListFlag = true;  // assume we adding the CodingSchemeRendering object to the ArrayList 
			
			lexCodingScheme = CommonResourceUtils.getLexCodingScheme(getLexBigService(), lexCodingSchemeRendering);
			if(cts2CodeSystemRestrictionMapRole != null && cts2CodeSystemSet != null){
				if(!CommonCodingSchemeUtils.checkIfCts2MapExists(lexCodingScheme, cts2CodeSystemSet, cts2CodeSystemRestrictionMapRole.value())){
					addToListFlag = false;
				}			
			}
			
			if (addToListFlag && cts2EntitiesRestriction != null) { 
				if (!checkIfMapExistsGivenCts2EntitiesRestriction(lexCodingScheme, cts2EntitiesRestriction)) {
					addToListFlag = false;					
				}
			}
			
			if (addToListFlag) {
				lexCodingSchemeRenderingList.add(lexCodingSchemeRendering);
			}
		} 

		return (CodingSchemeRendering[]) lexCodingSchemeRenderingList.toArray(new CodingSchemeRendering[0]);
	}
	
	protected boolean checkIfMapExistsGivenCts2EntitiesRestriction(CodingScheme lexCodingScheme, EntitiesRestriction cts2EntitiesRestriction) {
			
		// CTS2 RestrictionType specification notes:
		// A parameter used in queries where multiple elements are provided. It determines whether a candidate element 
		// must satisfy all restrictions or just one or more restriction in order to be considered as satisfying the 
		// restriction composite.  Valid values:
		//   ALL - a match is only considered valid if all defined restrictions are met
		//   AT_LEAST_ONE - a match is considered valid if one or more restrictions are met

		// How this logic precedes in determining if something is a match based on the defined EntitiesRestriction is
		// driven by the CTS2 EntitiesRestriction.RestrictionType value.
		//    If the RestrictionType is ALL then the subsequent logic will use an "AND" logic approach
		//    If the RestrictionType is AT_LEAST_ONE then the subsequent logic will use an "OR" logic approach
		
		RestrictionType restrictionType = cts2EntitiesRestriction.getAllOrSome();
		if (restrictionType == null) {
			// Set default processing rule to use all defined restrictions 
			restrictionType = RestrictionType.ALL;
		}
		
		if (restrictionType.value().equals(RestrictionType.AT_LEAST_ONE.value())) {
			return checkIfMatchForAtLeastOneCts2EntitiesRestriction(lexCodingScheme, cts2EntitiesRestriction);			
		} else {
			// Process as ALL RestrictionType
			return checkIfMatchForAllCts2EntitiesRestriction(lexCodingScheme, cts2EntitiesRestriction);
		}
	}
	
	protected boolean checkIfMatchForAllCts2EntitiesRestriction(CodingScheme lexCodingScheme, EntitiesRestriction cts2EntitiesRestriction) {

		MapStatus mapStatus = cts2EntitiesRestriction.getMapStatus();
		if (mapStatus == null) {
			// Set default status 
			mapStatus = MapStatus.MAPPED;  
		}
		
		// MapStatus values to consider via notes from CTS2 specification:
		//   ALLMAPENTRIES: The union of UNMAPPED and MAPPED entities - any entity that is a source or target of a map 
		//    whether mentioned or not.
		//   MAPPED: An entity is included in the "from" part of the map and appears in a MapEntry or it appears in the "to" 
		//    part of the map and appears in one or more MapRules. MAPPED includes NOMAP entities.
		//   NOMAP: An entity is included in the "from" part of the map and there is a MapEntry that references it but there 
		//    the target set is empty. NOMAP references entities that have been explicitly declared to have no mapping.
		//   UNMAPPED: An entity is included in the "from" part of the map but does not appear in an MapEntry or it appears 
		//    in the "to" part of the map but does not appear in the output of a MapRule.
		
		if (mapStatus == MapStatus.ALLMAPENTRIES) {
			throw new UnsupportedOperationException("MapStatus.ALLMAPENTRIES is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		if (mapStatus == MapStatus.UNMAPPED) {
			throw new UnsupportedOperationException("MapStatus.UNMAPPED is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		if (mapStatus == MapStatus.NOMAP) {
			throw new UnsupportedOperationException("MapStatus.NOMAP is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		
		Set<EntityNameOrURI> cts2RestrictedEntitiesSet = cts2EntitiesRestriction.getEntities();
		MapRole mapRole = cts2EntitiesRestriction.getMapRole();

		String lexRelationsContainerName = null;
		CodingSchemeVersionOrTag lexCodingSchemeVersionOrTag = new CodingSchemeVersionOrTag();
		lexCodingSchemeVersionOrTag.setVersion(lexCodingScheme.getRepresentsVersion());
		lexCodingSchemeVersionOrTag.setTag(lexCodingScheme.getCodingSchemeName());
		
		boolean matchFound = true;		
		if (mapStatus == MapStatus.MAPPED) {
			if (mapRole == MapRole.MAP_FROM_ROLE) {
				for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
					ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
					matchFound = isEntityFoundForMapFromRole(cts2EntityName, 
							lexCodingScheme, 
							lexCodingSchemeVersionOrTag, 
							lexRelationsContainerName);
					if (!matchFound) {
						break;
					} 
				}
			} else if (mapRole == MapRole.MAP_TO_ROLE) {
				for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
					ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
					matchFound = isEntityFoundForMapToRole(cts2EntityName, 
							lexCodingScheme, 
							lexCodingSchemeVersionOrTag, 
							lexRelationsContainerName);
					if (!matchFound) {
						break;
					} 
				}
			} else if (mapRole == MapRole.BOTH_MAP_ROLES) {
				for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
					ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
					matchFound = isEntityFoundForBothMapRoles(cts2EntityName, 
							lexCodingScheme, 
							lexCodingSchemeVersionOrTag, 
							lexRelationsContainerName);
					if (!matchFound) {
						break;
					} 
				}
			} else {
				throw new UnsupportedOperationException("The EntitiesRestriction for MapVersionQuery must have a MapRole defined " +
						"when the MapStatus is defined as MapStatus.MAPPED");
			}		
		}
		
		return matchFound;
	}
	
	protected boolean isEntityFoundForMapFromRole(ScopedEntityName cts2EntityName, 
			CodingScheme lexCodingScheme, 
			CodingSchemeVersionOrTag lexCodingSchemeVersionOrTag, 
			String lexRelationsContainerName) {
		
		ResolvedConceptReferencesIterator resolvedConceptReferencesIterator;
		Mapping lexMapping = null;
		try {
			lexMapping = mappingExtension.getMapping(lexCodingScheme.getCodingSchemeName(), 
					lexCodingSchemeVersionOrTag, 
					lexRelationsContainerName);
			String cts2SourceEntityName = cts2EntityName.getName();
			String cts2SourceEntityNamespace = cts2EntityName.getNamespace();
			ConceptReferenceList reference = Constructors.createConceptReferenceList(cts2SourceEntityName, 
					cts2SourceEntityNamespace, 
					lexCodingScheme.getCodingSchemeName());
			lexMapping = lexMapping.restrictToCodes(reference, SearchContext.SOURCE_CODES);
			
			resolvedConceptReferencesIterator = lexMapping.resolveMapping();
			return (resolvedConceptReferencesIterator != null && resolvedConceptReferencesIterator.hasNext());	
		
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected boolean isEntityFoundForMapToRole(ScopedEntityName cts2EntityName, 
			CodingScheme lexCodingScheme, 
			CodingSchemeVersionOrTag lexCodingSchemeVersionOrTag, 
			String lexRelationsContainerName) {
		
		ResolvedConceptReferencesIterator resolvedConceptReferencesIterator;
		Mapping lexMapping = null;
		try {
			lexMapping = mappingExtension.getMapping(lexCodingScheme.getCodingSchemeName(), 
					lexCodingSchemeVersionOrTag, 
					lexRelationsContainerName);
			String cts2SourceEntityName = cts2EntityName.getName();
			String cts2SourceEntityNamespace = cts2EntityName.getNamespace();
			ConceptReferenceList reference = Constructors.createConceptReferenceList(cts2SourceEntityName, 
					cts2SourceEntityNamespace, 
					lexCodingScheme.getCodingSchemeName());
			lexMapping = lexMapping.restrictToCodes(reference, SearchContext.TARGET_CODES);
			
			resolvedConceptReferencesIterator = lexMapping.resolveMapping();
			return (resolvedConceptReferencesIterator != null && resolvedConceptReferencesIterator.hasNext());	
		
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected boolean isEntityFoundForBothMapRoles(ScopedEntityName cts2EntityName, 
			CodingScheme lexCodingScheme, 
			CodingSchemeVersionOrTag lexCodingSchemeVersionOrTag, 
			String lexRelationsContainerName) {
		
		ResolvedConceptReferencesIterator resolvedConceptReferencesIterator;
		Mapping lexMapping = null;
		try {
			lexMapping = mappingExtension.getMapping(lexCodingScheme.getCodingSchemeName(), 
					lexCodingSchemeVersionOrTag, 
					lexRelationsContainerName);
			String cts2SourceEntityName = cts2EntityName.getName();
			String cts2SourceEntityNamespace = cts2EntityName.getNamespace();
			ConceptReferenceList reference = Constructors.createConceptReferenceList(cts2SourceEntityName, 
					cts2SourceEntityNamespace, 
					lexCodingScheme.getCodingSchemeName());
			lexMapping = lexMapping.restrictToCodes(reference, SearchContext.SOURCE_OR_TARGET_CODES);
			
			resolvedConceptReferencesIterator = lexMapping.resolveMapping();
			return (resolvedConceptReferencesIterator != null && resolvedConceptReferencesIterator.hasNext());	
		
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected boolean checkIfMatchForAtLeastOneCts2EntitiesRestriction(CodingScheme lexCodingScheme, EntitiesRestriction cts2EntitiesRestriction) {

		MapStatus mapStatus = cts2EntitiesRestriction.getMapStatus();
		if (mapStatus == null) {
			// Set default status
			mapStatus = MapStatus.MAPPED;  
		}
				
		if (mapStatus == MapStatus.ALLMAPENTRIES) {
			throw new UnsupportedOperationException("MapStatus.ALLMAPENTRIES is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		if (mapStatus == MapStatus.UNMAPPED) {
			throw new UnsupportedOperationException("MapStatus.UNMAPPED is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		if (mapStatus == MapStatus.NOMAP) {
			throw new UnsupportedOperationException("MapStatus.NOMAP is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		
		Set<EntityNameOrURI> cts2RestrictedEntitiesSet = cts2EntitiesRestriction.getEntities();
		MapRole mapRole = cts2EntitiesRestriction.getMapRole();
		
		String lexRelationsContainerName = null;
		CodingSchemeVersionOrTag lexCodingSchemeVersionOrTag = new CodingSchemeVersionOrTag();
		lexCodingSchemeVersionOrTag.setVersion(lexCodingScheme.getRepresentsVersion());
		lexCodingSchemeVersionOrTag.setTag(lexCodingScheme.getCodingSchemeName());
		
		boolean matchFound = false;
		if (mapStatus == MapStatus.MAPPED) {
			if (mapRole == MapRole.MAP_FROM_ROLE) {
				for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
					ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
					matchFound = isEntityFoundForMapFromRole(cts2EntityName, 
							lexCodingScheme, 
							lexCodingSchemeVersionOrTag, 
							lexRelationsContainerName);
					if (matchFound) {
						break;
					} 
				}
			} else if (mapRole == MapRole.MAP_TO_ROLE) {
				for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
					ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
					matchFound = isEntityFoundForMapToRole(cts2EntityName, 
							lexCodingScheme, 
							lexCodingSchemeVersionOrTag, 
							lexRelationsContainerName);
					if (matchFound) {
						break;
					} 
				}
			} else if (mapRole == MapRole.BOTH_MAP_ROLES) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForBothMapRoles(cts2EntityName, 
								lexCodingScheme, 
								lexCodingSchemeVersionOrTag, 
								lexRelationsContainerName);
						if (matchFound) {
							break;
						} 
					}
			} else {
				throw new UnsupportedOperationException("The EntitiesRestriction for MapVersionQuery must have a MapRole defined " +
						"when the MapStatus is defined as MapStatus.MAPPED");
			}		
		}
		
		return matchFound;
	}
	
	
	
	// -------- Implemented methods ----------------
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(Constants.MAPPING_EXTENSION);
	}
		
	@Override
	public int count(MapVersionQuery query) {
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<MapVersionQuery> queryData = new QueryData<MapVersionQuery>(query, null);
		
		CodingSchemeRendering[] lexCodingSchemeRendering = CommonResourceUtils.getLexCodingSchemeRenderings(lexBigService, nameConverter, queryData, this.mappingExtension, null);
		lexCodingSchemeRendering = filterByMapVersionQueryRestrictions(lexCodingSchemeRendering, queryData);		
		return lexCodingSchemeRendering.length;
	}
	
	@Override
	public DirectoryResult<MapVersionDirectoryEntry> getResourceSummaries(
			MapVersionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<MapVersionQuery> queryData = new QueryData<MapVersionQuery>(query, null);
				
		// Algorithm:
		//    1. Get all supported codingSchemes as a CodingSchemeRendingList object
		//    2. Filter list from step 1 to only codingSchemes that are of type map and further filter on based on if the MapVersionQuery.restrictions.map 
		//       is not a null value.  Return list as CodingSchemeRendingList object.
		//    3. Filter list from step 2 for any defined ResolvedFilters.  Return list as CodingSchemeRendingList object.
		CodingSchemeRendering[] lexCodingSchemeRendering = CommonResourceUtils.getLexCodingSchemeRenderings(lexBigService, nameConverter, queryData, this.mappingExtension, sortCriteria);

		//    4. Filter list from step 3 for any defined CodeSystemRestrictions.  Return list as CodingSchemeRendingList object.
		//    5. Filter list from step 4 for any defined EntitiesRestrictions.  Return list as CodingSchemeRendingList object.
		lexCodingSchemeRendering = filterByMapVersionQueryRestrictions(lexCodingSchemeRendering, queryData);		
	
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(lexCodingSchemeRendering, page);
		boolean atEnd = (page.getEnd() >= lexCodingSchemeRendering.length) ? true : false;
		return CommonResourceUtils.createDirectoryResultsWithSummary(this.transformer, csRenderingPage, atEnd);
	}

	@Override
	public DirectoryResult<MapVersion> getResourceList(MapVersionQuery query,
			SortCriteria sortCriteria, Page page) {

		LexBIGService lexBigService = this.getLexBigService();
		QueryData<MapVersionQuery> queryData = new QueryData<MapVersionQuery>(query, null);
		
		CodingSchemeRendering[] lexCodingSchemeRendering = CommonResourceUtils.getLexCodingSchemeRenderings(lexBigService, nameConverter, queryData, this.mappingExtension, sortCriteria);
		lexCodingSchemeRendering = filterByMapVersionQueryRestrictions(lexCodingSchemeRendering, queryData);		
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(lexCodingSchemeRendering, page);
		boolean atEnd = (page.getEnd() >= lexCodingSchemeRendering.length) ? true : false;
		return CommonResourceUtils.createDirectoryResultWithEntryFullVersionDescriptions(lexBigService, this.transformer, csRenderingPage, atEnd);
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.getLexSupportedSearchReferences();
	}

	// Not going to implement following methods
	// -----------------------------------------
	@Override
	public DirectoryResult<EntityDirectoryEntry> mapVersionEntities(
			NameOrURI mapVersion, MapRole mapRole, MapStatus mapStatus,
			EntityDescriptionQuery query, SortCriteria sort, Page page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DirectoryResult<EntityDescription> mapVersionEntityList(
			NameOrURI mapVersion, MapRole mapRole, MapStatus mapStatus,
			EntityDescriptionQuery query, SortCriteria sort, Page page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityReferenceList mapVersionEntityReferences(NameOrURI mapVersion,
			MapRole mapRole, MapStatus mapStatus, EntityDescriptionQuery query,
			SortCriteria sort, Page page) {
		throw new UnsupportedOperationException();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
