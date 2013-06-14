/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
