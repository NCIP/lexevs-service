/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */	
@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
public class LexEvsMapVersionReadServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapVersionReadService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testRead() throws Exception {
		
		String mapCodingSchemeNameAndVersion = "Mapping Sample-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(mapCodingSchemeNameAndVersion);
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.read(identifier, readContext);
		assertNotNull(mapVersion);		
	}

	@Test
	public void testReadNotMappingCodingScheme() throws Exception {
		
		String mapCodingSchemeNameAndVersion = "Automobiles-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(mapCodingSchemeNameAndVersion);
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.read(identifier, readContext);
		assertNull(mapVersion);		
	}
	
	@Test
	public void testReadByTag() throws Exception {
		String nameOrUri = "Mapping Sample";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.readByTag(identifier, tag, readContext);
		assertNotNull(mapVersion);		
	}
	
	@Test
	public void testReadByTagNotFound() throws Exception {
		String nameOrUri = "Mooopping Sample";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.readByTag(identifier, tag, readContext);
		assertNull(mapVersion);		
	}

	@Test
	public void testReadValidXML() throws Exception {
		
		String mapCodingSchemeNameAndVersion = "Mapping Sample-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(mapCodingSchemeNameAndVersion);
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.read(identifier, readContext);
		assertNotNull(mapVersion);		
		
		marshaller.marshal(mapVersion, new StreamResult(new StringWriter()));		
	}

	@Test
	public void testExistsTrue() throws Exception {
		String nameOrUri = "Mapping Sample-1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		assertTrue(this.service.exists(identifier, readContext));
	}
	
	@Test
	public void testExistsFalse() throws Exception {
		String nameOrUri = "Mapping Sample-2.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		assertFalse(this.service.exists(identifier, readContext));
	}
	
	@Test
	public void testExistsByTagTrue() throws Exception {
		String nameOrUri = "Mapping Sample";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		assertTrue(this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	public void testExistsByTagFalse() throws Exception {
		String nameOrUri = "Mooopping Sample";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		VersionTagReference tag = Constants.CURRENT_TAG; 
		ResolvedReadContext readContext = null;
		assertFalse(this.service.existsByTag(identifier, tag, readContext));
	}
	
	@Test
	public void testRead_ErrorWithoutDash() throws Exception {
		String nameOrUri = "Mapping Sample1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.read(identifier, readContext);
		assertNull(mapVersion);		
	}

	@Test
	public void testRead_WithSpace() throws Exception {
		String nameOrUri = "Mapping Sample - 1.0";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		ResolvedReadContext readContext = null;
		MapVersion mapVersion = this.service.read(identifier, readContext);
		assertNull(mapVersion);		
	}

}
