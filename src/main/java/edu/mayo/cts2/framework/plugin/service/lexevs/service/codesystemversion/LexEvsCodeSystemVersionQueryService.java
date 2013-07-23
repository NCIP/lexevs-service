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
*
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.types.TargetReferenceType;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQueryService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsCodeSystemVersionQueryService extends AbstractLexEvsService
		implements 
			InitializingBean,
			LexEvsChangeEventObserver, 
			CodeSystemVersionQueryService {

	@Resource
	private CodingSchemeToCodeSystemTransform transformer;

	@Resource
	private VersionNameConverter nameConverter;
	
	private Set<UriVersionPair> resolvedValueSets = new HashSet<UriVersionPair>();
	
	private Object mutex = new Object();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.buildResolvedValueSetCache();
	}
	
	public void buildResolvedValueSetCache() {
		synchronized(this.mutex){
			this.resolvedValueSets.clear();
			try {
				for(CodingScheme cs : this.getLexEVSResolvedService().listAllResolvedValueSets()){
					this.resolvedValueSets.add(new UriVersionPair(cs.getCodingSchemeURI(), cs.getRepresentsVersion()));
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	// ------ Local methods ----------------------
	public void setCodingSchemeTransformer(
			CodingSchemeToCodeSystemTransform codingSchemeTransformer) {
		this.transformer = codingSchemeTransformer;
	}
	
	public void setCodeSystemVersionNameConverter(VersionNameConverter converter){
		this.nameConverter = converter;
	}
	
	// -------- Implemented methods ----------------
	@Override
	public int count(CodeSystemVersionQuery query) {
		if(query == null){
			return 0;
		}
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);

		CodingSchemeRendering [] renderings = this.getNonResolvedValueSetCodingSchemes(queryData, null);
		
		return renderings.length;
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntry> getResourceList(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {
		LexBIGService lexBigService = this.getLexBigService();		
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRendering[] csRendering = this.getNonResolvedValueSetCodingSchemes(queryData, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;

		return CommonResourceUtils.createDirectoryResultWithEntryFullVersionDescriptions(lexBigService, this.transformer, csRenderingPage, atEnd);
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntrySummary> getResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRendering[] csRendering = this.getNonResolvedValueSetCodingSchemes(queryData, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		return CommonResourceUtils.createDirectoryResultsWithSummary(this.transformer, csRenderingPage, atEnd);
	}
	
	protected CodingSchemeRendering[] getNonResolvedValueSetCodingSchemes(QueryData<CodeSystemVersionQuery> queryData, SortCriteria sortCriteria){
		synchronized(this.mutex){
			List<CodingSchemeRendering> returnList = new ArrayList<CodingSchemeRendering>();
			
			CodingSchemeRendering[] renderings = 
				CommonResourceUtils.getLexCodingSchemeRenderings(
					this.getLexBigService(), this.nameConverter, queryData, null, sortCriteria);
			
			for(CodingSchemeRendering rendering : renderings){
				String uri = rendering.getCodingSchemeSummary().getCodingSchemeURI();
				String version = rendering.getCodingSchemeSummary().getRepresentsVersion();
				
				if(! this.resolvedValueSets.contains(new UriVersionPair(uri, version))){
					returnList.add(rendering);
				}
			}
			
			return returnList.toArray(new CodingSchemeRendering[returnList.size()]);
		}
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		Set<PropertyReference> references = new HashSet<PropertyReference>(
				CommonSearchFilterUtils.getLexSupportedSearchReferences());
		
		PropertyReference tag = new PropertyReference();
		tag.setReferenceType(TargetReferenceType.ATTRIBUTE);
		tag.setReferenceTarget(new URIAndEntityName());
		tag.getReferenceTarget().setName("tag");
		
		references.add(tag);
		
		return references;
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

	@Override
	public void onChange() {
		this.buildResolvedValueSetCache();
	}
	
	private static class UriVersionPair {
		private String uri;
		private String version;
		
		protected UriVersionPair(String uri, String version) {
			super();
			this.uri = uri;
			this.version = version;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((uri == null) ? 0 : uri.hashCode());
			result = prime * result
					+ ((version == null) ? 0 : version.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UriVersionPair other = (UriVersionPair) obj;
			if (uri == null) {
				if (other.uri != null)
					return false;
			} else if (!uri.equals(other.uri))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}	
	}

}
