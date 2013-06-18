package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import static org.junit.Assert.*;

import org.junit.Test;

public class UriUtilsTest {
	
	@Test
	public void combineWithHash(){
		assertEquals("http://test#it", UriUtils.combine("http://test#", "it"));
	}
	
}
