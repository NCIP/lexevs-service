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
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.codesystemversion;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.Impl.helpers.ResolvedConceptReferencesIteratorAdapter;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.AbstractBulkDownloader;
import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.Extractor;

/**
 * A {@link CodeSystemVersionBulkDownloader} implementation using the LexEVS SearchExtension.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class SearchExtensionBulkDownloader 
	extends AbstractBulkDownloader 
	implements CodeSystemVersionBulkDownloader, InitializingBean {

	@Resource
	private LexBIGService lexBigService;
	
	private SearchExtension searchExtension;
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	private final static Map<String, Extractor> EXTRACTOR_MAP = new HashMap<String,Extractor>(){

		private static final long serialVersionUID = -7214815015371005224L;
		{{
				put(CodeSystemVersionBulkDownloader.CODE_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCode();
					}	
				});
				
				put(CodeSystemVersionBulkDownloader.NAMESPACE_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodeNamespace();
					}	
				});
				
				put(CodeSystemVersionBulkDownloader.DESCRIPTION_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getEntityDescription().getContent();
					}	
				});
				
				put(CodeSystemVersionBulkDownloader.CODINGSCHEME_NAME_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodingSchemeName();
					}	
				});
				
				put(CodeSystemVersionBulkDownloader.CODINGSCHEME_URI_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodingSchemeURI();
					}	
				});
				
				put(CodeSystemVersionBulkDownloader.CODINGSCHEME_VERSION_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodingSchemeVersion();
					}	
				});

		}}};
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
		this.searchExtension = 
			(SearchExtension) this.lexBigService.getGenericExtension("SearchExtension");
		} catch (Exception e){
			log.warn("SearchExtension is not available.");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.bulk.controller.BulkDownloader#download(java.io.OutputStream, java.util.Set, java.util.List, java.lang.String)
	 */
	@Override
	public void download(OutputStream outputStream, Set<CodingSchemeReference> codingSchemes, List<String> fields, char separator) {
		ResolvedConceptReferencesIterator itr;
		try {
			itr = searchExtension.search(null, codingSchemes, MatchAlgorithm.LUCENE);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}

		this.doWrite(outputStream, new ResolvedConceptReferencesIteratorAdapter(itr), separator, fields);
	}

	@Override
	protected Map<String, Extractor> getExtractorMap() {
		return EXTRACTOR_MAP;
	}

}
