/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

import java.util.HashSet;
import java.util.Set;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsData.DataField;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsSystem;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.MappingExtensionImpl;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;

public class LexEvsMapQueryServiceTest {

	// Setup mocked environment
	// -------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LexEvsMapQueryService createService(
			FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs, 
			boolean withData) throws Exception{
		LexEvsMapQueryService service = new LexEvsMapQueryService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = fakeLexEvs.createMockedLexBIGServiceWithFakeLexEvsData(service, withData);
		
		service.setLexBigService(lexBigService);

		CodingSchemeToMapTransform transform = EasyMock.createNiceMock(CodingSchemeToMapTransform.class);
		EasyMock.replay(transform);

		// Overwrite objects in service object 
		service.setCodingSchemeToMapTransform(transform);
		service.setCodeSystemVersionNameConverter(new VersionNameConverter(new CodingSchemeNameTranslator(){

			@Override
			public String translateFromLexGrid(String name) {
				return name;
			}

			@Override
			public String translateToLexGrid(String name) {
				return name;
			}

			@Override
			public String translateLexGridURIToLexGrid(String uri) {
				return uri;
			}
	
		}));
		service.setMappingExtension(new MappingExtensionImpl(fakeLexEvs));
		
		return service;
	}
	
	final static String CODE_SYSTEM_NAME = null;
	

	// =============
	// Test methods
	// =============
	
	// Count with VALID and INVALID filters
	// ------------------------------------
	@Test
	public void testCount_Filter_About_Contains() throws Exception {
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapQueryImpl query = new MapQueryImpl(null, filters, null, null);
				
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, CODE_SYSTEM_NAME, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), testValidData);		
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, CODE_SYSTEM_NAME, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), !testValidData);		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_StartsWith() throws Exception {
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapQueryImpl query = new MapQueryImpl(null, filters, null, null);

		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, CODE_SYSTEM_NAME, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), testValidData);
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, CODE_SYSTEM_NAME, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), !testValidData);
	}
		
	@Test
	public void testCount_Filter_ResourceName_ExactMatch() throws Exception {
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapQueryImpl query = new MapQueryImpl(null, filters, null, null);

		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, CODE_SYSTEM_NAME, DataField.RESOURCE_NAME, 
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), testValidData);		
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, CODE_SYSTEM_NAME, DataField.RESOURCE_NAME, 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), !testValidData);		
	}
		
	// Count with All VALID Default filters
	// -------------------------------------
	@Test
	public void testCount_FilterDefault_ComponentReferencesValidIndex_AllSchemes() throws Exception {
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service = this.createService(fakeLexEvs, true); 

		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapQueryImpl query = new MapQueryImpl(null, filters, null, null);

		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, CODE_SYSTEM_NAME, true, true, true);		
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_FilterDefault_ComponentReferencesWrongIndex_AllSchemes() throws Exception {
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service = this.createService(fakeLexEvs, true); 
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapQueryImpl query = new MapQueryImpl(null, filters, null, null);

		// About wrong index
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, CODE_SYSTEM_NAME, false, true, true);
		// ResourceSynopsis wrong index
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, CODE_SYSTEM_NAME, true, false, true);
		// ResourceName wrong index
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, CODE_SYSTEM_NAME, true, true, false);
	}
	

	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_NoFilter_SchemeCountsFrom1to21_PageSizesFrom1to50_Pages() throws Exception {
		int maxSchemeCount = 21;
		int maxPageSize = 50;
		
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service; 
		
		MapQueryImpl query;
		DirectoryResult<MapCatalogEntrySummary> directoryResult; 
		
		Page page = new Page();
		int lastPage;
		
		for(int schemeCount=1; schemeCount <= maxSchemeCount; schemeCount++){
			fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>(schemeCount);		
			service = this.createService(fakeLexEvs, true); 
			for(int pageSize=1; pageSize <= maxPageSize; pageSize++){
				page.setMaxToReturn(pageSize);
				lastPage = fakeLexEvs.calculatePagePastLastPage(fakeLexEvs.size(), page.getMaxToReturn());
				
				query = new MapQueryImpl(null, null, null, null);
				directoryResult = null; 
				
				fakeLexEvs.executeGetResourceSummariesForEachPage(service, directoryResult, query, CODE_SYSTEM_NAME, page, lastPage);		
			}
		}
	}
	
	@Test
	@Ignore
	public void testGetResourceSummaries_DeepCompare_ComponentReferences_MatchingAlgorithms_Pages_CodindgSchemes_Substrings() throws Exception {
		Page page = new Page();		
		FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<MapCatalogEntry, MapCatalogEntrySummary, MapQuery, LexEvsMapQueryService>();
		LexEvsMapQueryService service = this.createService(fakeLexEvs, true);
		
		// Test one page past possible pages to ensure 0 is returned.
		int lastPage = fakeLexEvs.calculatePagePastLastPage(fakeLexEvs.size(), page.getMaxToReturn());

		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapQueryImpl query = new MapQueryImpl(null, filters, null, null);
		DirectoryResult<MapCatalogEntrySummary> directoryResult = null; 
		
		fakeLexEvs.executeGetResourceSummariesWithDeepComparisonForEachComponentReference(service, directoryResult, query, CODE_SYSTEM_NAME, page, lastPage);		
	}
}
