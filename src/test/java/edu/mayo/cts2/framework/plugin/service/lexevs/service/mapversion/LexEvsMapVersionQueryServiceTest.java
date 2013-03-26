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

import java.util.HashSet;
import java.util.Set;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsData.DataField;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsSystem;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;


/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsMapVersionQueryServiceTest {

	// Setup mocked environment
	// -------------------------
	public LexEvsMapVersionQueryService createService(
			FakeLexEvsSystem fakeLexEvs, 
			boolean withData) throws Exception{
		LexEvsMapVersionQueryService service = new LexEvsMapVersionQueryService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = fakeLexEvs.createMockedService_spoofSupportedCodingSchemes(service, withData);
		
		// Overwrite objects in service object 
		service.setLexBigService(lexBigService);
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		service.setCodingSchemeToMapVersionTransform(new CodingSchemeToMapVersionTransform());
		
		return service;
	}
	
//	QueryService<CodeSystemVersionCatalogEntry, 
//	CodeSystemVersionCatalogEntrySummary, 
//	CodeSystemVersionQuery>, Cts2Profile {

	// =============
	// Test methods
	// =============
	
	// Count with VALID and INVALID filters
	// ------------------------------------
	@Test
	public void testCount_Filter_About_Contains() throws Exception {
		FakeLexEvsSystem fakeLexEvs = new FakeLexEvsSystem();
		LexEvsMapVersionQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapVersionQueryImpl query = new MapVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;
				
		fakeLexEvs.executeCount_WithFilter(service, directoryResult, query, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), testValidData);		
		fakeLexEvs.executeCount_WithFilter(service, directoryResult, query, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), !testValidData);		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_StartsWith() throws Exception {
		FakeLexEvsSystem fakeLexEvs = new FakeLexEvsSystem();
		LexEvsMapVersionQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapVersionQueryImpl query = new MapVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeLexEvs.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), testValidData);
		fakeLexEvs.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), !testValidData);
	}
		
	@Test
	public void testCount_Filter_ResourceName_ExactMatch() throws Exception {
		FakeLexEvsSystem fakeLexEvs = new FakeLexEvsSystem();
		LexEvsMapVersionQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapVersionQueryImpl query = new MapVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeLexEvs.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_NAME, 
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), testValidData);		
		fakeLexEvs.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_NAME, 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), !testValidData);		
	}
		
	// Count with All VALID Default filters
	// -------------------------------------
	@Test
	public void testCount_FilterDefault_PropertyReferencesValidIndex_AllSchemes() throws Exception {
		FakeLexEvsSystem fakeLexEvs = new FakeLexEvsSystem();
		LexEvsMapVersionQueryService service = this.createService(fakeLexEvs, true); 

		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapVersionQueryImpl query = new MapVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeLexEvs.executeCount_CompareCodeSchemes(service, directoryResult, query, true, true, true);		
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_FilterDefault_PropertyReferencesWrongIndex_AllSchemes() throws Exception {
		FakeLexEvsSystem fakeLexEvs = new FakeLexEvsSystem();
		LexEvsMapVersionQueryService service = this.createService(fakeLexEvs, true); 
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapVersionQueryImpl query = new MapVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		// About wrong index
		fakeLexEvs.executeCount_CompareCodeSchemes(service, directoryResult, query, false, true, true);
		// ResourceSynopsis wrong index
		fakeLexEvs.executeCount_CompareCodeSchemes(service, directoryResult, query, true, false, true);
		// ResourceName wrong index
		fakeLexEvs.executeCount_CompareCodeSchemes(service, directoryResult, query, true, true, false);
	}
	

	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_NoFilter_SchemeCountsFrom1to21_PageSizesFrom1to50_Pages() throws Exception {
		int maxSchemeCount = 21;
		int maxPageSize = 50;
		
		FakeLexEvsSystem fakeLexEvs;
		LexEvsMapVersionQueryService service;
		
		MapVersionQueryImpl query;
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult; 
		
		Page page = new Page();
		int lastPage;
		
		for(int schemeCount=1; schemeCount <= maxSchemeCount; schemeCount++){
			fakeLexEvs = new FakeLexEvsSystem(schemeCount);		
			service = this.createService(fakeLexEvs, true); 
			for(int pageSize=1; pageSize <= maxPageSize; pageSize++){
				page.setMaxToReturn(pageSize);
				lastPage = fakeLexEvs.calculatePagePastLastPage(fakeLexEvs.size(), page.getMaxToReturn());
				
				query = new MapVersionQueryImpl(null, null, null, null);
				directoryResult = null; 
				
				fakeLexEvs.executeGetResourceSummaries_MultiplePages(service, directoryResult, query, page, lastPage);		
			}
		}
	}
	@Test
	public void testGetResourceSummaries_DeepCompare_PropertyReferences_MatchingAlgorithms_Pages_CodindgSchemes_Substrings() throws Exception {
		Page page = new Page();		
		FakeLexEvsSystem fakeLexEvs = new FakeLexEvsSystem();
		LexEvsMapVersionQueryService service = this.createService(fakeLexEvs, true);
		
		// Test one page past possible pages to ensure 0 is returned.
		int lastPage = fakeLexEvs.calculatePagePastLastPage(fakeLexEvs.size(), page.getMaxToReturn());

		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		MapVersionQueryImpl query = new MapVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
		
		fakeLexEvs.executeGetResourceSummaries_DeepComparison_PropertyReferences(service, directoryResult, query, page, lastPage);		
	}
}
