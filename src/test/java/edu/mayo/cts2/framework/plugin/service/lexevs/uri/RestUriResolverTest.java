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

import org.junit.Before;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

public class RestUriResolverTest {

	RestUriResolver resolver;

	@Before
	public void SetUp() throws Exception {
		resolver = new RestUriResolver(
				"http://uriresolver.nci.nih.gov");
		
		resolver.loadClojureScripts();
	}

	@Test
	public void TestIdToName() {
		assertEquals("LNC", resolver.idToName(
				"http://id.nlm.nih.gov/cui/C1136323", IdType.CODE_SYSTEM));
	}

	@Test
	public void TestIdToUri() {
		assertEquals("http://id.nlm.nih.gov/cui/C1136323",
				resolver.idToUri("LNC", IdType.CODE_SYSTEM));
	}
	
	@Test
	public void TestIdToUriWithSpaceNoException() {
		assertNull(resolver.idToUri("LN C", IdType.CODE_SYSTEM));
	}
	
	@Test
	public void TestIdToUriWithNonEscapedCharNoException() {
		assertNull(resolver.idToUri("HL7 Vocabulary (V3 02-36)", IdType.CODE_SYSTEM));
	}
	
	@Test
	public void TestIdAndVersionToVersionNameWithURLNoException() {
		assertNull(resolver.idAndVersionToVersionName(
				"http://www.hl7.org/Library/data-model/RIM/V3 R2", "36", IdType.CODE_SYSTEM));
	}

	@Test
	public void TestIdToBaseUri() {
		assertEquals("http://id.nlm.nih.gov/cui/C1136323/",
				resolver.idToBaseUri("LNC"));
	}
	
	@Test
	public void TestIdToIds() {
		assertTrue(resolver.idToIds("MDR").contains("MedDRA"));
	}

	@Test
	public void TestIdAndVersionToUri() {
		assertEquals("http://id.nlm.nih.gov/cui/C3260726",
				resolver.idAndVersionToVersionUri("LNC", "238",
						IdType.CODE_SYSTEM));
	}

	@Test
	public void TestIdAndVersionToName() {
		assertEquals("LNC238", resolver.idAndVersionToVersionName("LNC", "238",
				IdType.CODE_SYSTEM));
	}

}