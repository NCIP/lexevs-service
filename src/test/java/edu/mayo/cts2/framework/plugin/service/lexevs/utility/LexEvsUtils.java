package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.easymock.EasyMock;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.CodeSystemVersionQueryImpl;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.CodingSchemeToCodeSystemTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.LexEvsCodeSystemVersionQueryService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData.DataField;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.Cts2Profile;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;

public class LexEvsUtils {

	public static enum CodeSystem{
		AUTOMOBILES ("Automobiles");
		
		String name;
		CodeSystem(String name){
			this.name = name;
		}
		
		public String getName(){
			return this.name;
		}
	}
	
	// FILTER METHODS
	public static ResolvedFilter createFilter(PropertyReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setPropertyReference(property);
		
		return filter;
	}

	public static Set<ResolvedFilter> createFilterSet(PropertyReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setPropertyReference(property);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(filter));
		
		return filterSet;
	}
	
	public static Set<ResolvedFilter> createFilterSet(String about_contains, String resourceSynopsis_startsWith, String resourceName_exactMatch){
		ResolvedFilter aboutFilter = createFilter(
				StandardModelAttributeReference.ABOUT.getPropertyReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(), about_contains);

		ResolvedFilter synopsisFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_SYNOPSIS
						.getPropertyReference(),
				StandardMatchAlgorithmReference.STARTS_WITH
						.getMatchAlgorithmReference(), resourceSynopsis_startsWith);

		ResolvedFilter nameFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_NAME
						.getPropertyReference(),
				StandardMatchAlgorithmReference.EXACT_MATCH
						.getMatchAlgorithmReference(), resourceName_exactMatch);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(aboutFilter, synopsisFilter, nameFilter));
		
		return filterSet;
	}
	
	public static Set<ResolvedFilter> createFilterSet(LexEvsFakeData fakeData, int aboutIndex, int synopsisIndex,
			int nameIndex) {
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		
		PropertyReference propertyReference = StandardModelAttributeReference.ABOUT.getPropertyReference();
		MatchAlgorithmReference matchAlgorithmReference = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		String matchValue = fakeData.getScheme_DataField(aboutIndex, propertyReference);
		ResolvedFilter filter1 = LexEvsUtils.createFilter(propertyReference, matchAlgorithmReference, matchValue);
					
		filters.add(filter1);
		
		propertyReference = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		matchAlgorithmReference = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
		matchValue = fakeData.getScheme_DataField(synopsisIndex, propertyReference);
		ResolvedFilter filter2 = LexEvsUtils.createFilter(propertyReference, matchAlgorithmReference, matchValue);
					
		filters.add(filter2);
		
		propertyReference = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();
		matchAlgorithmReference = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		matchValue = fakeData.getScheme_DataField(nameIndex, propertyReference);
		ResolvedFilter filter3 = LexEvsUtils.createFilter(propertyReference, matchAlgorithmReference, matchValue);
					
		filters.add(filter3);
		
		return filters;
	}
	
	
	// Setup mocked environment
	// -------------------------
	public static LexEvsCodeSystemVersionQueryService createService(
			LexEvsFakeData fakeData, 
			boolean withData) throws Exception{
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = createMockedService_spoofSupportedCodingSchemes(service, fakeData, withData);
		
		service.setLexBigService(lexBigService);

		// Overwrite objects in service object 
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		
		return service;
	}

	// Create Mocked Service and generate a codingSchemeRenderingList filled with fake data
	// ------------------------------------------------------------------------------------
	public static LexBIGService createMockedService_spoofSupportedCodingSchemes(
			LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 
			boolean withData) throws Exception{
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
				
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			CodingSchemeRendering render = new CodingSchemeRendering();
			CodingSchemeSummary codingSchemeSummary = new CodingSchemeSummary();
			
			if(withData){	
				for(PropertyReference property : service.getSupportedSearchReferences()){
					fakeData.setProperty(codingSchemeSummary, schemeIndex, property);
				}
			}
			
			render.setCodingSchemeSummary(codingSchemeSummary);
			list.addCodingSchemeRendering(schemeIndex, render);
		}
		
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list).anyTimes();
		EasyMock.replay(lexBigService);
		
		return lexBigService;
	}
	
	
	public static Page createPage(int firstPage, int pageSize) {
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		page.setPage(firstPage);
		return page;
	}

	public static int calculateExpecting_WithPage(int resultsCount, Page page) {
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
	
	public static int calculatePagePastLastPage(int size, int maxToReturn) {
		return ((size / maxToReturn) + 2);
	}

    public static void executeCount(LexEvsCodeSystemVersionQueryService service, int expecting, Set<ResolvedFilter> filters) throws Exception {		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);

		// Test results
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	public static void executeCount_CompareCodeSchemes(
			LexEvsCodeSystemVersionQueryService service,
			LexEvsFakeData fakeData, 
			boolean aboutValid, boolean resourceSynopsisValid, boolean resourceNameValid) throws Exception{
		int schemeCount = fakeData.size();
		int expecting;
		int aboutIndex, synopsisIndex, nameIndex;
		
		Set<ResolvedFilter> filters;
		for(int schemeIndex=0; schemeIndex < schemeCount; schemeIndex++){
			aboutIndex = aboutValid ? schemeIndex : ((schemeIndex+1) % schemeCount);
			synopsisIndex = resourceSynopsisValid ? schemeIndex : ((schemeIndex+1) % schemeCount);
			nameIndex = resourceNameValid ? schemeIndex : ((schemeIndex+1) % schemeCount);
			
			filters = LexEvsUtils.createFilterSet(fakeData, aboutIndex, synopsisIndex, nameIndex);
		
			expecting = fakeData.getCount(filters);
			executeCount(service, expecting, filters);
		}
	}

	public static void executeCount_WithFilter(LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 
			DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference,
			boolean breakFilter) throws Exception {
		
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			String testValue = fakeData.getScheme_DataField(schemeIndex,  dataField);
			if(breakFilter){
				testValue += "---WRONG DATA---";
			}
			Set<ResolvedFilter> filter = LexEvsUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);
			int expecting = fakeData.getCount(filter);
			
			executeCount(service, expecting, filter);
		}
	}

	public static void executeGetResourceSummaries_MultiplePages(
			LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 			
			Page page, int lastPage) throws Exception {
		for(int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++){
			page.setPage(pageIndex);
			int expecting = calculateExpecting_WithPage(fakeData.size(), page);
			
			DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
			CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, null);
			executeGetResourceSummaries(service, directoryResult, query, page, expecting, null);
		}		
	}

	// Test MatchingAlgorithms->Pages->CodingSchemes->Substrings
	public static void executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(
			LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 
			Page page, int lastPage, DataField dataField) throws Exception {
		// test all available matching algorithms.
		for(MatchAlgorithmReference matchAlgorithmReference : service.getSupportedMatchAlgorithms()){
			// test several pages, which tests all schemes, which tests all substrings....
			executeGetResourceSummaries_DeepComparison_Pages(service, fakeData, page, lastPage, dataField, matchAlgorithmReference);
		}
	}

	// Test Pages->CodingSchemes->Substrings
	public static void executeGetResourceSummaries_DeepComparison_Pages(
			LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 
			Page page, int lastPage, 
			DataField dataField, 
			MatchAlgorithmReference matchAlgorithmReference) throws Exception{
		// Test multiple pages
		for(int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++){
			page.setPage(pageIndex);
			// Test across all coding schemes which tests all substrings
			executeGetResourceSummaries_DeepComparison_CodingSchemes(service, fakeData, page, dataField, matchAlgorithmReference);
		}
	}
	
	// Test CodingSchemes->Substrings
	public static void executeGetResourceSummaries_DeepComparison_CodingSchemes(
			LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 
			Page page, 
			DataField dataField, 
			MatchAlgorithmReference matchAlgorithmReference) throws Exception{
		// Continue test into each codingScheme, testing all substrings
		for(int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++){
			String testValue = fakeData.getScheme_DataField(schemeIndex, dataField);
			executeGetResourceSummaries_DeepComparison_Substrings(service, fakeData, testValue, page, dataField, matchAlgorithmReference);				
		}
	}

	public static void executeGetResourceSummaries_DeepComparison_Substrings(
			LexEvsCodeSystemVersionQueryService service, 
			LexEvsFakeData fakeData, 
			String testValue, Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference)
			throws Exception {
		
		// Test all valid substrings
		for (int start = 0; start < testValue.length(); start++) {
			for (int end = start; end < testValue.length(); end++) {
				testValue = testValue.substring(start, end);
				Set<ResolvedFilter> filter = LexEvsUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);

				int fakeResults = fakeData.getCount(filter);
				int expecting = calculateExpecting_WithPage(fakeResults, page);
				
				DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = null; 
				CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);
				executeGetResourceSummaries(service, directoryResult, query, page, expecting, filter);
			}
		}
	}
	
	public static <T extends QueryService, U, V> void executeGetResourceSummaries(
			T service, 
			DirectoryResult<U> directoryResult,
			ResourceQuery query,
			Page page, int expecting, 
			Set<ResolvedFilter> filters) throws Exception {		
		SortCriteria sortCriteria = null;		
		
		// Build query using filters
//		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
		//query = new V(null, filters, null, null);
		
//		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
//				((LexEvsCodeSystemVersionQueryService) service).getResourceSummaries(query, sortCriteria, page);
		directoryResult = 
				service.getResourceSummaries(query, sortCriteria, page);
		
		assertNotNull(directoryResult);
		
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
}
