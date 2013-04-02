package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsData.DataField;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

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

	// Create Mocked Service and generate a codingSchemeRenderingList filled
	// with fake data
	// ------------------------------------------------------------------------------------
	public LexBIGService createMockedLexBIGServiceWithFakeLexEvsData(
			Service service, boolean withData) throws Exception {
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
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

		EasyMock.expect(lexBigService.getSupportedCodingSchemes())
				.andReturn(list).anyTimes();
			
		
		final Capture<String> captureCodingScheme = new Capture<String>();
		final Capture<CodingSchemeVersionOrTag> captureVersion = new Capture<CodingSchemeVersionOrTag>();
		final Capture<LocalNameList> captureNameList = new Capture<LocalNameList>();
		EasyMock.expect(lexBigService.getNodeSet(EasyMock.capture(captureCodingScheme), 
				EasyMock.capture(captureVersion), 
				EasyMock.capture(captureNameList))).andAnswer(
//		EasyMock.expect(lexBigService.getNodeSet(EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject())).andAnswer(
		    new IAnswer<CodedNodeSet>() {
		        @Override
		        public CodedNodeSet answer() throws Throwable {
//		            return myCapture.getValue();
		        	FakeLexEvsCodedNodeSetImpl nodeSet = new FakeLexEvsCodedNodeSetImpl();
		        	String codingScheme = captureCodingScheme.getValue(); // (String) EasyMock.getCurrentArguments()[0];
		        	CodingSchemeVersionOrTag version = captureVersion.getValue(); // (CodingSchemeVersionOrTag) EasyMock.getCurrentArguments()[1];

        			nodeSet.restrictToMatchingDesignations(codingScheme, SearchDesignationOption.ALL, null, null);
        			nodeSet.restrictToMatchingDesignations(version.getVersion(), SearchDesignationOption.ALL, null, null);
		        	return nodeSet;
		        }
		    }
		);

		EasyMock.replay(lexBigService);

		return lexBigService;
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

	@SuppressWarnings("unchecked")
	public void executeCount(Service service,
			QueryTemplate query, int expecting)
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
			QueryTemplate query, boolean aboutValid, boolean resourceSynopsisValid,
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

			expecting = fakeData.getCount(query.getFilterComponent());
			this.executeCount(service, query, expecting);
		}
	}

	public void executeCountForEachExistingCodeSchemeWithSuppliedFilter(
			Service service, 
			QueryTemplate query, DataField dataField,
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

			int expecting = fakeData.getCount(query.getFilterComponent());
			executeCount(service, query, expecting);
		}
	}

	public void executeGetResourceSummariesForEachPage(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, Page page, int lastPage) throws Exception {
		for (int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++) {
			page.setPage(pageIndex);
			int expecting = calculateExpectingValueForSpecificPage(fakeData.size(), page);

			executeGetResourceSummaries(service, directoryResult, query, page,
					expecting);
		}
	}

	public void executeGetResourceSummariesWithDeepComparisonForEachPropertyReference(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, Page page, int lastPage) throws Exception {
		this.executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, page, lastPage,
				DataField.ABOUT);
		this.executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, page, lastPage,
				DataField.RESOURCE_SYNOPSIS);
		this.executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
				service, directoryResult, query, page, lastPage,
				DataField.RESOURCE_NAME);

	}

	// Test MatchingAlgorithms->Pages->CodingSchemes->Substrings
	public void executeGetResourceSummariesWithDeepComparisonForEachMatchingAlgorithm(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, Page page, int lastPage, DataField dataField)
			throws Exception {
		// test all available matching algorithms.
		for (MatchAlgorithmReference matchAlgorithmReference : service
				.getSupportedMatchAlgorithms()) {
			// test several pages, which tests all schemes, which tests all
			// substrings....
			executeGetResourceSummariesWithDeepComparisonForEachPage(service,
					directoryResult, query, page, lastPage, dataField,
					matchAlgorithmReference);
		}
	}

	// Test Pages->CodingSchemes->Substrings
	public void executeGetResourceSummariesWithDeepComparisonForEachPage(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, Page page, int lastPage, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		// Test multiple pages
		for (int pageIndex = page.getPage(); pageIndex <= lastPage; pageIndex++) {
			page.setPage(pageIndex);
			// Test across all coding schemes which tests all substrings
			executeGetResourceSummariesWithDeepComparisonForEachCodingScheme(service,
					directoryResult, query, page, dataField,
					matchAlgorithmReference);
		}
	}

	// Test CodingSchemes->Substrings
	public void executeGetResourceSummariesWithDeepComparisonForEachCodingScheme(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, Page page, DataField dataField,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		// Continue test into each codingScheme, testing all substrings
		for (int schemeIndex = 0; schemeIndex < fakeData.size(); schemeIndex++) {
			String testValue = fakeData.getScheme_DataField(schemeIndex,
					dataField);
			executeGetResourceSummariesForEachSubstring(service,
					directoryResult, query, testValue, page, dataField,
					matchAlgorithmReference);
		}
	}

	public void executeGetResourceSummariesForEachSubstring(
			Service service, DirectoryResult<EntryTemplate> directoryResult,
			QueryTemplate query, String testValue, Page page, DataField dataField,
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

				int fakeResults = fakeData.getCount(query.getFilterComponent());
				int expecting = calculateExpectingValueForSpecificPage(fakeResults, page);

				executeGetResourceSummaries(service, directoryResult, query,
						page, expecting);
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
}
