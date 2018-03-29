/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
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
@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
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
