/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionNameConverterTest {

	@Test
	public void fromCts2Name(){
		VersionNameConverter converter = new VersionNameConverter(new CodingSchemeNameTranslator(){

			@Override
			public String translateFromLexGrid(String name) {
				return name;
			}

			@Override
			public String translateToLexGrid(String name) {
				return name;
			}

			@Override
			public String translateLexGridURIToLexGrid(String uri) {
				return uri;
			}
	
		});
		
		NameVersionPair pair = converter.fromCts2VersionName("test-1.0");
		
		assertEquals(pair.getName(), "test");
		assertEquals(pair.getVersion(), "1.0");
	}
	
	@Test
	public void fromCts2NameWithEscape(){
		VersionNameConverter converter = new VersionNameConverter(new CodingSchemeNameTranslator(){

			@Override
			public String translateFromLexGrid(String name) {
				return name;
			}

			@Override
			public String translateToLexGrid(String name) {
				return name;
			}

			@Override
			public String translateLexGridURIToLexGrid(String uri) {
				return uri;
			}
	
		});
		
		NameVersionPair pair = converter.fromCts2VersionName("test-1[:]0");
		
		assertEquals("test", pair.getName());
		assertEquals("1-0", pair.getVersion());
	}
	
	@Test
	public void toCts2Name(){
		VersionNameConverter converter = new VersionNameConverter(new CodingSchemeNameTranslator(){

			@Override
			public String translateFromLexGrid(String name) {
				return name;
			}

			@Override
			public String translateToLexGrid(String name) {
				return name;
			}

			@Override
			public String translateLexGridURIToLexGrid(String uri) {
				return uri;
			}
	
		});
		
		String name = converter.toCts2VersionName("test", "1.0");
		
		assertEquals("test-1.0", name);
	}
	
	@Test
	public void toCts2NameWithEscape(){
		VersionNameConverter converter = new VersionNameConverter(new CodingSchemeNameTranslator(){

			@Override
			public String translateFromLexGrid(String name) {
				return name;
			}

			@Override
			public String translateToLexGrid(String name) {
				return name;
			}

			@Override
			public String translateLexGridURIToLexGrid(String uri) {
				// TODO Auto-generated method stub
				return uri;
			}
	
		});
		
		String name = converter.toCts2VersionName("test", "1-0");
		
		assertEquals("test-1[:]0", name);
	}
	
	@Test
	public void isValidCts2Name(){
		VersionNameConverter converter = new VersionNameConverter(new CodingSchemeNameTranslator(){

			@Override
			public String translateFromLexGrid(String name) {
				return name;
			}

			@Override
			public String translateToLexGrid(String name) {
				return name;
			}

			@Override
			public String translateLexGridURIToLexGrid(String uri) {
				return uri;
			}
	
		});
		
		assertTrue(converter.isValidVersionName("test-something-1.0"));
	}
}
