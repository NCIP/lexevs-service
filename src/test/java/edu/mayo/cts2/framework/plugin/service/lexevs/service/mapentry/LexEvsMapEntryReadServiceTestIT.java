/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractReadServiceTest;
import edu.mayo.cts2.framework.service.profile.ReadService;
import edu.mayo.cts2.framework.service.profile.mapentry.name.MapEntryReadId;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")})
public class LexEvsMapEntryReadServiceTestIT 
	extends AbstractReadServiceTest<MapEntry, MapEntryReadId> {

	@Resource
	private LexEvsMapEntryReadService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testReadEntityCodeNotFound() {
		
		String sourceEntityCode = "A0001zzz";
		ScopedEntityName mapFromName = new ScopedEntityName();
		mapFromName.setName(sourceEntityCode);

		String mapVersionName = "MappingSample-1.0";
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName(mapVersionName);
		MapEntryReadId identifier = new MapEntryReadId(mapFromName, mapVersion);
		
		ResolvedReadContext readContext = null;
		
		MapEntry mapEntry = this.service.read(identifier, readContext);
		assertNull(mapEntry);
				
	}

	@Test
	public void testReadMapVersionNotFound() {
		
		String sourceEntityCode = "A0001";
		ScopedEntityName mapFromName = new ScopedEntityName();
		mapFromName.setName(sourceEntityCode);

		String mapVersionName = "MappingSample-99999";
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName(mapVersionName);
		MapEntryReadId identifier = new MapEntryReadId(mapFromName, mapVersion);
		
		ResolvedReadContext readContext = null;
		
		assertNull(this.service.read(identifier, readContext));
	}

	@Test
	public void testExistsFound() {
		String sourceEntityCode = "Ford";
		ScopedEntityName mapFromName = new ScopedEntityName();
		mapFromName.setName(sourceEntityCode);

		String mapVersionName = "MappingSample-1.0";
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName(mapVersionName);
		MapEntryReadId identifier = new MapEntryReadId(mapFromName, mapVersion);
		
		ResolvedReadContext readContext = null;
		
		assertTrue(this.service.exists(identifier, readContext));		
	}

	@Test
	public void testExistsNotFound() {
		String sourceEntityCode = "Fjord";
		ScopedEntityName mapFromName = new ScopedEntityName();
		mapFromName.setName(sourceEntityCode);

		String mapVersionName = "MappingSample-1.0";
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName(mapVersionName);
		MapEntryReadId identifier = new MapEntryReadId(mapFromName, mapVersion);
		
		ResolvedReadContext readContext = null;
		
		assertFalse(this.service.exists(identifier, readContext));				
	}

	@Override
	protected ReadService<MapEntry, MapEntryReadId> getService() {
		return this.service;
	}

	@Override
	protected MapEntryReadId getGoodIdentifier() {
		String sourceEntityCode = "A0001";
		ScopedEntityName mapFromName = new ScopedEntityName();
		mapFromName.setName(sourceEntityCode);
		mapFromName.setNamespace("Automobiles");

		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		
		return new MapEntryReadId(mapFromName, mapVersion);
	}

	@Override
	protected MapEntryReadId getBadIdentifier() {
		String sourceEntityCode = "A0001";
		ScopedEntityName mapFromName = new ScopedEntityName();
		mapFromName.setName(sourceEntityCode);

		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.99999");
		
		return new MapEntryReadId(mapFromName, mapVersion);
	}

}
