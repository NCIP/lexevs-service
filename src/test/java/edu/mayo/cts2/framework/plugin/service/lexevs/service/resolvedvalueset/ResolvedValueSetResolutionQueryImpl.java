/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
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



	public void setResolvedValueSetResolutionEntityRestrictions(
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
