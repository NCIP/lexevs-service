package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;

public class QueryData <Query extends ResourceQuery>{
	Set<ResolvedFilter> filters = null;
	Object restrictions = null;
	NameOrURI codeSystem = null;
	CodeSystemRestriction codeSystemRestriction = null;
	String codingSchemeName = null;
	boolean isMapQuery = false;
	
	public QueryData(Query query){
		if (query != null) {
			if(query instanceof CodeSystemVersionQuery){
				restrictions = (CodeSystemVersionQueryServiceRestrictions) ((CodeSystemVersionQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystem = ((CodeSystemVersionQueryServiceRestrictions) restrictions).getCodeSystem();
					if(codeSystem != null){
						codingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
					}
				}
				codeSystemRestriction = null;
			}
			else if(query instanceof MapQuery){
				restrictions = (MapQueryServiceRestrictions) ((MapQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystemRestriction = ((MapQueryServiceRestrictions) restrictions).getCodeSystemRestriction();
				}
				codeSystem = null;
				codingSchemeName = null;
				isMapQuery = true;
			}
			else if(query instanceof MapVersionQuery){
				restrictions = (MapQueryServiceRestrictions) ((MapVersionQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystemRestriction = ((MapQueryServiceRestrictions) restrictions).getCodeSystemRestriction();
					if(codeSystemRestriction != null){
						codeSystem = ((MapVersionQueryServiceRestrictions) restrictions).getMap(); 
						if(codeSystem != null){
							codingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
						}
}
				}
				codingSchemeName = null;
				isMapQuery = true;
			}
			
			filters = query.getFilterComponent();
		}
		else{
			filters = null;
			restrictions = null;
			codeSystem = null;
			codeSystemRestriction = null;
			codingSchemeName = null;
		}
	}
	
	public Set<ResolvedFilter> getFilters(){
		return this.filters;
	}
	
	public Object getRestrictions(){
		return this.restrictions;
	}
	
	public NameOrURI getCodeSystem(){
		return this.codeSystem;
	}
	
	public String getCodingSchemeName(){
		return this.codingSchemeName;
	}
	
	public CodeSystemRestriction getCodeSystemRestriction(){
		return this.codeSystemRestriction;
	}
	
	public boolean isMapQuery(){
		return isMapQuery;
	}
}

