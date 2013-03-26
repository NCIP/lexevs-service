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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.HashSet;
import java.util.Set;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.ResourceVersionDescription;
import edu.mayo.cts2.framework.model.core.ResourceVersionDescriptionDirectoryEntry;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData.DataField;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.profile.Cts2Profile;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;


/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsCodeSystemVersionQueryServiceTest {
	// Setup mocked environment
	// -------------------------
	public LexEvsCodeSystemVersionQueryService createService(
			LexEvsFakeData fakeData, 
			boolean withData) throws Exception{
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = fakeData.createMockedService_spoofSupportedCodingSchemes(service, withData);
		
		service.setLexBigService(lexBigService);

		// Overwrite objects in service object 
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		
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
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;
		
//		QueryService<ResourceVersionDescription, ResourceVersionDescriptionDirectoryEntry, ResourceQuery> genericService; // = (QueryService<ResourceVersionDescription, ResourceVersionDescriptionDirectoryEntry, ResourceQuery>) service;
		
		fakeData.executeCount_WithFilter(service, directoryResult, query, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), testValidData);		
		fakeData.executeCount_WithFilter(service, directoryResult, query, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), !testValidData);		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_StartsWith() throws Exception {
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeData.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), testValidData);
		fakeData.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), !testValidData);
	}
		
	@Test
	public void testCount_Filter_ResourceName_ExactMatch() throws Exception {
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		boolean testValidData = true;
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeData.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_NAME, 
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), testValidData);		
		fakeData.executeCount_WithFilter(service, directoryResult, query, DataField.RESOURCE_NAME, 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), !testValidData);		
	}
		
	// Count with All VALID Default filters
	// -------------------------------------
	@Test
	public void testCount_Filter_AllDefault() throws Exception {
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 

		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeData.executeCount_CompareCodeSchemes(service, directoryResult, query, true, true, true);		
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_Filter_AllDefault_WrongIndex_About() throws Exception {
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeData.executeCount_CompareCodeSchemes(service, directoryResult, query, false, true, true);
	}
	
	@Test
	public void testCount_Filter_AllDefault_WrongIndex_ResourceSynopsis() throws Exception {
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeData.executeCount_CompareCodeSchemes(service, directoryResult, query, true, false, true);
	}

	@Test
	public void testCount_Filter_AllDefault_WrongIndex_ResourceName() throws Exception {
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null;

		fakeData.executeCount_CompareCodeSchemes(service, directoryResult, query, true, true, false);
	}

	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_FilterNone_3Summaries_Size50() throws Exception {
		int schemeCount = 3;
		LexEvsFakeData fakeData = new LexEvsFakeData(schemeCount);		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		
		Page page = new Page();
		int lastPage = fakeData.calculatePagePastLastPage(fakeData.size(), page.getMaxToReturn());
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 

		fakeData.executeGetResourceSummaries_MultiplePages(service, directoryResult, query, page, lastPage);		
	}
	
	@Test
	public void testGetResourceSummaries_FilterNone_20Summaries_Size10() throws Exception {
		int schemeCount = 20;
		LexEvsFakeData fakeData = new LexEvsFakeData(schemeCount);		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		
		int firstPage = 0;
		int pageSize = 10;
		Page page = fakeData.createPage(firstPage, pageSize);
		int lastPage = fakeData.calculatePagePastLastPage(fakeData.size(), page.getMaxToReturn());
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 

		fakeData.executeGetResourceSummaries_MultiplePages(service, directoryResult, query, page, lastPage);		
	}

	@Test
	public void testGetResourceSummaries_FilterNone_21Summaries_Size10() throws Exception {
		int schemeCount = 21;		
		LexEvsFakeData fakeData = new LexEvsFakeData(schemeCount);		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true); 
		
		int firstPage = 1;
		int pageSize = 10;
		Page page = fakeData.createPage(firstPage, pageSize);		
		int lastPage = fakeData.calculatePagePastLastPage(fakeData.size(), page.getMaxToReturn());
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
		
		fakeData.executeGetResourceSummaries_MultiplePages(service, directoryResult, query, page, lastPage);		
	}
	
	// -----------------------------------------
	// resourceSummaries, deep comparisons, with individual filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_DeepCompare_About_PageSize50() throws Exception {
		Page page = new Page();		
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true);
		
		// Test one page past possible pages to ensure 0 is returned.
		int lastPage = fakeData.calculatePagePastLastPage(fakeData.size(), page.getMaxToReturn());

		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
		
		fakeData.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(service, directoryResult, query, page, lastPage, DataField.ABOUT);		
	}

	@Test
	public void testGetResourceSummaries_DeepCompare_ResourceSynopsis_PageSize50() throws Exception {
		Page page = new Page();		
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true);
		
		int lastPage = fakeData.calculatePagePastLastPage(fakeData.size(), page.getMaxToReturn());
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
		
		fakeData.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(service, directoryResult, query, page, lastPage, DataField.RESOURCE_SYNOPSIS);		

//		fakeData.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(((QueryService<CodeSystemVersionCatalogEntry, CodeSystemVersionCatalogEntrySummary, CodeSystemVersionQueryImpl>) service), page, lastPage, DataField.RESOURCE_SYNOPSIS);		
	}
	
	@Test
	public void testGetResourceSummaries_DeepCompare_ResourceName_PageSize50() throws Exception {
		Page page = new Page();		
		LexEvsFakeData fakeData = new LexEvsFakeData();		
		LexEvsCodeSystemVersionQueryService service = this.createService(fakeData, true);
		
		int lastPage = fakeData.calculatePagePastLastPage(fakeData.size(), page.getMaxToReturn());

		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
		
		fakeData.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(service, directoryResult, query, page, lastPage, DataField.RESOURCE_NAME);		
	}	
}
