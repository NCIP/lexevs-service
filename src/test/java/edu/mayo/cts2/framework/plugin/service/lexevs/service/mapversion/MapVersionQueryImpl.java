package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;

public class MapVersionQueryImpl implements MapVersionQuery {

	private Query query;
	private Set<ResolvedFilter> filterComponent;
	private ResolvedReadContext readContext;
	private MapVersionQueryServiceRestrictions restrictions;
	
	
	public MapVersionQueryImpl() {
		super();
	}

	public MapVersionQueryImpl(Query query,
			Set<ResolvedFilter> filterComponent,
			ResolvedReadContext readContext,
			MapVersionQueryServiceRestrictions restrictions) {
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
	public MapVersionQueryServiceRestrictions getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(
			MapVersionQueryServiceRestrictions restrictions) {
		this.restrictions = restrictions;
	}

}
