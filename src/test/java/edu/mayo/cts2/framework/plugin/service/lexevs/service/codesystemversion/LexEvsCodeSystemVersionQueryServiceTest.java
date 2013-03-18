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
	private final static String [] ABOUT_VALUES = {"11.11.0.1", "9.0.0.1", "11.11.0.2"};
	private final static String [] SYNOPSIS_VALUES = {"Auto", "Car", "Auto2"};
	private final static String [] LOCALNAME_VALUES = {"Automobiles", "Vehicles", "Automobiles"};
	private final static String [] VERSION_VALUES = {"1.0", "1.0", "1.1"};
	
	
	private LexBIGService createMock_controlSupportedCodingSchemes_blank(int size) throws LBInvocationException{
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
		for(int i=0; i < size; i++){
			list.addCodingSchemeRendering(i, new CodingSchemeRendering());
		}
		
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list);
		EasyMock.replay(lexBigService);
		
		return lexBigService;
	}
	
	private LexBIGService createMock_controlSupportedCodingSchemes_withData(int size) throws LBInvocationException{
		LexBIGService lexBigService = EasyMock.createMock(LexBIGService.class);
		
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
		for(int i=0; i < size; i++){
			CodingSchemeRendering render = new CodingSchemeRendering();
			
			CodingSchemeSummary codingSchemeSummary = new CodingSchemeSummary();
			
			// Synopsis
			EntityDescription codingSchemeDescription = new EntityDescription();
			codingSchemeDescription.setContent(SYNOPSIS_VALUES[(i % SYNOPSIS_VALUES.length)] + ":" + i);
			codingSchemeSummary.setCodingSchemeDescription(codingSchemeDescription);
			
			
			// About
			codingSchemeSummary.setCodingSchemeURI(ABOUT_VALUES[(i % ABOUT_VALUES.length)] + ":" + i);
			
			
			// resource name
			codingSchemeSummary.setLocalName(LOCALNAME_VALUES[(i % LOCALNAME_VALUES.length)] + ":" + i);
			codingSchemeSummary.setRepresentsVersion(VERSION_VALUES[(i % VERSION_VALUES.length)] + ":" + i);

			render.setCodingSchemeSummary(codingSchemeSummary);
			
			list.addCodingSchemeRendering(i, render);
		}
		
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list);
		EasyMock.replay(lexBigService);
		
		return lexBigService;
	}
	

	@Test
	public void testGetResourceSummaries_3Summaries_Page0_Size50_Return3() throws LBInvocationException {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		
		Page page = new Page();
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_blank(3);
		service.setLexBigService(lexBigService);
				
		// Overwrite transformer to an anonymous transformer
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(){
			public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering){
				return new CodeSystemVersionCatalogEntrySummary();
			}
		});

		
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page);
		
		assertNotNull(directoryResult);
		
		int expecting = 3;
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page0_Size10_Return10() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		
		Page page = new Page();
		page.setMaxToReturn(10);
		
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
			
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_blank(20);
		service.setLexBigService(lexBigService);
		
		// Overwrite transformer to an anonymous transformer
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(){
			public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering){
				return new CodeSystemVersionCatalogEntrySummary();
			}
		});
		
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult;
		directoryResult = service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 
		
		assertNotNull(directoryResult);
		
		int expecting = 10;
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page1_Size10_Return10() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		
		Page page = new Page();
		page.setMaxToReturn(10);
		
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
			
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_blank(20);
		service.setLexBigService(lexBigService);
		
		// Overwrite transformer to an anonymous transformer
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(){
			public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering){
				return new CodeSystemVersionCatalogEntrySummary();
			}
		});
		
		// Test second page
		page.setPage(1);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult;
		directoryResult = service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 
		
		assertNotNull(directoryResult);
		
		int expecting = 10;
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);
	}
	
	
	@Test
	public void testGetResourceSummaries_20Summaries_Page2_Size10_Return0() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		
		Page page = new Page();
		page.setMaxToReturn(10);
		
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
			
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_blank(20);
		service.setLexBigService(lexBigService);
		
		// Overwrite transformer to an anonymous transformer
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(){
			public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering){
				return new CodeSystemVersionCatalogEntrySummary();
			}
		});
		
		page.setPage(2);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult;
		directoryResult = service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 
		
		assertNotNull(directoryResult);
		
		int expecting = 0;
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);		
	}

	@Test
	public void testGetResourceSummaries_21Summaries_Page2_Size10_Return1() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		
		Page page = new Page();
		page.setMaxToReturn(10);
		
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
			
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_blank(21);
		service.setLexBigService(lexBigService);
		
		// Overwrite transformer to an anonymous transformer
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(){
			public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering){
				return new CodeSystemVersionCatalogEntrySummary();
			}
		});
		
		page.setPage(2);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult;
		directoryResult = service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 

		assertNotNull(directoryResult);

		int expecting = 1;
		int actual = directoryResult.getEntries().size();
		assertEquals("Expecting " + expecting + " entries but got " + actual, expecting, actual);		
	}
	
	
	// ----------------------------------------
	// resourceSummaries with individual filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_Filter_About_Contains_Several() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		int supportedCodingSchemeCount = (ABOUT_VALUES.length * 2);
		int matchingCodingSchemeIndex = 0;

		// ---------- MOCK SOME OBJECTS REQUIRED -----------
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_withData(supportedCodingSchemeCount);
		service.setLexBigService(lexBigService);

		// Create an instance of CodingSchemeToCodeSystemTransform, must pass in a new CodeSystemVersionNameConverter
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		// --------------------------------------------------

		// Build query using filters
		Set<ResolvedFilter> filter = TestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				   										  	   StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
				   										  	   ABOUT_VALUES[matchingCodingSchemeIndex]);
		CodeSystemVersionQueryImpl query = TestUtils.createQuery_FiltersOnly(filter);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = TestUtils.createResourceSummaries_DirectoryResults_QueryOnly(service, query);
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = (supportedCodingSchemeCount / ABOUT_VALUES.length);
		if(matchingCodingSchemeIndex < (supportedCodingSchemeCount % ABOUT_VALUES.length)){
			expecting++;
		}
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testCount_Filter_ResorceSynopsis_Found() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		int supportedCodingSchemeCount = (SYNOPSIS_VALUES.length * 2);
		int matchingCodingSchemeIndex = 2;

		// ---------- MOCK SOME OBJECTS REQUIRED -----------
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_withData(supportedCodingSchemeCount);
		service.setLexBigService(lexBigService);

		// Create an instance of CodingSchemeToCodeSystemTransform, must pass in a new CodeSystemVersionNameConverter
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		// --------------------------------------------------
		
		// Build query using filters
		Set<ResolvedFilter> filter = TestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
														  	   StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
														  	   SYNOPSIS_VALUES[matchingCodingSchemeIndex]);

		CodeSystemVersionQueryImpl query = TestUtils.createQuery_FiltersOnly(filter);

		int expecting = (supportedCodingSchemeCount / SYNOPSIS_VALUES.length);
		if(matchingCodingSchemeIndex < (supportedCodingSchemeCount % SYNOPSIS_VALUES.length)){
			expecting++;
		}
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	@Test
	public void testCount_Filter_ResourceName_Found() throws Exception {
		LexEvsCodeSystemVersionQueryService service = new LexEvsCodeSystemVersionQueryService();
		int supportedCodingSchemeCount = (LOCALNAME_VALUES.length * 2);
		int matchingCodingSchemeIndex = 1;

		// ---------- MOCK SOME OBJECTS REQUIRED -----------
		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = this.createMock_controlSupportedCodingSchemes_withData(supportedCodingSchemeCount);
		service.setLexBigService(lexBigService);

		// Create an instance of CodingSchemeToCodeSystemTransform, must pass in a new CodeSystemVersionNameConverter
		service.setCodingSchemeTransformer(new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter()));
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		// --------------------------------------------------
		
		// Build query using filters
		Set<ResolvedFilter> filter = TestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
												  		       StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
												  		       LOCALNAME_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex + "-" + VERSION_VALUES[matchingCodingSchemeIndex] + ":" + matchingCodingSchemeIndex);

		CodeSystemVersionQueryImpl query = TestUtils.createQuery_FiltersOnly(filter);

		int expecting = 1;
		
		int actual = service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		


}
