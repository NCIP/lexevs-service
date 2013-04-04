/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class EntityDescriptionQueryImpl implements EntityDescriptionQuery{
	private Query query;
	private Set<ResolvedFilter> filterComponent;
	@SuppressWarnings("unused")
	private ResolvedReadContext readContext;
	@SuppressWarnings("unused")
	private EntitiesFromAssociationsQuery entities;
	private EntityDescriptionQueryServiceRestrictions restrictions;
	

	public EntityDescriptionQueryImpl(){
		super();
	}
	
	public EntityDescriptionQueryImpl(Query query, Set<ResolvedFilter> filters, EntityDescriptionQueryServiceRestrictions restrictions){
		super();
		this.query = query;
		this.filterComponent = filters;
		this.restrictions = restrictions;
	}	
	
	@Override
	public Query getQuery() {
		return query;
	}
	
	public void setQuery(Query query){
		this.query = query;
	}

	@Override
	public Set<ResolvedFilter> getFilterComponent() {
		return this.filterComponent;
	}
	
	public void setFilterComponent(Set<ResolvedFilter> filters){
		this.filterComponent = filters;
	}

	@Override
	public ResolvedReadContext getReadContext() {
		return null;
	}

	public void setReadContext(ResolvedReadContext context){
		this.readContext = context;
	}
	
	
	@Override
	public EntitiesFromAssociationsQuery getEntitiesFromAssociationsQuery() {
		return null;
	}
	
	public void setEntitiesFromAssociationsQuery(EntitiesFromAssociationsQuery entities){
		this.entities = entities;
	}

	@Override
	public EntityDescriptionQueryServiceRestrictions getRestrictions() {
		return restrictions;		
	}
	
	public void getRestrictions(EntityDescriptionQueryServiceRestrictions restrictions){
		this.restrictions = restrictions;
	}
		
}


