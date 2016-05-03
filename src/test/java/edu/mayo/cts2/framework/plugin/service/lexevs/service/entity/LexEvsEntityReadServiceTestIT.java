/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.EntityReference;
import edu.mayo.cts2.framework.model.core.Property;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.StatementTarget;
import edu.mayo.cts2.framework.model.entity.Designation;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.NamedEntityDescription;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractReadServiceTest;
import edu.mayo.cts2.framework.service.profile.ReadService;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class LexEvsEntityReadServiceTestIT extends
	AbstractReadServiceTest<EntityDescription, EntityDescriptionReadId> {

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
	public void testReadCorrect() throws Exception {

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
		boolean found = false;
		for(Designation d: entityDescription.getNamedEntity().getDesignation()){
			if(d.getAssertedInCodeSystemVersion()!= null && d.getAssertedInCodeSystemVersion().equals("lexgrid.org")){
				found = true;
			}
		}
		assertTrue(found);
		assertEquals("en", entityDescription.getNamedEntity().getDesignation(0).getLanguage().getContent());
		assertEquals("005",scopedEntityName2.getName());
		assertEquals("Automobiles",scopedEntityName2.getNamespace());
	}	


	@Test
	public void testReadNotFoundWrongNamespace() throws Exception {

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
	
	@Test
	public void testPropertyQualifier() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("A0001");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		EntityDescriptionReadId identifier = new EntityDescriptionReadId(entity,codeSystemVersion);		
		ResolvedReadContext readContext = null;
		
		EntityDescription a0001 = this.service.read(identifier, readContext);

		int propertyCount = a0001.getNamedEntity().getProperty(2).getPropertyQualifierCount();
		assertTrue(propertyCount == 2);
		
		int propertyQualifierValueCount = a0001.getNamedEntity().getProperty(2).getPropertyQualifier(0).getValueCount();
		assertTrue(1 == propertyQualifierValueCount);
	}	
	
	@Test
	public void testAvailableDescriptions() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("C0001");
		entity.setEntityName(scopedEntityName);
		
		EntityReference reference = this.service.availableDescriptions(entity, null);
		
		assertNotNull(reference);
		assertEquals(1, reference.getKnownEntityDescriptionCount());
	}	
	
	@Test
	public void testAvailableDescriptionsUri() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		entity.setUri("urn:oid:11.11.0.1:Chevy");
		
		EntityReference reference = this.service.availableDescriptions(entity, null);
		
		assertNotNull(reference);
		assertEquals(1, reference.getKnownEntityDescriptionCount());
	}	
	
	@Test
	public void testAvailableDescriptionsBadUri() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		entity.setUri("__INVALID__");
		
		EntityReference reference = this.service.availableDescriptions(entity, null);
		
		assertNull(reference);
	}	
	
	@Test
	public void testAvailableDescriptionsValidXml() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("C0001");
		entity.setEntityName(scopedEntityName);
		
		EntityReference reference = this.service.availableDescriptions(entity, null);

		assertNotNull(reference);
		assertEquals(1, reference.getKnownEntityDescriptionCount());
		this.marshaller.marshal(reference, new StreamResult(new StringWriter()));	
	}	
	
	@Test
	public void testParents() throws Exception {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("Chevy");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		EntityDescriptionReadId identifier = new EntityDescriptionReadId(entity,codeSystemVersion);		
		
		EntityDescription returnEntity = this.service.read(identifier, null);
		
		assertEquals(1, returnEntity.getNamedEntity().getParent().length);
		assertEquals("GM", returnEntity.getNamedEntity().getParent()[0].getName());
	}

	@Override
	protected ReadService<EntityDescription, EntityDescriptionReadId> getService() {
		return this.service;
	}

	@Override
	protected EntityDescriptionReadId getGoodIdentifier() {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("005");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		return new EntityDescriptionReadId(entity,codeSystemVersion);		
	}

	@Override
	protected EntityDescriptionReadId getBadIdentifier() {
		EntityNameOrURI entity = new EntityNameOrURI();
		ScopedEntityName scopedEntityName = new ScopedEntityName();
		scopedEntityName.setNamespace("Automobiles");
		scopedEntityName.setName("__INVALID");
		entity.setEntityName(scopedEntityName);
		
		NameOrURI codeSystemVersion = ModelUtils.nameOrUriFromName("Automobiles-1.0");
			
		return new EntityDescriptionReadId(entity,codeSystemVersion);		
	}	
	
}
