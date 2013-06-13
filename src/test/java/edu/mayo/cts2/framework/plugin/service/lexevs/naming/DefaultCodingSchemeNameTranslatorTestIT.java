package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

public class DefaultCodingSchemeNameTranslatorTestIT extends AbstractTestITBase {

	@Resource
	private DefaultCodingSchemeNameTranslator translator;
	
	@Test
	public void translateNotFound(){
		assertEquals("not found", translator.translate("not found"));
	}

}
