package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
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
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQueryService;
import edu.mayo.cts2.framework.util.spring.AggregateService;

@Component
@Primary
@AggregateService
public class DelegatingEntityQueryService extends AbstractLexEvsService 
	implements EntityDescriptionQueryService {

	private List<DelegateEntityQueryService> delegates;
	
	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		return this.getDelegate(query).getResourceSummaries(query, sortCriteria, page);
	}

	@Override
	public DirectoryResult<EntityDescription> getResourceList(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		return this.getDelegate(query).getResourceList(query, sortCriteria, page);
	}

	@Override
	public int count(EntityDescriptionQuery query) {
		return this.getDelegate(query).count(query);
	}
	
	protected EntityDescriptionQueryService getDelegate(EntityDescriptionQuery query) {
		for(DelegateEntityQueryService delegate : this.delegates){
			if(delegate.canHandle(query)){
				return delegate;
			}
		}
		
		throw new IllegalStateException(
			"Cannot find a Delegate EntityQueryService for the given Query.");
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		Set<MatchAlgorithmReference> refs = new HashSet<MatchAlgorithmReference>();
		for(DelegateEntityQueryService delegate : this.delegates){
			refs.addAll(delegate.getSupportedMatchAlgorithms());
		}
		
		return refs;
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		Set<PropertyReference> refs = new HashSet<PropertyReference>();
		for(DelegateEntityQueryService delegate : this.delegates){
			refs.addAll(delegate.getSupportedSearchReferences());
		}
		
		return refs;
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
	public boolean isEntityInSet(
			EntityNameOrURI entity,
			EntityDescriptionQuery restrictions, 
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
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

	public List<DelegateEntityQueryService> getDelegates() {
		return delegates;
	}

	@Autowired
	public void setDelegates(List<DelegateEntityQueryService> delegates) {
		Collections.sort(delegates, OrderComparator.INSTANCE);
		this.delegates = delegates;
	}

}
