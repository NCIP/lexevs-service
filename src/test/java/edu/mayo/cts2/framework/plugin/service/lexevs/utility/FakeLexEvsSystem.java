package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.easymock.EasyMock;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.ResourceVersionDescription;
import edu.mayo.cts2.framework.model.core.ResourceVersionDescriptionDirectoryEntry;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsData.DataField;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public class FakeLexEvsSystem {
	FakeLexEvsData fakeData;
	
	public FakeLexEvsSystem(int size){
		fakeData = new FakeLexEvsData(size);
	}

	public FakeLexEvsSystem() throws Exception{
		fakeData = new FakeLexEvsData();
	}

	// Create Mocked Service and generate a codingSchemeRenderingList filled with fake data
	// ------------------------------------------------------------------------------------
	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<Description, Entry, Query>> LexBIGService createMockedService_spoofSupportedCodingSchemes(
			Service service,
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
	
	public int size(){
		return fakeData.size();
	}
	
	public Page createPage(int firstPage, int pageSize) {
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		page.setPage(firstPage);
		return page;
	}

	public int calculateExpecting_WithPage(int resultsCount, Page page) {
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
	
	public int calculatePagePastLastPage(int size, int maxToReturn) {
		return ((size / maxToReturn) + 2);
	}

	@SuppressWarnings("unchecked")
	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeCount(
   		Service service,
			DirectoryResult<Entry> directoryResult,
			Query query,
    		int expecting) throws Exception {	
		// Test results
		QueryService<ResourceVersionDescription, ResourceVersionDescriptionDirectoryEntry, ResourceQuery> genericService = (QueryService<ResourceVersionDescription, ResourceVersionDescriptionDirectoryEntry, ResourceQuery>) service;
		int actual = genericService.count(query);

		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
    public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeCount_CompareCodeSchemes(
			Service service,
			DirectoryResult<Entry> directoryResult,
			Query query,
	  		boolean aboutValid, 
			boolean resourceSynopsisValid, 
			boolean resourceNameValid) throws Exception{
		int schemeCount = fakeData.size();
		int expecting;
		int aboutIndex, synopsisIndex, nameIndex;
		String aboutValue, synopsisValue, nameValue;
		
		Set<ResolvedFilter> filters;
		for(int schemeIndex=0; schemeIndex < schemeCount; schemeIndex++){
			aboutIndex = aboutValid ? schemeIndex : ((schemeIndex+1) % schemeCount);
			synopsisIndex = resourceSynopsisValid ? schemeIndex : ((schemeIndex+1) % schemeCount);
			nameIndex = resourceNameValid ? schemeIndex : ((schemeIndex+1) % schemeCount);
			
			aboutValue = fakeData.getScheme_DataField(aboutIndex, StandardModelAttributeReference.ABOUT.getPropertyReference());
			synopsisValue = fakeData.getScheme_DataField(synopsisIndex, StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference());
			nameValue = fakeData.getScheme_DataField(nameIndex, StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference());
					
			filters = CommonTestUtils.createFilterSet(aboutValue, synopsisValue, nameValue);

			// Enter filters into query
			for(ResolvedFilter filter : filters){
				query.getFilterComponent().add(filter);
			}

			expecting = fakeData.getCount(query.getFilterComponent());
			this.executeCount(service, directoryResult, query, expecting);
		}
	}

	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeCount_WithFilter(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference,
			boolean breakFilter) throws Exception {
		
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			String testValue = fakeData.getScheme_DataField(schemeIndex,  dataField);
			if(breakFilter){
				testValue += "---WRONG DATA---";
			}
			
			// Enter filters into query
			Set<ResolvedFilter> filters = CommonTestUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);
			for(ResolvedFilter filter : filters){
				query.getFilterComponent().add(filter);
			}

			int expecting = fakeData.getCount(query.getFilterComponent());
			executeCount(service, directoryResult, query, expecting);
		}
	}

	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries_MultiplePages(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			Page page, int lastPage) throws Exception {
		for(int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++){
			page.setPage(pageIndex);
			int expecting = calculateExpecting_WithPage(fakeData.size(), page);
			
			executeGetResourceSummaries(service, directoryResult, query, page, expecting);
		}		
	}

	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries_DeepComparison_PropertyReferences(
			Service service,
			DirectoryResult<Entry> directoryResult,
			Query query,
			Page page, int lastPage) throws Exception {
		this.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(service, directoryResult, query, page, lastPage, DataField.ABOUT);		
		this.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(service, directoryResult, query, page, lastPage, DataField.RESOURCE_SYNOPSIS);		
		this.executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(service, directoryResult, query, page, lastPage, DataField.RESOURCE_NAME);		

	}


	
	
	// Test MatchingAlgorithms->Pages->CodingSchemes->Substrings
	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			Page page, int lastPage, DataField dataField) throws Exception {
		// test all available matching algorithms.
		for(MatchAlgorithmReference matchAlgorithmReference : service.getSupportedMatchAlgorithms()){
			// test several pages, which tests all schemes, which tests all substrings....
			executeGetResourceSummaries_DeepComparison_Pages(service, directoryResult, query, page, lastPage, dataField, matchAlgorithmReference);
		}
	}

	// Test Pages->CodingSchemes->Substrings
	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries_DeepComparison_Pages(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			Page page, int lastPage, 
			DataField dataField, 
			MatchAlgorithmReference matchAlgorithmReference) throws Exception{
		// Test multiple pages
		for(int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++){
			page.setPage(pageIndex);
			// Test across all coding schemes which tests all substrings
			executeGetResourceSummaries_DeepComparison_CodingSchemes(service, directoryResult, query, page, dataField, matchAlgorithmReference);
		}
	}
	
	// Test CodingSchemes->Substrings
	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries_DeepComparison_CodingSchemes(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			Page page, 
			DataField dataField, 
			MatchAlgorithmReference matchAlgorithmReference) throws Exception{
		// Continue test into each codingScheme, testing all substrings
		for(int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++){
			String testValue = fakeData.getScheme_DataField(schemeIndex, dataField);
			executeGetResourceSummaries_DeepComparison_Substrings(service, directoryResult, query, testValue, page, dataField, matchAlgorithmReference);				
		}
	}

	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries_DeepComparison_Substrings(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			String testValue, Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference)
			throws Exception {
				
		// Test all valid substrings
		for (int start = 0; start < testValue.length(); start++) {
			for (int end = start; end < testValue.length(); end++) {
				testValue = testValue.substring(start, end);
				Set<ResolvedFilter> filters = CommonTestUtils.createFilterSet(dataField.propertyReference(), matchAlgorithmReference, testValue);
				for(ResolvedFilter filter : filters){
					query.getFilterComponent().add(filter);
				}

				int fakeResults = fakeData.getCount(query.getFilterComponent());
				int expecting = calculateExpecting_WithPage(fakeResults, page);
				
				executeGetResourceSummaries(service, directoryResult, query, page, expecting);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <Description extends ResourceVersionDescription, Entry extends ResourceVersionDescriptionDirectoryEntry , Query extends ResourceQuery, Service extends QueryService<?, ?, ?>> void executeGetResourceSummaries(
			Service service, 
			DirectoryResult<Entry> directoryResult,
			Query query,
			Page page, int expecting) throws Exception {		
		SortCriteria sortCriteria = null;		
		
		QueryService<ResourceVersionDescription, ResourceVersionDescriptionDirectoryEntry, ResourceQuery> genericService = (QueryService<ResourceVersionDescription, ResourceVersionDescriptionDirectoryEntry, ResourceQuery>) service;
		directoryResult = (DirectoryResult<Entry>) genericService.getResourceSummaries(query, sortCriteria, page);
//		directoryResult = service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
}
