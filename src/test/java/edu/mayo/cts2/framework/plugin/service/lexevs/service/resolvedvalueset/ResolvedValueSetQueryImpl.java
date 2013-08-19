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
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQuery;


public class ResolvedValueSetQueryImpl implements ResolvedValueSetQuery {
    Query query;
	private Set<ResolvedFilter> filterComponent;
	ResolvedValueSetQueryServiceRestrictions restrictions;

	public ResolvedValueSetQueryImpl(Query query,
			Set<ResolvedFilter> filterComponent,
			ResolvedValueSetQueryServiceRestrictions restrictions) {
		this.query = query;
		this.filterComponent = filterComponent;
		this.restrictions = restrictions;
	}
	
    public ResolvedValueSetQueryServiceRestrictions getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(
			ResolvedValueSetQueryServiceRestrictions restrictions) {
		this.restrictions = restrictions;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setFilterComponent(Set<ResolvedFilter> filterComponent) {
		this.filterComponent = filterComponent;
	}

    
	
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
	public ResolvedValueSetQueryServiceRestrictions getResolvedValueSetQueryServiceRestrictions() {
		// TODO Auto-generated method stub
		return restrictions;
	}

}
