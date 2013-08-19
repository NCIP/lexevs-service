/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class CodeSystemVersionQueryImpl implements CodeSystemVersionQuery {

	private Query query;
	private Set<ResolvedFilter> filterComponent;
	private ResolvedReadContext readContext;
	private CodeSystemVersionQueryServiceRestrictions restrictions;
	
	
	public CodeSystemVersionQueryImpl() {
		super();
	}

	public CodeSystemVersionQueryImpl(Query query,
			Set<ResolvedFilter> filterComponent,
			ResolvedReadContext readContext,
			CodeSystemVersionQueryServiceRestrictions restrictions) {
		super();
		this.query = query;
		this.filterComponent = filterComponent;
		this.readContext = readContext;
		this.restrictions = restrictions;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ResourceQuery#getQuery()
	 */
	@Override
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ResourceQuery#getFilterComponent()
	 */
	@Override
	public Set<ResolvedFilter> getFilterComponent() {
		return filterComponent;
	}

	public void setFilterComponent(Set<ResolvedFilter> filterComponent) {
		this.filterComponent = filterComponent;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ResourceQuery#getReadContext()
	 */
	@Override
	public ResolvedReadContext getReadContext() {
		return readContext;
	}

	public void setReadContext(ResolvedReadContext readContext) {
		this.readContext = readContext;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery#getRestrictions()
	 */
	@Override
	public CodeSystemVersionQueryServiceRestrictions getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(
			CodeSystemVersionQueryServiceRestrictions restrictions) {
		this.restrictions = restrictions;
	}

}
