/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractReadServiceTest;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.ReadService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class LexEvsCodeSystemVersionReadServiceTestIT 
	extends AbstractReadServiceTest<CodeSystemVersionCatalogEntry, NameOrURI> {
	
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
	public void testExistsWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage(nameOrUri), this.service.exists(identifier, readContext));
	
	}
	
	@Test
	public void testExistsWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier;
		ArrayList<String> values = CommonTestUtils.createInvalidNameURIs(0);
		// Test invalid values
		for(int i=0; i < values.size(); i++){
			identifier = ModelUtils.nameOrUriFromName(values.get(i));
			assertFalse(CommonTestUtils.createInvalidValuesMessage(values.get(i)), this.service.exists(identifier, readContext));
		}
	}
	
	@Test
	public void testExistsWithNullNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier = null;
		assertFalse(CommonTestUtils.createNullValueMessage("NameURI"), this.service.exists(identifier, readContext));
	}
	
	// Test existsByTag method
	// -----------------------
	@Test
	public void testExistsByTagWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		VersionTagReference tag = Constants.CURRENT_TAG; 
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + tag), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	public void testExistsByTagWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index] + "FOO";  		
		VersionTagReference tag = Constants.CURRENT_TAG; 
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createInvalidValuesMessage(nameOrUri), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
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
	public void testExistsByTagWithNullTag() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		VersionTagReference tag = new VersionTagReference();
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createNullValueMessage("tag"), this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	public void testExistsByTagWithNullNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier = null;
		
		VersionTagReference tag = Constants.CURRENT_TAG; 		
		assertFalse(CommonTestUtils.createNullValueMessage("nameUri"), this.service.existsByTag(identifier, tag, readContext));
	}
	
	
	// Test existsVersionID method
	// ---------------------------
	@Test
	public void testExistsByVersionIdWithValidValues() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertTrue(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + version), this.service.existsVersionId(identifier, version));
	}
	
	@Test
	public void testExistsByVersionWithInvalidNameUri() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index] + "FOO";  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		assertFalse(CommonTestUtils.createInvalidValuesMessage(nameOrUri), this.service.existsVersionId(identifier, version));
	}	

	@Test
	public void testExistsByVersionWithInvalidVersionID() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index] + "444";
		
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertFalse(CommonTestUtils.createInvalidValuesMessage(version), this.service.existsVersionId(identifier, version));
	}	

	@Test
	public void testExistsByVersionWithNullNameUid() throws Exception {
		int index = 0;
		String version = CommonTestUtils.VALID_VERSIONS[index];
		
		NameOrURI identifier = null;
		
		assertFalse(CommonTestUtils.createNullValueMessage("nameURI"), this.service.existsVersionId(identifier, version));
	}	

	@Test
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
	public void testGetCodeSystemByVersionIDWithValidValues() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + version), results);
	}

	@Test
	public void testGetCodeSystemByVersionIDWithInvalidNameUri() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index] + "FOO";  		
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNull(CommonTestUtils.createInvalidValuesMessage(nameOrUri + ", " + version), results);
	}

	@Test
	public void testGetCodeSystemByVersionIDWithInvalidVersion() throws Exception {
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
		String version = CommonTestUtils.VALID_VERSIONS[index] + "444";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNull(CommonTestUtils.createInvalidValuesMessage(nameOrUri + ", " + version), results);
	}

	@Test
	public void testGetCodeSystemByVersionIDWithNullNameUri() throws Exception {
		int index = 0;
		String version = CommonTestUtils.VALID_VERSIONS[index];
		NameOrURI identifier = null;
		
		CodeSystemVersionCatalogEntry results = this.service.getCodeSystemByVersionId(identifier, version, null);
		assertNull(CommonTestUtils.createNullValueMessage("nameURI"), results);
	}

	@Test
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
	public void testReadWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri), csvCatalogEntry);		
	}

	@Test
	public void testReadWithNullNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier = null;
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);
		assertNull(CommonTestUtils.createNullValueMessage("nameUri"), csvCatalogEntry);		
	}

	@Test
	public void testReadValidXML() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.read(identifier, readContext);		
		marshaller.marshal(csvCatalogEntry, new StreamResult(new StringWriter()));		
	}
	
	
	@Test
	public void testReadWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		NameOrURI identifier;
		ArrayList<String> values = CommonTestUtils.createInvalidNameURIs(0);
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
	public void testReadByTagWithValidValues() throws Exception {
		ResolvedReadContext readContext = null;
		int index = 0;
		String nameOrUri = CommonTestUtils.VALID_URI_NAMES[index];  		
//		String nameOrUri = CommonTestUtils.getValidNameAndVersion(index); // Get Automobiles-1.0
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		VersionTagReference tag = Constants.CURRENT_TAG; 
		
		
		CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.readByTag(identifier, tag, readContext);
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri + ", " + tag.getContent()), csvCatalogEntry);	
	}

	@Test
	@Ignore
	public void testReadByTagWithInvalidNameUri() throws Exception {
		ResolvedReadContext readContext = null;
		VersionTagReference tag = Constants.CURRENT_TAG; 
		NameOrURI identifier;
		ArrayList<String> values = CommonTestUtils.createInvalidNameURIs(0);
		// Test invalid values
		for(int i=0; i < values.size(); i++){
			identifier = ModelUtils.nameOrUriFromName(values.get(i));
			CodeSystemVersionCatalogEntry csvCatalogEntry = this.service.readByTag(identifier, tag, readContext);
			assertNull(CommonTestUtils.createInvalidValuesMessage(values.get(i)), csvCatalogEntry);		
		}
	}

	@Override
	protected ReadService<CodeSystemVersionCatalogEntry, NameOrURI> getService() {
		return this.service;
	}

	@Override
	protected NameOrURI getGoodIdentifier() {
		return ModelUtils.nameOrUriFromName("Automobiles-1.0");
	}

	@Override
	protected NameOrURI getBadIdentifier() {
		return ModelUtils.nameOrUriFromName("Automobiles-2.0");
	}
}
