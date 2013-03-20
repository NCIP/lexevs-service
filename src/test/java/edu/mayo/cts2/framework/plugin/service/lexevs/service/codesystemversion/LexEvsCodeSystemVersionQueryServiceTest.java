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
import org.LexGrid.LexBIG.DataModel.Core.ReferenceLink;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.commonTypes.EntityDescription;
import org.easymock.EasyMock;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData.DataFields;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;


/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsCodeSystemVersionQueryServiceTest {

	LexEvsFakeData fakeData = null;
	
	// Setup mocked environment
	// -------------------------
	private LexEvsCodeSystemVersionQueryService createService(int schemeCount, boolean withData) throws Exception{
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMockedService_spoofSupportedCodingSchemes(schemeCount, withData);
		
		service.setLexBigService(lexBigService);

		// Overwrite objects in service object 
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		
		return service;
	}

	// Create Mocked Service and generate a codingSchemeRenderingList filled with fake data
	// ------------------------------------------------------------------------------------
	private LexBIGService createMockedService_spoofSupportedCodingSchemes(int size, boolean withData) throws Exception{
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
		
				
		for(int i=0; i < size; i++){
			CodingSchemeRendering render = new CodingSchemeRendering();
			CodingSchemeSummary codingSchemeSummary = new CodingSchemeSummary();
			
			if(withData){				
				// Synopsis
				EntityDescription codingSchemeDescription = new EntityDescription();
				codingSchemeDescription.setContent(fakeData.get(DataFields.RESOURCE_SYNOPSIS, i)); 
				codingSchemeSummary.setCodingSchemeDescription(codingSchemeDescription);
				
				
				// About
				codingSchemeSummary.setCodingSchemeURI(fakeData.get(DataFields.ABOUT, i)); 
				
				
				// resource name
				codingSchemeSummary.setLocalName(fakeData.get(DataFields.RESOURCE_LOCALNAME, i));
				codingSchemeSummary.setRepresentsVersion(fakeData.get(DataFields.RESOURCE_VERSION, i)); 	
			}
			
			render.setCodingSchemeSummary(codingSchemeSummary);
			list.addCodingSchemeRendering(i, render);
		}
		
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list);
		EasyMock.replay(lexBigService);
		
		return lexBigService;
	}
	
	private void executeGetResourceSummaries_0_Filter(int schemeCount,  int pageSize, int pageIndex, int expecting) throws Exception{
		LexEvsCodeSystemVersionQueryService service = this.createService(schemeCount, false);
				
		SortCriteria sortCriteria = null;		
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		page.setPage(pageIndex);
		
		CodeSystemVersionQuery query = null;
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				service.getResourceSummaries(query, sortCriteria, page);
		
		assertNotNull(directoryResult);
		
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
	private void executeGetResourceSummaries_1_Filter(int schemeCount,  int pageSize, int pageIndex, int expecting, 
			PropertyReference propertyReference, MatchAlgorithmReference matchAlgorithmReference, String matchValue) throws Exception{
		LexEvsCodeSystemVersionQueryService service = this.createService(schemeCount, true);
				
		SortCriteria sortCriteria = null;		
		Page page = new Page();
		page.setMaxToReturn(pageSize);
		page.setPage(pageIndex);
		
		// Build query using filters
		Set<ResolvedFilter> filter = TestUtils.createFilterSet(propertyReference, matchAlgorithmReference, matchValue);
		CodeSystemVersionQueryImpl query = TestUtils.createQuery_FiltersOnly(filter);
		
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				service.getResourceSummaries(query, sortCriteria, page);
		
		assertNotNull(directoryResult);
		
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
	private void executeCount_3_Filters(int aboutIndex, int synopsisIndex, int nameIndex, int schemeCount, int expecting) throws Exception{
		LexEvsCodeSystemVersionQueryService service = this.createService(schemeCount, true);
		
		// Build query using filters
		String about = fakeData.get(DataFields.ABOUT, aboutIndex); 
		String resourceSynopsis = fakeData.get(DataFields.RESOURCE_SYNOPSIS, synopsisIndex); 
		String resourceName = fakeData.get(DataFields.RESOURCE_NAME, nameIndex); 
		
		Set<ResolvedFilter> filterComponent = TestUtils.createFilterSet(about, resourceSynopsis, resourceName);
		CodeSystemVersionQueryImpl query = TestUtils.createQuery_FiltersOnly(filterComponent);

		// Test results
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	private void executeCount_1_Filter(int schemeCount, String testValue, int expecting, 
			PropertyReference propertyReference, 
			MatchAlgorithmReference matchAlgorithmReference) throws Exception{
		
		LexEvsCodeSystemVersionQueryService service = this.createService(schemeCount, true);
		
		// Build query using filters
		Set<ResolvedFilter> filter = TestUtils.createFilterSet(propertyReference, matchAlgorithmReference, testValue); 
		CodeSystemVersionQueryImpl query = TestUtils.createQuery_FiltersOnly(filter);

		// Test results
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	private int calculateExpecting_WithPage(int schemeCount, int pageSize, int pageIndex) {
		int expecting = 0;
		if(schemeCount >= (pageSize * (pageIndex + 1))){
			if(schemeCount < pageSize){
				expecting = schemeCount;
			}
			else{
				expecting = pageSize;
			}
		}
		else{
			expecting = schemeCount - (pageSize * pageIndex);
			expecting = (expecting < 0) ? 0 : expecting;
		}
		
		return expecting;
	}
	
	private void performCountFilterTests_Valid(int schemeCount, DataFields field,
			PropertyReference propertyReference,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		
		for(int i=0; i < schemeCount; i++){
			String testValue = fakeData.get(field, i);
			int expecting = fakeData.getCount(field, matchAlgorithmReference, testValue);
	
			this.executeCount_1_Filter(schemeCount, testValue, expecting, propertyReference, matchAlgorithmReference);
		}
	}

	private void performCountFilterTests_InValid(int schemeCount, DataFields field,
			PropertyReference propertyReference,
			MatchAlgorithmReference matchAlgorithmReference) throws Exception {
		
		for(int i=0; i < schemeCount; i++){
			String testValue = fakeData.get(field, i) + "FOO";
			int expecting = fakeData.getCount(field, matchAlgorithmReference, testValue);
	
			this.executeCount_1_Filter(schemeCount, testValue, expecting, propertyReference, matchAlgorithmReference);
		}
	}

	
	// =============
	// Test methods
	// =============
	
	// Count with VALID filters
	// -------------------------
	@Test
	public void testCount_Filter_About_Found_CheckAll() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		performCountFilterTests_Valid(schemeCount, DataFields.ABOUT, 
				StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference());		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_Found_CheckAll() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		performCountFilterTests_Valid(schemeCount, DataFields.RESOURCE_SYNOPSIS, 					
				StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference());
	}
		
	@Test
	public void testCount_Filter_ResourceName_Found_CheckAll() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		performCountFilterTests_Valid(schemeCount, DataFields.RESOURCE_NAME, 
					StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());		
	}
		
	// Count with INVALID filters
	// -------------------------
	@Test
	public void testCount_Filter_About_NotFound() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		performCountFilterTests_InValid(schemeCount, DataFields.ABOUT, 
				StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference());		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_NotFound() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		performCountFilterTests_InValid(schemeCount, DataFields.RESOURCE_SYNOPSIS, 					
				StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference());
	}
		
	@Test
	public void testCount_Filter_ResourceName_NotFound() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		performCountFilterTests_InValid(schemeCount, DataFields.RESOURCE_NAME, 
				StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());		
}
		
	// Count with All VALID filters
	// -----------------------------
	@Test
	public void testCount_FilterSet_Found_CheckAll() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		
		for(int i=0; i < schemeCount; i++){
			int expecting = fakeData.getAllFilters_Count(
					fakeData.get(DataFields.ABOUT, i), 
					fakeData.get(DataFields.RESOURCE_SYNOPSIS, i), 
					fakeData.get(DataFields.RESOURCE_NAME, i));
			
			this.executeCount_3_Filters(i, i, i, schemeCount, expecting);
		}
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_FilterSet_NotFound_IndexError_About() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		
		for(int i=0; i < schemeCount; i++){
			int aboutIndex = ((i+1) % schemeCount);
			int synopsisIndex =  i;
			int nameIndex = synopsisIndex;
			
			int expecting = fakeData.getAllFilters_Count(
					fakeData.get(DataFields.ABOUT, aboutIndex), 
					fakeData.get(DataFields.RESOURCE_SYNOPSIS, synopsisIndex), 
					fakeData.get(DataFields.RESOURCE_NAME, nameIndex));
	
			this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
		}
	}
	
	@Test
	public void testCount_FilterSet_NotFound_IndexError_Synopsis() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		
		for(int i=0; i < schemeCount; i++){
			int aboutIndex = i;
			int synopsisIndex =  ((i+1) % schemeCount);
			int nameIndex = aboutIndex;
			
			int expecting = fakeData.getAllFilters_Count(
					fakeData.get(DataFields.ABOUT, aboutIndex), 
					fakeData.get(DataFields.RESOURCE_SYNOPSIS, synopsisIndex), 
					fakeData.get(DataFields.RESOURCE_NAME, nameIndex));
	
			this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
		}
	}
	
	@Test
	public void testCount_FilterSet_NotFound_IndexError_Name() throws Exception {
		fakeData = new LexEvsFakeData();
		int schemeCount = fakeData.size();
		
		for(int i=0; i < schemeCount; i++){
			int aboutIndex = i;
			int synopsisIndex =  aboutIndex;
			int nameIndex = ((i+1) % schemeCount);
			
			int expecting = fakeData.getAllFilters_Count(
					fakeData.get(DataFields.ABOUT, aboutIndex), 
					fakeData.get(DataFields.RESOURCE_SYNOPSIS, synopsisIndex), 
					fakeData.get(DataFields.RESOURCE_NAME, nameIndex));
	
			this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
		}
	}
		
	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_3Summaries_Page0to2_Size50() throws Exception {
		int schemeCount = 3;
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		
		fakeData = new LexEvsFakeData(schemeCount);
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			int expecting = calculateExpecting_WithPage(schemeCount, pageSize, pageIndex);
			this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
		}
	}
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page0to3_Size10() throws Exception {
		int schemeCount = 20;
		int firstPage = 0;
		int lastPage = 3;
		int pageSize = 10;
		fakeData = new LexEvsFakeData(schemeCount);
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			int expecting = calculateExpecting_WithPage(schemeCount, pageSize, pageIndex);
			this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
		}
	}

	@Test
	public void testGetResourceSummaries_21Summaries_Page1to3_Size10() throws Exception {
		int schemeCount = 21;
		int firstPage = 1;
		int lastPage = 3;
		int pageSize = 10;
		fakeData = new LexEvsFakeData(schemeCount);
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){			
			int expecting = calculateExpecting_WithPage(schemeCount, pageSize, pageIndex);
			this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
		}
	}
	
	
	// -----------------------------------------
	// resourceSummaries with individual filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_FilterSubstring_About_Page0to2_Size50() throws Exception {
		fakeData = new LexEvsFakeData();		
		int schemeCount = fakeData.size();
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
				
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			for(int schemeIndex = 0; schemeIndex < schemeCount; schemeIndex++){
				String testValue = fakeData.get(DataFields.ABOUT, schemeIndex);
				
				for(int start=0; start < testValue.length(); start++){
					for(int end=start; end < testValue.length(); end++){
						testValue = testValue.substring(start, end);
						
						int fakeResults = fakeData.getAbout_ContainsCount(testValue);
						int expecting = calculateExpecting_WithPage(fakeResults, pageSize, pageIndex);
						this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
								StandardModelAttributeReference.ABOUT.getPropertyReference(), 
								StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
								testValue);
					}
				}
			}
		}
	}
	
	@Test
	public void testGetResourceSummaries_FilterSubstring_ResourceSynopsis_Page0to2_Size50() throws Exception {
		fakeData = new LexEvsFakeData();		
		int schemeCount = fakeData.size();
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			for(int schemeIndex = 0; schemeIndex < schemeCount; schemeIndex++){
				String testValue = fakeData.get(DataFields.RESOURCE_SYNOPSIS, schemeIndex);
				
				for(int start=0; start < testValue.length(); start++){
					for(int end=start; end < testValue.length(); end++){
						testValue = testValue.substring(start, end);
						
						int fakeResults = fakeData.getResourceSynopsis_StartWithCount(testValue);
						int expecting = calculateExpecting_WithPage(fakeResults, pageSize, pageIndex);
		
						this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
								StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
								StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
								testValue);
					}
				}
			}
		}
	}
	
	@Test
	public void testGetResourceSummaries_FilterSubstring_ResourceName_Page0to2_Size50() throws Exception {
		fakeData = new LexEvsFakeData();		
		int schemeCount = fakeData.size();
		int firstPage = 0;
		int lastPage = 2;
		int pageSize = 50;
		
		for(int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++){
			for(int schemeIndex = 0; schemeIndex < schemeCount; schemeIndex++){
				String testValue = fakeData.get(DataFields.RESOURCE_NAME, schemeIndex);

				for(int start=0; start < testValue.length(); start++){
					for(int end=start; end < testValue.length(); end++){
						testValue = testValue.substring(start, end);
						
						int fakeResults = fakeData.getResourceName_ExactMatchCount(testValue);
						int expecting = calculateExpecting_WithPage(fakeResults, pageSize, pageIndex);
		
						this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
								StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
								StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
								testValue);
					}
				}
			}
		}
	}	
}
