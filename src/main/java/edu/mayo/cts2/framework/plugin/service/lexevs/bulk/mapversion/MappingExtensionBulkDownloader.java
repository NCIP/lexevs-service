/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Impl.helpers.ResolvedConceptReferencesIteratorAdapter;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.AbstractBulkDownloader;
import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.Extractor;

/**
 * A Bulk Downloader based on the LexEVS MappingExtension.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class MappingExtensionBulkDownloader 
	extends AbstractBulkDownloader 
	implements MapVersionBulkDownloader, InitializingBean {

	@Resource
	private MappingExtension mappingExtension;
	
	private final static Map<String, Extractor> SOURCE_EXTRACTOR_MAP = new HashMap<String,Extractor>(){

		private static final long serialVersionUID = -7214815015371005224L;
		{{
				put(MapVersionBulkDownloader.SOURCE_CODE_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCode();
					}	
				});
				
				put(MapVersionBulkDownloader.SOURCE_NAMESPACE_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodeNamespace();
					}	
				});
				
				put(MapVersionBulkDownloader.SOURCE_DESCRIPTION_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						if(ref.getEntityDescription() != null) {
							return ref.getEntityDescription().getContent();
						} else {
							return "";
						}
					}	
				});
				
				put(MapVersionBulkDownloader.SOURCE_CODINGSCHEME_NAME_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodingSchemeName();
					}	
				});
				
				put(MapVersionBulkDownloader.SOURCE_CODINGSCHEME_URI_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodingSchemeURI();
					}	
				});
				
				put(MapVersionBulkDownloader.SOURCE_CODINGSCHEME_VERSION_FIELD, new Extractor(){
					@Override
					public String extract(ResolvedConceptReference ref) {
						return ref.getCodingSchemeVersion();
					}	
				});

		}}};
		
		private final static Map<String, Extractor> TARGET_EXTRACTOR_MAP = new HashMap<String,Extractor>(){

			private ResolvedConceptReference getTarget(ResolvedConceptReference ref){
				return ref.getSourceOf().getAssociation(0).getAssociatedConcepts().getAssociatedConcept(0);
			}
			
			private static final long serialVersionUID = -7214815015371005224L;
			{{
					put(MapVersionBulkDownloader.TARGET_CODE_FIELD, new Extractor(){
						@Override
						public String extract(ResolvedConceptReference ref) {
							return getTarget(ref).getCode();
						}	
					});
					
					put(MapVersionBulkDownloader.TARGET_NAMESPACE_FIELD, new Extractor(){
						@Override
						public String extract(ResolvedConceptReference ref) {
							return getTarget(ref).getCodeNamespace();
						}	
					});
					
					put(MapVersionBulkDownloader.TARGET_DESCRIPTION_FIELD, new Extractor(){
						@Override
						public String extract(ResolvedConceptReference ref) {
							return getTarget(ref).getEntityDescription().getContent();
						}	
					});
					
					put(MapVersionBulkDownloader.TARGET_CODINGSCHEME_NAME_FIELD, new Extractor(){
						@Override
						public String extract(ResolvedConceptReference ref) {
							return getTarget(ref).getCodingSchemeName();
						}	
					});
					
					put(MapVersionBulkDownloader.TARGET_CODINGSCHEME_URI_FIELD, new Extractor(){
						@Override
						public String extract(ResolvedConceptReference ref) {
							return getTarget(ref).getCodingSchemeURI();
						}	
					});
					
					put(MapVersionBulkDownloader.TARGET_CODINGSCHEME_VERSION_FIELD, new Extractor(){
						@Override
						public String extract(ResolvedConceptReference ref) {
							return getTarget(ref).getCodingSchemeVersion();
						}	
					});

			}}};
			
	private Map<String, Extractor> extractorMap;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Extractor> map = new HashMap<String, Extractor>();
		map.putAll(SOURCE_EXTRACTOR_MAP);
		map.putAll(TARGET_EXTRACTOR_MAP);
		
		this.extractorMap = map;
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion.MapVersionBulkDownloader#download(java.io.OutputStream, org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference, java.util.List, char)
	 */
	@Override
	public void download(
			OutputStream outputStream,
			CodingSchemeReference codingScheme, 
			List<String> fields,
			char separator) {
		
		ResolvedConceptReferencesIterator itr;
		try {
			itr = this.mappingExtension.resolveMapping(
				codingScheme.getCodingScheme(), 
				codingScheme.getVersionOrTag(), 
				null, 
				null);
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
		return this.extractorMap;
	}


}
