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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class LexEvsCodeSystemVersionQueryServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private LexEvsCodeSystemVersionQueryService service;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testQueryByResourceSummariesCodeSetName() throws Exception {

		Page page = new Page();
		SortCriteria sortCriteria = null;	
		
		Query query = null;
		Set<ResolvedFilter> filterComponent = null;
		ResolvedReadContext readContext = null;
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName("Automobiles");
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = new CodeSystemVersionQueryServiceRestrictions();
		csvQueryServiceRestrictions.setCodeSystem(codeSystem);
		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query,filterComponent,readContext,csvQueryServiceRestrictions);

		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page);
		assertNotNull(dirResult);
		assertEquals(1, dirResult.getEntries().size());
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testQueryByResourceSummariesCodeSetNameNotFound() throws Exception {

		Page page = new Page();
		SortCriteria sortCriteria = null;	
		
		Query query = null;
		Set<ResolvedFilter> filterComponent = null;
		ResolvedReadContext readContext = null;
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName("Automooobiles");
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = new CodeSystemVersionQueryServiceRestrictions();
		csvQueryServiceRestrictions.setCodeSystem(codeSystem);
		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query,filterComponent,readContext,csvQueryServiceRestrictions);

		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page);
		assertNotNull(dirResult);
		assertEquals(0, dirResult.getEntries().size());
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testQueryByResourceSummariesAllFilters() throws Exception {

		Page page = new Page();
		SortCriteria sortCriteria = null;	
		
		Query query = null;
		
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS
				.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH
				.getMatchAlgorithmReference();
		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH
				.getMatchAlgorithmReference();
		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();
		PropertyReference resourceSynopsis = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		PropertyReference resourceName = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();	
		
		ResolvedFilter aboutContains = new ResolvedFilter();
		aboutContains.setMatchValue("11.11.0.1");
		aboutContains.setMatchAlgorithmReference(contains);
		aboutContains.setPropertyReference(about);
		
		ResolvedFilter resourceSynopsisStartsWith = new ResolvedFilter();
		resourceSynopsisStartsWith.setMatchValue("Auto");
		resourceSynopsisStartsWith.setMatchAlgorithmReference(startsWith);
		resourceSynopsisStartsWith.setPropertyReference(resourceSynopsis);
		
		ResolvedFilter resourceNameExactMatch = new ResolvedFilter();
		resourceNameExactMatch.setMatchValue("Automobiles-1.0");
		resourceNameExactMatch.setMatchAlgorithmReference(exactMatch);
		resourceNameExactMatch.setPropertyReference(resourceName);		

		Set<ResolvedFilter> filterComponent = new HashSet<ResolvedFilter>(Arrays.asList(aboutContains,resourceSynopsisStartsWith,resourceNameExactMatch));
		
		ResolvedReadContext readContext = null;
		
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = null;
		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query,filterComponent,readContext,csvQueryServiceRestrictions);

		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page);
		assertNotNull(dirResult);
		assertEquals(1, dirResult.getEntries().size());
		
		// Verify LexEVS to CTS2 transform worked 
		CodeSystemVersionCatalogEntrySummary csvCatalogEntrySummary = dirResult.getEntries().get(0);
		assertNotNull(csvCatalogEntrySummary.getFormalName());
		assertEquals("Formal name not transformed - ", "autos", csvCatalogEntrySummary.getFormalName());
		assertNotNull(csvCatalogEntrySummary.getCodeSystemVersionName());
		assertEquals("CodeSystemVersionName not transformed - ","Automobiles-1.0",csvCatalogEntrySummary.getCodeSystemVersionName());
		assertNotNull(csvCatalogEntrySummary.getDocumentURI());
		assertEquals("DocumentURI not transformed - ","urn:oid:11.11.0.1",csvCatalogEntrySummary.getDocumentURI());		
		assertNotNull(csvCatalogEntrySummary.getAbout());
		assertEquals("About not transformed - ","urn:oid:11.11.0.1",csvCatalogEntrySummary.getAbout());		
		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis());
		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis().getValue());
		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis().getValue().getContent());
		assertEquals("Resource Synopsis not transformed - ","Automobiles",csvCatalogEntrySummary.getResourceSynopsis().getValue().getContent());						
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testQueryByExactResourceName() throws Exception {

		Page page = new Page();
		SortCriteria sortCriteria = null;	
		
		Query query = null;
		
		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH
				.getMatchAlgorithmReference();
	
		PropertyReference resourceName = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();	

		ResolvedFilter resourceNameExactMatch = new ResolvedFilter();
		resourceNameExactMatch.setMatchValue("Automobiles-1.0");
		resourceNameExactMatch.setMatchAlgorithmReference(exactMatch);
		resourceNameExactMatch.setPropertyReference(resourceName);		

		Set<ResolvedFilter> filterComponent = new HashSet<ResolvedFilter>(Arrays.asList(resourceNameExactMatch));
		
		ResolvedReadContext readContext = null;
		
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = null;
		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query,filterComponent,readContext,csvQueryServiceRestrictions);

		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page);
		assertNotNull(dirResult);
		assertEquals(1, dirResult.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testQueryByCount() throws Exception {

		Query query = null;
		
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS
				.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH
				.getMatchAlgorithmReference();
		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH
				.getMatchAlgorithmReference();
		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();
		PropertyReference resourceSynopsis = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		PropertyReference resourceName = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();	
		
		ResolvedFilter aboutContains = new ResolvedFilter();
		aboutContains.setMatchValue("11.11.0.1");
		aboutContains.setMatchAlgorithmReference(contains);
		aboutContains.setPropertyReference(about);
		
		ResolvedFilter resourceSynopsisStartsWith = new ResolvedFilter();
		resourceSynopsisStartsWith.setMatchValue("Auto");
		resourceSynopsisStartsWith.setMatchAlgorithmReference(startsWith);
		resourceSynopsisStartsWith.setPropertyReference(resourceSynopsis);
		
		ResolvedFilter resourceNameExactMatch = new ResolvedFilter();
		resourceNameExactMatch.setMatchValue("Automobiles-1.0");
		resourceNameExactMatch.setMatchAlgorithmReference(exactMatch);
		resourceNameExactMatch.setPropertyReference(resourceName);		

		Set<ResolvedFilter> filterComponent = new HashSet<ResolvedFilter>(Arrays.asList(aboutContains,resourceSynopsisStartsWith,resourceNameExactMatch));
		
		ResolvedReadContext readContext = null;
		
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = null;
		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query,filterComponent,readContext,csvQueryServiceRestrictions);

		assertEquals(1, this.service.count(codeSystemVersionQuery));
	}
		
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testQueryByCountFilterMismatch() throws Exception {

		Query query = null;
		
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS
				.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH
				.getMatchAlgorithmReference();
		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH
				.getMatchAlgorithmReference();
		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();
		PropertyReference resourceSynopsis = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		PropertyReference resourceName = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();	
		
		ResolvedFilter aboutContains = new ResolvedFilter();
		aboutContains.setMatchValue("11.11.0.1");
		aboutContains.setMatchAlgorithmReference(contains);
		aboutContains.setPropertyReference(about);
		
		ResolvedFilter resourceSynopsisStartsWith = new ResolvedFilter();
		resourceSynopsisStartsWith.setMatchValue("Auto");
		resourceSynopsisStartsWith.setMatchAlgorithmReference(startsWith);
		resourceSynopsisStartsWith.setPropertyReference(resourceSynopsis);
		
		ResolvedFilter resourceNameExactMatch = new ResolvedFilter();
		resourceNameExactMatch.setMatchValue("Automooobiles-1.0");
		resourceNameExactMatch.setMatchAlgorithmReference(exactMatch);
		resourceNameExactMatch.setPropertyReference(resourceName);		

		Set<ResolvedFilter> filterComponent = new HashSet<ResolvedFilter>(Arrays.asList(aboutContains,resourceSynopsisStartsWith,resourceNameExactMatch));
		
		ResolvedReadContext readContext = null;
		
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = null;
		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query,filterComponent,readContext,csvQueryServiceRestrictions);

		assertEquals(0, this.service.count(codeSystemVersionQuery));
	}
		
}
