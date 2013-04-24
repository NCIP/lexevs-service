package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetResolutionEntityRestrictions;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResolutionEntityQuery;

public class ResolvedValueSetResolutionQueryImpl implements ResolvedValueSetResolutionEntityQuery{
	   Query query;
		private Set<ResolvedFilter> filterComponent;
		ResolvedValueSetResolutionEntityRestrictions restrictions;

	@Override
	public Query getQuery() {
		// TODO Auto-generated method stub
		return query;
	}

	@Override
	public Set<ResolvedFilter> getFilterComponent() {
		// TODO Auto-generated method stub
		return filterComponent;
	}

	@Override
	public ResolvedValueSetResolutionEntityRestrictions getResolvedValueSetResolutionEntityRestrictions() {
		// TODO Auto-generated method stub
		return restrictions;
	}



	public void setgetResolvedValueSetResolutionEntityRestrictions(
			ResolvedValueSetResolutionEntityRestrictions restrictions) {
		this.restrictions = restrictions;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setFilterComponent(Set<ResolvedFilter> filterComponent) {
		this.filterComponent = filterComponent;
	}

}
