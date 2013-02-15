package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversionread;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;


public class LexEvsCodeSystemVersionReadServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsCodeSystemVersionReadService service;

	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

}
