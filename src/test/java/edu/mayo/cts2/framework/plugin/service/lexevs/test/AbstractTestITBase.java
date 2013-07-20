package edu.mayo.cts2.framework.plugin.service.lexevs.test;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.BaseContentLoadingInMemoryTest;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventHandler;

@ContextConfiguration("/test-lexevs-context.xml")
public abstract class AbstractTestITBase extends BaseContentLoadingInMemoryTest {
	
	@Resource
	private LexEvsChangeEventHandler lexEvsChangeEventHandler;
	
	@Before
	public void clearCaches(){
		this.lexEvsChangeEventHandler.fireChange();
	}
	
}