/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import org.junit.Test;
import org.springframework.test.annotation.IfProfileValue;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@IfProfileValue(name = "spring.profiles.active", value = "withUriResolver")
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
