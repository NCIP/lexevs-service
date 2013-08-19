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
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

/**
 * Returns URIs based on a {@link UriResolver}.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class RestUriResolverUriHandler implements DelegateUriHandler {

	@Resource
	private UriResolver uriResolver;

	/* 
	 * This constructs an Entity URI based on the SupportedNamespace
	 * of LexEVS.
	 * 
	 * (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getEntityUri(org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference)
	 */
	@Override
	public String getEntityUri(ResolvedCodedNodeReference reference) {
		String name = reference.getCode();
		String namespace = reference.getCodeNamespace();
		
		String baseUri = this.uriResolver.idToBaseUri(namespace);
		
		if(StringUtils.isNotBlank(baseUri)){
			return baseUri + name;
		} else {
			return null;
		}
	}

	@Override
	public String getPredicateUri(String codingSchemeUri,
			String codingSchemeVersion, String associationName) {
		//this doesn't apply here
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemUri(CodingScheme codingScheme) {
		return this.doGetCodeSystemUri(codingScheme.getCodingSchemeURI());

	}
	
	@Override
	public String getCodeSystemUri(CodingSchemeSummary codingScheme) {
		return this.doGetCodeSystemUri(codingScheme.getCodingSchemeURI());
	}
	
	protected String doGetCodeSystemUri(String uri) {
		return this.uriResolver.
				idToUri(
					uri, 
					IdType.CODE_SYSTEM);
	}
	

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemVersionUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemVersionUri(CodingScheme codingScheme) {
		return this.doGetCodeSystemVersionUri(
			codingScheme.getCodingSchemeURI(),
			codingScheme.getRepresentsVersion());
	}

	protected String doGetCodeSystemVersionUri(String uri, String version) {
		return this.uriResolver.
				idAndVersionToVersionUri(
					uri, 
					version,
					IdType.CODE_SYSTEM);
	}
	
	@Override
	public String getCodeSystemVersionUri(
			CodingSchemeSummary codingSchemeSummary) {
		return this.doGetCodeSystemVersionUri(
				codingSchemeSummary.getCodingSchemeURI(),
				codingSchemeSummary.getRepresentsVersion());
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
