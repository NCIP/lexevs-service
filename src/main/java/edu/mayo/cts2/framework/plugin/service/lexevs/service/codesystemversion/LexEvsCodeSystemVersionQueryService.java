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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.match.ContainsMatcher;
import edu.mayo.cts2.framework.filter.match.ExactMatcher;
import edu.mayo.cts2.framework.filter.match.ResolvableMatchAlgorithmReference;
import edu.mayo.cts2.framework.filter.match.StartsWithMatcher;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
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
	private CodingSchemeToCodeSystemTransform codingSchemeTransformer;

	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	

	// ------ Local methods ----------------------
	public CodingSchemeToCodeSystemTransform getCodingSchemeTransformer() {
		return codingSchemeTransformer;
	}

	public void setCodingSchemeTransformer(
			CodingSchemeToCodeSystemTransform codingSchemeTransformer) {
		this.codingSchemeTransformer = codingSchemeTransformer;
	}
	
	public CodeSystemVersionNameConverter getCodeSystemVersionNameConverter(){
		return nameConverter;
	}

	public void setCodeSystemVersionNameConverter(CodeSystemVersionNameConverter converter){
		this.nameConverter = converter;
	}
	
	protected CodingSchemeRendering[] doGetResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria) {

		Set<ResolvedFilter> filters = null; 
		CodeSystemVersionQueryServiceRestrictions codeSystemVersionQueryServiceRestrictions = null;
		
		if (query != null) {
			codeSystemVersionQueryServiceRestrictions = query.getRestrictions();
			filters = query.getFilterComponent();
		}		
		
		NameOrURI codeSystem = null;
		if (codeSystemVersionQueryServiceRestrictions != null) {
			codeSystem = query.getRestrictions().getCodeSystem();
		}
		
		String searchCodingSchemeName = null;
		if (codeSystem != null) {
			searchCodingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
		}
		
		LexBIGService lexBigService = getLexBigService();
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			if (searchCodingSchemeName != null) {
				csrFilteredList = CommonUtils.filterResourceSummariesByCodingSchemeName(searchCodingSchemeName, csrFilteredList);
			}
			
			if ((filters != null) && (csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)) {
				Iterator<ResolvedFilter> filtersItr = filters.iterator();
				while (filtersItr.hasNext() && (csrFilteredList.getCodingSchemeRenderingCount() > 0)) {
						ResolvedFilter resolvedFilter = filtersItr.next();
						csrFilteredList = CommonUtils.filterResourceSummariesByResolvedFilter(resolvedFilter, 
								csrFilteredList,
								nameConverter);
				}
			}
			
			return csrFilteredList.getCodingSchemeRendering();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	// -------- Implemented methods ----------------
	@Override
	public int count(CodeSystemVersionQuery query) {
		return this.doGetResourceSummaries(query, null).length;
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntry> getResourceList(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {

		CodingSchemeRendering[] csRendering = this.doGetResourceSummaries(query, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = CommonUtils.getRenderingPage(csRendering, page);
		
		List<CodeSystemVersionCatalogEntry> list = new ArrayList<CodeSystemVersionCatalogEntry>();

		for (CodingSchemeRendering render : csRenderingPage) {
			String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
			String version = render.getCodingSchemeSummary().getRepresentsVersion();
			CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
			CodingScheme codingScheme;
			try {
				codingScheme = this.getLexBigService().resolveCodingScheme(codingSchemeName, tagOrVersion);
				list.add(codingSchemeTransformer.transform(codingScheme));
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}

		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		
		return new DirectoryResult<CodeSystemVersionCatalogEntry>(list, atEnd);
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntrySummary> getResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {

		CodingSchemeRendering[] csRendering = this.doGetResourceSummaries(query, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = CommonUtils.getRenderingPage(csRendering, page);

		List<CodeSystemVersionCatalogEntrySummary> list = new ArrayList<CodeSystemVersionCatalogEntrySummary>();

		if(csRenderingPage != null){
			for (CodingSchemeRendering render : csRenderingPage) {
				list.add(codingSchemeTransformer.transform(render));
			}
		}
		
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		
		return new DirectoryResult<CodeSystemVersionCatalogEntrySummary>(list, atEnd);
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {

		Set<MatchAlgorithmReference> returnSet = new HashSet<MatchAlgorithmReference>();

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();

		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(exactMatch,
						new ExactMatcher()));

		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();

		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(contains,
						new ContainsMatcher()));

		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(startsWith,
						new StartsWithMatcher()));

		return returnSet;
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		
		PropertyReference
			name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();
		
		PropertyReference
			about = StandardModelAttributeReference.ABOUT.getPropertyReference();
		
		PropertyReference
			description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		
		return new HashSet<PropertyReference>(Arrays.asList(name,about,description));
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

}
