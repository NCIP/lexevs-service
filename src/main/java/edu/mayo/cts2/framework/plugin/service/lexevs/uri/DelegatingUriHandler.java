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

import java.util.Collections;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

/**
 * The Class DelegatingUriHandler.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
@Primary
public class DelegatingUriHandler implements UriHandler, InitializingBean {

	private List<DelegateUriHandler> delegateUriHandlers;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Collections.sort(this.delegateUriHandlers, OrderComparator.INSTANCE);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getEntityUri(org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference)
	 */
	@Override
	public String getEntityUri(final ResolvedCodedNodeReference reference) {
		return this.doIn(new DoInDelegates(){
			@Override
			public String f(UriHandler uriHandler) {
				return uriHandler.getEntityUri(reference);
			}	
		});
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemUri(final CodingScheme codingScheme) {
		return this.doIn(new DoInDelegates(){
			@Override
			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemUri(codingScheme);
			}	
		});
	}
	
	@Override
	public String getCodeSystemUri(final CodingSchemeSummary codingScheme) {
		return this.doIn(new DoInDelegates(){
			@Override
			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemUri(codingScheme);
			}	
		});
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getCodeSystemVersionUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemVersionUri(final CodingScheme codingScheme) {
		return this.doIn(new DoInDelegates(){
			@Override
			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemVersionUri(codingScheme);
			}			
		});
	}

	@Override
	public String getCodeSystemVersionUri(final CodingSchemeSummary codingSchemeSummary) {
		return this.doIn(new DoInDelegates(){
			@Override
			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemVersionUri(codingSchemeSummary);
			}			
		});
	}

	@Override
	public String getPredicateUri(
			final String codingSchemeUri,
			final String codingSchemeVersion, 
			final String associationName) {
		return this.doIn(new DoInDelegates(){
			@Override
			public String f(UriHandler uriHandler) {
				return uriHandler.getPredicateUri(
						codingSchemeUri, 
						codingSchemeVersion, 
						associationName);
			}			
		});
	}
	
	private interface DoInDelegates {
		public String f(UriHandler uriHandler);
	}
	
	protected String doIn(DoInDelegates doIn){
		for(UriHandler handler : this.delegateUriHandlers){
			String uri = doIn.f(handler);
			if(StringUtils.isNotBlank(uri)){
				return uri;
			}
		}
		
		throw new IllegalStateException("Uri not found - please implement a Fallback Handler.");
	}
	
	public List<DelegateUriHandler> getDelegateUriHandlers() {
		return delegateUriHandlers;
	}

	@Autowired
	public void setDelegateUriHandlers(List<DelegateUriHandler> delegateUriHandlers) {
		this.delegateUriHandlers = delegateUriHandlers;
	}


}
