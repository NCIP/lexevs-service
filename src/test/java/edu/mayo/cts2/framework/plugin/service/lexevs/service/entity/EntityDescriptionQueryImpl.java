package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.HashSet;
import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

public class EntityDescriptionQueryImpl implements EntityDescriptionQuery{
	private Query query;
	private Set<ResolvedFilter> filterComponent;
	private ResolvedReadContext readContext;
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


