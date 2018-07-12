/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

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
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.map.MapCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */	
@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}
	
	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}

	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(1,resourceList.getEntries().size());
	}


	@Test
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
		
		DirectoryResult<MapCatalogEntryListEntry> resourceList = this.service.getResourceList(mapQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		assertEquals(0,resourceList.getEntries().size());
	}


	@Test
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
