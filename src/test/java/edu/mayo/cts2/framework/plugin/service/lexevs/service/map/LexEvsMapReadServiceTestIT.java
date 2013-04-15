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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@LoadContents(
		{
			@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
			@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
		}
)
public class LexEvsMapReadServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapReadService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	public void testExistsNameFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromName("Mapping Sample"); 
		ResolvedReadContext readContext = null;
		
		assertTrue(service.exists(identifier, readContext));
	}
	
	@Test
	public void testExistsNameNotFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromName("Mapping Sample-1.0"); 
		ResolvedReadContext readContext = null;
		
		assertFalse(service.exists(identifier, readContext));
	}

	@Test
	public void testExistsNameNotFoundNotMappingCodingScheme() {
		NameOrURI identifier = ModelUtils.nameOrUriFromName("Automobiles"); 
		ResolvedReadContext readContext = null;
		
		assertFalse(service.exists(identifier, readContext));
	}
	
	@Test
	public void testExistsUriFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromUri("urn:oid:mapping:sample"); 
		ResolvedReadContext readContext = null;
		
		assertTrue(service.exists(identifier, readContext));
	}

	@Test
	public void testExistsUriNotFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromUri("urn:oid:mapping:samplezzz"); 
		ResolvedReadContext readContext = null;
		
		assertFalse(service.exists(identifier, readContext));
	}
	
	@Test
	public void testExistsUriNotFoundNotMappingCodingScheme() {
		// URI value from Automobiles
		NameOrURI identifier = ModelUtils.nameOrUriFromUri("urn:oid:11.11.0.1"); 
		ResolvedReadContext readContext = null;
		
		assertFalse(service.exists(identifier, readContext));
	}
	
	@Test
	public void testReadNameFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromName("Mapping Sample"); 
		ResolvedReadContext readContext = null;
		
		assertNotNull(service.read(identifier, readContext));
	}
	
	@Test
	public void testReadNameNotFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromName("Mapping Sample-1.0"); 
		ResolvedReadContext readContext = null;
		
		assertNull(service.read(identifier, readContext));
	}

	@Test
	public void testReadNameNotFoundNotMappingCodingScheme() {
		NameOrURI identifier = ModelUtils.nameOrUriFromName("Automobiles"); 
		ResolvedReadContext readContext = null;
		
		assertNull(service.read(identifier, readContext));
	}
	
	@Test
	public void testReadUriFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromUri("urn:oid:mapping:sample"); 
		ResolvedReadContext readContext = null;
		
		assertNotNull(service.read(identifier, readContext));
	}

	@Test
	public void testReadUriNotFound() {
		NameOrURI identifier = ModelUtils.nameOrUriFromUri("urn:oid:mapping:samplezzz"); 
		ResolvedReadContext readContext = null;
		
		assertNull(service.read(identifier, readContext));
	}
	
	@Test
	public void testReadUriNotFoundNotMappingCodingScheme() {
		// URI value from Automobiles
		NameOrURI identifier = ModelUtils.nameOrUriFromUri("urn:oid:11.11.0.1"); 
		ResolvedReadContext readContext = null;
		
		assertNull(service.read(identifier, readContext));
	}
	

}
