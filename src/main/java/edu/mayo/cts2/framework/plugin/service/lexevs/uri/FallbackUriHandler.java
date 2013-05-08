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
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

/**
 * Last resort. This returns back a URI no matter what, even if we
 * have to make something up.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class FallbackUriHandler implements DelegateUriHandler {

	@Resource
	private LexBIGService lexBigService;

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getEntityUri(org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference)
	 */
	@Override
	public String getEntityUri(ResolvedCodedNodeReference reference) {
		String codingSchemeUri = reference.getCodingSchemeURI();
	
		String name = reference.getCode();
		
		return codingSchemeUri + "/" + name;
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemUri(CodingScheme codingScheme) {
		return codingScheme.getCodingSchemeURI();
	}
	
	@Override
	public String getCodeSystemUri(CodingSchemeSummary codingSchemeSummary) {
		return codingSchemeSummary.getCodingSchemeURI();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemVersionUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemVersionUri(CodingScheme codingScheme) {
		return codingScheme.getCodingSchemeURI() + "/" + codingScheme.getRepresentsVersion();
	}

	@Override
	public String getCodeSystemVersionUri(CodingSchemeSummary codingSchemeSummary) {
		return codingSchemeSummary.getCodingSchemeURI() + "/" + codingSchemeSummary.getRepresentsVersion();
	}

	@Override
	public String getPredicateUri(String codingSchemeUri,
			String codingSchemeVersion, String associationName) {
		return codingSchemeUri + "/" + associationName;
	}
	
	@Override
	public int getOrder() {
		return 2;
	}

}
