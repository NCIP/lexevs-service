package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.Test;

import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

public class EntityUriResolverTestIT extends AbstractTestITBase {
	
	@Resource
	private EntityUriResolver uriResolver;
	
	@Test
	public void testResolve(){
		ScopedEntityName name = this.uriResolver.resolveUri("http://snomed.info/id/74400008");
		
		assertEquals("74400008", name.getName());
		assertEquals("SNOMEDCT", name.getNamespace());
	}
	
	@Test
	public void testResolveInvalid(){
		ScopedEntityName name = this.uriResolver.resolveUri("http://invalid/74400008");
		
		assertNull(name);
	}


}
