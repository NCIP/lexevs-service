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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.map.MapCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
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
		implements MapQueryService {
	
	@Resource
	CodingSchemeToMapTransform transformer;
	
	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private MappingExtension mappingExtension;
	
	// ------ Local methods ----------------------
	public void setCodingSchemeToMapTransform(CodingSchemeToMapTransform transformer) {
		this.transformer = transformer;
	}

	public void setCodeSystemVersionNameConverter(VersionNameConverter converter){
		this.nameConverter = converter;
	}

	public void setMappingExtension(MappingExtension extension) {
		this.mappingExtension = extension;
	}

	// -------- Implemented methods ----------------	
	@Override
	public DirectoryResult<MapCatalogEntrySummary> getResourceSummaries(
			MapQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		return getResourceSummaries(query, sortCriteria, page, null);
	}

	@Override
	public DirectoryResult<MapCatalogEntrySummary> getResourceSummaries(
			MapQuery query, 
			SortCriteria sortCriteria, 
			Page page,
			String uri) {
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query, null);
		List<CodingScheme> codingSchemeList;
		
		codingSchemeList = CommonResourceUtils.getLexCodingSchemeList(lexBigService, this.nameConverter, mappingExtension, queryData, sortCriteria);
		CodingScheme[] codingSchemePage = (CodingScheme[]) CommonPageUtils.getPage(codingSchemeList.toArray(new CodingScheme[0]), page);
		
		boolean atEnd = (page.getEnd() >= codingSchemeList.size()) ? true : false;
			
		return CommonResourceUtils.createDirectoryResultsWithSummary(this.transformer, codingSchemePage, atEnd);		
	}


	@Override
	public DirectoryResult<MapCatalogEntryListEntry> getResourceList(MapQuery query,
			SortCriteria sortCriteria, Page page) {

		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query, null);
		codingSchemeList = CommonResourceUtils.getLexCodingSchemeList(lexBigService, this.nameConverter, mappingExtension, queryData, sortCriteria);		
		CodingScheme[] codingSchemePage = (CodingScheme[]) CommonPageUtils.getPage(codingSchemeList.toArray(new CodingScheme[0]), page);
		
		boolean atEnd = (page.getEnd() >= codingSchemeList.size()) ? true : false;
		
		return CommonResourceUtils.createDirectoryResultsWithList(transformer, codingSchemePage, atEnd);
	}


	@Override
	public int count(MapQuery query) {
		LexBIGService lexBigService = this.getLexBigService();
		List<CodingScheme> codingSchemeList;
		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query, null);
		codingSchemeList = CommonResourceUtils.getLexCodingSchemeList(lexBigService, this.nameConverter, mappingExtension, queryData, null);
		return codingSchemeList.size();
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.getLexSupportedSearchReferences();
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return new HashSet<ComponentReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}
}
