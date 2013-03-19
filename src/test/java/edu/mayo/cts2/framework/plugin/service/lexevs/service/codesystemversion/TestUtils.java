package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

public class TestUtils {
	public static CodeSystemVersionQueryServiceRestrictions createRestrictions_NameOnly(String nameOrURI){
		NameOrURI codeSystem = ModelUtils.nameOrUriFromName(nameOrURI);	
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(codeSystem);
		return restrictions;
	}
	
	public static CodeSystemVersionQueryImpl createQuery_RestrictionsOnly(CodeSystemVersionQueryServiceRestrictions restrictions){
		Query query = null;
		Set<ResolvedFilter> filterComponent = null;
		ResolvedReadContext readContext = null;		
		CodeSystemVersionQueryImpl codeSystemVersionQuery = new CodeSystemVersionQueryImpl(query, filterComponent, readContext, restrictions);
		return codeSystemVersionQuery;
	}
	
	public static ResolvedFilter createFilter(PropertyReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setPropertyReference(property);
		
		return filter;
	}

	public static Set<ResolvedFilter> createFilterSet(PropertyReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setPropertyReference(property);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(filter));
		
		return filterSet;
	}
	
	public static Set<ResolvedFilter> createFilterSet(String about_contains, String resourceSynopsis_startsWith, String resourceName_exactMatch){
		ResolvedFilter aboutFilter = createFilter(
				StandardModelAttributeReference.ABOUT.getPropertyReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(), about_contains);

		ResolvedFilter synopsisFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_SYNOPSIS
						.getPropertyReference(),
				StandardMatchAlgorithmReference.STARTS_WITH
						.getMatchAlgorithmReference(), resourceSynopsis_startsWith);

		ResolvedFilter nameFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_NAME
						.getPropertyReference(),
				StandardMatchAlgorithmReference.EXACT_MATCH
						.getMatchAlgorithmReference(), resourceName_exactMatch);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(aboutFilter, synopsisFilter, nameFilter));
		
		return filterSet;
	}
	
	public static CodeSystemVersionQueryImpl createQuery_FiltersOnly(Set<ResolvedFilter> filters){
		Query query = null;
		ResolvedReadContext readContext = null;
		CodeSystemVersionQueryServiceRestrictions csvQueryServiceRestrictions = null;
		CodeSystemVersionQueryImpl codeSystemQuery = new CodeSystemVersionQueryImpl(query, filters, readContext, csvQueryServiceRestrictions);
		
		return codeSystemQuery;
	}
	
	public static DirectoryResult<CodeSystemVersionCatalogEntrySummary> createResourceSummaries_DirectoryResults_QueryOnly(LexEvsCodeSystemVersionQueryService service, CodeSystemVersionQueryImpl query){
		SortCriteria sortCriteria = null;	
		Page page = new Page();
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, sortCriteria, page);
		
		return dirResult;
	}

	public static DirectoryResult<CodeSystemVersionCatalogEntry> createResourceList_DirectoryResults_QueryOnly(LexEvsCodeSystemVersionQueryService service, CodeSystemVersionQueryImpl query){
		SortCriteria sortCriteria = null;	
		Page page = new Page();
		DirectoryResult<CodeSystemVersionCatalogEntry> dirResult = service.getResourceList(query, sortCriteria, page);
		
		return dirResult;
	}
}
