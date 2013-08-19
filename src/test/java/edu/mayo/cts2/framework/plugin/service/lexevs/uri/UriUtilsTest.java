/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import static org.junit.Assert.*;

import org.junit.Test;

public class UriUtilsTest {
	
	@Test
	public void combineWithHash(){
		assertEquals("http://test#it", UriUtils.combine("http://test#", "it"));
	}
	
	@Test
	public void getSeparator(){
		assertEquals('#', UriUtils.getSeparator("http://test#something"));
	}
	
}
