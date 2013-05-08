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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.AssociationQueryServiceRestrictions;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class LexEvsAssociationQueryServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsAssociationQueryService service;
	
	private Cts2Marshaller marshaller = new DelegatingMarshaller();
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	public void testResourceSummaries() throws Exception {
		final String srcEntityName = "A0001";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace("Automobiles");
		srcEntityNameOrURI.setEntityName(seName);
	
		AssociationQueryServiceRestrictions restrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		restrictions.setCodeSystemVersion(csvName);
		restrictions.setSourceEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery =
			new AssociationQueryImpl(null,null,null,restrictions);
		Page page = new Page();
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = 
			this.service.getResourceSummaries(assnQuery, null, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals(5,resourceSummaries.getEntries().size());
		assertTrue(resourceSummaries.isAtEnd());
	}
	
	@Test
	public void testResourceSummariesNone() throws Exception {
		final String srcEntityName = "Jaguar";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace("Automobiles");
		srcEntityNameOrURI.setEntityName(seName);
	
		AssociationQueryServiceRestrictions restrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		restrictions.setCodeSystemVersion(csvName);
		restrictions.setSourceEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery =
			new AssociationQueryImpl(null,null,null,restrictions);
		Page page = new Page();
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = 
			this.service.getResourceSummaries(assnQuery, null, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals(0,resourceSummaries.getEntries().size());
		assertTrue(resourceSummaries.isAtEnd());
	}
	
	@Test
	public void testResourceSummariesTarget() throws Exception {
		final String srcEntityName = "Jaguar";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace("Automobiles");
		srcEntityNameOrURI.setEntityName(seName);
	
		AssociationQueryServiceRestrictions restrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		restrictions.setCodeSystemVersion(csvName);
		restrictions.setTargetEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery =
			new AssociationQueryImpl(null,null,null,restrictions);
		Page page = new Page();
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = 
			this.service.getResourceSummaries(assnQuery, null, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals(1,resourceSummaries.getEntries().size());
		assertTrue(resourceSummaries.isAtEnd());
	}
	
	@Test
	public void testResourceSummariesWithLimit() throws Exception {
		final String srcEntityName = "A0001";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace("Automobiles");
		srcEntityNameOrURI.setEntityName(seName);
	
		AssociationQueryServiceRestrictions restrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		restrictions.setCodeSystemVersion(csvName);
		restrictions.setSourceEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery =
			new AssociationQueryImpl(null,null,null,restrictions);
		Page page = new Page();
		page.setMaxToReturn(2);
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = 
			this.service.getResourceSummaries(assnQuery, null, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals(2,resourceSummaries.getEntries().size());
		assertFalse(resourceSummaries.isAtEnd());
	}
	
	@Test
	public void testResourceSummariesOverLimit() throws Exception {
		final String srcEntityName = "A0001";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace("Automobiles");
		srcEntityNameOrURI.setEntityName(seName);
	
		AssociationQueryServiceRestrictions restrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		restrictions.setCodeSystemVersion(csvName);
		restrictions.setSourceEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery =
			new AssociationQueryImpl(null,null,null,restrictions);
		Page page = new Page();
		page.setPage(10);
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = 
			this.service.getResourceSummaries(assnQuery, null, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals(0,resourceSummaries.getEntries().size());
		assertTrue(resourceSummaries.isAtEnd());
	}

	@Test
	public void testResourceSummariesValidXml() throws Exception {
		final String srcEntityName = "A0001";  // LexEVS entity code for entity description "Automobile"
		NameOrURI csvName = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		EntityNameOrURI srcEntityNameOrURI = new EntityNameOrURI();
		ScopedEntityName seName = new ScopedEntityName();
		seName.setName(srcEntityName);
		seName.setNamespace("Automobiles");
		srcEntityNameOrURI.setEntityName(seName);
	
		AssociationQueryServiceRestrictions restrictions = new AssociationQueryServiceRestrictions();
		// Add the source graphNode - using [A0001] Automobile
		restrictions.setCodeSystemVersion(csvName);
		restrictions.setSourceEntity(srcEntityNameOrURI);
		
		AssociationQueryImpl assnQuery =
			new AssociationQueryImpl(null,null,null,restrictions);
		Page page = new Page();
		
		DirectoryResult<AssociationDirectoryEntry> resourceSummaries = 
			this.service.getResourceSummaries(assnQuery, null, page);
		
		assertNotNull(resourceSummaries);		
		assertEquals(5,resourceSummaries.getEntries().size());
		
		for(AssociationDirectoryEntry entry : resourceSummaries.getEntries()){
			this.marshaller.marshal(entry, new StreamResult(new StringWriter()));
		}
	}
}
