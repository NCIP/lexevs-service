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
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonGetResourceSummaries;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
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
	CodingSchemeToMapTransform codingSchemeToMapTransform;
	
	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";	

	// ------ Local methods ----------------------
	public CodingSchemeToMapTransform getCodingSchemeToMapTransform() {
		return codingSchemeToMapTransform;
	}

	public void setCodingSchemeToMapTransform(
			CodingSchemeToMapTransform codingSchemeToMapTransform) {
		this.codingSchemeToMapTransform = codingSchemeToMapTransform;
	}

	public CodeSystemVersionNameConverter getCodeSystemVersionNameConverter(){
		return this.nameConverter;
	}
	
	public void setCodeSystemVersionNameConverter(CodeSystemVersionNameConverter converter){
		this.nameConverter = converter;
	}

	// -------- Implemented methods ----------------
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}
	
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
		
		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		codingSchemeList = CommonGetResourceSummaries.getCodingSchemeList(lexBigService, this.getCodeSystemVersionNameConverter(), mappingExtension, queryData, sortCriteria);
		CodingScheme[] codingSchemeArray = codingSchemeList.toArray(new CodingScheme[0]);
		CodingScheme[] codingSchemePage = (CodingScheme[]) CommonUtils.getRenderingPage(codingSchemeArray, page);
		
		List<MapCatalogEntrySummary> list = new ArrayList<MapCatalogEntrySummary>();

		for (CodingScheme codingScheme : codingSchemePage) {
			list.add(codingSchemeToMapTransform.transformToMapCatalogEntrySummary(codingScheme));
		}

		boolean atEnd = (page.getEnd() >= codingSchemeArray.length) ? true : false;
		
		return new DirectoryResult<MapCatalogEntrySummary>(list, atEnd);
	}


	@Override
	public DirectoryResult<MapCatalogEntry> getResourceList(MapQuery query,
			SortCriteria sortCriteria, Page page) {

		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		codingSchemeList = CommonGetResourceSummaries.getCodingSchemeList(lexBigService, this.getCodeSystemVersionNameConverter(), mappingExtension, queryData, sortCriteria);
		CodingScheme[] codingSchemeArray = codingSchemeList.toArray(new CodingScheme[0]);
		CodingScheme[] codingSchemePage = (CodingScheme[]) CommonUtils.getRenderingPage(codingSchemeArray, page);
		
		List<MapCatalogEntry> list = new ArrayList<MapCatalogEntry>();

		for (CodingScheme codingScheme : codingSchemePage) {
			list.add(codingSchemeToMapTransform.transformToMapCatalogEntry(codingScheme));
		}

		boolean atEnd = (page.getEnd() >= codingSchemeArray.length) ? true : false;
		
		return new DirectoryResult<MapCatalogEntry>(list, atEnd);
	}


	@Override
	public int count(MapQuery query) {
		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		codingSchemeList = CommonGetResourceSummaries.getCodingSchemeList(lexBigService, this.getCodeSystemVersionNameConverter(), mappingExtension, queryData, null);
		return codingSchemeList.size();
	}

}
