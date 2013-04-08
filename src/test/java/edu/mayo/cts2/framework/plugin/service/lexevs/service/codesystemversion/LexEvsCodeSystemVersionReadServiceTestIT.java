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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsCodeSystemVersionReadServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsCodeSystemVersionReadService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
		assertNotNull(this.marshaller);
	}
	
	// Test exists method
	// ------------------
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage(nameOrUri), this.service.exists(identifier, readContext));
	
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier;
		ArrayList<String> values = CommonTestUtils.createInvalidNameURIs(1);
		// Test invalid values
		for(int i=0; i < values.size(); i++){
			identifier = ModelUtils.nameOrUriFromName(values.get(i));
			assertFalse(CommonTestUtils.createInvalidValuesMessage(values.get(i)), this.service.exists(identifier, readContext));
		}
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsWithNullNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier = null;
		assertFalse(CommonTestUtils.createNullValueMessage("NameURI"), this.service.exists(identifier, readContext));
	}
	
	// Test existsByTag method
	// -----------------------
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		VersionTagReference tag = Constants.CURRENT_TAG; 
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + tag), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index] + "FOO";  		
		VersionTagReference tag = Constants.CURRENT_TAG; 
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createInvalidValuesMessage(nameOrUri), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagWithInvalidTag() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String tagValue = Constants.CURRENT_TAG_TEXT + "FOO";
		VersionTagReference tag = new VersionTagReference(tagValue);
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createInvalidValuesMessage(tagValue), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagWithNullTag() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		VersionTagReference tag = new VersionTagReference();
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createNullValueMessage("tag"), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByTagWithNullNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier = null;
		
		VersionTagReference tag = Constants.CURRENT_TAG; 		
		assertFalse(CommonTestUtils.createNullValueMessage("nameUri"), this.service.existsByTag(identifier, tag, readContext));
	}
	
	
	// Test existsVersionID method
	// ---------------------------
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionIdWithValidValues() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + version), this.service.existsVersionId(identifier, version));
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionWithInvalidNameUri() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index] + "FOO";  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		assertFalse(CommonTestUtils.createInvalidValuesMessage(nameOrUri), this.service.existsVersionId(identifier, version));
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionWithInvalidVersionID() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index] + "444";
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createInvalidValuesMessage(version), this.service.existsVersionId(identifier, version));
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionWithNullNameUid() throws Exception {
		int index = 0;
		String version = CommonTestUtils.VALID_VERSIONS[index];
		
		NameOrURI identifier = null;
		
		assertFalse(CommonTestUtils.createNullValueMessage("nameURI"), this.service.existsVersionId(identifier, version));
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsByVersionWithNullVersionID() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = null;
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage("NULL VersionID"), this.service.existsVersionId(identifier, version));
	}	

	// Test getCodeSystemByVersionID method
	// ------------------------------------	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetCodeSystemByVersionIDWithValidValues() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + version), results);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetCodeSystemByVersionIDWithInvalidNameUri() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index] + "FOO";  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNull(CommonTestUtils.createInvalidValuesMessage(nameOrUri + ", " + version), results);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetCodeSystemByVersionIDWithInvalidVersion() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index] + "444";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNull(CommonTestUtils.createInvalidValuesMessage(nameOrUri + ", " + version), results);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetCodeSystemByVersionIDWithNullNameUri() throws Exception {
		int index = 0;
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = null;
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNull(CommonTestUtils.createNullValueMessage("nameURI"), results);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetCodeSystemByVersionIDWithNullVersion() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = null;
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNotNull(CommonTestUtils.createValidValuesMessage("VersionID is NULL"), results);
	}

	
	// Test getSupportedTags method
	// ----------------------------
	
	// Test read method
	// -----------------
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri), csvCatalogEntry);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadWithNullNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier = null;
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNull(CommonTestUtils.createNullValueMessage("nameUri"), csvCatalogEntry);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadValidXML() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);		
		marshaller.marshal(csvCatalogEntry, new StreamResult(new StringWriter()));		
	}
	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier;
		ArrayList<String> values = CommonTestUtils.createInvalidNameURIs(1);
		// Test invalid values
		for(int i=0; i < values.size(); i++){
			identifier = ModelUtils.nameOrUriFromName(values.get(i));
			CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
			assertNull(CommonTestUtils.createInvalidValuesMessage(values.get(i)), csvCatalogEntry);		
		}
	}
	
	
	
	// Test readByTag method
	// ----------------------	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadByTagWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
//		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		VersionTagReference tag = Constants.CURRENT_TAG; 
		
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.readByTag(identifier, tag, readContext);
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + tag.getContent()), csvCatalogEntry);
		
//		// Verify LexEVS to CTS2 transform worked 
//		assertNotNull(csvCatalogEntry.getFormalName());
//		assertEquals("Formal name not transformed - ", "autos", csvCatalogEntry.getFormalName());
//		assertNotNull(csvCatalogEntry.getCodeSystemVersionName());
//		assertEquals("CodeSystemVersionName not transformed - ","Automobiles-1.0",csvCatalogEntry.getCodeSystemVersionName());
//		assertNotNull(csvCatalogEntry.getDocumentURI());
//		assertEquals("DocumentURI not transformed - ","urn:oid:11.11.0.1#1.0",csvCatalogEntry.getDocumentURI());		
//		assertNotNull(csvCatalogEntry.getAbout());
//		assertEquals("About not transformed - ","urn:oid:11.11.0.1",csvCatalogEntry.getAbout());		
//		assertNotNull(csvCatalogEntry.getResourceSynopsis());
//		assertNotNull(csvCatalogEntry.getResourceSynopsis().getValue());
//		assertNotNull(csvCatalogEntry.getResourceSynopsis().getValue().getContent());
//		assertEquals("Resource Synopsis not transformed - ","Automobiles",csvCatalogEntry.getResourceSynopsis().getValue().getContent());
//		assertNotNull(csvCatalogEntry.getKeyword());
//		assertEquals("Number of KeyWords not transformed correctly - ",3,csvCatalogEntry.getKeywordCount());
//		String[] keyWordsArray = csvCatalogEntry.getKeyword();
//		assertEquals("KeyWord value not transformed correctly - ","11.11.0.1",keyWordsArray[0]);
//		assertEquals("KeyWord value not transformed correctly - ","Automobiles",keyWordsArray[1]);
//		assertEquals("KeyWord value not transformed correctly - ","SomeOtherValue",keyWordsArray[2]);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadByTagWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		VersionTagReference tag = Constants.CURRENT_TAG; 
		NameOrURI identifier;
		ArrayList<String> values = CommonTestUtils.createInvalidNameURIs(1);
		// Test invalid values
		for(int i=0; i < values.size(); i++){
			identifier = ModelUtils.nameOrUriFromName(values.get(i));
			CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.readByTag(identifier, tag, readContext);
			assertNull(CommonTestUtils.createInvalidValuesMessage(values.get(i)), csvCatalogEntry);		
		}
	}
}
