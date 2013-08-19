/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetResolutionEntityRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResult;

@LoadContents({
	@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml"),
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")})
public class LexEVSResolvedValuesetResolutionServiceTestIT 
	extends AbstractTestITBase {

	@Resource
	private ResolvedValueSetResolutionService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetResolution() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	@Test
	public void testGetResolutionNotFoundDefintion() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("__INVALID__"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		assertNull(dirResult);
	}
	
	@Test
	public void testGetResolutionNotFoundDefintionValueSet() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("__INVALID__"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		assertNull(dirResult);
	}
	
	@Test
	public void testGetResolutionNotFoundId() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("__INVALID__",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		assertNull(dirResult);
	}

	@Test
	public void testGetResolutionEntitiesNoFilter() {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, null, null, new Page());
		assertTrue(dirResult.getEntries().size() > 0);
	}

	@Test
	public void testGetResolutionEntitiesNoFilterValidXML() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, null, null, new Page());
		
		assertTrue(dirResult.getEntries().size() > 0);
		
		for(EntityDirectoryEntry entry : dirResult.getEntries()){
			StreamResult result = new StreamResult(new StringWriter());
			marshaller.marshal(entry, result);	
		}
	}
	
	@Test
	public void testGetResolutionEntitiesWithFilter() {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
		  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
		  		"GM");
		
		ResolvedValueSetResolutionQueryImpl query= new ResolvedValueSetResolutionQueryImpl();
		query.setFilterComponent(filter);
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, query, null, new Page());
		assertTrue(dirResult.getEntries().size() > 0);

	}	
	
	
	
	@Test
	public void testGetResolutionEntitiesWithEntityRestriction() {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		ResolvedValueSetResolutionQueryImpl query= new ResolvedValueSetResolutionQueryImpl();
		ResolvedValueSetResolutionEntityRestrictions entityRestriction= new ResolvedValueSetResolutionEntityRestrictions();
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		//scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("GM");
		entity.setEntityName(scopedEntityName);
		Set<EntityNameOrURI> entities= new HashSet<EntityNameOrURI>();
		entities.add(entity);
		entityRestriction.setEntities(entities);
		query.setResolvedValueSetResolutionEntityRestrictions(entityRestriction);
		query.setResolvedValueSetResolutionEntityRestrictions(entityRestriction);
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, query, null, new Page());
		assertTrue(dirResult.getEntries().size() > 0);

	}	
	
	@Test
	public void testGetResolutionValidXML() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		ResolvedValueSetResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		for (EntitySynopsis synopsis: dirResult.getEntries()) {
			
			StringWriter sw = new StringWriter();
			StreamResult result= new StreamResult(sw);
			marshaller.marshal(synopsis, result);
		}		
	}
	
	@Test
	public void testGetResolutionValidHasValidHeader() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
		  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
		  		"GM");
		
		ResolvedValueSetResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, filter, new Page());

		assertNotNull(dirResult.getResolvedValueSetHeader());
		
		StreamResult result = new StreamResult(new StringWriter());
		marshaller.marshal(dirResult.getResolvedValueSetHeader(), result);		
	}
	
	@Test
	public void testGetResolutionHasCorrectHrefs() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
		  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
		  		"GM");
		
		ResolvedValueSetResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, filter, new Page());

		EntitySynopsis synopsis = dirResult.getEntries().get(0);
		String href = synopsis.getHref();
			
		assertNotNull(href);
		assertEquals("http://localhost:8080/webapp/codesystem/Automobiles/version/1.0/entity/Automobiles:GM", href);
	}
	
	@Test
	public void testGetResolutionHasCorrectUri() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("1",
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"),
				ModelUtils.nameOrUriFromName("5ER0"));
		
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
		  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
		  		"GM");
		
		ResolvedValueSetResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, filter, new Page());

		EntitySynopsis synopsis = dirResult.getEntries().get(0);
		String uri = synopsis.getUri();
		
		assertEquals("urn:oid:11.11.0.1:GM", uri);		
	}
	
}
