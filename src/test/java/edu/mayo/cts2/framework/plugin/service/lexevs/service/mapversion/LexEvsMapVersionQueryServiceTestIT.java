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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.RestrictionType;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions.EntityRestriction;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions.EntitiesRestriction;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
public class LexEvsMapVersionQueryServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapVersionQueryService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	// TODO Uncomment or delete CodeSystemRestrictions test cases below after we determine if its use is valid
	//    and if valid the LexEvsMapVersionQueryService's code has been implemented for CodeSystemRestrictions
	
	// Test cases using CodeSystemRestriction as the only service restrictions
/*	
	@Test
	public void testGetResourceListMapToRoleFound() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testGetResourceSummariesMapToRoleFound() {
		
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
	public void testMapToRoleFoundForMultipleCodeSchemes() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testMapToRoleNotFoundForMultipleCodeSchemes() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceListMapToRoleNotFound() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
	public void testGetResourceSummariesMapToRoleNotFound() {
		
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
	public void testMapFromRoleFound() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testMapFromRoleNotFound() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
	public void testBothMapRolesFoundViaMapTo() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	public void testBothMapRolesFoundViaMapFrom() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}


	@Test
	public void testBothMapRolesNotFound() {
		
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
		
		DirectoryResult<MapVersion> resourceList = this.service.getResourceList(MapVersionQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
	public void testCountUsingMapFromRoleRestrictionOneFound() {
		
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
	public void testCountUsingNoMapFromRoleRestrictionsOneFound() {
		
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
	
*/	

	// TODO Test data and case scenarios for MapVersionQueryServiceRestrictions.EntityRestriction assuming MapVersionQueryService
	//    will be implemented to filter on MapVersionQueryServiceRestrictions.EntityRestriction object. Test cases will need
	//    to cover each data object on its own if applicable and then eventually test cases will need to combine different
	//    test data objects.  
	public void testFootNotes() {	
		MapVersionQueryServiceRestrictions mvqsr = new MapVersionQueryServiceRestrictions();
		EntitiesRestriction entitiesRestriction = new EntitiesRestriction();
		
		// MapRole test values to consider
		entitiesRestriction.setMapRole(MapRole.BOTH_MAP_ROLES);
		entitiesRestriction.setMapRole(MapRole.MAP_FROM_ROLE);
		entitiesRestriction.setMapRole(MapRole.MAP_TO_ROLE);
		
		
		// MapStatus test values to consider
		entitiesRestriction.setMapStatus(MapStatus.ALLMAPENTRIES);		
		entitiesRestriction.setMapStatus(MapStatus.MAPPED);		
		entitiesRestriction.setMapStatus(MapStatus.NOMAP);		
		entitiesRestriction.setMapStatus(MapStatus.UNMAPPED);		
		
		
		// Set<EntityNameOrURI> entities test values to consider
		Set<EntityNameOrURI> entities = entitiesRestriction.getEntities();
		Set<EntityNameOrURI> entitiesSet = new HashSet<EntityNameOrURI>();
		// Valid sourceEntity (MapRole.MAP_FROM_ROLE) examples
		ScopedEntityName scopedEntityName_A0001 = ModelUtils.createScopedEntityName("A0001", "Automobiles");
		EntityNameOrURI entityNameOrURI_A0001 = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		ScopedEntityName scopedEntityName_Jaguar = ModelUtils.createScopedEntityName("Jaguar", "Automobiles");
		EntityNameOrURI entityNameOrURI_Jaguar = ModelUtils.entityNameOrUriFromName(scopedEntityName_Jaguar);
		ScopedEntityName scopedEntityName_005 = ModelUtils.createScopedEntityName("005", "Automobiles");
		ScopedEntityName scopedEntityName_Ford = ModelUtils.createScopedEntityName("Ford", "Automobiles");
		// Valid targetEntity (MapRole.MAP_TO_ROLE) examples:
		//   R0001 maps to single sourceEntity A0001
		//   E0001 maps to 3 sourceEntities:  Jaguar, Ford and C0001
		//   P0001 maps to 2 sourceEntities:  005 and C0002
		ScopedEntityName scopedEntityName_R0001 = ModelUtils.createScopedEntityName("R0001", "GermanMadePartsNamespace");
		ScopedEntityName scopedEntityName_E0001 = ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace");
		ScopedEntityName scopedEntityName_P0001 = ModelUtils.createScopedEntityName("P0001", "GermanMadePartsNamespace");
		EntityNameOrURI entityNameOrURI = ModelUtils.entityNameOrUriFromName(scopedEntityName_A0001);
		
		// RestrictionType test values to consider
		entitiesRestriction.setAllOrSome(RestrictionType.ALL);
		entitiesRestriction.setAllOrSome(RestrictionType.AT_LEAST_ONE);
		
	}
}
