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

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractQueryServiceTest;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions.HierarchyRestriction;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions.HierarchyRestriction.HierarchyType;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
public class LexEvsAssociationEntityQueryServiceTestIT 
	extends AbstractQueryServiceTest<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> {
	
	@Resource
	private LexEvsAssociationEntityQueryService service;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Override
	protected QueryService<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> getService() {
		return this.service;
	}
	
	@Test
	@Ignore
	@Override
	public void testCount(){
		//count not supported
	}
	
	@Test
	public void testValidateChildren(){
		DirectoryResult<EntityDirectoryEntry> results = 
			this.getService().getResourceSummaries(this.getQuery(), null, new Page());
		
		assertEquals(2, results.getEntries().size());
		assertTrue(results.isAtEnd());
	}

	@Override
	protected EntityDescriptionQuery getQuery() {
		EntityDescriptionQueryServiceRestrictions restrictions = 
			new EntityDescriptionQueryServiceRestrictions();
		HierarchyRestriction hierarchy = new HierarchyRestriction();
		hierarchy.setHierarchyType(HierarchyType.CHILDREN);
		
		EntityNameOrURI entity = new EntityNameOrURI();
		entity.setEntityName(ModelUtils.createScopedEntityName("A0001", "Automobiles"));
		hierarchy.setEntity(entity);
		restrictions.setHierarchyRestriction(hierarchy);
		
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
		
		return 
			new EntityDescriptionQueryImpl(null,null,restrictions);
	}

}

