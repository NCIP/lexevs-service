/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.ValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetQuery;

/**
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 */
public class ValueSetQueryImpl implements ValueSetQuery {
    Query query;
	private Set<ResolvedFilter> filterComponent;
	ValueSetQueryServiceRestrictions restrictions;
	ResolvedReadContext context;

	/**
	 * @param query -- The query against some element or attribute text
	 * @param filterComponent  -- element containing text
	 * @param restrictions -- restrictions, such as code system for this query
	 * @param context -- context filters for this query
	 */
	public ValueSetQueryImpl(Query query, Set<ResolvedFilter> filterComponent,
			ValueSetQueryServiceRestrictions restrictions, ResolvedReadContext context) {
		super();
		this.query = query;
		this.filterComponent = filterComponent;
		this.restrictions = restrictions;
		this.context = context;
	}

	/**
	 * @param query
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @param filterComponent
	 */
	public void setFilterComponent(Set<ResolvedFilter> filterComponent) {
		this.filterComponent = filterComponent;
	}

	/**
	 * @param restrictions
	 */
	public void setRestrictions(ValueSetQueryServiceRestrictions restrictions) {
		this.restrictions = restrictions;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ResourceQuery#getQuery()
	 */
	@Override
	public Query getQuery() {
		return query;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ResourceQuery#getFilterComponent()
	 */
	@Override
	public Set<ResolvedFilter> getFilterComponent() {
		return filterComponent;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ResourceQuery#getReadContext()
	 */
	@Override
	public ResolvedReadContext getReadContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.valueset.ValueSetQuery#getRestrictions()
	 */
	@Override
	public ValueSetQueryServiceRestrictions getRestrictions() {
		return restrictions;
	}

}
