/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.Impl.helpers.ResolvedConceptReferencesIteratorAdapter;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.compass.core.util.CollectionUtils;
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
	
	protected Logger log = LogManager.getLogger(this.getClass());
	
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
						if(ref.getEntityDescription() != null) {
							return ref.getEntityDescription().getContent();
						} else {
							return "";
						}
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
	public void download(OutputStream outputStream, Set<CodingSchemeReference> codingSchemes, Set<CodingSchemeReference> excludedCodingSchemes, List<String> fields, char separator) {
		ResolvedConceptReferencesIterator itr;
		
		if(CollectionUtils.isEmpty(codingSchemes)){
			codingSchemes = null;
		}
		
		try {
			itr = searchExtension.search(null, codingSchemes, excludedCodingSchemes, MatchAlgorithm.LUCENE, false, false);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}

		this.doWrite(outputStream, new ResolvedConceptReferencesIteratorAdapter(itr), separator, fields);
		
		try {
			itr.release();
		} catch (LBResourceUnavailableException e) {
			itr = null;
		}
	}

	@Override
	protected Map<String, Extractor> getExtractorMap() {
		return EXTRACTOR_MAP;
	}

}
