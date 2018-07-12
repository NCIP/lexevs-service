/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") 
public class DelegatingUriHandlerTestIT extends AbstractTestITBase {

	@Resource
	private DelegatingUriHandler uriHandler;
	
	@Test
	public void testSetUp(){
		assertTrue(CollectionUtils.isNotEmpty(this.uriHandler.getDelegateUriHandlers()));
	}
	
	@Test
	public void testOrder(){
		List<? extends UriHandler> handlers = this.uriHandler.getDelegateUriHandlers();
		assertEquals(RestUriResolverUriHandler.class, handlers.get(0).getClass());
		assertEquals(LexEvsSupportedPropertiesUriHandler.class, handlers.get(1).getClass());
		assertEquals(FallbackUriHandler.class, handlers.get(2).getClass());
	}
	
}
