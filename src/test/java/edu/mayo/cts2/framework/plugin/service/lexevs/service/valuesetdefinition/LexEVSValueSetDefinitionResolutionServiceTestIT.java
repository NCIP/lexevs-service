/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSet;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetDefinitionUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResult;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;
@LoadContents({
@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml")})
public class LexEVSValueSetDefinitionResolutionServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private LexEvsValueSetDefinitionResolutionService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	public static void main(String[] args){
		System.out.println(ValueSetDefinitionUtils.getValueSetDefinitionLocalId("SRITEST:AUTO:AllDomesticANDGM"));
	}
	
	@Test
	public void testGetResolution() throws Exception {	
		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("571eb4e6", ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"));
		
		ResolvedValueSetResult<URIAndEntityName>dirResult = service.
				resolveDefinition(defintionId, null, null, null, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
		assertTrue(dirResult.getResolvedValueSetHeader().getResolvedUsingCodeSystem().length > 0);
	     
	}
	
	@Test
	public void testGetResolutionEntities() throws Exception {	
		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("571eb4e6", ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"));
		
		DirectoryResult<EntityDirectoryEntry> dirResult = service.
				resolveDefinitionAsEntityDirectory(defintionId, null, null, null, null, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	@Test
	public void testGetResolutionComplete() throws Exception {	
		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("571eb4e6", ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"));
		
		ResolvedValueSet resolution = service.resolveDefinitionAsCompleteSet(defintionId, null, null, null, null);
		assertNotNull(resolution);
		String codeSystem = resolution.getResolutionInfo().getResolvedUsingCodeSystem(0).getCodeSystem().getContent();
		String CTS2version = resolution.getResolutionInfo().getResolvedUsingCodeSystem(0).getVersion().getContent();
		assertEquals("Expected: Automobiles but got  " + codeSystem, "Automobiles", codeSystem);
		assertEquals("Expected Automobiles-1.0 but got " + CTS2version, "Automobiles-1.0", CTS2version);
		assertEquals(1, resolution.getEntryCount());
	}
		
	@Test
	public void testGetResolutionInvalidId() throws Exception {

		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("__INVALID__", ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"));
		
		DirectoryResult<URIAndEntityName> dirResult = service.
				resolveDefinition(defintionId, null, null, null, null, new Page());

		assertNull(dirResult);
	}
	
	@Test
	public void testGetResolutionInvalidBoth() throws Exception {

		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("INVALID", ModelUtils.nameOrUriFromName("INVALID"));
		
		DirectoryResult<URIAndEntityName> dirResult = service.
				resolveDefinition(defintionId, null, null, null, null, new Page());

		assertNull(dirResult);
	}

}
