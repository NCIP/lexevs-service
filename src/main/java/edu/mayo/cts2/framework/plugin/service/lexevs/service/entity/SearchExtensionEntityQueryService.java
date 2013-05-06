package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.CodeSystemReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.filter.match.StateAdjustingPropertyReference;
import edu.mayo.cts2.framework.filter.match.StateAdjustingPropertyReference.StateUpdater;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

@Component
public class SearchExtensionEntityQueryService 
	extends AbstractLexEvsService
	implements InitializingBean, DelegateEntityQueryService {
	
	@Resource
	private EntityTransform transformer;
	
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

	private class SearchExtensionCallback implements Callback<String,EntityDirectoryEntry>{

		private List<NameOrURI> codeSystemVersions;
		
		private SearchExtensionCallback(NameOrURI... codeSystemVersions){
			super();
			this.codeSystemVersions = Arrays.asList(codeSystemVersions);
		}
		
		@Override
		public DirectoryResult<EntityDirectoryEntry> execute(
				String state, 
				int start,
				int maxResults) {
			ResolvedConceptReferencesIterator iterator;
			try {
				iterator = searchExtension.search(state, toCodeSystemReference(this.codeSystemVersions));
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
			
			ResolvedConceptReferenceList list;
			boolean atEnd;
			try {
				list = iterator.get(start, start + maxResults);
				atEnd = iterator.numberRemaining() <= start + maxResults;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		
			List<EntityDirectoryEntry> returnList = new ArrayList<EntityDirectoryEntry>();
			for(ResolvedConceptReference ref : list.getResolvedConceptReference()){
				returnList.add(transformer.transformSummaryDescription(ref));
			}
				
			return new DirectoryResult<EntityDirectoryEntry>(returnList, atEnd);
		}

		@Override
		public int executeCount(String state) {
			try {
				return 
					searchExtension.search(state, toCodeSystemReference(this.codeSystemVersions)).numberRemaining();
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private Set<CodeSystemReference> toCodeSystemReference(Iterable<NameOrURI> codeSystemVersions){
		Set<CodeSystemReference> returnSet = new HashSet<CodeSystemReference>();
		
		for(NameOrURI version : codeSystemVersions){
			NameVersionPair pair = 
				this.versionNameConverter.fromCts2VersionName(version.getName());
			
			CodeSystemReference ref = new CodeSystemReference();
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		return new BasicEntityDirectoryBuilder<EntityDirectoryEntry>(
				new SearchExtensionCallback(query.getRestrictions().getCodeSystemVersion()), 
				this.getSupportedMatchAlgorithms(), 
				this.getSupportedSearchReferences()).
				restrict(query.getFilterComponent()).
				addMaxToReturn(page.getMaxToReturn()).
				addStart(page.getStart()).
				resolve();
	}

	@Override
	public DirectoryResult<EntityDescription> getResourceList(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count(EntityDescriptionQuery query) {
		return new BasicEntityDirectoryBuilder<EntityDirectoryEntry>(
				new SearchExtensionCallback(query.getRestrictions().getCodeSystemVersion()), 
				this.getSupportedMatchAlgorithms(), 
				this.getSupportedSearchReferences()).restrict(query.getFilterComponent()).count();
	}
	
	@Override
	public Set<StateAdjustingPropertyReference<String>> getSupportedSearchReferences() {
		StateAdjustingPropertyReference<String> resourceSynopsis =
			StateAdjustingPropertyReference.toPropertyReference(
					StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference(),
					RESOURCE_SYNOPSIS_STATE_UPDATER);
		
		StateAdjustingPropertyReference<String> resourceName =
				StateAdjustingPropertyReference.toPropertyReference(
						StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(),
						RESOURCE_NAME_STATE_UPDATER);
		
		Set<StateAdjustingPropertyReference<String>> set = 
			new HashSet<StateAdjustingPropertyReference<String>>();	
		
		set.add(resourceSynopsis);
		set.add(resourceName);
		
		return set;
	}
	
	private StateUpdater<String> RESOURCE_NAME_STATE_UPDATER = new AbstractStateUpdater(){
		@Override
		protected String decorate(String string) {
			return "code:" + string;
		}
	};
	
	private StateUpdater<String> RESOURCE_SYNOPSIS_STATE_UPDATER = new AbstractStateUpdater(){
		@Override
		protected String decorate(String string) {
			return string;
		}
	};
	
	private abstract class AbstractStateUpdater implements StateUpdater<String>{

		protected abstract String decorate(String string);
		
		@Override
		public String updateState(
				String currentState,
				MatchAlgorithmReference matchAlgorithm, 
				String queryString) {
			String andOrBlank = "";
			if(StringUtils.isNotBlank(currentState)){
				andOrBlank = " AND ";
			}
			return currentState + andOrBlank + decorate(adjustQueryString(queryString, matchAlgorithm));
		}
		
	};
	
	protected String adjustQueryString(String string, MatchAlgorithmReference reference){
		if(reference.getContent().equals(
			StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference().getContent())){
			return string + "*";
		} else {
			throw new IllegalStateException();
		}
	}

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
		
		returnSet.addAll(
				Arrays.asList(
						StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(),
						StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference())
		);
		
		return returnSet;
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
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
	public boolean canHandle(EntityDescriptionQuery query) {
		return this.searchExtension != null &&
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
