/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractQueryServiceTest;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class LexEvsEntityQueryServiceTestIT 
	extends AbstractQueryServiceTest<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> {
	
	@Resource
	private LexEvsEntityQueryService service;

	// ---- Test methods ----	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	public void testCountWithNullQuery() throws Exception {
		int expecting = 0;
		int actual = this.service.count(null);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	
	@Test
	public void testGetResourceSummaries_CodingSchemeExists_andNotEmpty() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();		
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}
	
	@Test
	public void testGetResourceList_CodingSchemeExists_andNotEmpty() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();		
		DirectoryResult<EntityListEntry> directoryResult = this.service.getResourceList(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		// Test results
		// ------------
		List<EntityListEntry> list = directoryResult.getEntries();
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}

	@Test
	public void testGetResourceSummaries_CodingSchemeDoesNotExist() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automooobiles-1.0");
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();		
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		
		// Test results
		// ------------
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		String msg = "Expecting directoryResult.getEntries().size() == 0, instead directoryResult.getEntries().size() == " + directoryResult.getEntries().size();
		assertTrue(msg, directoryResult.getEntries().size() == 0);		
	}
	
	@Test
	public void testGetResourceSummaries_Filter_startsWith() throws Exception {
				
		// Create query
		// ------------
//		Set<ResolvedFilter> filters = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(),, "Jaguar")
		EntityDescriptionQuery query = CommonTestUtils.createQuery("startsWith", "Jaguar", "Automobiles-1.0", 
			StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference());	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}	

	@Test
	public void testGetResourceSummaries_Filter_startsWith_Empty() throws Exception {
				
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("startsWith", "JUGUAR", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
	}	

	
	@Test
	public void testGetResourceSummaries_Filter_exactMatch() throws Exception {
				
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("exactMatch", "Jaguar", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}	

	@Test
	public void testGetResourceSummaries_Filter_exactMatch_Empty() throws Exception {
				
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("exactMatch", "Jagua", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
	}	

	@Test
	public void testGetResourceSummaries_Filter_contains() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("contains", "GE", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}	
	
	@Test
	public void testGetResourceSummaries_Filter_contains_Empty() throws Exception {
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("contains", "MOOVIES", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
	}	

	@Test
	public void testGetResourceSummaries_Filter_contains_Multiple() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("contains", "Car", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 4, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 4);		
	}	
	

	@Test
	public void testGetResourceSummaries_Filter_Leading_Wildcard() throws Exception {
		
		// Create query - searching for "General Motors" entity
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("LuceneQuery", "*neRal motors", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}	
	
	
	@Test
	public void testGetResourceSummaries_Filter_Leading_Wildcard_Empty() throws Exception {

		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("LuceneQuery", "* eral", "Automobiles-1.0");
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
	}	
	
	@Test
	public void testGetResourceSummaries_Filter_Lagging_Wildcard() throws Exception {
		
		// Create query - searching for "General Motors" entity
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("LuceneQuery", "general *", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}	
	
	
	@Test
	public void testGetResourceSummaries_Filter_Lagging_Wildcard_Empty() throws Exception {
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("LuceneQuery", "eneral*", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
	}	
	
	
	@Test
	public void testGetResourceSummaries_Filter_LeadingAndLagging_Wildcard() throws Exception {
		
		// Create query - searching for "General Motors" entity
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("LuceneQuery", "*eneRal *", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() > 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() > 0);		
	}	
	
	
	@Test
	public void testGetResourceSummaries_Filter_LeadingAndLagging_Wildcard_Empty() throws Exception {
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("LuceneQuery", "* eneRal*", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
	}	
	
	@Test
	public void testGetResourceSummaries_Filter_Paging() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("contains", "Car", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		page.setMaxToReturn(3);
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
				
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		assertTrue("Expected first 1 of 2 pages returned ",list.size() == 3);
		assertFalse("Expected not to be at the end of the pages ", directoryResult.isAtEnd());
		
		page.setPage(1);
		directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
		list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 1, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 1);		
		assertTrue("Expected to be at the end of the pages ", directoryResult.isAtEnd());

	}	
	
	@Test
	public void testGetResourceSummaries_Filter_Paging_Empty() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("exactMatch", "waz up", "Automobiles-1.0");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull("Expecting data returned but instead directoryResult is null", directoryResult);
				
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull("Expecting data returned but list is null", list);
		String msg = "Expecting list.size() == 0, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 0);		
		assertTrue("Expected to be at the end of the pages ", directoryResult.isAtEnd());
	}	

	@Override
	protected QueryService<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> getService() {
		return this.service;
	}

	@Override
	protected EntityDescriptionQuery getQuery() {
		return new EntityDescriptionQuery(){

			@Override
			public Query getQuery() {
				return null;
			}

			@Override
			public Set<ResolvedFilter> getFilterComponent() {
				return null;
			}

			@Override
			public ResolvedReadContext getReadContext() {
				return null;
			}

			@Override
			public EntitiesFromAssociationsQuery getEntitiesFromAssociationsQuery() {
				return null;
			}

			@Override
			public EntityDescriptionQueryServiceRestrictions getRestrictions() {
				EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
				restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
				
				return restrictions;
			}
			
		};
	}
}

