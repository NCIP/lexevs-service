/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import org.LexGrid.codingSchemes.CodingScheme;
import org.easymock.EasyMock;
import org.junit.Test;

import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.TransformUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

public class CodingSchemeToCodeSystemTransformTest {

	@Test
	@SuppressWarnings("deprecation")
	public void testTransformWithEmpty(){
		
		CodingSchemeToCodeSystemTransform transform = 
				new CodingSchemeToCodeSystemTransform(
					new VersionNameConverter(
						new CodingSchemeNameTranslator(){

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
			
		}));
		
		UriHandler uriHandler = EasyMock.createNiceMock(UriHandler.class);
		EasyMock.replay(uriHandler);
		
		transform.setUriHandler(uriHandler);
		
		UrlConstructor urlConstructor = org.easymock.classextension.EasyMock.createNiceMock(UrlConstructor.class);
		CodingSchemeNameTranslator translator = org.easymock.classextension.EasyMock.createNiceMock(CodingSchemeNameTranslator.class);
		TransformUtils utils = org.easymock.classextension.EasyMock.createNiceMock(TransformUtils.class);
		org.easymock.classextension.EasyMock.replay(urlConstructor, translator, utils);

		transform.setUrlConstructor(urlConstructor);
		transform.setCodingSchemeNameTranslator(translator);
		transform.setTransformUtils(utils);
		
		transform.transformFullDescription(new CodingScheme());
	}
}
