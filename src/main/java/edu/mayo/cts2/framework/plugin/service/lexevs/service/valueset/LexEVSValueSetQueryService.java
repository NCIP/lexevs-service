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
	
//	private interface TransformClosure<T>{
//		T transform(org.LexGrid.valueSets.ValueSetDefinition item);
//	}
	
//	private final Callback<List<String>, ValueSetCatalogEntryListEntry> 
//	listCallack = 
//		new DefinitionCallback<ValueSetCatalogEntryListEntry>(
//			new TransformClosure<ValueSetCatalogEntryListEntry>() {
//
//
//				@Override
//				public ValueSetCatalogEntryListEntry transform(ValueSetDefinition item) {
//					return transformer.transformFullDescription(item);
//				}
//
//			});

//private final Callback<List<String>, ValueSetCatalogEntrySummary> summariesCallback = 
//	new DefinitionCallback<ValueSetCatalogEntrySummary>(
//		new TransformClosure<ValueSetCatalogEntrySummary>() {
//
//			@Override
//			public ValueSetCatalogEntrySummary transform(
//					ValueSetDefinition item) {
//				return transformer.transformSummaryDescription(item);
//			}
//
//		});

//	@Override
//	public DirectoryResult<ValueSetCatalogEntrySummary> getResourceSummaries(
//			ValueSetQuery query, SortCriteria sortCriteria, Page page) {
//		List<String> uris = this.definitionServices.listValueSetDefinitionURIs();
//		
//		ValueSetDirectoryBuilder<ValueSetCatalogEntrySummary> builder = 
//			new ValueSetDirectoryBuilder<ValueSetCatalogEntrySummary>(
//					uris, 
//					this.summariesCallback, 
//					null, 
//					null);
//		
//		return builder.
//				addMaxToReturn(page.getMaxToReturn()).
//				addStart(page.getStart()).
//				resolve();
//	}
//	@Override
//	public DirectoryResult<ValueSetDefinitionDirectoryEntry> getResourceSummaries(
//			ValueSetQuery query, SortCriteria sortCriteria, Page page) {
//		try {
//		List<ValueSetDefinition> restrictedList= processQuery(query);
//		List<ValueSetDefinitionDirectoryEntry> results= transformer.transform(restrictedList);
//		List<ValueSetDefinitionDirectoryEntry> pagedResult = CommonPageUtils.getPage(results, page);
//        boolean moreResults = results.size() > page.getEnd();
//		
//		
//		
//		return new DirectoryResult<ValueSetDefinitionDirectoryEntry>(pagedResult,!moreResults);
//
//		}catch (Exception ex) {
//			throw new RuntimeException(ex);
//		}
//	}

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

//	@Override
//	public DirectoryResult<ValueSetCatalogEntryListEntry> getResourceList(
//			ValueSetQuery query, SortCriteria sortCriteria, Page page) {
//		List<String> uris = this.definitionServices.listValueSetDefinitionURIs();
//		
//		ValueSetDirectoryBuilder<ValueSetCatalogEntryListEntry> builder = 
//			new ValueSetDirectoryBuilder<ValueSetCatalogEntryListEntry>(
//					uris, 
//					this.listCallack, 
//					null, 
//					null);
//		
//		return builder.
//				addMaxToReturn(page.getMaxToReturn()).
//				addStart(page.getStart()).
//				resolve();
//	}
	

	@Override
	public DirectoryResult<ValueSetCatalogEntryListEntry> getResourceList(
			ValueSetQuery query, SortCriteria sortCriteria, Page page) {
//		List<String> uris = this.definitionServices.listValueSetDefinitionURIs();
//		List<ValueSetCatalogEntryListEntry> definitions = new ArrayList<ValueSetCatalogEntryListEntry>();
//		
//		for(String uri: uris){
//			try {
//			URI u = new URI(uri);
//			
//				definitions.add(transformer.transformFullDescription(this.definitionServices.getValueSetDefinition(u, null)));
//
//			} catch (LBException e) {
//				throw new RuntimeException(e);
//			} catch (URISyntaxException e) {
//				throw new RuntimeException(e);
//			}
//		}
//			List<ValueSetCatalogEntryListEntry> pagedResult = CommonPageUtils
//					.getPage(definitions, page);
//			boolean moreResults = definitions.size() > page.getEnd();
//			return new DirectoryResult<ValueSetCatalogEntryListEntry>(
//					pagedResult, !moreResults);
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
//		ComponentReference about = StandardModelAttributeReference.ABOUT
//				.getComponentReference();
//		ComponentReference keyword = StandardModelAttributeReference.KEYWORD
//				.getComponentReference();
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
		return null;
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
	
//private class DefinitionCallback<T> implements Callback<List<String>, T> {
//		
//		private TransformClosure<T> transformClosure;
//		
//		private DefinitionCallback(TransformClosure<T> transformClosure){
//			super();
//			this.transformClosure = transformClosure;
//		}
//
//		@Override
//		public DirectoryResult<T> execute(List<String> state, int start,
//				int maxResults) {
//			List<T> returnList = new ArrayList<T>();
//
//			int counter = 0;
//			for(String uri : state){ 
//				if(counter >= start && returnList.size() < maxResults){
//					org.LexGrid.valueSets.ValueSetDefinition definition;
//					try {
//						definition = definitionServices.getValueSetDefinition(new URI(uri), null);
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}
//					
//					returnList.add(this.transformClosure.transform(definition));
//				}
//				
//				counter++;
//			}
//			
//			return 
//				new DirectoryResult<T>(
//						returnList, 
//						start + maxResults > state.size());
//		}
//
//		@Override
//		public int executeCount(List<String> state) {
//			return state.size();
//		}
//		
//
//	}
	
	private String getKey(String uri, String name){
		return uri + name;
		//TODO Update to value set definition key of some kind
}





}
