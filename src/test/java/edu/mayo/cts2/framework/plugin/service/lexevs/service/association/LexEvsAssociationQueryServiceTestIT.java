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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.AssociationQueryServiceRestrictions;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class LexEvsAssociationQueryServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsAssociationQueryService service;
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testResourceSummaries() throws Exception {
		final String srcEntityName = "A0001";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace(csvName.getName());
		srcEntityNameOrURI.setEntityName(seName);
		//srcEntityNameOrURI.setUri(uri);
		
		Query query = new Query();
		query.setMatchAlgorithm(csvName);
		Set<ResolvedFilter> filterComponent = new HashSet<ResolvedFilter>();
		ResolvedReadContext readContext = null;		
		AssociationQueryServiceRestrictions assnQueryServiceRestrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		assnQueryServiceRestrictions.setCodeSystemVersion(csvName);
		assnQueryServiceRestrictions.setSourceEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery = new AssociationQueryImpl(query,filterComponent,readContext,assnQueryServiceRestrictions);
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = this.service.getResourceSummaries(assnQuery, sortCriteria, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals("Unexpected # of Associations for Automobile A0005 - ",5,resourceSummaries.getEntries().size());
		assertEquals("Unexpected result of not being at the end of the paged results ",true, resourceSummaries.isAtEnd());
		
		page.setMaxToReturn(4);
		page.setPage(0);
		
		resourceSummaries = this.service.getResourceSummaries(assnQuery, sortCriteria, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals("Unexpected # of Associations for Automobile A0005 - ",4,resourceSummaries.getEntries().size());	
		assertEquals("Unexpected result of being at the end of the paged results ",false, resourceSummaries.isAtEnd());

		page.setMaxToReturn(4);
		page.setPage(1);
		
		resourceSummaries = this.service.getResourceSummaries(assnQuery, sortCriteria, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals("Unexpected # of Associations for Automobile A0005 - ",1,resourceSummaries.getEntries().size());	
		assertEquals("Unexpected result of not being at the end of the paged results for the last page ",true, resourceSummaries.isAtEnd());

	}
	

}
