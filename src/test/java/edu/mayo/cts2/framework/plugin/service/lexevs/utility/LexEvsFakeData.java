package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.commonTypes.EntityDescription;
import org.easymock.EasyMock;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.CodeSystemVersionQueryImpl;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

import scala.actors.threadpool.Arrays;

public class LexEvsFakeData {	
	final static PropertyReference ABOUT_REF = StandardModelAttributeReference.ABOUT.getPropertyReference();
	final static PropertyReference RESOURCE_SYNOPSIS_REF = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
	final static PropertyReference RESOURCE_NAME_REF = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();
	
	final static MatchAlgorithmReference CONTAINS_REF = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
	final static MatchAlgorithmReference STARTS_WITH_REF = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
	final static MatchAlgorithmReference EXACT_MATCH_REF = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
	
	public enum DataField{
		ABOUT (0, ABOUT_REF),
		RESOURCE_SYNOPSIS (1, RESOURCE_SYNOPSIS_REF),
		RESOURCE_LOCALNAME (2, null),
		RESOURCE_VERSION (3, null),
		RESOURCE_NAME (4, RESOURCE_NAME_REF);
		
		private int index;
		private PropertyReference propertyReference;
		DataField(int index, PropertyReference propertyReference){
			this.index = index;
			this.propertyReference = propertyReference;
		}
		
		public int index(){
			return this.index;
		}
		
		public PropertyReference propertyReference(){
			return this.propertyReference;
		}
	}
	
	private final static String [][] DEFAULT_DATA = {
		{"11.11.0.1", "Auto", "Automobiles", "1.0", ""},
		{"9.0.0.1", "Car", "Vehicles", "1.0", ""},
		{"13.11.0.2", "Auto3", "Automobiles", "1.1", ""},
		{"1.2.3.4", "2Auto", "automobiles", "1.0", ""},
		{"5.6.7.8", "auto", "vehicles", "1.0", ""},
		{"7.6.5.4", "utoA", "hicle", "1.0", ""}
	};
	
	private final static int CODESYSTEM_FIELDCOUNT = DataField.values().length;
	
	private List<String[]> codeSystemList = null;
	
	private int codeSystemCount = 0;
	
	private void initializeDefaultData(){
		for(int i=0; i < DEFAULT_DATA.length; i++){
			DEFAULT_DATA[i][DataField.RESOURCE_NAME.index()] = DEFAULT_DATA[i][DataField.RESOURCE_LOCALNAME.index()];
			DEFAULT_DATA[i][DataField.RESOURCE_NAME.index()] += "-";
			DEFAULT_DATA[i][DataField.RESOURCE_NAME.index()] += DEFAULT_DATA[i][DataField.RESOURCE_VERSION.index()];
		}
	}
	
	public LexEvsFakeData() throws IOException{
		initializeDefaultData();
		codeSystemList = Arrays.asList(DEFAULT_DATA);
		
		this.codeSystemCount = codeSystemList.size();
	}
	
	public LexEvsFakeData(int size){
		initializeDefaultData();
		this.codeSystemCount = (size <= DEFAULT_DATA.length) ? size : DEFAULT_DATA.length;
		codeSystemList = new ArrayList<String[]>();
		for(int i=0; i < this.codeSystemCount; i++){
			codeSystemList.add(new String[CODESYSTEM_FIELDCOUNT]);
			this.setFields(i, DEFAULT_DATA[i]);
		}
	}

	public LexEvsFakeData(String [][] data){
		this.codeSystemCount = data.length;
		codeSystemList = new ArrayList<String[]>();
		for(int i=0; i < this.codeSystemCount; i++){
			codeSystemList.add(new String[CODESYSTEM_FIELDCOUNT]);
			this.setFields(i, data[i]);
		}
	}
	
	
	public int size(){
		return this.codeSystemCount;
	}
	
	private void setFields(int index, String [] values){
		DataField[] fields = DataField.values();
		if(index < this.codeSystemList.size()){
			for(int i=0; i < fields.length; i++){
				this.codeSystemList.get(index)[fields[i].index()] = values[i];
			}
		}
	}
	
	public String getScheme_DataField(int schemeIndex, DataField dataField){
		String results = null;
		if(schemeIndex < this.codeSystemCount){
			results = this.codeSystemList.get(schemeIndex)[dataField.index()];
		}
		return results;
	}

	public String getScheme_DataField(int schemeIndex,
			PropertyReference propertyReference) {
		String results = null;
		if(schemeIndex < this.codeSystemCount){
			int fieldIndex = this.getPropertyReferenceIndex(propertyReference);
			
			results = this.codeSystemList.get(schemeIndex)[fieldIndex];
		}
		return results;
	}

	
	private int getPropertyReferenceIndex(PropertyReference propertyReference) {
		int index = 0;
		DataField [] fields = DataField.values();
		for(int i=0; i < fields.length; i++){
			PropertyReference ref = fields[i].propertyReference();
			if(ref != null){
				if(ref.equals(propertyReference)){
					index = i;
				}
			}
		}
		return index;
	}

