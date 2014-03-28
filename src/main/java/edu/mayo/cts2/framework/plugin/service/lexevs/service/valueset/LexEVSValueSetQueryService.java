package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.filter.match.ContainsMatcher;
import edu.mayo.cts2.framework.filter.match.ExactMatcher;
import edu.mayo.cts2.framework.filter.match.ResolvableMatchAlgorithmReference;
import edu.mayo.cts2.framework.filter.match.StartsWithMatcher;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntrySummary;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonValueSetUtils;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQuery;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetQuery;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetQueryService;

@Component
public class LexEVSValueSetQueryService extends AbstractLexEvsService implements
LexEvsChangeEventObserver, ValueSetQueryService, InitializingBean {
	
	@Resource 
	private CommonValueSetUtils valueSetUtils;
	
	
	//Probably don't need this
	@Resource
	private LexEVSValueSetDefinitionServices definitionServices;
	
	@Resource
	private LexEVSValueSetDefinitionToValueSetEntryTransform transformer;
	
	private Object mutex = new Object();

	private Set<String> activeCache = new HashSet<String>();
	

	@Override
	public DirectoryResult<ValueSetCatalogEntrySummary> getResourceSummaries(
			ValueSetQuery query, SortCriteria sortCriteria, Page page) {
		try {
			List<ValueSetDefinition> restrictedList = processQuery(query);
			List<ValueSetCatalogEntrySummary> results = new ArrayList<ValueSetCatalogEntrySummary>();
			for (ValueSetDefinition vsd : restrictedList) {
				results.add(transformer.transformSummaryDescription(vsd));
			}
			List<ValueSetCatalogEntrySummary> pagedResult = CommonPageUtils
					.getPage(results, page);
			boolean moreResults = results.size() > page.getEnd();
			return new DirectoryResult<ValueSetCatalogEntrySummary>(
					pagedResult, !moreResults);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	

	@Override
	public DirectoryResult<ValueSetCatalogEntryListEntry> getResourceList(
			ValueSetQuery query, SortCriteria sortCriteria, Page page) {

		throw new UnsupportedOperationException();
	
	}

	@Override
	public int count(ValueSetQuery query) {
		try {
		List<ValueSetDefinition> restrictedList = processQuery(query);
		return restrictedList.size();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
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
//		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH
//				.getMatchAlgorithmReference();
//		returnSet.add(ResolvableMatchAlgorithmReference
//				.toResolvableMatchAlgorithmReference(startsWith,
//						new StartsWithMatcher()));
		return returnSet;
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		ComponentReference name = StandardModelAttributeReference.RESOURCE_NAME
				.getComponentReference();
		ComponentReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS
				.getComponentReference();
		ComponentReference about = StandardModelAttributeReference.ABOUT
				.getComponentReference();
//		ComponentReference keyword = StandardModelAttributeReference.KEYWORD
//				.getComponentReference();
		return new HashSet<ComponentReference>(Arrays.asList(name,
				description, about));
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
	public void afterPropertiesSet() throws Exception {
		this.onChange();
	}

	@Override
	public void onChange() {
		synchronized(this.mutex){
			this.activeCache.clear();
			for(String uri: 
				getLexEVSValueSetDefinitionServices().listValueSetDefinitionURIs()){
                ValueSetDefinition vsd = null;
                try {
                    vsd = getLexEVSValueSetDefinitionServices().getValueSetDefinition(new URI(uri), null);
            		this.activeCache.add(
							this.getKey(
									vsd.getValueSetDefinitionURI(),
									vsd.getValueSetDefinitionName()));
       
                } catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	private List<ValueSetDefinition>  processQuery(ValueSetQuery query) throws LBException{
		List<ValueSetDefinition> vdList = filterInactive(getValueSetDefinitions());
		List<ValueSetDefinition> restrictedList= valueSetUtils.restrictByQuery(vdList, query);
		if (query!= null) {
			restrictedList= CommonSearchFilterUtils.filterLexValueSetDefinitionList(restrictedList, query.getFilterComponent());
		}
		return restrictedList;
	}
	
	private List<ValueSetDefinition> getValueSetDefinitions(){
		List<String> uris = definitionServices.listValueSetDefinitionURIs();
		List<ValueSetDefinition> definitions = new ArrayList<ValueSetDefinition>();
		
		for(String uri: uris){
			try {
				definitions.add(definitionServices.getValueSetDefinition(new URI(uri), null));
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}
		return definitions;
	}
	
	private List<ValueSetDefinition> filterInactive(List<ValueSetDefinition> valuesets){
			if(valuesets == null){
				return null;
			}
			
			List<ValueSetDefinition> returnList = new ArrayList<ValueSetDefinition>();
			
			synchronized(this.mutex){
				for(ValueSetDefinition vsd : valuesets){
					if(this.activeCache.contains(
							this.getKey(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName()))){
						returnList.add(vsd);
					}
				}
			}
			
			return returnList;
	
		
	}
	
	
	private String getKey(String uri, String name){
		return uri + name;
		//TODO Update to value set definition key of some kind
}





}
