/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils.UriVersionPair;
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
		implements CodeSystemVersionQueryService {

	@Resource
	private CodingSchemeToCodeSystemTransform transformer;

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private MappingExtension mappingExtension;
	
	@Resource
	private CommonResolvedValueSetUtils commonResolvedValueSetUtils;

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

		CodingSchemeRendering [] renderings = this.getNonMappingAndNonResolvedValueSetCodingSchemes(queryData, null);
		
		return renderings.length;
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntryListEntry> getResourceList(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {
		LexBIGService lexBigService = this.getLexBigService();		
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRendering[] csRendering = this.getNonMappingAndNonResolvedValueSetCodingSchemes(queryData, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;

		return CommonResourceUtils.createDirectoryResultWithEntryFullVersionDescriptions(lexBigService, this.transformer, csRenderingPage, atEnd);
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntrySummary> getResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {
		return getResourceSummaries(query, sortCriteria, page, null);
	}
	
	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntrySummary> getResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page, String uri) {
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRendering[] csRendering = this.getNonMappingAndNonResolvedValueSetCodingSchemes(queryData, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPage(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		return CommonResourceUtils.createDirectoryResultsWithSummary(this.transformer, csRenderingPage, atEnd);
	}
	
	protected CodingSchemeRendering[] getNonMappingAndNonResolvedValueSetCodingSchemes(QueryData<CodeSystemVersionQuery> queryData, SortCriteria sortCriteria){
			List<CodingSchemeRendering> returnList = new ArrayList<CodingSchemeRendering>();
			
			CodingSchemeRendering[] renderings = 
				CommonResourceUtils.getLexCodingSchemeRenderings(
					this.getLexBigService(), this.nameConverter, queryData, null, sortCriteria);
			
			for(CodingSchemeRendering rendering : renderings){
				String uri = rendering.getCodingSchemeSummary().getCodingSchemeURI();
				String version = rendering.getCodingSchemeSummary().getRepresentsVersion();
				
				boolean isMapping = this.isMappingCodingScheme(uri, version);
				if(! isMapping && 
						! this.commonResolvedValueSetUtils.isResolvedValueSet(new UriVersionPair(uri, version))){
					returnList.add(rendering);
				}
			}
			
			return returnList.toArray(new CodingSchemeRendering[returnList.size()]);
	}
	
	protected boolean isMappingCodingScheme(String uri, String version){
		try {
			return this.mappingExtension.
				isMappingCodingScheme(uri, Constructors.createCodingSchemeVersionOrTagFromVersion(version));
		} catch (LBParameterException e) {
			//didn't find it or a LexEVS error -- assume not a Mapping CodingScheme.
			log.warn(e);
			return false;
		}
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		Set<ComponentReference> references = new HashSet<ComponentReference>(
				CommonSearchFilterUtils.getLexSupportedSearchReferences());
		
		ComponentReference tag = new ComponentReference();
		tag.setPropertyReference(new URIAndEntityName());
		tag.getPropertyReference().setName("tag");
		
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
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return new HashSet<ComponentReference>();
	}

	public MappingExtension getMappingExtension() {
		return mappingExtension;
	}

	public void setMappingExtension(MappingExtension mappingExtension) {
		this.mappingExtension = mappingExtension;
	}

	public CommonResolvedValueSetUtils getCommonResolvedValueSetUtils() {
		return commonResolvedValueSetUtils;
	}

	public void setCommonResolvedValueSetUtils(
			CommonResolvedValueSetUtils commonResolvedValueSetUtils) {
		this.commonResolvedValueSetUtils = commonResolvedValueSetUtils;
	}


}
