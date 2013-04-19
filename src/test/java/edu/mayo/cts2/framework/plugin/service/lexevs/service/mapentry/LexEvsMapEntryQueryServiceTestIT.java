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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.MapEntryQueryServiceRestrictions;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */	
@LoadContents(
	{
		@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
		@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
		@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	}
)
public class LexEvsMapEntryQueryServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapEntryQueryService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetResourceListValidMapNoRestrictions() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
				
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(6,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListInvalidMapVersionNoRestrictions() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-2.2");
		restrictions.setMapVersion(mapVersion);
				
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListInvalidMapNameNoRestrictions() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSampleFOO-1.0");
		restrictions.setMapVersion(mapVersion);
				
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListMapAndRestrictedToOneValidEntity() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
		Set<EntityNameOrURI> targetEntities = new HashSet<EntityNameOrURI>();
		
		EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
		ScopedEntityName entityName = new ScopedEntityName(); //"A0001";
		entityName.setName("A0001");
		entityName.setNamespace("Automobiles");
		entityNameOrURI.setEntityName(entityName);
//		String uri;
//		entityNameOrURI.setUri(uri);
		targetEntities.add(entityNameOrURI);
		
		restrictions.setTargetEntities(targetEntities );
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListMapAndRestrictedToOneInvalidEntityName() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
		Set<EntityNameOrURI> targetEntities = new HashSet<EntityNameOrURI>();
		
		EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
		ScopedEntityName entityName = new ScopedEntityName(); //"A0001";
		entityName.setName("A0001FOO");
		entityName.setNamespace("Automobiles");
		entityNameOrURI.setEntityName(entityName);
//		String uri;
//		entityNameOrURI.setUri(uri);
		targetEntities.add(entityNameOrURI);
		
		restrictions.setTargetEntities(targetEntities );
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListMapAndRestrictedToOneInvalidEntityNamespace() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
		Set<EntityNameOrURI> targetEntities = new HashSet<EntityNameOrURI>();
		
		EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
		ScopedEntityName entityName = new ScopedEntityName(); //"A0001";
		entityName.setName("A0001");
		entityName.setNamespace("AutomobilesFOO");
		entityNameOrURI.setEntityName(entityName);
	
//		String uri;
//		entityNameOrURI.setUri(uri);
		targetEntities.add(entityNameOrURI);
		
		restrictions.setTargetEntities(targetEntities );
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}

	
//	@Test
//	public void testGetResourceSummariesMapToRoleFound() {
//		
//		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
//		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
//		restrictions.setMapVersion(mapVersion);
//		
//		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
//		
//		SortCriteria sortCriteria = null;
//		Page page = new Page();
//		
//		DirectoryResult<MapEntryDirectoryEntry> list = this.service.getResourceSummaries(mapEntryQueryImpl, sortCriteria, page);
//		assertNotNull(list);
//		assertEquals(1,list.getEntries().size());
//	}
//
}
