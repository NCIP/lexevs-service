package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
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

@Component
public class LexEvsCodeSystemVersionQueryService extends AbstractLexEvsService
		implements CodeSystemVersionQueryService {

	public final static String SEARCH_TYPE_CONTAINS = "contains";
	public final static String SEARCH_TYPE_EXACT_MATCH = "exactMatch";
	public final static String SEARCH_TYPE_STARTS_WITH = "startsWith";
	
	public final static String ATTRIBUTE_NAME_ABOUT = "about";
	public final static String ATTRIBUTE_NAME_RESOURCE_SYNOPSIS = "resourceSynopsis";
	public final static String ATTRIBUTE_NAME_RESOURCE_NAME = "resourceName";

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
			if (codeSystem.getUri() != null) {
				searchCodingSchemeName = codeSystem.getUri();
			} else {
				searchCodingSchemeName = codeSystem.getName();
			}
		}
		
		LexBIGService lexBigService = getLexBigService();
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			if (searchCodingSchemeName != null) {
				csrFilteredList = filterResourceSummariesByCodingSchemeName(searchCodingSchemeName, csrFilteredList);
			}
			
			if (filters != null)  {
				// Check csrFilteredList size up front and don't enter filtering legs if list is empty
				if ((csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)) {
					Iterator<ResolvedFilter> filtersItr = filters.iterator();
					while (filtersItr.hasNext()) {
						if (csrFilteredList.getCodingSchemeRenderingCount() == 0) {
							break;
						} else {
							ResolvedFilter resolvedFilter = filtersItr.next();
							csrFilteredList = filterResourceSummariesByResolvedFilter(resolvedFilter, csrFilteredList);
						}						
					}
				}
			}
			
			return csrFilteredList.getCodingSchemeRendering();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private CodingSchemeRenderingList filterResourceSummariesByCodingSchemeName(String searchCodingSchemeName, CodingSchemeRenderingList csrFilteredList) {
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			if (codingSchemeSummary.getLocalName().equals(searchCodingSchemeName)) {
				temp.addCodingSchemeRendering(render);
			}
		}
		
		return temp;
	}
	
	private CodingSchemeRenderingList filterResourceSummariesByResolvedFilter(ResolvedFilter resolvedFilter, CodingSchemeRenderingList csrFilteredList) {
		
		boolean caseSensitive = false;
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		MatchAlgorithmReference matchAlgorithmReference = resolvedFilter.getMatchAlgorithmReference();
		String searchType = matchAlgorithmReference.getContent();
		
		PropertyReference propertyReference = resolvedFilter.getPropertyReference();
		String searchAttribute = propertyReference.getReferenceTarget().getName();
		
		String matchStr = resolvedFilter.getMatchValue();	
		String lowerCaseMatchStr = matchStr.toLowerCase(); // Assuming default Locale is ok to use
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for (CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			String retrievedAttrValue = null;
			if (searchAttribute.equals(ATTRIBUTE_NAME_ABOUT)) {
				retrievedAttrValue = codingSchemeSummary.getCodingSchemeURI();
			} else if (searchAttribute.equals(ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
				retrievedAttrValue = codingSchemeSummary.getCodingSchemeDescription().getContent();
			} else if (searchAttribute.equals(ATTRIBUTE_NAME_RESOURCE_NAME)) {
				retrievedAttrValue = 
					this.nameConverter.toCts2CodeSystemVersionName(
						codingSchemeSummary.getLocalName(), 
						codingSchemeSummary.getRepresentsVersion());
			}
			if (retrievedAttrValue != null) {
				if (searchType.equals(SEARCH_TYPE_EXACT_MATCH)) {
					if (caseSensitive) {
						if (retrievedAttrValue.equals(matchStr)) {
							temp.addCodingSchemeRendering(render);
						}
					} else {
						if (retrievedAttrValue.equalsIgnoreCase(matchStr)) {
							temp.addCodingSchemeRendering(render);
						}						
					}
				} else if (searchType.equals(SEARCH_TYPE_CONTAINS)) {
					if (caseSensitive) {
						if (retrievedAttrValue.indexOf(matchStr) != -1) {
							temp.addCodingSchemeRendering(render);
						}
					} else {
						retrievedAttrValue = retrievedAttrValue.toLowerCase(); // Assuming default Locale is ok to use
						if (retrievedAttrValue.indexOf(lowerCaseMatchStr) != -1) {
							temp.addCodingSchemeRendering(render);
						}						
					}
				} else if (searchType.equals(SEARCH_TYPE_STARTS_WITH)) {
					if (caseSensitive) {
						if (retrievedAttrValue.startsWith(matchStr)) {
							temp.addCodingSchemeRendering(render);
						}
					} else {
						retrievedAttrValue = retrievedAttrValue.toLowerCase(); // Assuming default Locale is ok to use
						if (retrievedAttrValue.startsWith(lowerCaseMatchStr)) {
							temp.addCodingSchemeRendering(render);
						}						
					}
				}  
			} // end brace retrievedAttr != null
		}  // end brace for loop
		
		return temp;
	}

	// -------- Implemented methods ----------------
	@Override
	public int count(CodeSystemVersionQuery query) {
		return this.doGetResourceSummaries(query, null).length;
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntry> getResourceList(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {

		CodingSchemeRendering[] csRendering = this.doGetResourceSummaries(
				query, sortCriteria);

		List<CodeSystemVersionCatalogEntry> list = new ArrayList<CodeSystemVersionCatalogEntry>();

		for (CodingSchemeRendering render : csRendering) {
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

		DirectoryResult<CodeSystemVersionCatalogEntry> directoryResult = CommonUtils.getSublist(list, page);
		return directoryResult;
	}

	@Override
	public DirectoryResult<CodeSystemVersionCatalogEntrySummary> getResourceSummaries(
			CodeSystemVersionQuery query, SortCriteria sortCriteria, Page page) {

		CodingSchemeRendering[] csRendering = this.doGetResourceSummaries(
				query, sortCriteria);

		List<CodeSystemVersionCatalogEntrySummary> list = new ArrayList<CodeSystemVersionCatalogEntrySummary>();

		for (CodingSchemeRendering render : csRendering) {
			list.add(codingSchemeTransformer.transform(render));
		}

		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = CommonUtils.getSublist(list, page);
		return directoryResult;
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