	public int getCount(Set<ResolvedFilter> filters) {
		int count = 0;
		String exactMatch = EXACT_MATCH_REF.getContent().toLowerCase();
		String contains = CONTAINS_REF.getContent().toLowerCase();
		String startsWith = STARTS_WITH_REF.getContent().toLowerCase();
		
		for(int schemeIndex=0; schemeIndex < this.codeSystemCount; schemeIndex++){
			boolean found = true;
			Iterator<ResolvedFilter> filterIterator = filters.iterator();
			while(found && filterIterator.hasNext()){
				ResolvedFilter filter = filterIterator.next();
				String matchAlgorithmReferenceName = filter.getMatchAlgorithmReference().getContent().toLowerCase();
				PropertyReference propertyReference = filter.getPropertyReference();
				String matchValue = filter.getMatchValue().toLowerCase();
				
				String dataValue = this.getScheme_DataField(schemeIndex, propertyReference).toLowerCase();
				if(matchAlgorithmReferenceName.equals(exactMatch)){
					if(dataValue.equals(matchValue) == false){
						found = false;
					}
				}
				else if(matchAlgorithmReferenceName.equals(contains)){
					if(dataValue.contains(matchValue) == false){
						found = false;
					}
				}
				else if(matchAlgorithmReferenceName.equals(startsWith)){
					if(dataValue.startsWith(matchValue) == false){
						found = false;
					}
				}
				else{
					found = false;
				}
				
			}
			if(found){
				count++;
			}
		}
		return count;
	}

	public void setProperty(CodingSchemeSummary codingSchemeSummary, int schemeIndex, PropertyReference property) {
		if(property.equals(RESOURCE_SYNOPSIS_REF)){
			EntityDescription codingSchemeDescription = new EntityDescription();
			codingSchemeDescription.setContent(this.getScheme_DataField(schemeIndex, DataField.RESOURCE_SYNOPSIS)); 
			codingSchemeSummary.setCodingSchemeDescription(codingSchemeDescription);
		}		
		else if(property.equals(ABOUT_REF)){
			codingSchemeSummary.setCodingSchemeURI(this.getScheme_DataField(schemeIndex, DataField.ABOUT)); 
		}
		else if(property.equals(RESOURCE_NAME_REF)){
			codingSchemeSummary.setLocalName(this.getScheme_DataField(schemeIndex, DataField.RESOURCE_LOCALNAME));
			codingSchemeSummary.setRepresentsVersion(this.getScheme_DataField(schemeIndex, DataField.RESOURCE_VERSION)); 	
		}
	}	
	
	public enum CodeSystem{
		AUTOMOBILES ("Automobiles");
		
		String name;
		CodeSystem(String name){
			this.name = name;
		}
		
		public String getName(){
			return this.name;
		}
	}
	
	// Create Mocked Service and generate a codingSchemeRenderingList filled with fake data
	// ------------------------------------------------------------------------------------
	public <T extends QueryService> LexBIGService createMockedService_spoofSupportedCodingSchemes(
			T service, 
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

    public <T extends QueryService> void executeCount(
    		T service, int expecting, Set<ResolvedFilter> filters) throws Exception {		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);

		// Test results
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	public <T extends QueryService> void executeCount_CompareCodeSchemes(
			T service,
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

	public <T extends QueryService> void executeCount_WithFilter(
			T service, 
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

	public <T extends QueryService> void executeGetResourceSummaries_MultiplePages(
			T service, 
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
	public <T extends QueryService> void executeGetResourceSummaries_DeepComparison_MatchingAlgorithms(
			T service, 
			LexEvsFakeData fakeData, 
			Page page, int lastPage, DataField dataField) throws Exception {
		// test all available matching algorithms.
		for(MatchAlgorithmReference matchAlgorithmReference : service.getSupportedMatchAlgorithms()){
			// test several pages, which tests all schemes, which tests all substrings....
			executeGetResourceSummaries_DeepComparison_Pages(service, fakeData, page, lastPage, dataField, matchAlgorithmReference);
		}
	}

	// Test Pages->CodingSchemes->Substrings
	public <T extends QueryService> void executeGetResourceSummaries_DeepComparison_Pages(
			T service, 
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
	public <T extends QueryService> void executeGetResourceSummaries_DeepComparison_CodingSchemes(
			T service, 
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

	public <T extends QueryService> void executeGetResourceSummaries_DeepComparison_Substrings(
			T service, 
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
	
	public <T extends QueryService, U, V> void executeGetResourceSummaries(
			T service, 
			DirectoryResult<U> directoryResult,
			ResourceQuery query,
			Page page, int expecting, 
			Set<ResolvedFilter> filters) throws Exception {		
		SortCriteria sortCriteria = null;		
		
		directoryResult = 
				service.getResourceSummaries(query, sortCriteria, page);
		
		assertNotNull(directoryResult);
		
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	

	
}
