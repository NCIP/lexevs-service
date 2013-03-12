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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
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
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.CodingSchemeToCodeSystemTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;


public class LexEvsEntityQueryServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsEntityQueryService service;
	
	@Resource
	private LexBIGService lbs;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
		assertNotNull(this.lbs);
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadByOfficialVersionId() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles");
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
			
		// Create a query from an anonymous EntityDescriptonQuery class
		// ------------------------------------------------------------
		EntityDescriptionQuery query = new EntityDescriptionQuery(){

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
				EntityDescriptionQueryServiceRestrictions query = new EntityDescriptionQueryServiceRestrictions();
				query.setCodeSystemVersion(name);
				
				return query;
			}
			
		};
		
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();		
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();
		assertTrue(list.size() > 0);		
	}

	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testFilter_startsWith() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
//		service.setEntityTransformer(new EntityTransform(){
//			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
//				return new EntityDirectoryEntry();
//			}
//		});
		
		// Create filters for query
		// ------------------------
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		
		ResolvedFilter filter = new ResolvedFilter();			
		filter.setMatchAlgorithmReference(new MatchAlgorithmReference("startsWith"));
		filter.setMatchValue("Jaguar");
		filter.setPropertyReference(null);				// Should this field be used??			
		filters.add(filter);
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query
		// ------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);	
		
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertTrue(list.size() > 0);
	}	


	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testFilter_contains() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
//		service.setEntityTransformer(new EntityTransform(){
//			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
//				return new EntityDirectoryEntry();
//			}
//		});
//		
		// Create filters for query
		// ------------------------
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		
		ResolvedFilter filter = new ResolvedFilter();			
		filter.setMatchAlgorithmReference(new MatchAlgorithmReference("contains"));
		filter.setMatchValue("GE");
		filter.setPropertyReference(null);				// Should this field be used??			
		filters.add(filter);
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query
		// ------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);	
		
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertTrue(list.size() > 0);
	}	

}

