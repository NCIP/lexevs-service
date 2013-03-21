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

import static org.junit.Assert.*;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.commonTypes.EntityDescription;
import org.easymock.EasyMock;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData.DataField;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsUtils;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;


/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsCodeSystemVersionQueryServiceTest {

	LexEvsFakeData fakeData = null;
	
	// Setup mocked environment
	// -------------------------
	private LexEvsCodeSystemVersionQueryService createService(boolean withData) throws Exception{
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMockedService_spoofSupportedCodingSchemes(withData);
		
		service.setLexBigService(lexBigService);

		// Overwrite objects in service object 
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		
		return service;
	}

	// Create Mocked Service and generate a codingSchemeRenderingList filled with fake data
	// ------------------------------------------------------------------------------------
	private LexBIGService createMockedService_spoofSupportedCodingSchemes(boolean withData) throws Exception{
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
		
				
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			CodingSchemeRendering render = new CodingSchemeRendering();
			CodingSchemeSummary codingSchemeSummary = new CodingSchemeSummary();
			
			if(withData){				
				// Synopsis
				EntityDescription codingSchemeDescription = new EntityDescription();
				codingSchemeDescription.setContent(fakeData.getScheme_DataField(schemeIndex, DataField.RESOURCE_SYNOPSIS)); 
				codingSchemeSummary.setCodingSchemeDescription(codingSchemeDescription);
				
				
				// About
				codingSchemeSummary.setCodingSchemeURI(fakeData.getScheme_DataField(schemeIndex, DataField.ABOUT)); 
				
				
				// resource name
				codingSchemeSummary.setLocalName(fakeData.getScheme_DataField(schemeIndex, DataField.RESOURCE_LOCALNAME));
				codingSchemeSummary.setRepresentsVersion(fakeData.getScheme_DataField(schemeIndex, DataField.RESOURCE_VERSION)); 	
			}
			
			render.setCodingSchemeSummary(codingSchemeSummary);
			list.addCodingSchemeRendering(schemeIndex, render);
		}
		
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list);
		EasyMock.replay(lexBigService);
		
		return lexBigService;
	}
	
    private void executeCount(int expecting, Set<ResolvedFilter> filters) throws Exception {		
		
		LexEvsCodeSystemVersionQueryService service = this.createService(true);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);

		// Test results
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	private int calculateExpecting_WithPage(int resultsCount, Page page) {
		int expecting = 0;
		if(resultsCount >= (page.getMaxToReturn() * (page.getPage() + 1))){
			if(resultsCount < page.getMaxToReturn()){
				expecting = resultsCount;
			}
			else{
				expecting = page.getMaxToReturn();
			}
		}
		else{
			expecting = resultsCount - (page.getMaxToReturn() * page.getPage());
			expecting = (expecting < 0) ? 0 : expecting;
		}
		
		return expecting;
	}
	
	private void executeCount_WithFilter_Valid(DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			String testValue = fakeData.getScheme_DataField(schemeIndex,  dataField);
			Set<ResolvedFilter> filter = LexEvsUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);
			int expecting = fakeData.getCount(filter);
			
			this.executeCount(expecting, filter);
		}
	}

	private void executeCount_WithFilter_InValid(DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			String testValue = fakeData.getScheme_DataField(schemeIndex, dataField) + "FOO";
			Set<ResolvedFilter> filter = LexEvsUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);
			int expecting = fakeData.getCount(filter);
	
			this.executeCount(expecting, filter);
		}
	}


	
	// =============
	// Test methods
	// =============
	
	// Count with VALID and INVALID filters
	// ------------------------------------
	@Test
	public void testCount_Filter_About_Contains() throws Exception {
		fakeData = new LexEvsFakeData();
		executeCount_WithFilter_Valid(DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference());		
		executeCount_WithFilter_InValid(DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference());		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_StartsWith() throws Exception {
		fakeData = new LexEvsFakeData();
		executeCount_WithFilter_Valid(DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference());
		executeCount_WithFilter_InValid(DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference());
	}
		
	@Test
	public void testCount_Filter_ResourceName_ExactMatch() throws Exception {
		fakeData = new LexEvsFakeData();
		executeCount_WithFilter_Valid(DataField.RESOURCE_NAME, 
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());		
		executeCount_WithFilter_InValid(DataField.RESOURCE_NAME, 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());		
	}
		
	// Count with All VALID Default filters
	// -------------------------------------
	@Test
	public void testCount_Filter_AllDefault() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		Set<ResolvedFilter> filters;
		int expecting;
		
		for(int schemeIndex=0; schemeIndex < schemeCount; schemeIndex++){
			filters = LexEvsUtils.createFilterSet(fakeData, schemeIndex, schemeIndex, schemeIndex);
		
			expecting = fakeData.getCount(filters);
			this.executeCount(expecting, filters);
		}
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_Filter_AllDefault_WrongIndex_About() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		Set<ResolvedFilter> filters;
		int expecting;
		
		for(int schemeIndex=0; schemeIndex < schemeCount; schemeIndex++){
			filters = LexEvsUtils.createFilterSet(fakeData, ((schemeIndex+1) % schemeCount), schemeIndex, schemeIndex);
		
			expecting = fakeData.getCount(filters);
			this.executeCount(expecting, filters);
		}
	}
	
	@Test
	public void testCount_Filter_AllDefault_WrongIndex_ResourceSynopsis() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		Set<ResolvedFilter> filters;
		int expecting;
		
		for(int schemeIndex=0; schemeIndex < schemeCount; schemeIndex++){
			filters = LexEvsUtils.createFilterSet(fakeData, schemeIndex, ((schemeIndex+1) % schemeCount), schemeIndex);
			
			expecting = fakeData.getCount(filters);
			this.executeCount(expecting, filters);
		}
	}

	@Test
	public void testCount_Filter_AllDefault_WrongIndex_ResourceName() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		Set<ResolvedFilter> filters;
		int expecting;
		
		for(int schemeIndex=0; schemeIndex < schemeCount; schemeIndex++){
			filters = LexEvsUtils.createFilterSet(fakeData, schemeIndex, schemeIndex, ((schemeIndex+1) % schemeCount));
			
			expecting = fakeData.getCount(filters);
			this.executeCount(expecting, filters);
		}
	}

	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_FilterNone_3Summaries_Page0to2_Size50() throws Exception {
		int schemeCount = 3;
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		
		fakeData = new LexEvsFakeData(schemeCount);
		schemeCount = fakeData.size();
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			page.setPage(pageIndex);
			int expecting = calculateExpecting_WithPage(schemeCount, page);
			this.executeGetResourceSummaries(page, expecting, null);
		}
	}
	
	@Test
	public void testGetResourceSummaries_FilterNone_20Summaries_Page0to3_Size10() throws Exception {
		int schemeCount = 20;
		int firstPage = 0;
		int lastPage = 3;
		int pageSize = 10;
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		
		fakeData = new LexEvsFakeData(schemeCount);
		schemeCount = fakeData.size();
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			page.setPage(pageIndex);
			int expecting = calculateExpecting_WithPage(schemeCount, page);
			this.executeGetResourceSummaries(page, expecting, null);
		}
	}

	@Test
	public void testGetResourceSummaries_FilterNone_21Summaries_Page1to3_Size10() throws Exception {
		int schemeCount = 21;
		int firstPage = 1;
		int lastPage = 3;
		int pageSize = 10;
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		
		fakeData = new LexEvsFakeData(schemeCount);
		schemeCount = fakeData.size();
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){			
			page.setPage(pageIndex);
			int expecting = calculateExpecting_WithPage(schemeCount, page);
			this.executeGetResourceSummaries(page, expecting, null);
		}
	}
	
	
	// -----------------------------------------
	// resourceSummaries with individual filters
	// -----------------------------------------
	private void executeGetResourceSummaries_Pages_1Filter(int pageSize, int firstPage, int lastPage, DataField dataField, MatchAlgorithmReference matchAlgorithmReference) throws Exception{
		Page page = new Page();
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			page.setMaxToReturn(pageSize);
			page.setPage(pageIndex);
			for(int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++){
				String testValue = fakeData.getScheme_DataField(schemeIndex, dataField);
				executeGetResourceSummaries_Substrings_1Filter(testValue, page, dataField, matchAlgorithmReference);				
			}
		}
	}

	private void executeGetResourceSummaries_Substrings_1Filter(
			String testValue, Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference)
			throws Exception {
		for (int start = 0; start < testValue.length(); start++) {
			for (int end = start; end < testValue.length(); end++) {
				testValue = testValue.substring(start, end);
				Set<ResolvedFilter> filter = LexEvsUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);

				int fakeResults = fakeData.getCount(filter);
				int expecting = calculateExpecting_WithPage(fakeResults, page);
				
				this.executeGetResourceSummaries(page, expecting, filter);
			}
		}
	}
	
	private void executeGetResourceSummaries(Page page, int expecting, Set<ResolvedFilter> filters) throws Exception {		
		LexEvsCodeSystemVersionQueryService service = this.createService(true);
				
		SortCriteria sortCriteria = null;		
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				service.getResourceSummaries(query, sortCriteria, page);
		
		assertNotNull(directoryResult);
		
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummaries_FilterSubstring_About_Page0to2_Size50() throws Exception {
		fakeData = new LexEvsFakeData();		
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		
		DataField dataField = DataField.ABOUT;
		
		MatchAlgorithmReference matchAlgorithmReference;
		matchAlgorithmReference = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
		
		matchAlgorithmReference = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
		
		matchAlgorithmReference = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
	}
	
	@Test
	public void testGetResourceSummaries_FilterSubstring_ResourceSynopsis_Page0to2_Size50() throws Exception {
		fakeData = new LexEvsFakeData();		
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		
		DataField dataField = DataField.RESOURCE_SYNOPSIS;
		
		MatchAlgorithmReference matchAlgorithmReference;
		matchAlgorithmReference = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
		
		matchAlgorithmReference = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
		
		matchAlgorithmReference = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
	}
	
	@Test
	public void testGetResourceSummaries_FilterSubstring_ResourceName_Page0to2_Size50() throws Exception {
		fakeData = new LexEvsFakeData();		
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		
		DataField dataField = DataField.RESOURCE_NAME;
		
		MatchAlgorithmReference matchAlgorithmReference;
		matchAlgorithmReference = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
		
		matchAlgorithmReference = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
		
		matchAlgorithmReference = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		executeGetResourceSummaries_Pages_1Filter(pageSize, firstPage, lastPage, dataField, matchAlgorithmReference);
	}	
}
