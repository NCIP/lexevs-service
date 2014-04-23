/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.match.ContainsMatcher;
import edu.mayo.cts2.framework.filter.match.ExactMatcher;
import edu.mayo.cts2.framework.filter.match.ResolvableMatchAlgorithmReference;
import edu.mayo.cts2.framework.filter.match.StartsWithMatcher;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQuery;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQueryService;
@Component
public class LexEvsResolvedValueSetQueryService extends AbstractLexEvsService
		implements LexEvsChangeEventObserver, ResolvedValueSetQueryService, InitializingBean {
	@Resource 
	private CommonResolvedValueSetUtils resolverUtils;
	@Resource
	private ResolvedCodingSchemeTransform transform;
	@Resource
	private VersionNameConverter nameConverter;
	
	private Object mutex = new Object();

	private Set<String> activeCache = new HashSet<String>();

	@Override
	public void afterPropertiesSet() throws Exception {
		this.onChange();
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		Set<MatchAlgorithmReference> returnSet = new HashSet<MatchAlgorithmReference>();
		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH
				.getMatchAlgorithmReference();
		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(exactMatch,
						new ExactMatcher()));
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS
				.getMatchAlgorithmReference();
		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(contains,
						new ContainsMatcher()));
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH
				.getMatchAlgorithmReference();
		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(startsWith,
						new StartsWithMatcher()));
		return returnSet;
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		ComponentReference name = StandardModelAttributeReference.RESOURCE_NAME
				.getComponentReference();
		ComponentReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS
				.getComponentReference();
		return new HashSet<ComponentReference>(Arrays.asList(name,
				description));

	}

	@Override
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return null;
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return null;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return null;
	}

	@Override
	public DirectoryResult<ResolvedValueSetDirectoryEntry> getResourceSummaries(
			ResolvedValueSetQuery query, SortCriteria sort, Page page)  {
		try {
		List<CodingScheme> restrictedList= processQuery(query);
		List<ResolvedValueSetDirectoryEntry> results= transform.transform(restrictedList);
		List<ResolvedValueSetDirectoryEntry> pagedResult =CommonPageUtils.getPage(results, page);
        boolean moreResults = results.size() > page.getEnd();
		
		
		
		return new DirectoryResult<ResolvedValueSetDirectoryEntry>(pagedResult,!moreResults);

		}catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public int count(ResolvedValueSetQuery query) {
		try {
		List<CodingScheme> restrictedList= processQuery(query);
		return restrictedList.size();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}

	public CommonResolvedValueSetUtils getResolverUtils() {
		return resolverUtils;
	}

	public void setResolverUtils(CommonResolvedValueSetUtils resolverUtils) {
		this.resolverUtils = resolverUtils;
	}
	
	private List<CodingScheme>  processQuery(ResolvedValueSetQuery query) throws LBException{
		List<CodingScheme> csList= this.filterInactive(getLexEVSResolvedService().listAllResolvedValueSets());
		List<CodingScheme> restrictedList= resolverUtils.restrictByQuery(csList, query);
		if (query!= null) {
			restrictedList= CommonSearchFilterUtils.filterLexCodingSchemeList(restrictedList, query.getFilterComponent(), nameConverter);
		}
		return restrictedList;
	}
	
	private List<CodingScheme> filterInactive(List<CodingScheme> codingSchemes){
		if(codingSchemes == null){
			return null;
		}
		
		List<CodingScheme> returnList = new ArrayList<CodingScheme>();
		
		synchronized(this.mutex){
			for(CodingScheme cs : codingSchemes){
				if(this.activeCache.contains(
						this.getKey(cs.getCodingSchemeURI(), cs.getRepresentsVersion()))){
					returnList.add(cs);
				}
			}
		}
		
		return returnList;
	}

	@Override
	public void onChange() {
		synchronized(this.mutex){
			this.activeCache.clear();
			
			try {
				for(CodingSchemeRendering cs : 
					this.getLexBigService().getSupportedCodingSchemes().getCodingSchemeRendering()){
					if(cs.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE)){
						this.activeCache.add(
								this.getKey(
										cs.getCodingSchemeSummary().getCodingSchemeURI(),
										cs.getCodingSchemeSummary().getRepresentsVersion()));
					}
				}
			} catch (LBInvocationException e) {
				throw new IllegalStateException(e);
			}
		}	
	}
	
	private String getKey(String uri, String version){
		return uri + version;
	}

}
