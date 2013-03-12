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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;


public class LexEvsCodeSystemVersionReadServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsCodeSystemVersionReadService service;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadByOfficialVersionId() throws Exception {
		NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles");
		
		assertNotNull(this.service.getCodeSystemByVersionId(name, "1.0", null));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadByTag() throws Exception {
		String nameOrUri = "Automobiles";
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.readByTag(codeSystem, tag, readContext);
		assertNotNull(csvCatalogEntry);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadByTagNotFound() throws Exception {
		String nameOrUri = "Automooobiles";
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.readByTag(codeSystem, tag, readContext);
		assertNull(csvCatalogEntry);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testRead() throws Exception {
		String nameOrUri = "Automobiles-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNotNull(csvCatalogEntry);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testRead_ErrorWithoutDash() throws Exception {
		String nameOrUri = "Automobiles1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNull(csvCatalogEntry);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testRead_WithSpace() throws Exception {
		String nameOrUri = "Automobiles - 1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNull(csvCatalogEntry);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadNotFound() throws Exception {
		String nameOrUri = "Automooobiles-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNull(csvCatalogEntry);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsTrue() throws Exception {
		String nameOrUri = "Automobiles-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		assertTrue(this.service.exists(identifier, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsFalse() throws Exception {
		String nameOrUri = "Automooobiles-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		assertFalse(this.service.exists(identifier, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagTrue() throws Exception {
		String nameOrUri = "Automobiles";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		assertTrue(this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagFalse() throws Exception {
		String nameOrUri = "Automooobiles";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		assertFalse(this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionIdTrue() throws Exception {
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName("Automobiles");
		String officialResourceVersionId = "1.0";
		assertTrue(this.service.existsVersionId(codeSystem, officialResourceVersionId));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionIdFalse() throws Exception {
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName("Automooobiles");
		String officialResourceVersionId = "1.0";
		assertFalse(this.service.existsVersionId(codeSystem, officialResourceVersionId));
	}	

}
