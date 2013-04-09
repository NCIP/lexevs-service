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
