/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference.StateUpdater;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.*;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.ActiveOrAll;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.DelegatingEntityQueryService.QueryType;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.lexevs.dao.index.indexer.LuceneLoaderCodeIndexer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class SearchExtensionEntityQueryService 
	extends AbstractLexEvsService
	implements InitializingBean, DelegateEntityQueryService {
	
	private static final String LUCENE_QUERY = "luceneQuery";
	
	@Resource
	private EntityNameQueryBuilder entityNameQueryBuilder;
	
	@Resource
	private EntityTransform transformer;
	
	@Resource
	private EntityUriResolver entityUriResolver;
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	private SearchExtension searchExtension;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
		this.searchExtension = 
			(SearchExtension) this.getLexBigService().getGenericExtension("SearchExtension");
		} catch (Exception e){
			log.warn("SearchExtension is not available.");
		}
	}

	private class SearchExtensionSummariesCallback extends
		AbstractSearchExtensionCallback<EntityDirectoryEntry>{

		private SearchExtensionSummariesCallback(Set<NameOrURI> codeSystemVersions, boolean includeInactive, boolean includeAnonymous){
			super(codeSystemVersions, includeInactive, includeAnonymous);
		}
		
		@Override
		protected EntityDirectoryEntry doTransform(ResolvedConceptReference ref) {
			return transformer.transformSummaryDescription(ref);
		}
	}
	
	private abstract class AbstractSearchExtensionCallback<T> implements Callback<String,T>{

		private List<NameOrURI> codeSystemVersions;
		private boolean includeInactive;
		private boolean includeAnonymous;
		
		private AbstractSearchExtensionCallback(Set<NameOrURI> codeSystemVersions, boolean includeInactive, boolean includeAnonymous){
			super();
			this.codeSystemVersions = this.removeNulls(codeSystemVersions);
			this.includeInactive = includeInactive;
			this.includeAnonymous = includeAnonymous;
		}

		private <I> List<I> removeNulls(Iterable<I> items) {
			List<I> returnList = new ArrayList<I>();
			if(items != null){
				for(I item : items){
					if(item != null){
						returnList.add(item);
					}
				}
			}
			
			return returnList;
		}

		@Override
		public DirectoryResult<T> execute(
				String state, 
				int start,
				int maxResults) {
			ResolvedConceptReferencesIterator iterator;
			try {
				iterator = searchExtension.search(state, toCodingSchemeReference(this.codeSystemVersions), null, MatchAlgorithm.LUCENE, this.includeAnonymous, this.includeInactive);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
			
			ResolvedConceptReferenceList list;
			boolean atEnd;
			try {
				int total = iterator.numberRemaining();
				int position = start + maxResults;

				int end = Math.min(total, position);
				list = iterator.get(start, end);

				atEnd = iterator.numberRemaining() <= start + maxResults;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		
			List<T> returnList = new ArrayList<T>();
			for(ResolvedConceptReference ref : list.getResolvedConceptReference()){
				returnList.add(this.doTransform(ref));
			}
				
			return new DirectoryResult<T>(returnList, atEnd);
		}
		
		protected abstract T doTransform(ResolvedConceptReference ref);

		@Override
		public int executeCount(String state) {
			try {
				return 
					searchExtension.search(
						state, 
						toCodingSchemeReference(this.codeSystemVersions), 
						MatchAlgorithm.LUCENE).numberRemaining();
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private Set<CodingSchemeReference> toCodingSchemeReference(Iterable<NameOrURI> codeSystemVersions){
		Set<CodingSchemeReference> returnSet = new HashSet<CodingSchemeReference>();
		
		for(NameOrURI version : codeSystemVersions){
			NameVersionPair pair = 
				this.versionNameConverter.fromCts2VersionName(version.getName());
			
			CodingSchemeReference ref = new CodingSchemeReference();
			ref.setCodingScheme(pair.getName());
			ref.setVersionOrTag(Constructors.createCodingSchemeVersionOrTagFromVersion(pair.getVersion()));
			
			returnSet.add(ref);
		}
		
		return returnSet;
	}
	
	@Override
	public boolean isEntityInSet(
			EntityNameOrURI entity,
			EntityDescriptionQuery restrictions, 
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	private boolean getIncludeInactive(EntityDescriptionQuery query) {
		boolean includeInactive = false;

		if(query != null && query.getReadContext() != null && query.getReadContext().getActive() != null) {
			if(query.getReadContext().getActive().equals(ActiveOrAll.ACTIVE_AND_INACTIVE)) {
				includeInactive = true;
			}
		}

		return includeInactive;
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		return new BasicEntityDirectoryBuilder<EntityDirectoryEntry>(
				this.entityNameQueryBuilder,
				this.entityUriResolver,
				new SearchExtensionSummariesCallback(query.getRestrictions().getCodeSystemVersions(), this.getIncludeInactive(query), false),
				this.getSupportedMatchAlgorithms(), 
				this.getSupportedSearchReferences()).
				restrict(query).
				addMaxToReturn(page.getMaxToReturn()).
				addStart(page.getStart()).
				resolve();
	}

	@Override
	public DirectoryResult<EntityListEntry> getResourceList(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int count(EntityDescriptionQuery query) {
		return new BasicEntityDirectoryBuilder<EntityDirectoryEntry>(
				this.entityNameQueryBuilder,
				this.entityUriResolver,
				new SearchExtensionSummariesCallback(query.getRestrictions().getCodeSystemVersions(), this.getIncludeInactive(query), false),
				this.getSupportedMatchAlgorithms(), 
				this.getSupportedSearchReferences()).restrict(query.getFilterComponent()).count();
	}
	
	@Override
	public Set<StateAdjustingComponentReference<String>> getSupportedSearchReferences() {
		StateAdjustingComponentReference<String> resourceSynopsis =
			StateAdjustingComponentReference.toComponentReference(
					StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference(),
					RESOURCE_SYNOPSIS_STATE_UPDATER);
		
		StateAdjustingComponentReference<String> resourceName =
				StateAdjustingComponentReference.toComponentReference(
						StandardModelAttributeReference.RESOURCE_NAME.getComponentReference(),
						RESOURCE_NAME_STATE_UPDATER);
		
		Set<StateAdjustingComponentReference<String>> set = 
			new HashSet<StateAdjustingComponentReference<String>>();	
		
		set.add(resourceSynopsis);
		set.add(resourceName);
		
		return set;
	}
	
	private StateUpdater<String> RESOURCE_NAME_STATE_UPDATER = new AbstractStateUpdater(){
		@Override
		protected String decorate(String string, MatchAlgorithmReference matchAlgorithm) {
			return "code:" + QueryParser.escape(string);
		}
	};

	private String getTextQuery(String text) {
		return "(+" + LuceneLoaderCodeIndexer.PROPERTY_VALUE_FIELD + ":" + text.toLowerCase() + " +isPreferred:T +propertyType:presentation)";
	}
	
	private StateUpdater<String> RESOURCE_SYNOPSIS_STATE_UPDATER = new AbstractStateUpdater(){
		@Override
		protected String decorate(String text, MatchAlgorithmReference matchAlgorithm) {
			if(matchAlgorithm.getContent().equals(
				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference().getContent())){
				text = QueryParser.escape(text);
	            StringBuilder sb = new StringBuilder();
	            sb.append("(");
	            sb.append("(");
	            for(String token : text.split("\\s+")){
	               sb.append(getTextQuery(token + "*"));
	            }
	            sb.append(")");
	            sb.append(" OR " + getTextQuery("\""+text+"\""));
	            sb.append(" OR " + getTextQuery("\"" + QueryParser.escape(text) + "\""));
	            sb.append(")");
	            return sb.toString().trim();
			} else if(matchAlgorithm.getContent().equals(
					StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference().getContent())){
				return getTextQuery("\"" + QueryParser.escape(text) + "\"");
			} else if(matchAlgorithm.getContent().equals(LUCENE_QUERY)){
				return text;
			} else {
				throw new IllegalStateException();
			}
		}
	};
  
	private abstract class AbstractStateUpdater implements StateUpdater<String>{

		@Override
		public String updateState(
				String currentState,
				MatchAlgorithmReference matchAlgorithm, 
				String queryString) {
			String andOrBlank = "";
			if(StringUtils.isNotBlank(currentState)){
				andOrBlank = " AND ";
			}
			return currentState + andOrBlank + decorate(queryString, matchAlgorithm);
		}

		protected abstract String decorate(String queryString, MatchAlgorithmReference matchAlgorithm);
	};

	@Override
	public EntityReferenceList resolveAsEntityReferenceList(
			EntityDescriptionQuery restrictions,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityNameOrURIList intersectEntityList(
			Set<EntityNameOrURI> entities, 
			EntityDescriptionQuery restrictions,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends VersionTagReference> getSupportedTags() {
		return new HashSet<VersionTagReference>(Arrays.asList(Constants.CURRENT_TAG));
	}

	@Override
	public Set<MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		Set<MatchAlgorithmReference> returnSet = new HashSet<MatchAlgorithmReference>();
		
		MatchAlgorithmReference lucene = new MatchAlgorithmReference();
		lucene.setContent(LUCENE_QUERY);
		
		returnSet.addAll(
				Arrays.asList(
						lucene,
						StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(),
						StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference())
		);
		
		return returnSet;
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
	public boolean canHandle(EntityDescriptionQuery query, QueryType queryType) {
		return this.searchExtension != null &&
				!queryType.equals(QueryType.LIST) &&
				query.getEntitiesFromAssociationsQuery() == null &&
				query.getRestrictions().getHierarchyRestriction() == null &&
				this.checkFilters(query.getFilterComponent());
	}
		
	private boolean checkFilters(Set<ResolvedFilter> filters) {
		 for(ResolvedFilter filter : filters){
			 if(filter.getMatchAlgorithmReference().
					 equals(StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference())
			 	){
				 return false;
			 }
		 }
		 
		 return true;
	}
	
	@Override
	public int getOrder() {
		return 0;
	}

}
