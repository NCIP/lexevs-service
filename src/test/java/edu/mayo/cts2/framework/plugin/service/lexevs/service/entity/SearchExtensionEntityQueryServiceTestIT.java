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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.ActiveOrAll;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractQueryServiceTest;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
//@Ignore
@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")})
public class SearchExtensionEntityQueryServiceTestIT 
	extends AbstractQueryServiceTest<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> {
	
	final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles-1.0");
	
	@Resource
	private SearchExtensionEntityQueryService service;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	public void testGetEntitiesFromUriList() throws Exception {
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		
		for(String uri : Arrays.asList(
				"urn:oid:11.11.0.1:C0001",
				"urn:oid:11.11.0.1:A0001",
				"urn:oid:11.11.0.2:H0001"
				)){
			EntityNameOrURI nameOrUri = new EntityNameOrURI();
			nameOrUri.setUri(uri);
			
			restrictions.getEntities().add(nameOrUri);
		}

		// Create query, no filters
		// -------------------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		
		// Call getResourceSummaries from service
		// --------------------------------------
		Page page = new Page();		
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, null, page);
		
		// Test results
		// ------------
		assertEquals(3, directoryResult.getEntries().size());		
	}

	@Test
	public void testGetResourceList_ActiveEntities() throws Exception {
		
		// Test to return only active entities
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		ResolvedReadContext resolvedReadContext = new ResolvedReadContext();
		resolvedReadContext.setActive(ActiveOrAll.ACTIVE_ONLY);
		
		EntityDescriptionQueryImpl query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		query.setReadContext(resolvedReadContext);
				
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
		
		EntityDirectoryEntry entry = null;
		for(int x=0; x<list.size(); x ++){
			entry = list.get(x);
			// Oldsmobile entity ID is 73.  By default, it is set to inactive.
			if (entry.getName().getName().equals("73")){
				fail();
			}
		}

	}
	
	@Test
	public void testGetResourceList_ActiveAndInactiveEntities() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		// Test to return only active and inactive entities
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		ResolvedReadContext resolvedReadContext = new ResolvedReadContext();
		resolvedReadContext.setActive(ActiveOrAll.ACTIVE_AND_INACTIVE);
		
		EntityDescriptionQueryImpl query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		query.setReadContext(resolvedReadContext);
				
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
		
		EntityDirectoryEntry entry = null;
		boolean foundInactive = false;
		
		for(int x=0; x<list.size(); x ++){
			entry = list.get(x);
			// Oldsmobile entity ID is 73.  By default, it is set to inactive.
			if (!foundInactive && entry.getName().getName().equals("73")){
				foundInactive = true;
				break;				
			}
		}
		
		// if we didn't find an inactive entity, fail
		if (!foundInactive) {
			fail();
		}
	}
	
	
	@Test
	public void testGetResourceSummaries_Filter_contains() throws Exception {
		// Test filter contains on active entity (GE) (active is set by default).
		
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
		String msg = "Expecting list.size() = 1, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 1);		
	}
	
	@Test
	public void testGetResourceSummaries_Filter_containsTwoSearchTerms() throws Exception {
		// Test filter contains on active entity with 2 search terms (General Motors) (active is set by default).
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Create query
		// ------------
		EntityDescriptionQuery query = CommonTestUtils.createQuery("contains", "General Motors", "Automobiles-1.0");	
				
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
		String msg = "Expecting list.size() = 1, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 1);		
	}
	
	@Test
	public void testGetResourceSummaries_Filter_contains_Inactive() throws Exception {
		// Test filter contains on inactive entity (Oldsmobile).
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Create query
		// ------------
		EntityDescriptionQueryImpl query = (EntityDescriptionQueryImpl)CommonTestUtils.createQuery("contains", "Olds", "Automobiles-1.0");	
		
		ResolvedReadContext resolvedReadContext = new ResolvedReadContext();
		resolvedReadContext.setActive(ActiveOrAll.ACTIVE_AND_INACTIVE);
		query.setReadContext(resolvedReadContext);
				
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
		String msg = "Expecting list.size() = 1, instead list.size() = " + list.size();
		assertTrue(msg, list.size() == 1);	

	}	
	
	@Ignore
	@Override
	public void testQueryLists(){
		//no-op -- this service can't resolve lists -- will delegate to the other query service.
	}
	
	@Override
	protected QueryService<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> getService() {
		return this.service;
	}

	@Override
	protected EntityDescriptionQuery getQuery() {
		return new EntityDescriptionQueryImpl(null,null,new EntityDescriptionQueryServiceRestrictions());
	}

}

