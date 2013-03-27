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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQueryService;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsMapVersionQueryService extends AbstractLexEvsService
		implements MapVersionQueryService, InitializingBean {

	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeToMapVersionTransform codingSchemeToMapVersionTransform;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";	
	
	public void setCodeSystemVersionNameConverter(CodeSystemVersionNameConverter converter){
		this.nameConverter = converter;
	}
	public void setCodingSchemeToMapVersionTransform(CodingSchemeToMapVersionTransform transformer){
			this.codingSchemeToMapVersionTransform = transformer;
	}


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
			MapVersionQuery query, SortCriteria sortCriteria) {

		Set<ResolvedFilter> filters = null; 
		MapVersionQueryServiceRestrictions mapVersionQueryServiceRestrictions = null;
		
		if (query != null) {
			mapVersionQueryServiceRestrictions = query.getRestrictions();
			filters = query.getFilterComponent();
		}		
		
		NameOrURI codeSystem = null;
		if (mapVersionQueryServiceRestrictions != null) {
			codeSystem = query.getRestrictions().getMap();  
		}
		
		String searchCodingSchemeName = null;
		if (codeSystem != null) {
			searchCodingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
		}
		
		LexBIGService lexBigService = getLexBigService();
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			// Remove any items in above returned list that are not LexEVS MappingCodeScheme type CodeSchemes 
			csrFilteredList = filterByMappingCodeSchemes(csrFilteredList);
			
			if (searchCodingSchemeName != null) {
				csrFilteredList = CommonSearchFilterUtils.filterResourceSummariesByCodingSchemeName(searchCodingSchemeName, csrFilteredList);
			}
			
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
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#count(edu.mayo.cts2.framework.service.profile.ResourceQuery)
	 */
	@Override
	public int count(MapVersionQuery query) {
		return this.doGetResourceSummaries(query, null).length;
	}

	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceSummaries(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<MapVersionDirectoryEntry> getResourceSummaries(
			MapVersionQuery query, SortCriteria sortCriteria, Page page) {

		CodingSchemeRendering[] csRendering = this.doGetResourceSummaries(query, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = CommonUtils.getRenderingPage(csRendering, page);

		List<MapVersionDirectoryEntry> list = new ArrayList<MapVersionDirectoryEntry>();

		if(csRenderingPage != null){
			if(csRenderingPage.length > 0){
				for (CodingSchemeRendering render : csRenderingPage) {
					list.add(codingSchemeToMapVersionTransform.transform(render));
				}
			}
		}
		
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		
		return new DirectoryResult<MapVersionDirectoryEntry>(list, atEnd);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceList(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<MapVersion> getResourceList(MapVersionQuery query,
			SortCriteria sortCriteria, Page page) {

		CodingSchemeRendering[] csRendering = this.doGetResourceSummaries(query, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = CommonUtils.getRenderingPage(csRendering, page);
		
		List<MapVersion> list = new ArrayList<MapVersion>();

		for (CodingSchemeRendering render : csRenderingPage) {
			String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
			String version = render.getCodingSchemeSummary().getRepresentsVersion();
			CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
			CodingScheme codingScheme;
			try {
				codingScheme = this.getLexBigService().resolveCodingScheme(codingSchemeName, tagOrVersion);
				list.add(codingSchemeToMapVersionTransform.transform(codingScheme));
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}

		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		
		return new DirectoryResult<MapVersion>(list, atEnd);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedMatchAlgorithms()
	 */
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		return new HashSet<MatchAlgorithmReference>(Arrays.asList(exactMatch,contains,startsWith));
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedSearchReferences()
	 */
	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		
		PropertyReference name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();	
		PropertyReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		
		return new HashSet<PropertyReference>(Arrays.asList(name,about,description));
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedSortReferences()
	 */
	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getKnownProperties()
	 */
	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQueryService#mapVersionEntities(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.service.mapversion.types.MapRole, edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus, edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<EntityDirectoryEntry> mapVersionEntities(
			NameOrURI mapVersion, MapRole mapRole, MapStatus mapStatus,
			EntityDescriptionQuery query, SortCriteria sort, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQueryService#mapVersionEntityList(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.service.mapversion.types.MapRole, edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus, edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<EntityDescription> mapVersionEntityList(
			NameOrURI mapVersion, MapRole mapRole, MapStatus mapStatus,
			EntityDescriptionQuery query, SortCriteria sort, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQueryService#mapVersionEntityReferences(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.service.mapversion.types.MapRole, edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus, edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public EntityReferenceList mapVersionEntityReferences(NameOrURI mapVersion,
			MapRole mapRole, MapStatus mapStatus, EntityDescriptionQuery query,
			SortCriteria sort, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

}
