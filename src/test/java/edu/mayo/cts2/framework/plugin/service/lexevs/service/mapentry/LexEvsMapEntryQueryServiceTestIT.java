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

import com.hp.hpl.jena.rdf.model.Model;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
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

	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	// Data to match values present in testMapping.xml
	String [] lexSchemeNames = {"Mapping Sample", "MappingSample", "Mapping:sample", "Mappings", "MappingSample"};
	String [] lexSchemeVersions = {"1.0"};
	String [] lexSchemeURIs = {"urn:oid:mapping:sample"};

	String [] lexAssociationNames = {"hasPart", "mapsTo"};
	
	String [] lexSourceNameSpaces = {"Automobiles", "Automobiles", "Automobiles", "Automobiles", "Automobiles", "Automobiles_Different_NS"};
	String [] lexTargetNameSpaces = {"GermanMadePartsNamespace", "GermanMadePartsNamespace",  "GermanMadePartsNamespace",  
			"GermanMadePartsNamespace",  "GermanMadePartsNamespace", "GermanMadePartsNamespace_Different_NS"};
	String [] lexSourceEntityCodes = {"Jaguar", "A0001", "C0001", "005", "Ford", "C0002"};
	String [] lexTargetEntityCodes = {"E0001", "R0001", "E0001", "P0001", "E0001", "P0001"};
	
	
	private String getTestValue(String fieldValue, boolean keepValid){
		String results = fieldValue;
		if(!keepValid){
			results = fieldValue + "FOO";
		}
		
		return results;
	}
	
	private void compareAllSchemeNames(MapEntryQueryServiceRestrictions restrictions, boolean [] fieldsValid){
		String schemeName;
		for(int nameIndex = 0; nameIndex < lexSchemeNames.length; nameIndex++){
			schemeName = this.getTestValue(lexSchemeNames[nameIndex], fieldsValid[0]);
			this.compareAllVersions(restrictions, schemeName, fieldsValid);
		}				
	}
	
	private void compareAllVersions(
			MapEntryQueryServiceRestrictions restrictions, 
			String lexSchemeName,
			boolean [] fieldsValid) {
		String cts2Name;
		String version;
		for(int versionIndex = 0; versionIndex < lexSchemeVersions.length; versionIndex++){
			version = this.getTestValue(lexSchemeVersions[versionIndex], fieldsValid[1]);
			cts2Name = lexSchemeName + "-" + version;
			
			NameOrURI mapVersion = ModelUtils.nameOrUriFromName(cts2Name);
			restrictions.setMapVersion(mapVersion);
			
			this.compareAllNamespaces(restrictions, fieldsValid);
//			}
//			else if(compareEntities){
//				this.compareAllSourceEntities(restrictions, null);
//			}
//			else{			
//				MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
//				
//				SortCriteria sortCriteria = null;
//				Page page = new Page();
//				
//				DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
//				assertNotNull(resourceList);
//				int expecting = lexSourceEntityCodes.length;
//				int actual = resourceList.getEntries().size();
//				assertEquals("Unexpected results using CTS2 Name (" + cts2Name + ")" + actual, expecting, actual);
//			}
		}
	}
	
	private void compareAllNamespaces(
			MapEntryQueryServiceRestrictions restrictions,
			boolean [] fieldsValid) {
		String namespace, name;
		for(int namespaceIndex = 0; namespaceIndex < lexSourceNameSpaces.length; namespaceIndex++){
			namespace = this.getTestValue(lexSourceNameSpaces[namespaceIndex], fieldsValid[2]);
			ScopedEntityName scopedEntityName = new ScopedEntityName();
			scopedEntityName.setNamespace(namespace);
			name = this.getTestValue(lexSourceEntityCodes[namespaceIndex], fieldsValid[3]);
			scopedEntityName.setName(name);
			
			this.compareEntity(restrictions, scopedEntityName);			
		}
	}

	private void compareAllSourceEntities(
			MapEntryQueryServiceRestrictions restrictions, 
			ScopedEntityName entityName,
			boolean [] fieldsValid) {
		ScopedEntityName scopedEntityName = (entityName != null) ? entityName : new ScopedEntityName();
		String name;
		for(int entityIndex = 0; entityIndex < lexSourceEntityCodes.length; entityIndex++){		
			name = this.getTestValue(lexSourceEntityCodes[entityIndex], fieldsValid[3]);
			scopedEntityName.setName(name);
			
			this.compareEntity(restrictions, scopedEntityName);			
		}
	}

	/**
	 * @param restrictions
	 * @param scopedEntityName
	 */
	private void compareEntity(MapEntryQueryServiceRestrictions restrictions,
			ScopedEntityName scopedEntityName) {
		Set<EntityNameOrURI> targetEntities = new HashSet<EntityNameOrURI>();
		EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
		entityNameOrURI.setEntityName(scopedEntityName);
		//		String uri;
		//		entityNameOrURI.setUri(uri);
		targetEntities.add(entityNameOrURI);
	
		restrictions.setTargetEntities(targetEntities);
	
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
	
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(resourceList);
		int expecting = this.calculateExpecting(restrictions);
		int actual = resourceList.getEntries().size();
		assertEquals("Unexpected results using: " + restrictions.getMapVersion().getName() + ", " +
				scopedEntityName.getNamespace() + ", " + scopedEntityName.getName(), expecting, actual);
	}

	/**
	 * @param restrictions
	 * @return
	 */
	private int calculateExpecting(MapEntryQueryServiceRestrictions restrictions) {
		int count = 0;
		String cts2CodeSystemName = restrictions.getMapVersion().getName();
		NameVersionPair nameVersion = this.versionNameConverter.fromCts2VersionName(cts2CodeSystemName);
		String lexCodeSchemeName = nameVersion.getName();
		String lexCodeSchemeVersion = nameVersion.getVersion();
		String namespace = null;
		String entity = null;
		
		if(restrictions != null){		
			Set<EntityNameOrURI> targetEntities = restrictions.getTargetEntities();
			for(EntityNameOrURI targetEntity : targetEntities){
				namespace = targetEntity.getEntityName().getNamespace();
				entity = targetEntity.getEntityName().getName();
				count += getOcccurrances(lexCodeSchemeName, lexCodeSchemeVersion, namespace, entity);
			}
			
		}
		else{
			count = getOcccurrances(lexCodeSchemeName, lexCodeSchemeVersion, namespace, entity);
		}
		return count;
	}


	private int getOcccurrances(String lexCodeSchemeName,
			String lexCodeSchemeVersion, String namespace, String entity) {
		int count = 0;
		
		for(int i=0; i < lexSchemeNames.length; i++){
			if(lexCodeSchemeName.equals(lexSchemeNames[i])){
				for(int j=0; j < lexSchemeVersions.length; j++){
					if(lexCodeSchemeVersion.equals(lexSchemeVersions[j])){
						count += this.getOccurances(namespace, entity);
					}
				}
			}
		}
		return count;
	}

	/**
	 * @param namespace
	 * @param entity
	 * @return
	 */
	private int getOccurances(String namespace, String entity) {
		int count = 0;
		for(int i=0; i < lexSourceNameSpaces.length; i++){
			if(namespace.equals(lexSourceNameSpaces[i]) &&
			   entity.equals(lexSourceEntityCodes[i])){
				count++;
			}
		}
		
		return count;
	}

	@Test
	public void testGetResourceListAllValid() {
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		boolean [] fieldsValid = {true, true, true, true};
		this.compareAllSchemeNames(restrictions, fieldsValid);
	}


	@Test
	public void testGetResourceListMapNameInvalid() {
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		boolean [] fieldsValid = {false, true, true, true};
		this.compareAllSchemeNames(restrictions, fieldsValid);
	}

	@Test
	public void testGetResourceListVersionInvalid() {
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		boolean [] fieldsValid = {true, false, true, true};
		this.compareAllSchemeNames(restrictions, fieldsValid);
	}

	@Test
	public void testGetResourceListNamespaceInvalid() {
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		boolean [] fieldsValid = {true, true, false, true};
		this.compareAllSchemeNames(restrictions, fieldsValid);
	}

	@Test
	public void testGetResourceListEntityInvalid() {
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		boolean [] fieldsValid = {true, true, true, false};
		this.compareAllSchemeNames(restrictions, fieldsValid);
	}

