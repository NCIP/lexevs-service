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

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceSummaryUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
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
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRenderingList csrFilteredList;
		csrFilteredList = CommonResourceSummaryUtils.getCodingSchemeRenderingList(lexBigService, nameConverter, null, queryData, null);
		return csrFilteredList.getCodingSchemeRendering().length;
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntry> getResourceList(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {
		LexBIGService lexBigService = this.getLexBigService();		
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRendering[] csRendering = CommonResourceSummaryUtils.getCodingSchemeRendering(lexBigService, nameConverter, queryData, null, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPageFromArray(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		return CommonResourceSummaryUtils.createDirectoryResultWithEntryFullVersionDescriptions(lexBigService, this.transformer, csRenderingPage, atEnd);
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntrySummary> getResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<CodeSystemVersionQuery> queryData = new QueryData<CodeSystemVersionQuery>(query, null);
		
		CodingSchemeRendering[] csRendering = CommonResourceSummaryUtils.getCodingSchemeRendering(lexBigService, nameConverter, queryData, null, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPageFromArray(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		return CommonResourceSummaryUtils.createDirectoryResultWithEntryDescriptions(this.transformer, csRenderingPage, atEnd, Constants.SUMMARY_DESCRIPTION);
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.createSupportedMatchAlgorithms();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.createSupportedSearchReferences();
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

}
