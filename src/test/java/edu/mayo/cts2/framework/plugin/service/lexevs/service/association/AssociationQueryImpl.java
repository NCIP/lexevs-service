package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.AssociationQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.association.AssociationQuery;

public class AssociationQueryImpl implements AssociationQuery {


	private Query query;
	private Set<ResolvedFilter> filterComponent;
	private ResolvedReadContext readContext;
	private AssociationQueryServiceRestrictions restrictions; 
	
	
	public AssociationQueryImpl() {
		super();
	}

	
	public AssociationQueryImpl(Query query,
			Set<ResolvedFilter> filterComponent,
			ResolvedReadContext readContext,
			AssociationQueryServiceRestrictions restrictions) {
		super();
		this.query = query;
		this.filterComponent = filterComponent;
		this.readContext = readContext;
		this.restrictions = restrictions;
	}


	@Override
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	@Override
	public Set<ResolvedFilter> getFilterComponent() {
		return filterComponent;
	}

	public void setFilterComponent(Set<ResolvedFilter> filterComponent) {
		this.filterComponent = filterComponent;
	}

	@Override
	public ResolvedReadContext getReadContext() {
		return readContext;
	}

	public void setReadContext(ResolvedReadContext readContext) {
		this.readContext = readContext;
	}

	@Override
	public AssociationQueryServiceRestrictions getRestrictions() {
		return restrictions;
	}
	
	public void setRestrictions(AssociationQueryServiceRestrictions restrictions) {
		this.restrictions = restrictions;
	}


}
