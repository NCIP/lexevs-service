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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class LexEvsMapQueryServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapQueryService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testLoadMap() {
		// Just a test to see time involved with the load
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testGetResourceListMapToRoleFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testGetResourceSummariesMapToRoleFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntrySummary> list = this.service.getResourceSummaries(mapQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(1,list.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testMapToRoleFoundForMultipleCodeSchemes() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
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
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testMapToRoleNotFoundForMultipleCodeSchemes() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
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
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testGetResourceListMapToRoleNotFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testGetResourceSummariesMapToRoleNotFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_TO_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntrySummary> list = this.service.getResourceSummaries(mapQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(0,list.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testMapFromRoleFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testMapFromRoleNotFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("GermanMadeParts");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testBothMapRolesFoundViaMapTo() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
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
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testBothMapRolesFoundViaMapFrom() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
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
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}


	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testBothMapRolesNotFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
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
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapCatalogEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testCountUsingMapFromRoleRestrictionOneFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
		NameOrURI codeSchemeName = ModelUtils.nameOrUriFromName("Automobiles");
		Set<NameOrURI> codeSystems = new HashSet<NameOrURI>();
		codeSystems.add(codeSchemeName);
		CodeSystemRestriction csr = new CodeSystemRestriction();
		csr.setCodeSystems(codeSystems);
		MapRole mapRole = MapRole.fromValue(Constants.MAP_FROM_ROLE);
		csr.setMapRole(mapRole);
		restrictions.setCodeSystemRestriction(csr);
		
		MapQueryImpl mapQueryImpl = new MapQueryImpl(null,null,null,restrictions);
		
		assertEquals(1,this.service.count(mapQueryImpl));
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	public void testCountUsingNoMapFromRoleRestrictionsOneFound() {
		
		MapQueryServiceRestrictions restrictions = new MapQueryServiceRestrictions();
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

}
