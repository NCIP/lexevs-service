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

import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

/**
 * Returns URIs based on a {@link UriResolver}.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class RestUriResolverUriHandler implements DelegateUriHandler, InitializingBean {

	private UriResolver uriResolver;

	@Value("${uriResolutionServiceUrl}")
	private String uriResolutionServiceUrl;
	  
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.uriResolver = new RestUriResolver(this.uriResolutionServiceUrl);
	}
	
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
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemUri(CodingScheme codingScheme) {
		return this.uriResolver.
				idToUri(
					codingScheme.getCodingSchemeURI(), 
					IdType.CODE_SYSTEM);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemVersionUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemVersionUri(CodingScheme codingScheme) {
		return this.uriResolver.
				idAndVersionToVersionUri(
					codingScheme.getCodingSchemeURI(), 
					codingScheme.getRepresentsVersion(),
					IdType.CODE_SYSTEM);
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
