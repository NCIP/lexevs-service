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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractQueryServiceTest;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions.HierarchyRestriction;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions.HierarchyRestriction.HierarchyType;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class LexEvsAssociationEntityQueryServiceTestIT 
	extends AbstractQueryServiceTest<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery> {
	
	@Resource
	private LexEvsAssociationEntityQueryService service;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Override
	protected QueryService<EntityDescription, EntityDirectoryEntry, EntityDescriptionQuery> getService() {
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

