package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Before;
import org.junit.Test;

import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;


@LoadContent(contentPath="lexevs/test-content/valueset/VSDOnlyTest.xml")
public class LexEVSValueSetReadServiceTestIT extends
AbstractTestITBase {


	@Resource
	private LexEVSValueSetReadService service;

	@Before
	public void setUp() throws Exception {
		assertNotNull(service);
	}


	@Test
	public void testRead(){
		ValueSetCatalogEntry entry = service.read(ModelUtils.nameOrUriFromName("Property Reference Test 1"),null);
		assertNotNull(entry);
	}
	
	@Test 
	public void testReadByUri(){
		assertNotNull( service.read(ModelUtils.nameOrUriFromUri("SRITEST:AUTO:PropertyRefTest1-VSDONLY"), null));
	}
	
	@Test
	public void testHasAlternateId(){
		ValueSetCatalogEntry vs = service.read(ModelUtils.nameOrUriFromUri("SRITEST:AUTO:PropertyRefTest1-VSDONLY"), null);
		assertEquals(1,vs.getAlternateIDCount());
		assertEquals("Property Reference Test 1",vs.getAlternateID()[0]);
	}
	
	@Test
	public void testReadCorrectName(){
		ValueSetCatalogEntry vs = service.read(ModelUtils.nameOrUriFromUri("SRITEST:AUTO:PropertyRefTest1-VSDONLY"), null);
		assertEquals("Property Reference Test 1", vs.getValueSetName());
	}
	
	@Test
	public void testReadCorrectAbout(){
		ValueSetCatalogEntry vs = service.read(ModelUtils.nameOrUriFromUri("SRITEST:AUTO:PropertyRefTest1-VSDONLY"), null);
		assertEquals("SRITEST:AUTO:PropertyRefTest1-VSDONLY", vs.getAbout());
	}
	
	

}
