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
	
		});
		
		assertTrue(converter.isValidVersionName("test-something-1.0"));
	}
}
