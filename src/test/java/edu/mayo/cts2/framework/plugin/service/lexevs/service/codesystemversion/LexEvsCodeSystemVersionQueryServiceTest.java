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
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;


/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsCodeSystemVersionQueryServiceTest {
	private final static int SCHEME_COUNT = 3;
	private final static String [] ABOUT_VALUES = {"11.11.0.1", "9.0.0.1", "13.11.0.2"};
	private final static String [] SYNOPSIS_VALUES = {"Auto", "Car", "Auto2"};
	private final static String [] LOCALNAME_VALUES = {"Automobiles", "Vehicles", "Automobiles"};
	private final static String [] VERSION_VALUES = {"1.0", "1.0", "1.1"};
	
	
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
	
	// Create Mocked Service and generate a codingSchemeRenderingList filled with data stored in local arrays.
	// -------------------------------------------------------------------------------------------------------
	private LexBIGService createMockedService_spoofSupportedCodingSchemes(int size, boolean withData) throws Exception{
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
		for(int i=0; i < size; i++){
			CodingSchemeRendering render = new CodingSchemeRendering();
			CodingSchemeSummary codingSchemeSummary = new CodingSchemeSummary();
			
			if(withData){				
				// Synopsis
				EntityDescription codingSchemeDescription = new EntityDescription();
				codingSchemeDescription.setContent(SYNOPSIS_VALUES[(i % SYNOPSIS_VALUES.length)] + ":" + i);
				codingSchemeSummary.setCodingSchemeDescription(codingSchemeDescription);
				
				
				// About
				codingSchemeSummary.setCodingSchemeURI(ABOUT_VALUES[(i % ABOUT_VALUES.length)] + ":" + i);
				
				
				// resource name
				codingSchemeSummary.setLocalName(LOCALNAME_VALUES[(i % LOCALNAME_VALUES.length)] + ":" + i);
				codingSchemeSummary.setRepresentsVersion(VERSION_VALUES[(i % VERSION_VALUES.length)] + ":" + i);	
			}
			
			render.setCodingSchemeSummary(codingSchemeSummary);
			list.addCodingSchemeRendering(i, render);
		}
		
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list);
		EasyMock.replay(lexBigService);
		
		return lexBigService;
	}
	
	private void executeCount_3_Filters(int aboutIndex, int synopsisIndex, int nameIndex, int schemeCount, int expecting) throws Exception{
		LexEvsCodeSystemVersionQueryService service = this.createService(schemeCount, true);
		
		// Build query using filters
		String about = ABOUT_VALUES[aboutIndex];
		String resourceSynopsis = SYNOPSIS_VALUES[synopsisIndex];
		String resourceName = LOCALNAME_VALUES[nameIndex] + ":" + nameIndex + "-" + 
				   			  VERSION_VALUES[nameIndex] + ":" + nameIndex;
		
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
	
	// =============
	// Test methods
	// =============
	
	// Count with VALID filters
	// -------------------------
	@Test
	public void testCount_Filter_About_Found_Index2_SchemeCountTimes2() throws Exception {
		int matchingCodingSchemeIndex = 2;
		int schemeCount = (SCHEME_COUNT * 2);
		
		String testValue = ABOUT_VALUES[matchingCodingSchemeIndex];
		int expecting = (schemeCount / SCHEME_COUNT);
		if(matchingCodingSchemeIndex < (schemeCount % SCHEME_COUNT)){
			expecting++;
		}

		this.executeCount_1_Filter(schemeCount, testValue, expecting, 
				StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference());
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_Found_Index2_SchemeCountTimes2() throws Exception {
		int matchingCodingSchemeIndex = 2;
		int schemeCount = (SCHEME_COUNT * 2);
		
		String testValue = SYNOPSIS_VALUES[matchingCodingSchemeIndex];
		int expecting = (schemeCount / SCHEME_COUNT);
		if(matchingCodingSchemeIndex < (schemeCount % SCHEME_COUNT)){
			expecting++;
		}

		this.executeCount_1_Filter(schemeCount, testValue, expecting, 
				StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference());
	}
		
	@Test
	public void testCount_Filter_ResourceName_Found_Index2_SchemeCountTimes2() throws Exception {
		int matchingCodingSchemeIndex = 2;
		int schemeCount = (SCHEME_COUNT * 2);
		
		String testValue = LOCALNAME_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex + "-" + 
						   VERSION_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex;
		int expecting = 1;
		
		this.executeCount_1_Filter(schemeCount, testValue, expecting, 
				StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());
	}
		
	// Count with INVALID filters
	// -------------------------
	@Test
	public void testCount_Filter_About_NotFound() throws Exception {
		int matchingCodingSchemeIndex = 2;
		String testValue = ABOUT_VALUES[matchingCodingSchemeIndex] + "FOO";
		int schemeCount = (SCHEME_COUNT * 2);
		int expecting = 0;

		this.executeCount_1_Filter(schemeCount, testValue, expecting, 
				StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference());
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_NotFound() throws Exception {
		int matchingCodingSchemeIndex = 2;
		String testValue = SYNOPSIS_VALUES[matchingCodingSchemeIndex] + "FOO";
		int schemeCount = (SCHEME_COUNT * 2);
		int expecting = 0;

		this.executeCount_1_Filter(schemeCount, testValue, expecting, 
				StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference());
	}
		
	@Test
	public void testCount_Filter_ResourceName_NotFound() throws Exception {
		int matchingCodingSchemeIndex = 2;
		String testValue = LOCALNAME_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex + "-" + 
						   VERSION_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex + "FOO";
		int schemeCount = (SCHEME_COUNT * 2);
		int expecting = 0;
		
		this.executeCount_1_Filter(schemeCount, testValue, expecting, 
				StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());
	}
		
	// Count with All VALID filters
	// -----------------------------
	@Test
	public void testCount_FilterSet_Found_Index0_SchemeCountTimes2() throws Exception {
		int aboutIndex = 0;
		int schemeCount = (SCHEME_COUNT * 2);
		
		int synopsisIndex = aboutIndex;
		int nameIndex = aboutIndex;
		int expecting = 1;
		
		this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
	}

	@Test
	public void testCount_FilterSet_Found_Index1_SchemeCountTimes2() throws Exception {
		int aboutIndex = 1;
		int schemeCount = (SCHEME_COUNT * 2);
		
		int synopsisIndex = aboutIndex;
		int nameIndex = aboutIndex;
		int expecting = 1;
		
		this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
	}

	@Test
	public void testCount_FilterSet_Found_IndexLast_SchemeCountTimes1() throws Exception {
		int aboutIndex = (SCHEME_COUNT - 1);
		int schemeCount = SCHEME_COUNT;
		
		int synopsisIndex = aboutIndex;
		int nameIndex = aboutIndex;
		int expecting = 1;
		
		this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_FilterSet_NotFound_IndexError_About() throws Exception {
		int aboutIndex = SCHEME_COUNT % 3;
		int synopsisIndex = SCHEME_COUNT % 2;
		int nameIndex = synopsisIndex;
		int schemeCount = (SCHEME_COUNT * 2);
		int expecting = 0;

		this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
	}
	
	@Test
	public void testCount_FilterSet_NotFound_IndexError_Synopsis() throws Exception {
		int aboutIndex = SCHEME_COUNT % 3;
		int synopsisIndex = SCHEME_COUNT % 2;
		int nameIndex = aboutIndex;
		int schemeCount = (SCHEME_COUNT * 2);
		int expecting = 0;

		this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
	}
	
	@Test
	public void testCount_FilterSet_NotFound_IndexError_Name() throws Exception {
		int aboutIndex = SCHEME_COUNT % 3;
		int synopsisIndex = aboutIndex;
		int nameIndex = SCHEME_COUNT % 2;
		int schemeCount = (SCHEME_COUNT * 2);
		int expecting = 0;

		this.executeCount_3_Filters(aboutIndex, synopsisIndex, nameIndex, schemeCount, expecting);
	}
		
	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_3Summaries_Page0_Size50_Return3() throws Exception {
		int schemeCount = 3;
		int expecting = 3;
		int pageSize = 50;
		int pageIndex = 0;

		this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
	}
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page0_Size10_Return10() throws Exception {
		int schemeCount = 20;
		int expecting = 10;
		int pageSize = 10;
		int pageIndex = 0;

		this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
	}
	
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page1_Size10_Return10() throws Exception {
		int schemeCount = 20;
		int expecting = 10;
		int pageSize = 10;
		int pageIndex = 1;

		this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
	}
	
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page2_Size10_Return0() throws Exception {
		int schemeCount = 20;
		int expecting = 0;
		int pageSize = 10;
		int pageIndex = 2;

		this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
	}

	@Test
	public void testGetResourceSummaries_21Summaries_Page2_Size10_Return1() throws Exception {
		int schemeCount = 21;
		int expecting = 1;
		int pageSize = 10;
		int pageIndex = 2;

		this.executeGetResourceSummaries_0_Filter(schemeCount, pageSize, pageIndex, expecting);
	}
	
	
	// -----------------------------------------
	// resourceSummaries with individual filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_Filter_About_Found_IndexLast_SchemeCountTimes1() throws Exception {
		int matchingCodingSchemeIndex = (SCHEME_COUNT - 1);
		String testValue = ABOUT_VALUES[matchingCodingSchemeIndex];
		int schemeCount = SCHEME_COUNT;
		int expecting = (schemeCount / SCHEME_COUNT);
		if(matchingCodingSchemeIndex < (schemeCount % SCHEME_COUNT)){
			expecting++;
		}
		
		int pageSize = 50;
		int pageIndex = 0;

		this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
				StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
				testValue);
	}
	
	@Test
	public void testGetResourceSummaries_Filter_ResourceSynopsis_Found() throws Exception {
		int matchingCodingSchemeIndex = (SCHEME_COUNT - 1);
		String testValue = SYNOPSIS_VALUES[matchingCodingSchemeIndex];
		int schemeCount = SCHEME_COUNT;
		int expecting = (schemeCount / SCHEME_COUNT);
		if(matchingCodingSchemeIndex < (schemeCount % SCHEME_COUNT)){
			expecting++;
		}
		
		int pageSize = 50;
		int pageIndex = 0;

		this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
				StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
				testValue);
	}

	@Test
	public void testGetResourceSummaries_Filter_ResourceName_Found() throws Exception {
		int matchingCodingSchemeIndex = (SCHEME_COUNT - 1);
		int schemeCount = SCHEME_COUNT;
		
		String testValue = LOCALNAME_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex + "-" + 
				VERSION_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex;
		int expecting = 1;
		
		int pageSize = 50;
		int pageIndex = 0;

		this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
				StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
				testValue);
	}
	
	@Test
	public void testGetResourceSummaries_Filter_About_Found_Index2_SchemeCountTimes1_PageSize1_PageIndex0() throws Exception {
		int matchingCodingSchemeIndex = 2;
		String testValue = ABOUT_VALUES[matchingCodingSchemeIndex];
		int schemeCount = SCHEME_COUNT;
		int expecting = 1;
		
		int pageSize = 1;
		int pageIndex = 0;

		this.executeGetResourceSummaries_1_Filter(schemeCount, pageSize, pageIndex, expecting, 				
				StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
				testValue);
	}
	

}
