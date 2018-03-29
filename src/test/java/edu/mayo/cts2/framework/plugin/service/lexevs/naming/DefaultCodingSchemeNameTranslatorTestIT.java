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

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")
public class DefaultCodingSchemeNameTranslatorTestIT extends AbstractTestITBase {

	@Resource
	private DefaultCodingSchemeNameTranslator translator;
	
	@Test
	public void translateNotFound(){
		assertEquals("not found", translator.translateFromLexGrid("not found"));
	}

}
