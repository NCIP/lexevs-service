package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.ValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetQuery;

public class ValueSetQueryImpl implements ValueSetQuery {
    Query query;
	private Set<ResolvedFilter> filterComponent;
	ValueSetQueryServiceRestrictions restrictions;
	ResolvedReadContext context;

	public ValueSetQueryImpl(Query query, Set<ResolvedFilter> filterComponent,
			ValueSetQueryServiceRestrictions restrictions, ResolvedReadContext context) {
		super();
		this.query = query;
		this.filterComponent = filterComponent;
		this.restrictions = restrictions;
		this.context = context;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setFilterComponent(Set<ResolvedFilter> filterComponent) {
		this.filterComponent = filterComponent;
	}

	public void setRestrictions(ValueSetQueryServiceRestrictions restrictions) {
		this.restrictions = restrictions;
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public Set<ResolvedFilter> getFilterComponent() {
		return filterComponent;
	}

	@Override
	public ResolvedReadContext getReadContext() {
		return context;
	}

	@Override
	public ValueSetQueryServiceRestrictions getRestrictions() {
		return restrictions;
	}

}
