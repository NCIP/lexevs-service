package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
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
