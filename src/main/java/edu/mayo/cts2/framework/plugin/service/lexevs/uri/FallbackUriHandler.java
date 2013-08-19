/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
		
		return UriUtils.combine(codingSchemeUri, name);
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
		return UriUtils.combine(
				codingScheme.getCodingSchemeURI(), 
				codingScheme.getRepresentsVersion());
	}

	@Override
	public String getCodeSystemVersionUri(CodingSchemeSummary codingSchemeSummary) {
		return UriUtils.combine(
				codingSchemeSummary.getCodingSchemeURI(),
				codingSchemeSummary.getRepresentsVersion());
	}

	@Override
	public String getPredicateUri(String codingSchemeUri,
			String codingSchemeVersion, String associationName) {
		return UriUtils.combine(codingSchemeUri, associationName);
	}
	
	@Override
	public int getOrder() {
		return 2;
	}

}
