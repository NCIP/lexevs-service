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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.ValueSetRestriction;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;
import edu.mayo.cts2.framework.service.profile.map.MapQueryService;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsMapQueryService extends AbstractLexEvsService
		implements MapQueryService, InitializingBean {
	
	
	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";	


	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}

	
	// ------ Local methods ----------------------
	protected boolean validateMappingCodingScheme(String uri, String version){
		try {
			if(this.mappingExtension != null){
				return this.mappingExtension.
					isMappingCodingScheme(
							uri, 
							Constructors.createCodingSchemeVersionOrTagFromVersion(version));
			}
			else {
				return false;
			}
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}

	protected CodingSchemeRendering[] doGetResourceSummaries(
			MapQuery query, SortCriteria sortCriteria) {

		Set<ResolvedFilter> filters = null; 
		MapQueryServiceRestrictions mapQueryServiceRestrictions = null;
		
		if (query != null) {
			mapQueryServiceRestrictions = query.getRestrictions();
			filters = query.getFilterComponent();
		}		
		
		// TODO Will probably have different restrictions to retrieve.  Source and/or target codingScheme names.
		CodeSystemRestriction codeSystemRestriction = null;
		ValueSetRestriction valueSetRestriction = null;
		if (mapQueryServiceRestrictions != null) {
			codeSystemRestriction = query.getRestrictions().getCodeSystemRestriction();  
			valueSetRestriction = query.getRestrictions().getValueSetRestriction();
		}
		
		MapRole codeSystemRestrictionMapRole = null;
		Set<NameOrURI> codeSystemSet = null;
		if (codeSystemRestriction != null) {
			codeSystemRestrictionMapRole = codeSystemRestriction.getMapRole();
			codeSystemSet = codeSystemRestriction.getCodeSystems();
		}
		
		MapRole valueSetRestrictionMapRole = null;
		Set<NameOrURI> valueSetSet = null;
		if (valueSetRestriction != null) {
			valueSetRestrictionMapRole = valueSetRestriction.getMapRole();
			valueSetSet = valueSetRestriction.getValueSets();
		}
		
		// TODO Question about MapRole value of type String.  Can be "MAP_FROM_ROLE", "MAP_TO_ROLE", or "BOTH_MAP_ROLES".  Does 
		//    "BOTH_MAP_ROLES" mean an 'or' condition of from or to role or does it mean something else?
		
		
//		String searchCodingSchemeName = null;
//		if (codeSystem != null) {
//			searchCodingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
//		}
		
		LexBIGService lexBigService = getLexBigService();
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			// Remove any items in above returned list that are not LexEVS MappingCodeScheme type CodeSchemes 
			csrFilteredList = filterByMappingCodeSchemes(csrFilteredList);
			
			// TODO Will probably need new filtering logic based on source and/or target codingSchemes as noted in the
			//   relations section of the mapping metadata. Part of queryService restrictions?
			if (codeSystemRestriction != null) {
				csrFilteredList = filterByCodeSystemRestriction(csrFilteredList, codeSystemRestriction);
			}
			
//			if (searchCodingSchemeName != null) {
//				csrFilteredList = CommonSearchFilterUtils.filterResourceSummariesByCodingSchemeName(searchCodingSchemeName, csrFilteredList);
//			}
			
			if ((filters != null) && (csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)) {
				Iterator<ResolvedFilter> filtersItr = filters.iterator();
				while (filtersItr.hasNext() && (csrFilteredList.getCodingSchemeRenderingCount() > 0)) {
						ResolvedFilter resolvedFilter = filtersItr.next();
						csrFilteredList = CommonSearchFilterUtils.filterResourceSummariesByResolvedFilter(resolvedFilter, 
								csrFilteredList, nameConverter);
				}
			}
						
			return csrFilteredList.getCodingSchemeRendering();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	protected CodingSchemeRenderingList filterByCodeSystemRestriction(CodingSchemeRenderingList csrFilteredList, 
			CodeSystemRestriction codeSystemRestriction) {

		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		MapRole codeSystemRestrictionMapRole = null;
		Set<NameOrURI> codeSystemSet = null;
		if (codeSystemRestriction != null) {
			codeSystemRestrictionMapRole = codeSystemRestriction.getMapRole();
			codeSystemSet = codeSystemRestriction.getCodeSystems();
		}
		
		String csrMapRoleValue = null;
		if (codeSystemRestrictionMapRole != null) {
			csrMapRoleValue = codeSystemRestrictionMapRole.value();
		}

		// Check codeSystemSet not null and size > 0 and csrMapRoleValue not null
		if (csrMapRoleValue != null && codeSystemSet != null && codeSystemSet.size() > 0) {
			// Get array of CodingSchemeRendering object and loop checking each item in array
			// Call 1 of 3 local methods based on csrMapRoleValue passing codeSystemSet - local method for "BOTH_MAP_ROLES" can leverage
			//    other 2 local methods
			// Add CodingSchemeRendering object to temp list if csrMapRole method(s) found it
		} else {
			return csrFilteredList;  // No valid restrictions to apply
		}
		
		return temp;		
	}
	
	// TODO refactor by moving method to utils class - also used in MapVersionQueryService impl
	private CodingSchemeRenderingList filterByMappingCodeSchemes(CodingSchemeRenderingList csrFilteredList) {
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			
			String uri = codingSchemeSummary.getCodingSchemeURI();
			String version = codingSchemeSummary.getRepresentsVersion();
			
			if (validateMappingCodingScheme(uri, version)) {
				temp.addCodingSchemeRendering(render);
			}
		}		
		return temp;		
	}
	
	
	
	// -------- Implemented methods ----------------
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		return new HashSet<MatchAlgorithmReference>(Arrays.asList(exactMatch,contains,startsWith));
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		
		PropertyReference name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();	
		PropertyReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		
		return new HashSet<PropertyReference>(Arrays.asList(name,about,description));
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}


	@Override
	public DirectoryResult<MapCatalogEntrySummary> getResourceSummaries(
			MapQuery query, SortCriteria sortCriteria, Page page) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DirectoryResult<MapCatalogEntry> getResourceList(MapQuery query,
			SortCriteria sortCriteria, Page page) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int count(MapQuery query) {
		// TODO Auto-generated method stub
		return 0;
	}

}
