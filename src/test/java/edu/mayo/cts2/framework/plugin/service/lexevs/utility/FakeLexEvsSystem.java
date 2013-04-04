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
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsData.DataField;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.TagAwareReadService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
//public class FakeLexEvsSystem <DescriptionTemplate extends ResourceVersionDescription, EntryTemplate extends ResourceVersionDescriptionDirectoryEntry, QueryTemplate extends ResourceQuery, Service extends QueryService<?,?,?>> {
public class FakeLexEvsSystem <DescriptionTemplate, EntryTemplate, QueryTemplate extends ResourceQuery, Service extends QueryService<?,?,?>> {
	boolean PRINT = true;
	FakeLexEvsData fakeData;

	public FakeLexEvsSystem(int size) {
		fakeData = new FakeLexEvsData(size);
	}

	public FakeLexEvsSystem() throws Exception {
		fakeData = new FakeLexEvsData();
	}
	
	public boolean isMappingCodingScheme(String codingSchemeName,
			CodingSchemeVersionOrTag codingSchemeVersionOrTag){
		boolean answer = false;
		String fakeName, fakeVersion, codeVersion;
		for(int schemeIndex=0; schemeIndex < fakeData.size(); schemeIndex++){
			fakeName = fakeData.getScheme_DataField(schemeIndex, DataField.ABOUT).toUpperCase();
			fakeVersion = fakeData.getScheme_DataField(schemeIndex, DataField.RESOURCE_VERSION).toUpperCase();
			codeVersion = codingSchemeVersionOrTag.getVersion().toUpperCase();
			if(fakeName.equals(codingSchemeName.toUpperCase())){
				if(fakeVersion.equals(codeVersion)){
					answer = fakeData.isMapping(schemeIndex);					
				}
			}
		}
		return answer;
	}

	public CodingSchemeRenderingList createFakeCodingSchemeRenderingList(Service service, boolean withData){
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();

		for (int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++) {
			CodingSchemeRendering render = new CodingSchemeRendering();
			CodingSchemeSummary codingSchemeSummary = new CodingSchemeSummary();

			if (withData) {
				for (PropertyReference property : service
						.getSupportedSearchReferences()) {
					fakeData.setProperty(codingSchemeSummary, schemeIndex,
							property);
				}
			}

			render.setCodingSchemeSummary(codingSchemeSummary);
			list.addCodingSchemeRendering(schemeIndex, render);
		}

		return list;
	}
	
	public void createMockedGetNodeSetMethod(LexBIGService lexBigService) throws LBException{
		EasyMock.expect(lexBigService.getNodeSet((String) EasyMock.anyObject(), (CodingSchemeVersionOrTag) EasyMock.anyObject(), (LocalNameList) EasyMock.anyObject())).andAnswer(
		    new IAnswer<CodedNodeSet>() {
		        @Override
		        public CodedNodeSet answer() throws Throwable {		        	
		        	FakeCodedNodeSetImpl codedNodeSet = new FakeCodedNodeSetImpl();
		        	String codingScheme =  (String) EasyMock.getCurrentArguments()[0];
		        	CodingSchemeVersionOrTag version = (CodingSchemeVersionOrTag) EasyMock.getCurrentArguments()[1];

		        	for(int i=0; i < fakeData.size(); i++){
		        		if(fakeData.getScheme_DataField(i, DataField.ABOUT).toUpperCase().equals(codingScheme.toUpperCase()) &&
		        				(fakeData.getScheme_DataField(i, DataField.RESOURCE_VERSION).toUpperCase().equals(version.getVersion().toUpperCase()))){
		        			codedNodeSet.add(new FakeCodedNode(codingScheme, version.getVersion()));
		        		}
		        	}
		        	
		        	return codedNodeSet;
		        }
		    }
		).anyTimes();
	}


	private void createMockedResolveCodingScheme(LexBIGService lexBigService) throws LBException{
		EasyMock.expect(lexBigService.resolveCodingScheme((String) EasyMock.anyObject(), (CodingSchemeVersionOrTag) EasyMock.anyObject())).andAnswer(
			    new IAnswer<CodingScheme>() {
			        @Override
			        public CodingScheme answer() throws Throwable {		        	
			        	String codingSchemeName =  (String) EasyMock.getCurrentArguments()[0];
			        	CodingSchemeVersionOrTag tagOrVersion = (CodingSchemeVersionOrTag) EasyMock.getCurrentArguments()[1];
			        	CodingScheme codingScheme = new CodingScheme();
			        	
			        	//TODO: Need to set the values with correct data
			        	codingScheme.setCodingSchemeName(codingSchemeName);
			        	codingScheme.setRepresentsVersion(tagOrVersion.getVersion());
			        	codingScheme.setCodingSchemeURI("");
			        	codingScheme.setFormalName("");
			        	codingScheme.setLocalName(new String[0]);
			        	return codingScheme;
			        }
			    }
			).anyTimes();
	}

