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

import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.relations.AssociationPredicate;
import org.LexGrid.relations.AssociationSource;
import org.LexGrid.relations.AssociationTarget;
import org.LexGrid.relations.Relations;
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
		
		// Initialize items for CTS2 EntitiesRestrictions
		if (cts2EntitiesRestriction != null) {
			
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
		
		if (restrictionType.value().equals("AT_LEAST_ONE")) {
			return checkIfMatchForAllCts2EntitiesRestriction(lexCodingScheme, cts2EntitiesRestriction);			
		} else {
			// Process as ALL RestrictionType
			return checkIfMatchForAtLeastOneCts2EntitiesRestriction(lexCodingScheme, cts2EntitiesRestriction);
		}
	}
	
	protected boolean checkIfMatchForAllCts2EntitiesRestriction(CodingScheme lexCodingScheme, EntitiesRestriction cts2EntitiesRestriction) {

		MapStatus mapStatus = cts2EntitiesRestriction.getMapStatus();
		if (mapStatus == null) {
			// Set default status to 
			mapStatus = MapStatus.MAPPED;  // ??? Use MAPPED
		}
		
		// TODO Under construction
		
		// MapStatus values to consider via notes from CTS2 specification:
		//   ALLMAPENTRIES: The union of UNMAPPED and MAPPED entities - any entity that is a source or target of a map 
		//    whether mentioned or not.
		//   MAPPED: An entity is included in the "from" part of the map and appears in a MapEntry or it appears in the "to" 
		//    part of the map and appears in one or more MapRules. MAPPED includes NOMAP entities.
		//   NOMAP: An entity is included in the "from" part of the map and there is a MapEntry that references it but there 
		//    the target set is empty. NOMAP references entities that have been explicitly declared to have no mapping.
		//   UNMAPPED: An entity is included in the "from" part of the map but does not appear in an MapEntry or it appears 
		//    in the "to" part of the map but does not appear in the output of a MapRule.
		
		// TODO For MapStatus.NOMAP scenario, it is not clear if an AssociateSource object would represent its List<AssociationTarget> as
		//   an empty list or as null. Assume both null and empty list are valid?
		
		// TODO For MapStatus.UNMAPPED and MapStatus.ALLMAPENTRIES scenarios, throw UnsupportedOperationException for now.
		
		if (mapStatus == MapStatus.ALLMAPENTRIES) {
			throw new UnsupportedOperationException("MapStatus.ALLMAPENTRIES is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		if (mapStatus == MapStatus.UNMAPPED) {
			throw new UnsupportedOperationException("MapStatus.UNMAPPED is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		
		Set<EntityNameOrURI> cts2RestrictedEntitiesSet = cts2EntitiesRestriction.getEntities();
		MapRole mapRole = cts2EntitiesRestriction.getMapRole();

		boolean matchFound = true;
		
		Relations relations = lexCodingScheme.getRelations(0);
		List<AssociationPredicate> lexAssociationPredicatesList = relations.getAssociationPredicateAsReference();

		if (mapStatus == MapStatus.MAPPED) {
			// TODO Not sure what "MAPPED includes NOMAP entities." means (especially if the set of entities to restrict is not empty). If
			//   the set of entities to restrict is empty then what's the point of having EntitiesRestriction?  Maybe its just a stmt to 
			//   be sure not to discard the NOMAP conditions found.
			if (mapRole == MapRole.MAP_FROM_ROLE) {
				while (matchFound) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForMapFromRole(cts2EntityName, lexAssociationPredicatesList);
					}
				}
			} else if (mapRole == MapRole.MAP_TO_ROLE) {
				while (matchFound) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForMapToRole(cts2EntityName, lexAssociationPredicatesList);
					}
				}				
			} else if (mapRole == MapRole.BOTH_MAP_ROLES) {
				while (matchFound) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForBothMapRoles(cts2EntityName, lexAssociationPredicatesList);
					}
				}								
			} else {
				// TODO Throw an UnsupportedOperationException here?
			}		
		}
		
		if (mapStatus == MapStatus.NOMAP) {
			// TODO Note the MapRoles MAP_TO_ROLE and BOTH_MAP_ROLE rules defined does not make sense with this MapStatus. Ignore MapRole or 
			// throw an UnsupportedOperationException?
			
			// Treat all CTS2 Entities in the restriction list as LexEvs source entities (i.e. map from)
			
			
		}
		
		// If MapRole is  defined as MapRole.MAP_FROM_ROLE then only need to dive to AssociationSource level below
		// If MapRole is defined as MapRole.MAP_TO_ROLE then must dive to the AssociationTarget level below
		// If MapRole is defined as MapRole.BOTH_MAP_ROLES then will need to dive to the AssociationTarget level if entity info 
		//   not found at AssociationSource level below
