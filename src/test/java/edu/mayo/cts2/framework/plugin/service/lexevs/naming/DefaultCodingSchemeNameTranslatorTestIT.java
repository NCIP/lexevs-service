/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
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
		assertEquals("not found", translator.translateFromLexGrid("not found"));
	}

}
