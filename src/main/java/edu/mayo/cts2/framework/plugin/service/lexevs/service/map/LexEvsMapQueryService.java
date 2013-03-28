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
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.relations.Relations;
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
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
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
		
		CodeSystemRestriction codeSystemRestriction = null;
		ValueSetRestriction valueSetRestriction = null;  // TODO Do we need to check this restriction type???
		if (mapQueryServiceRestrictions != null) {
			codeSystemRestriction = query.getRestrictions().getCodeSystemRestriction();  
			valueSetRestriction = query.getRestrictions().getValueSetRestriction();
		}
		
		
		LexBIGService lexBigService = getLexBigService();
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			// Remove any items in above returned list that are not LexEVS MappingCodeScheme type CodeSchemes 
			csrFilteredList = filterByMappingCodeSchemes(csrFilteredList);
			
			// Filter items based on the CodingScheme Relations sourceCodingScheme and/or targetCodingScheme string values
			if (codeSystemRestriction != null) {
				csrFilteredList = filterByCodeSystemRestriction(csrFilteredList, codeSystemRestriction);
			}
			
			// TODO *** Need to look into filtering and check if below logic is valid ***
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
		
		if (csrMapRoleValue != null && codeSystemSet != null && codeSystemSet.size() > 0) {
			// Get array of CodingSchemeRendering object and loop checking each item in array
			CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
			for (CodingSchemeRendering render : csRendering) {
				if (isFoundViaCodeSystemRestriction(render, codeSystemSet, csrMapRoleValue)) {
					temp.addCodingSchemeRendering(render);
				}
			}
		} else {
			return csrFilteredList;  // No restrictions to apply
		}
		
		return temp;		
	}
	
	protected boolean isFoundViaCodeSystemRestriction(CodingSchemeRendering render, 
			Set<NameOrURI> codeSystemSet, 
			String csrMapRoleValue) {

		boolean returnFlag = false;
		CodingScheme codingScheme = getCodingScheme(render);
		
		// Assuming format of Map has only has 1 relations section/1 relations element in xml file
		if (codingScheme.getRelationsCount() != 1) {
			throw new RuntimeException("Invalid format for Map. Expecting only one metadata section for Relations.");
		}
		Relations relations = codingScheme.getRelations(0);
		String sourceCodingScheme = relations.getSourceCodingScheme();
		String targetCodingScheme = relations.getTargetCodingScheme();
		
		if (csrMapRoleValue.equals(Constants.MAP_TO_ROLE)) {
			return isCodingSchemeFound(sourceCodingScheme, codeSystemSet);
		}
		
		if (csrMapRoleValue.equals(Constants.MAP_FROM_ROLE)) { 
			return isCodingSchemeFound(targetCodingScheme, codeSystemSet);
		}
		
		if (csrMapRoleValue.equals(Constants.BOTH_MAP_ROLES)) {
			if (isCodingSchemeFound(sourceCodingScheme, targetCodingScheme, codeSystemSet)) {
				return true;
			}
		}
		
		return returnFlag;
	}
	
	protected boolean isCodingSchemeFound(String relationCodingScheme, Set<NameOrURI> codeSystemSet) {

		boolean returnFlag = false;
		Iterator<NameOrURI> iterator = codeSystemSet.iterator();
		while (iterator.hasNext() && returnFlag == false) {
			NameOrURI nameOrURI = iterator.next();
			if (nameOrURI.getName() != null && nameOrURI.getName().equals(relationCodingScheme)) {
				returnFlag = true;
			}
			if (nameOrURI.getUri() != null && nameOrURI.getUri().equals(relationCodingScheme)) {
				returnFlag = true;
			}
		}
		return returnFlag;
	}
	
	
	protected boolean isCodingSchemeFound(String srcCodingScheme, String targetCodingScheme, Set<NameOrURI> codeSystemSet) {

		boolean returnFlag = false;
		Iterator<NameOrURI> iterator = codeSystemSet.iterator();
		while (iterator.hasNext() && returnFlag == false) {
			NameOrURI nameOrURI = iterator.next();
			if (nameOrURI.getName() != null && (nameOrURI.getName().equals(srcCodingScheme) || 
					nameOrURI.getName().equals(targetCodingScheme))) {
				returnFlag = true;
			}
			if (nameOrURI.getUri() != null && (nameOrURI.getUri().equals(srcCodingScheme) || 
					nameOrURI.getUri().equals(targetCodingScheme))) {
				returnFlag = true;
			}
		}
		return returnFlag;
	}
	
	protected CodingScheme getCodingScheme(CodingSchemeRendering render) {
		String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
		String version = render.getCodingSchemeSummary().getRepresentsVersion();
		CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
		CodingScheme codingScheme;
		try {
			codingScheme = this.getLexBigService().resolveCodingScheme(codingSchemeName, tagOrVersion);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		return codingScheme;
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