//		Relations relations = lexCodingScheme.getRelations(0);
//		List<AssociationPredicate> lexAssociationPredicatesList = relations.getAssociationPredicateAsReference();
		// Think about using while loop with conditional flag to indicated match found or not 
//		for (AssociationPredicate lexAssociationPredicate : lexAssociationPredicatesList) {
//			List<AssociationSource> lexAssociationSourceList = lexAssociationPredicate.getSourceAsReference();
//			for (AssociationSource lexAssociationSource : lexAssociationSourceList) {
//				lexAssociationSource.getSourceEntityCode();
//				lexAssociationSource.getSourceEntityCodeNamespace();
//				List<AssociationTarget> lexAssociationTargetList = lexAssociationSource.getTargetAsReference();
//				for (AssociationTarget lexAssociationTarget : lexAssociationTargetList) {
//					lexAssociationTarget.getTargetEntityCode();
//					lexAssociationTarget.getTargetEntityCodeNamespace();
//				}
//			}
//		}
		

		return matchFound;
	}
	
	protected boolean isEntityFoundForMapFromRole(ScopedEntityName cts2EntityName, List<AssociationPredicate> lexAssociationPredicatesList) {
		
		boolean matchFound = false;

		while (!matchFound) {
			for (AssociationPredicate lexAssociationPredicate : lexAssociationPredicatesList) {
				List<AssociationSource> lexAssociationSourceList = lexAssociationPredicate.getSourceAsReference();
				for (AssociationSource lexAssociationSource : lexAssociationSourceList) {
					if (cts2EntityName.getName().equals(lexAssociationSource.getSourceEntityCode()) &&
							cts2EntityName.getNamespace().equals(lexAssociationSource.getSourceEntityCodeNamespace())) {
						matchFound = true;
					}
				}
			}
		}
		return matchFound;
	}
	
	protected boolean isEntityFoundForMapToRole(ScopedEntityName cts2EntityName, List<AssociationPredicate> lexAssociationPredicatesList) {
		
		boolean matchFound = false;

		while (!matchFound) {
			for (AssociationPredicate lexAssociationPredicate : lexAssociationPredicatesList) {
				List<AssociationSource> lexAssociationSourceList = lexAssociationPredicate.getSourceAsReference();
				for (AssociationSource lexAssociationSource : lexAssociationSourceList) {
					List<AssociationTarget> lexAssociationTargetList = lexAssociationSource.getTargetAsReference();
					for (AssociationTarget lexAssociationTarget : lexAssociationTargetList) {
						if (cts2EntityName.getName().equals(lexAssociationTarget.getTargetEntityCode()) &&
								cts2EntityName.getNamespace().equals(lexAssociationTarget.getTargetEntityCodeNamespace())) {
							matchFound = true;
						}
					}
				}
			}
		}
		return matchFound;
	}
	
	protected boolean isEntityFoundForBothMapRoles(ScopedEntityName cts2EntityName, List<AssociationPredicate> lexAssociationPredicatesList) {
		
		boolean matchFound = false;

		while (!matchFound) {
			for (AssociationPredicate lexAssociationPredicate : lexAssociationPredicatesList) {
				List<AssociationSource> lexAssociationSourceList = lexAssociationPredicate.getSourceAsReference();
				for (AssociationSource lexAssociationSource : lexAssociationSourceList) {
					if (cts2EntityName.getName().equals(lexAssociationSource.getSourceEntityCode()) &&
							cts2EntityName.getNamespace().equals(lexAssociationSource.getSourceEntityCodeNamespace())) {
						matchFound = true;
					}
					List<AssociationTarget> lexAssociationTargetList = lexAssociationSource.getTargetAsReference();
					for (AssociationTarget lexAssociationTarget : lexAssociationTargetList) {
						if (cts2EntityName.getName().equals(lexAssociationTarget.getTargetEntityCode()) &&
								cts2EntityName.getNamespace().equals(lexAssociationTarget.getTargetEntityCodeNamespace())) {
							matchFound = true;
						}
					}
				}
			}
		}
		return matchFound;
	}
	
	protected boolean checkIfMatchForAtLeastOneCts2EntitiesRestriction(CodingScheme lexCodingScheme, EntitiesRestriction cts2EntitiesRestriction) {

		MapStatus mapStatus = cts2EntitiesRestriction.getMapStatus();
		if (mapStatus == null) {
			// Set default status to 
			mapStatus = MapStatus.MAPPED;  // ??? Use MAPPED
		}
				
		if (mapStatus == MapStatus.ALLMAPENTRIES) {
			throw new UnsupportedOperationException("MapStatus.ALLMAPENTRIES is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		if (mapStatus == MapStatus.UNMAPPED) {
			throw new UnsupportedOperationException("MapStatus.UNMAPPED is currently not a supported option in respect " +
					"to EntitiesRestriction for MapVersionQuery");	
		}
		
		Set<EntityNameOrURI> cts2RestrictedEntitiesSet = cts2EntitiesRestriction.getEntities();
		MapRole mapRole = cts2EntitiesRestriction.getMapRole();

		boolean matchFound = false;
		
		Relations relations = lexCodingScheme.getRelations(0);
		List<AssociationPredicate> lexAssociationPredicatesList = relations.getAssociationPredicateAsReference();

		if (mapStatus == MapStatus.MAPPED) {
			// TODO Not sure what "MAPPED includes NOMAP entities." means (especially if the set of entities to restrict is not empty). If
			//   the set of entities to restrict is empty then what's the point of having EntitiesRestriction?  Maybe its just a stmt to 
			//   be sure not to discard the NOMAP conditions found.
			if (mapRole == MapRole.MAP_FROM_ROLE) {
				while (!matchFound) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForMapFromRole(cts2EntityName, lexAssociationPredicatesList);
					}
				}
			} else if (mapRole == MapRole.MAP_TO_ROLE) {
				while (!matchFound) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForMapToRole(cts2EntityName, lexAssociationPredicatesList);
					}
				}				
			} else if (mapRole == MapRole.BOTH_MAP_ROLES) {
				while (!matchFound) {
					for (EntityNameOrURI cts2RestrictedEntity : cts2RestrictedEntitiesSet) {
						ScopedEntityName cts2EntityName = cts2RestrictedEntity.getEntityName();
						matchFound = isEntityFoundForBothMapRoles(cts2EntityName, lexAssociationPredicatesList);
					}
				}								
			} else {
				// TODO Throw an UnsupportedOperationException here?
			}		
		}
		
		if (mapStatus == MapStatus.NOMAP) {
			// TODO Note the MapRoles MAP_TO_ROLE and BOTH_MAP_ROLE rules defined does not make sense with this MapStatus. Ignore MapRole or 
			// throw an UnsupportedOperationException?
			
			// Treat all CTS2 Entities in the restriction list as LexEvs source entities (i.e. map from)
			
			
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

		//    4. Filter list from step 3 for any defined CodeSystemRestrictions.  Return list as CodingSchemeRendingList object ???
		//    5. Filter list from step 4 for any defined EntitiesRestrictions.  Return list as CodingSchemeRendingList object ???
		lexCodingSchemeRendering = filterByMapVersionQueryRestrictions(lexCodingSchemeRendering, queryData);		
	
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(lexCodingSchemeRendering, page);
		boolean atEnd = (page.getEnd() >= lexCodingSchemeRendering.length) ? true : false;
		return CommonResourceUtils.createDirectoryResultsWithSummaryDescriptions(this.transformer, csRenderingPage, atEnd, Constants.SUMMARY_DESCRIPTION);
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
