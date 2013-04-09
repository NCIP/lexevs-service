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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class LexEvsCodeSystemVersionQueryServiceTestIT extends
		AbstractTestITBase {
	
	private final static String ABOUT_CONTAINS = "11.11.0.1";
	private final static String RESOURCESYNOPSIS_STARTSWITH = "Auto";
	private final static String RESOURCENAME_EXACTMATCH = "Automobiles-1.0";
	
	@Resource
	private LexEvsCodeSystemVersionQueryService service;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	// -----------------------------
	// Count with All valid filters
	// -----------------------------
	@Test
	public void testCount_FilterSet() throws Exception {
		// Call local method to create set of all filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	// -------------------------
	// Count with valid filters
	// -------------------------
	@Test
	public void testCount_Filter_About_Found() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getPropertyReference(), 
												  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
												  		  ABOUT_CONTAINS);

		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	@Test
	public void testCount_Filter_ResorceSynopsis_Found() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
														  StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
														  RESOURCESYNOPSIS_STARTSWITH);

		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	@Test
	public void testCount_Filter_ResourceName_Found() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
												  		  StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
												  		  RESOURCENAME_EXACTMATCH);

		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		

	// ---------------------------
	// Count with invalid filters
	// ---------------------------
	@Test
	public void testCount_Filter_About_NotFound() throws Exception {

		// Call local method to create set of all filters, Create error in resource name
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS + "FOO", RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
	
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 0;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testCount_Filter_ResourceName_NotFound() throws Exception {

		// Call local method to create set of all filters, Create error in resource name
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH + "FOO");
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 0;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	@Test
	public void testCount_Filter_ResorceSynopsis_NotFound() throws Exception {

		// Call local method to create set of all filters, Create error in resource name
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH + "FOO", RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 0;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	// ----------------------------------------
	// resourceSummaries test codeSetName
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_Restriction_CodeSetName_Found() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		// Get Directory Results for given codeSystem (no restrictions and empty query so return all entities)
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummaries_Restriction_CodeSetName_NotFound() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles" + "FOO"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);
		
		// Get Directory Results for given codeSystem
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, doesn't exist so will return list with no elements.
		assertNotNull(dirResult);
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	// ----------------------------------------
	// resourceSummaries with All valid filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_FiltersSet() throws Exception {

		// Call local method to create set of all filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummaries_FilterSet_VerifyTransformation() throws Exception {

		// Call local method to create set of all filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
		
		// Verify LexEVS to CTS2 transform worked 
		CodeSystemVersionCatalogEntrySummary csvCatalogEntrySummary = dirResult.getEntries().get(0);
		assertNotNull(csvCatalogEntrySummary.getFormalName());
		assertEquals("Formal name not transformed - ", "autos", csvCatalogEntrySummary.getFormalName());
		
		assertNotNull(csvCatalogEntrySummary.getCodeSystemVersionName());
		assertEquals("CodeSystemVersionName not transformed - ","Automobiles-1.0",csvCatalogEntrySummary.getCodeSystemVersionName());

		assertNotNull(csvCatalogEntrySummary.getDocumentURI());
		assertEquals("DocumentURI not transformed - ","urn:oid:11.11.0.1/1.0",csvCatalogEntrySummary.getDocumentURI());		

		assertNotNull(csvCatalogEntrySummary.getAbout());
		assertEquals("About not transformed - ","urn:oid:11.11.0.1",csvCatalogEntrySummary.getAbout());		

		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis());
		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis().getValue());
		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis().getValue().getContent());
		assertEquals("Resource Synopsis not transformed - ","Automobiles", csvCatalogEntrySummary.getResourceSynopsis().getValue().getContent());						
	}
	
	// ----------------------------------------
	// resourceSummaries with individual filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummaries_Filter_About_Contains_Found() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getPropertyReference(), 
				   										  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
				   										  ABOUT_CONTAINS);
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Filter_ResourceSynopsis_StartsWith_Found() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
														  StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
				   										  RESOURCESYNOPSIS_STARTSWITH);
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Filter_ResourceName_ExactMatch_Found() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
														  StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
														  RESOURCENAME_EXACTMATCH);
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Filter_About_Contains_NotFound() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getPropertyReference(), 
														  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
														  ABOUT_CONTAINS + "FOO");
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Filter_ResourceSynopsis_StartsWith_NotFound() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(), 
														  StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
														  RESOURCESYNOPSIS_STARTSWITH + "FOO");
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Filter_ResourceName_ExactMatch_NotFound() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
														  StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
														  RESOURCENAME_EXACTMATCH + "FOO");
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		// Test results, should return one entity
		assertNotNull(dirResult);
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}	


	// ----------------------------------------
	// resourceList test codeSetName
	// -----------------------------------------
	@Test
	public void testGetResourceList_Restriction_CodeSetName_Found() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		// Get Directory Results for given codeSystem (no restrictions and empty query so return all entities)
		DirectoryResult<CodeSystemVersionCatalogEntry> dirResult = service.getResourceList(query, null, new Page());
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceList_Restriction_CodeSetName_NotFound() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles" + "FOO"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		// Get Directory Results for given codeSystem (no restrictions and empty query so return all entities)
		DirectoryResult<CodeSystemVersionCatalogEntry> dirResult = service.getResourceList(query, null, new Page());
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

}
