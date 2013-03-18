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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.NamedEntityDescription;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class LexEvsEntityReadServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsEntityReadService service;
	
	@Resource
	private LexBIGService lbs;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
		assertNotNull(this.lbs);
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testRead() throws Exception {

		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("005");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		EntityDescriptionReadId identifier = new EntityDescriptionReadId(entity,codeSystemVersion);		
		ResolvedReadContext readContext = null;
		
		EntityDescription entityDescription = this.service.read(identifier, readContext);
		
		assertNotNull(entityDescription);
		NamedEntityDescription namedEntityDescription = entityDescription.getNamedEntity();
		assertNotNull(namedEntityDescription);
		assertNotNull(namedEntityDescription.getAbout());
		ScopedEntityName scopedEntityName2 = namedEntityDescription.getEntityID();
		assertNotNull(scopedEntityName2);
		assertEquals("005",scopedEntityName2.getName());
		assertEquals("Automobiles",scopedEntityName2.getNamespace());
	}	


	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testReadNotFound() throws Exception {

		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automooobiles");
		scopedEntityName.setName("005");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		EntityDescriptionReadId identifier = new EntityDescriptionReadId(entity,codeSystemVersion);		
		ResolvedReadContext readContext = null;
		
		EntityDescription entityDescription = this.service.read(identifier, readContext);
		
		assertNull(entityDescription);
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsForInactiveEntity() throws Exception {
		// Note:  this retired entity should exist since the search is using 
		// the entityCode value (73)
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("73");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		EntityDescriptionReadId identifier = new EntityDescriptionReadId(entity,codeSystemVersion);		
		ResolvedReadContext readContext = null;
		
		boolean existsFlag = this.service.exists(identifier, readContext);
		
		assertEquals(true, existsFlag);
	}	


	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testExistsForActiveEntity() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("VD005");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		EntityDescriptionReadId identifier = new EntityDescriptionReadId(entity,codeSystemVersion);		
		ResolvedReadContext readContext = null;
		
		boolean existsFlag = this.service.exists(identifier, readContext);
		
		assertEquals(true, existsFlag);
	}	
	
}
