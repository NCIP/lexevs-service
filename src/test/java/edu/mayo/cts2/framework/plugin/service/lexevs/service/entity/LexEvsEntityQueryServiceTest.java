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

import java.util.HashSet;
import java.util.Set;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsData.DataField;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsSystem;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;


/**
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsEntityQueryServiceTest {
	// Setup mocked environment
	// -------------------------
	public LexEvsEntityQueryService createService(
			FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs, 
			boolean withData) throws Exception{
		
		LexEvsEntityQueryService service = new LexEvsEntityQueryService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = fakeLexEvs.createMockedLexBIGServiceWithFakeLexEvsData(service, withData);
		
		service.setLexBigService(lexBigService);

		// Overwrite objects in service object 
		service.setEntityTransformer(new EntityTransform());
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		
		return service;
	}
	

	// =============
	// Test methods
	// =============
	
	// Count with VALID and INVALID filters
	// ------------------------------------
	@Test
	public void testCount_Filter_About_Contains() throws Exception {
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		// Restrict to given codeSystem
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);
				
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), testValidData);		
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, DataField.ABOUT, 
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), !testValidData);		
	}
	
	@Test
	public void testCount_Filter_ResorceSynopsis_StartsWith() throws Exception {
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		// Restrict to given codeSystem
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);

		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), testValidData);
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, DataField.RESOURCE_SYNOPSIS, 					
				StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), !testValidData);
	}
		
	@Test
	public void testCount_Filter_ResourceName_ExactMatch() throws Exception {
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service = this.createService(fakeLexEvs, true); 
		boolean testValidData = true;
		
		// Restrict to given codeSystem
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);

		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, DataField.RESOURCE_NAME, 
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), testValidData);		
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, DataField.RESOURCE_NAME, 
				StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), !testValidData);		
	}
		
	// Count with All VALID Default filters
	// -------------------------------------
	@Test
	public void testCount_FilterDefault_PropertyReferencesValidIndex_AllSchemes() throws Exception {
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service = this.createService(fakeLexEvs, true); 

		
		// Restrict to given codeSystem
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);

		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, true, true, true);		
	}

	// Count with VALID values with one MISMATCHED
	// --------------------------------------------
	@Test
	public void testCount_FilterDefault_PropertyReferencesWrongIndex_AllSchemes() throws Exception {
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service = this.createService(fakeLexEvs, true); 
		
		// Restrict to given codeSystem
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);

		// About wrong index
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, false, true, true);
		// ResourceSynopsis wrong index
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, true, false, true);
		// ResourceName wrong index
		fakeLexEvs.executeCountForEachExistingCodeSchemeWithDefaultFilterCreated(service, query, true, true, false);
	}
	

	// --------------------------------------------
	@Test
	public void testGetResourceSummaries_NoFilter_SchemeCountsFrom1to21_PageSizesFrom1to50_Pages() throws Exception {
		int maxSchemeCount = 21;
		int maxPageSize = 50;
		
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service;
		
		EntityDescriptionQuery query = null;
		DirectoryResult<EntityDirectoryEntry> directoryResult = null;
		
		Page page = new Page();
		int lastPage;
		
		for(int schemeCount=1; schemeCount <= maxSchemeCount; schemeCount++){
			fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>(schemeCount);		
			service = this.createService(fakeLexEvs, true); 
			for(int pageSize=1; pageSize <= maxPageSize; pageSize++){
				page.setMaxToReturn(pageSize);
				lastPage = fakeLexEvs.calculatePagePastLastPage(fakeLexEvs.size(), page.getMaxToReturn());
				
				query = new EntityDescriptionQueryImpl(null, null, null);
				directoryResult = null; 
				
				fakeLexEvs.executeGetResourceSummariesForEachPage(service, directoryResult, query, page, lastPage);		
			}
		}
	}
	@Test
	public void testGetResourceSummaries_DeepCompare_PropertyReferences_MatchingAlgorithms_Pages_CodindgSchemes_Substrings() throws Exception {
		Page page = new Page();		
		FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService> fakeLexEvs;
		fakeLexEvs = new FakeLexEvsSystem<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery, LexEvsEntityQueryService>();
		LexEvsEntityQueryService service = this.createService(fakeLexEvs, true);
		
		// Test one page past possible pages to ensure 0 is returned.
		int lastPage = fakeLexEvs.calculatePagePastLastPage(fakeLexEvs.size(), page.getMaxToReturn());

		// Restrict to given codeSystem
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);
		DirectoryResult<EntityDirectoryEntry> directoryResult = null;
		
		fakeLexEvs.executeGetResourceSummariesWithDeepComparisonForEachPropertyReference(service, directoryResult, query, page, lastPage);		
	}
}