//
//	@Test
//	public void testGetResourceListCodeSchemeNameNoRestrictionsSchemeNameInvalid() {
//		
//		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
//		String cts2Name;
//		for(int nameIndex = 0; nameIndex < lexSchemeNames.length; nameIndex++){
//			for(int versionIndex = 0; versionIndex < lexSchemeVersions.length; versionIndex++){
//				cts2Name = lexSchemeNames[nameIndex] + "FOO-" + lexSchemeVersions[versionIndex];
//				NameOrURI mapVersion = ModelUtils.nameOrUriFromName(cts2Name);
//				restrictions.setMapVersion(mapVersion);
//				MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
//				
//				SortCriteria sortCriteria = null;
//				Page page = new Page();
//				
//				DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
//				assertNotNull(resourceList);
//				int expecting = 0;
//				int actual = resourceList.getEntries().size();
//				assertEquals("Unexpected results using CTS2 Name (" + cts2Name + ")" + actual, expecting, actual);
//			}
//		}				
//	}
//
//	@Test
//	public void testGetResourceListCodeSchemeNameNoRestrictionsVersionInvalid() {
//		
//		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
//		String cts2Name;
//		for(int nameIndex = 0; nameIndex < lexSchemeNames.length; nameIndex++){
//			for(int versionIndex = 0; versionIndex < lexSchemeVersions.length; versionIndex++){
//				cts2Name = lexSchemeNames[nameIndex] + "-" + lexSchemeVersions[versionIndex] + "999";
//				NameOrURI mapVersion = ModelUtils.nameOrUriFromName(cts2Name);
//				restrictions.setMapVersion(mapVersion);
//				MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
//				
//				SortCriteria sortCriteria = null;
//				Page page = new Page();
//				
//				DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
//				assertNotNull(resourceList);
//				int expecting = 0;
//				int actual = resourceList.getEntries().size();
//				assertEquals("Unexpected results using CTS2 Name (" + cts2Name + ")" + actual, expecting, actual);
//			}
//		}				
//	}
//
//	@Test
//	public void testGetResourceListMapAndRestrictedToOneInvalidEntityName() {
//		
//		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
//		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
//		restrictions.setMapVersion(mapVersion);
//		Set<EntityNameOrURI> targetEntities = new HashSet<EntityNameOrURI>();
//		
//		EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
//		ScopedEntityName entityName = new ScopedEntityName(); //"A0001";
//		entityName.setName("A0001FOO");
//		entityName.setNamespace("Automobiles");
//		entityNameOrURI.setEntityName(entityName);
////		String uri;
////		entityNameOrURI.setUri(uri);
//		targetEntities.add(entityNameOrURI);
//		
//		restrictions.setTargetEntities(targetEntities );
//		
//		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
//		
//		SortCriteria sortCriteria = null;
//		Page page = new Page();
//		
//		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
//		assertNotNull(resourceList);
//		assertEquals(0,resourceList.getEntries().size());
//	}
//
//	@Test
//	public void testGetResourceListMapAndRestrictedToOneInvalidEntityNamespace() {
//		
//		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
//		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
//		restrictions.setMapVersion(mapVersion);
//		Set<EntityNameOrURI> targetEntities = new HashSet<EntityNameOrURI>();
//		
//		EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
//		ScopedEntityName entityName = new ScopedEntityName(); //"A0001";
//		entityName.setName("A0001");
//		entityName.setNamespace("AutomobilesFOO");
//		entityNameOrURI.setEntityName(entityName);
//	
////		String uri;
////		entityNameOrURI.setUri(uri);
//		targetEntities.add(entityNameOrURI);
//		
//		restrictions.setTargetEntities(targetEntities );
//		
//		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
//		
//		SortCriteria sortCriteria = null;
//		Page page = new Page();
//		
//		DirectoryResult<MapEntry> resourceList = this.service.getResourceList(mapEntryQueryImpl, sortCriteria, page);
//		assertNotNull(resourceList);
//		assertEquals(0,resourceList.getEntries().size());
//	}
//
//	
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