	public void createMockedGetSupportedCodingSchemes(Service service, LexBIGService lexBigService, boolean withData) throws LBException{
		// Mock LexBigService.getSupportedCodingSchemes method
		CodingSchemeRenderingList list = createFakeCodingSchemeRenderingList(service, withData);
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list).anyTimes();					
	}
	
	// Create Mocked Service and generate a codingSchemeRenderingList filled
	// with fake data
	// ------------------------------------------------------------------------------------
	public LexBIGService createMockedLexBIGServiceWithFakeLexEvsData (
			Service service, boolean withData) throws LBException {
		// Mock LexBigService
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		createMockedGetSupportedCodingSchemes(service, lexBigService, withData);
		createMockedGetNodeSetMethod(lexBigService);
		createMockedResolveCodingScheme(lexBigService);
		EasyMock.replay(lexBigService);

		return lexBigService;
	}
	
	public <ReadService extends TagAwareReadService<CodeSystemVersionCatalogEntry, NameOrURI>> LexBIGService createMockedLexBIGServiceWithFakeLexEvsData(
			ReadService service, boolean withData) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

	
	
	
	
	
	public int size() {
		return fakeData.size();
	}

	public Page createPage(int firstPage, int pageSize) {
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		page.setPage(firstPage);
		return page;
	}

	public int calculateExpectingValueForSpecificPage(int resultsCount, Page page) {
		int expecting = 0;
		if (resultsCount >= (page.getMaxToReturn() * (page.getPage() + 1))) {
			if (resultsCount < page.getMaxToReturn()) {
				expecting = resultsCount;
			} else {
				expecting = page.getMaxToReturn();
			}
		} else {
			expecting = resultsCount - (page.getMaxToReturn() * page.getPage());
			expecting = (expecting < 0) ? 0 : expecting;
		}
		return expecting;
	}

	public int calculatePagePastLastPage(int size, int maxToReturn) {
		return ((size / maxToReturn) + 2);
	}

	// -------------------------
	// --- Methods for getCount
	// -------------------------
	@SuppressWarnings("unchecked")
	public void executeCount(Service service,
			QueryTemplate query, 
			int expecting)
			throws Exception {
		// Test results
		QueryService<DescriptionTemplate, EntryTemplate, QueryTemplate> genericService;
		genericService = (QueryService<DescriptionTemplate, EntryTemplate, QueryTemplate>) service;
		int actual = genericService.count(query);

		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	public void executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(
			Service service,
			QueryTemplate query, 
			String codeSystemRestriction,
			boolean aboutValid, boolean resourceSynopsisValid,
			boolean resourceNameValid) throws Exception {
		int schemeCount = fakeData.size();
		int expecting;
		int aboutIndex, synopsisIndex, nameIndex;
		String aboutValue, synopsisValue, nameValue;

		Set<ResolvedFilter> filters;
		for (int schemeIndex = 0; schemeIndex < schemeCount; schemeIndex++) {
			aboutIndex = aboutValid ? schemeIndex
					: ((schemeIndex + 1) % schemeCount);
			synopsisIndex = resourceSynopsisValid ? schemeIndex
					: ((schemeIndex + 1) % schemeCount);
			nameIndex = resourceNameValid ? schemeIndex
					: ((schemeIndex + 1) % schemeCount);

			aboutValue = fakeData.getScheme_DataField(aboutIndex,
					StandardModelAttributeReference.ABOUT
							.getPropertyReference());
			synopsisValue = fakeData.getScheme_DataField(synopsisIndex,
					StandardModelAttributeReference.RESOURCE_SYNOPSIS
							.getPropertyReference());
			nameValue = fakeData.getScheme_DataField(nameIndex,
					StandardModelAttributeReference.RESOURCE_NAME
							.getPropertyReference());

			filters = CommonTestUtils.createFilterSet(aboutValue,
					synopsisValue, nameValue);

			// Enter filters into query
			for (ResolvedFilter filter : filters) {
				query.getFilterComponent().add(filter);
			}
			
			expecting = fakeData.getCount(query.getFilterComponent(), codeSystemRestriction);
			this.executeCount(service, query, expecting);
		}
	}

	public void executeCountForEachExistingCodeSchemeWithSuppliedFilter(
			Service service, 
			QueryTemplate query,
			String codeSystemRestriction,
			DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference, boolean breakFilter)
			throws Exception {

		for (int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++) {
			String testValue = fakeData.getScheme_DataField(schemeIndex,
					dataField);
			if (breakFilter) {
				testValue += "---WRONG DATA---";
			}

			// Enter filters into query
			Set<ResolvedFilter> filters = CommonTestUtils.createFilterSet(
					dataField.propertyReference(), matchAlgorithmReference,
					testValue);
			for (ResolvedFilter filter : filters) {
				query.getFilterComponent().add(filter);
			}

			int expecting = fakeData.getCount(query.getFilterComponent(), codeSystemRestriction);
			executeCount(service, query, expecting);
		}
	}

	// -------------------------------------
	// --- Methods for getResourceSummaries
	// -------------------------------------
	public void executeGetResourceSummariesForEachPage(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage) throws Exception {
		for (int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++) {
			page.setPage(pageIndex);
			
			int tempCount = fakeData.getCount(null, codeSystemRestriction);
			int expecting = calculateExpectingValueForSpecificPage(tempCount, page);

			executeGetResourceSummaries(service, directoryResult, query, page, expecting);
		}
	}

	public void executeGetResourceSummariesWithDeepComparisonForEachPropertyReference(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage) throws Exception {
		this.executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, codeSystemRestriction, page, lastPage,
				DataField.ABOUT);
		this.executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, codeSystemRestriction, page, lastPage,
				DataField.RESOURCE_SYNOPSIS);
		this.executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, codeSystemRestriction, page, lastPage,
				DataField.RESOURCE_NAME);

	}

	// Test MatchingAlgorithms->Pages->CodingSchemes->Substrings
	public void executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage, DataField dataField)
			throws Exception {
		// test all available matching algorithms.
		for (MatchAlgorithmReference matchAlgorithmReference : service
				.getSupportedMatchAlgorithms()) {
			// test several pages, which tests all schemes, which tests all
			// substrings....
			executeGetResourceSummariesWithDeepComparisonForEachPage(service,
					directoryResult, query, codeSystemRestriction, page, lastPage, dataField,
					matchAlgorithmReference);
		}
	}

	// Test Pages->CodingSchemes->Substrings
	public void executeGetResourceSummariesWithDeepComparisonForEachPage(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		// Test multiple pages
		for (int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++) {
			page.setPage(pageIndex);
			// Test across all coding schemes which tests all substrings
			executeGetResourceSummariesWithDeepComparisonForEachCodingScheme(service,
					directoryResult, query, codeSystemRestriction, page, dataField,
					matchAlgorithmReference);
		}
	}

	// Test CodingSchemes->Substrings
	public void executeGetResourceSummariesWithDeepComparisonForEachCodingScheme(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		// Continue test into each codingScheme, testing all substrings
		for (int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++) {
			String testValue = fakeData.getScheme_DataField(schemeIndex,
					dataField);
			executeGetResourceSummariesForEachSubstring(service,
					directoryResult, query, codeSystemRestriction, testValue, page, dataField,
					matchAlgorithmReference);
		}
	}

	public void executeGetResourceSummariesForEachSubstring(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			String testValue, Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {

		// Test all valid substrings
		for (int start = 0; start < testValue.length(); start++) {
			for (int end = start; end < testValue.length(); end++) {
				testValue = testValue.substring(start, end);
				Set<ResolvedFilter> filters = CommonTestUtils.createFilterSet(
						dataField.propertyReference(), matchAlgorithmReference,
						testValue);
				for (ResolvedFilter filter : filters) {
					query.getFilterComponent().add(filter);
				}

				int fakeResults = fakeData.getCount(query.getFilterComponent(), codeSystemRestriction);
				int expecting = calculateExpectingValueForSpecificPage(fakeResults, page);

				executeGetResourceSummaries(service, directoryResult, query, page, expecting);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void executeGetResourceSummaries(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, Page page, int expecting) throws Exception {
		SortCriteria sortCriteria = null;

		QueryService<DescriptionTemplate, EntryTemplate, QueryTemplate> genericService;
		genericService = (QueryService<DescriptionTemplate, EntryTemplate, QueryTemplate>) service;
		
		directoryResult = (DirectoryResult<EntryTemplate>) genericService
				.getResourceSummaries(query, sortCriteria, page);
		
		assertNotNull(directoryResult);

		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual,
				expecting, actual);
	}

	// -------------------------------------
	// --- Methods for getResourceList
	// -------------------------------------
	public void executeGetResourceListForEachPage(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage) throws Exception {
		for (int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++) {
			page.setPage(pageIndex);
			
			int tempCount = fakeData.getCount(null, codeSystemRestriction);
			int expecting = calculateExpectingValueForSpecificPage(tempCount, page);

			executeGetResourceList(service, directoryResult, query, page, expecting);
		}
	}

	public void executeGetResourceListWithDeepComparisonForEachPropertyReference(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage) throws Exception {
		this.executeGetResourceListWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, codeSystemRestriction, page, lastPage,
				DataField.ABOUT);
		this.executeGetResourceListWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, codeSystemRestriction, page, lastPage,
				DataField.RESOURCE_SYNOPSIS);
		this.executeGetResourceListWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, codeSystemRestriction, page, lastPage,
				DataField.RESOURCE_NAME);

	}

	// Test MatchingAlgorithms->Pages->CodingSchemes->Substrings
	public void executeGetResourceListWithDeepComparisonForEachMatchingAlgorithm(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage, DataField dataField)
			throws Exception {
		// test all available matching algorithms.
		for (MatchAlgorithmReference matchAlgorithmReference : service
				.getSupportedMatchAlgorithms()) {
			// test several pages, which tests all schemes, which tests all
			// substrings....
			executeGetResourceListWithDeepComparisonForEachPage(service,
					directoryResult, query, codeSystemRestriction, page, lastPage, dataField,
					matchAlgorithmReference);
		}
	}

	// Test Pages->CodingSchemes->Substrings
	public void executeGetResourceListWithDeepComparisonForEachPage(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, int lastPage, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		// Test multiple pages
		for (int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++) {
			page.setPage(pageIndex);
			// Test across all coding schemes which tests all substrings
			executeGetResourceListWithDeepComparisonForEachCodingScheme(service,
					directoryResult, query, codeSystemRestriction, page, dataField,
					matchAlgorithmReference);
		}
	}

	// Test CodingSchemes->Substrings
	public void executeGetResourceListWithDeepComparisonForEachCodingScheme(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		// Continue test into each codingScheme, testing all substrings
		for (int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++) {
			String testValue = fakeData.getScheme_DataField(schemeIndex,
					dataField);
			executeGetResourceListForEachSubstring(service,
					directoryResult, query, codeSystemRestriction, testValue, page, dataField,
					matchAlgorithmReference);
		}
	}

	public void executeGetResourceListForEachSubstring(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, 
			String codeSystemRestriction,
			String testValue, Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {

		// Test all valid substrings
		for (int start = 0; start < testValue.length(); start++) {
			for (int end = start; end < testValue.length(); end++) {
				testValue = testValue.substring(start, end);
				Set<ResolvedFilter> filters = CommonTestUtils.createFilterSet(
						dataField.propertyReference(), matchAlgorithmReference,
						testValue);
				for (ResolvedFilter filter : filters) {
					query.getFilterComponent().add(filter);
				}

				int fakeResults = fakeData.getCount(query.getFilterComponent(), codeSystemRestriction);
				int expecting = calculateExpectingValueForSpecificPage(fakeResults, page);

				executeGetResourceList(service, directoryResult, query, page, expecting);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void executeGetResourceList(
			Service service, DirectoryResult<DescriptionTemplate> directoryResult,
			QueryTemplate query, Page page, int expecting) throws Exception {
		SortCriteria sortCriteria = null;

		QueryService<DescriptionTemplate, EntryTemplate, QueryTemplate> genericService;
		genericService = (QueryService<DescriptionTemplate, EntryTemplate, QueryTemplate>) service;
		
		directoryResult = (DirectoryResult<DescriptionTemplate>) genericService
				.getResourceList(query, sortCriteria, page);
		
		assertNotNull(directoryResult);

		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual,
				expecting, actual);
	}

}
