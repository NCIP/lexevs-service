package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import org.LexGrid.codingSchemes.CodingScheme;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;

public class CodingSchemeToCodeSystemTransformTest {

	@Test
	public void testTransformWithEmpty(){
		CodingSchemeToCodeSystemTransform transform = 
				new CodingSchemeToCodeSystemTransform(new CodeSystemVersionNameConverter());
		
		transform.transform(new CodingScheme());
	}
}
