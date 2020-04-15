/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

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
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.model.mapversion.MapVersionListEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.RestrictionType;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions.EntitiesRestriction;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")})
public class LexEvsMapVersionQueryServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapVersionQueryService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	// Test cases using CTS2 CodeSystemRestriction as the only service restrictions
	@Test
	public void testGetResourceListMapToRoleFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceSummariesMapToRoleFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionDirectoryEntry> list = this.service.getResourceSummaries(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(1,list.getEntries().size());
	}

	@Test
	public void testMapToRoleFoundForMultipleCodeSchemesUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName1 = ModelUtils.nameOrUriFromName("DeutchMadeParts");
		NameOrURI codeSchemeName2 = ModelUtils.nameOrUriFromName("GermanMadeParts");
		NameOrURI codeSchemeName3 = ModelUtils.nameOrUriFromName("DEMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName1);
		codeSystems.add(codeSchemeName2);
		codeSystems.add(codeSchemeName3);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testMapToRoleNotFoundForMultipleCodeSchemesUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName1 = ModelUtils.nameOrUriFromName("DeutchMadeParts");
		NameOrURI codeSchemeName2 = ModelUtils.nameOrUriFromName("Das GermanMadeParts");
		NameOrURI codeSchemeName3 = ModelUtils.nameOrUriFromName("DEMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName1);
		codeSystems.add(codeSchemeName2);
		codeSystems.add(codeSchemeName3);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleNotFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceSummariesMapToRoleNotFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionDirectoryEntry> list = this.service.getResourceSummaries(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(0,list.getEntries().size());
	}

	@Test
	public void testMapFromRoleFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testMapFromRoleNotFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
	public void testBothMapRolesFoundViaMapToUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName1 = ModelUtils.nameOrUriFromName("ItalianMadeParts");
		NameOrURI codeSchemeName2 = ModelUtils.nameOrUriFromName("GermanMadeParts");
		NameOrURI codeSchemeName3 = ModelUtils.nameOrUriFromName("FrenchMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName1);
		codeSystems.add(codeSchemeName2);
		codeSystems.add(codeSchemeName3);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.BOTH_MAP_ROLES);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testBothMapRolesFoundViaMapFromUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName1 = ModelUtils.nameOrUriFromName("ItalianMadeParts");
		NameOrURI codeSchemeName2 = ModelUtils.nameOrUriFromName("Automobiles");
		NameOrURI codeSchemeName3 = ModelUtils.nameOrUriFromName("FrenchMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName1);
		codeSystems.add(codeSchemeName2);
		codeSystems.add(codeSchemeName3);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.BOTH_MAP_ROLES);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}


	@Test
	public void testBothMapRolesNotFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName1 = ModelUtils.nameOrUriFromName("ItalianMadeParts");
		NameOrURI codeSchemeName2 = ModelUtils.nameOrUriFromName("AutomobileParts");
		NameOrURI codeSchemeName3 = ModelUtils.nameOrUriFromName("FrenchMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName1);
		codeSystems.add(codeSchemeName2);
		codeSystems.add(codeSchemeName3);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.BOTH_MAP_ROLES);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
	public void testCountUsingMapFromRoleRestrictionOneFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		assertEquals(1,this.service.count(MapVersionQueryImpl));
	}

	@Test
	public void testCountUsingNoMapFromRoleRestrictionsOneFoundUsingCts2CodeSystemRestrictionOnly() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		assertEquals(1,this.service.count(null));
	}

	
	// Test cases using CTS2 EntitiesRestriction as the only service restrictions
	@Test
	public void testGetResourceListMapFromRoleFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapFromRoleFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		ScopedEntityName scopedEntityName_005 = ModelUtils.createScopedEntityName("005", "Automobiles");
		EntityNameOrURI entityNameOrURI_005 = ModelUtils.entityNameOrUriFromName(scopedEntityName_005);
		entitiesSet.add(entityNameOrURI_005);
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Ford", "Automobiles");
		EntityNameOrURI entityNameOrURI_Ford = ModelUtils.entityNameOrUriFromName(scopedEntityName_Ford);
		entitiesSet.add(entityNameOrURI_Ford);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapFromRoleNotFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_BOGUS = ModelUtils.createScopedEntityName("BOGUS", "Automobiles");
		EntityNameOrURI entityNameOrURI_BOGUS = ModelUtils.entityNameOrUriFromName(scopedEntityName_BOGUS);
		entitiesSet.add(entityNameOrURI_BOGUS);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapFromRoleNotFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		// DAWG does not exist in Map 
		ScopedEntityName scopedEntityName_Dawg = ModelUtils.createScopedEntityName("Dawgie", "Automobiles");
		EntityNameOrURI entityNameOrURI_Dawg = ModelUtils.entityNameOrUriFromName(scopedEntityName_Dawg);
		entitiesSet.add(entityNameOrURI_Dawg);
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Ford", "Automobiles");
		EntityNameOrURI entityNameOrURI_Ford = ModelUtils.entityNameOrUriFromName(scopedEntityName_Ford);
		entitiesSet.add(entityNameOrURI_Ford);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListMapFromRoleFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapFromRoleFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAnd3Entities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		ScopedEntityName scopedEntityName_005 = ModelUtils.createScopedEntityName("005", "Automobiles");
		EntityNameOrURI entityNameOrURI_005 = ModelUtils.entityNameOrUriFromName(scopedEntityName_005);
		entitiesSet.add(entityNameOrURI_005);
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Ford", "Automobiles");
		EntityNameOrURI entityNameOrURI_Ford = ModelUtils.entityNameOrUriFromName(scopedEntityName_Ford);
		entitiesSet.add(entityNameOrURI_Ford);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapFromRoleNotFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_BOGUS = ModelUtils.createScopedEntityName("BOGUS", "Automobiles");
		EntityNameOrURI entityNameOrURI_BOGUS = ModelUtils.entityNameOrUriFromName(scopedEntityName_BOGUS);
		entitiesSet.add(entityNameOrURI_BOGUS);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapFromRoleFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		// Dawgie does not exist in Map 
		ScopedEntityName scopedEntityName_Dawg = ModelUtils.createScopedEntityName("Dawgie", "Automobiles");
		EntityNameOrURI entityNameOrURI_Dawg = ModelUtils.entityNameOrUriFromName(scopedEntityName_Dawg);
		entitiesSet.add(entityNameOrURI_Dawg);
		// Fordzilla does not exist in Map
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Fordzilla", "Automobiles");
		EntityNameOrURI entityNameOrURI_Ford = ModelUtils.entityNameOrUriFromName(scopedEntityName_Ford);
		entitiesSet.add(entityNameOrURI_Ford);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	// --- MAP_TO_ROLE 
	@Test
	public void testGetResourceListMapToRoleFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_E0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_E0001);
		entitiesSet.add(entityNameOrURI_E0001);
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_P0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_P0001);
		entitiesSet.add(entityNameOrURI_P0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleNotFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_BOGUS = ModelUtils.createScopedEntityName("BOGUS", "Automobiles");
		EntityNameOrURI entityNameOrURI_BOGUS = ModelUtils.entityNameOrUriFromName(scopedEntityName_BOGUS);
		entitiesSet.add(entityNameOrURI_BOGUS);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleNotFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		// Dawgie does not exist in Map 
		ScopedEntityName scopedEntityName_Dawg = ModelUtils.createScopedEntityName("Dawgie", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_Dawg = ModelUtils.entityNameOrUriFromName(scopedEntityName_Dawg);
		entitiesSet.add(entityNameOrURI_Dawg);
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_E0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_E0001);
		entitiesSet.add(entityNameOrURI_E0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);

		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListMapToRoleFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_E0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_E0001);
		entitiesSet.add(entityNameOrURI_E0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAnd3Entities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_E0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_E0001);
		entitiesSet.add(entityNameOrURI_E0001);
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_P0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_P0001);
		entitiesSet.add(entityNameOrURI_P0001);

		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleNotFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_BOGUS = ModelUtils.createScopedEntityName("BOGUS", "Automobiles");
		EntityNameOrURI entityNameOrURI_BOGUS = ModelUtils.entityNameOrUriFromName(scopedEntityName_BOGUS);
		entitiesSet.add(entityNameOrURI_BOGUS);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_P0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_P0001);
		entitiesSet.add(entityNameOrURI_P0001);
		// Dawgie does not exist in Map 
		ScopedEntityName scopedEntityName_Dawg = ModelUtils.createScopedEntityName("Dawgie", "Automobiles");
		EntityNameOrURI entityNameOrURI_Dawg = ModelUtils.entityNameOrUriFromName(scopedEntityName_Dawg);
		entitiesSet.add(entityNameOrURI_Dawg);
		// Fordzilla does not exist in Map
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Fordzilla", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_Ford = ModelUtils.entityNameOrUriFromName(scopedEntityName_Ford);
		entitiesSet.add(entityNameOrURI_Ford);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	//  MapRole.BOTH_MAP_ROLES
	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneTargetEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneSourceEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_P0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_P0001);
		entitiesSet.add(entityNameOrURI_P0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesNotFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_BOGUS = ModelUtils.createScopedEntityName("BOGUS", "Automobiles");
		EntityNameOrURI entityNameOrURI_BOGUS = ModelUtils.entityNameOrUriFromName(scopedEntityName_BOGUS);
		entitiesSet.add(entityNameOrURI_BOGUS);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesNotFoundUsingCts2EntitiesRestrictionOnlyAllMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		// Dawgie does not exist in Map 
		ScopedEntityName scopedEntityName_Dawg = ModelUtils.createScopedEntityName("Dawgie", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_Dawg = ModelUtils.entityNameOrUriFromName(scopedEntityName_Dawg);
		entitiesSet.add(entityNameOrURI_Dawg);
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_E0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_E0001);
		entitiesSet.add(entityNameOrURI_E0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);

		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneTargetEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_E0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_E0001);
		entitiesSet.add(entityNameOrURI_E0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneSourceEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAnd3Entities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_R0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_R0001);
		entitiesSet.add(entityNameOrURI_R0001);
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		entitiesSet.add(entityNameOrURI_A0001);
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_P0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_P0001);
		entitiesSet.add(entityNameOrURI_P0001);

		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesNotFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndOneEntity() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_BOGUS = ModelUtils.createScopedEntityName("BOGUS", "Automobiles");
		EntityNameOrURI entityNameOrURI_BOGUS = ModelUtils.entityNameOrUriFromName(scopedEntityName_BOGUS);
		entitiesSet.add(entityNameOrURI_BOGUS);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListBothMapRolesFoundUsingCts2EntitiesRestrictionOnlyAtLeastOneMappedAndMultipleEntities() {
		
		MapVersionQueryServiceRestrictions restrictions = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_P0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_P0001);
		entitiesSet.add(entityNameOrURI_P0001);
		// Dawgie does not exist in Map 
		ScopedEntityName scopedEntityName_Dawg = ModelUtils.createScopedEntityName("Dawgie", "Automobiles");
		EntityNameOrURI entityNameOrURI_Dawg = ModelUtils.entityNameOrUriFromName(scopedEntityName_Dawg);
		entitiesSet.add(entityNameOrURI_Dawg);
		// Fordzilla does not exist in Map
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Fordzilla", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI_Ford = ModelUtils.entityNameOrUriFromName(scopedEntityName_Ford);
		entitiesSet.add(entityNameOrURI_Ford);
		entitiesRestriction.setEntities(entitiesSet);
		restrictions.setEntitiesRestriction(entitiesRestriction);
		
		MapVersionQueryImpl MapVersionQueryImpl = new MapVersionQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapVersionListEntry> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}
	

}
