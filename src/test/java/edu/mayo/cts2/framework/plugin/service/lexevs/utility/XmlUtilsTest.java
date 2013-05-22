package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.*;

import org.junit.Test;

public class XmlUtilsTest {
	
	@Test
	public void testIsValidNcNameInvalid(){
		assertFalse(XmlUtils.isNCName("hi there"));
	}
	
	@Test
	public void testIsValidNcNameValid(){
		assertTrue(XmlUtils.isNCName("hithere"));
	}

}
