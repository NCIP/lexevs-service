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
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceSummaryUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;
import edu.mayo.cts2.framework.service.profile.map.MapQueryService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapQueryService extends AbstractLexEvsService
		implements MapQueryService, InitializingBean {
	
	@Resource
	CodingSchemeToMapTransform transformer;
	
	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";	

	// ------ Local methods ----------------------
	public void setCodingSchemeToMapTransform(
			CodingSchemeToMapTransform codingSchemeToMapTransform) {
		this.transformer = codingSchemeToMapTransform;
	}

	public void setCodeSystemVersionNameConverter(CodeSystemVersionNameConverter converter){
		this.nameConverter = converter;
	}

	public MappingExtension getMappingExtension() {
		return mappingExtension;
	}

	public void setMappingExtension(MappingExtension mappingExtension) {
		this.mappingExtension = mappingExtension;
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
		return CommonSearchFilterUtils.createSupportedMatchAlgorithms();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.createSupportedSearchReferences();
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
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		List<CodingScheme> codingSchemeList;
		
		codingSchemeList = CommonResourceSummaryUtils.getCodingSchemeList(lexBigService, this.nameConverter, mappingExtension, queryData, sortCriteria);
		CodingScheme[] codingSchemePage = (CodingScheme[]) CommonUtils.getRenderingPage(codingSchemeList, page);
		
		boolean atEnd = (page.getEnd() >= codingSchemeList.size()) ? true : false;
			
		return CommonResourceSummaryUtils.createDirectoryResultWithEntrySummaryData(lexBigService, transformer, codingSchemePage, atEnd);		
	}


	@Override
	public DirectoryResult<MapCatalogEntry> getResourceList(MapQuery query,
			SortCriteria sortCriteria, Page page) {

		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		codingSchemeList = CommonResourceSummaryUtils.getCodingSchemeList(lexBigService, this.nameConverter, mappingExtension, queryData, sortCriteria);		
		CodingScheme[] codingSchemePage = (CodingScheme[]) CommonUtils.getRenderingPage(codingSchemeList, page);
		
		boolean atEnd = (page.getEnd() >= codingSchemeList.size()) ? true : false;
		
		return CommonResourceSummaryUtils.createDirectoryResultWithEntryData(lexBigService, transformer, codingSchemePage, atEnd);
	}


	@Override
	public int count(MapQuery query) {
		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		codingSchemeList = CommonResourceSummaryUtils.getCodingSchemeList(lexBigService, this.nameConverter, mappingExtension, queryData, null);
		return codingSchemeList.size();
	}

}
