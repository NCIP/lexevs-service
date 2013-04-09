package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;

public class QueryData <Query extends ResourceQuery>{
	private Set<ResolvedFilter> filters = null;
	private Object restrictions = null;
	private NameOrURI codeSystem = null;
	private CodeSystemRestriction codeSystemRestriction = null;
	private String codingSchemeName = null;
	private boolean isMapQuery = false;
	
	private String codeSystemVersionName = null;
	private boolean hasNameAndVersion = false;
	private CodingSchemeVersionOrTag versionOrTag = null;
	
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
			}
			else if(query instanceof EntityDescriptionQuery){
				restrictions = (EntityDescriptionQueryServiceRestrictions) ((EntityDescriptionQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystemVersionName = ((EntityDescriptionQueryServiceRestrictions) restrictions).getCodeSystemVersion().getName();
					// Remaining data is collected via QueryData.setVersionOrTag method
				}
			}
			else if(query instanceof MapQuery){
				restrictions = ((MapQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystemRestriction = ((MapQueryServiceRestrictions) restrictions).getCodeSystemRestriction();
				}
				isMapQuery = true;
			}
			else if(query instanceof MapVersionQuery){
				restrictions = ((MapVersionQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystemRestriction =  ((MapQueryServiceRestrictions) restrictions).getCodeSystemRestriction();
					if(codeSystemRestriction != null){
						codeSystem = ((MapVersionQueryServiceRestrictions) restrictions).getMap(); 
						if(codeSystem != null){
							codingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
						}
					}
				}
				isMapQuery = true;
			}
			else if(query instanceof MapEntryQuery){
				restrictions = ((MapEntryQuery) query).getRestrictions();
				if(restrictions != null){
					codeSystemRestriction = ((MapQueryServiceRestrictions) restrictions).getCodeSystemRestriction();
					if(codeSystemRestriction != null){
						codeSystem = ((MapVersionQueryServiceRestrictions) restrictions).getMap(); 
						if(codeSystem != null){
							codingSchemeName = (codeSystem.getUri() != null) ? codeSystem.getUri() : codeSystem.getName();
						}
					}
				}
				isMapQuery = true;		
			}
			
			filters = query.getFilterComponent();
		}
//		else{
//			filters = null;
//			restrictions = null;
//			codeSystem = null;
//			codeSystemRestriction = null;
//			codingSchemeName = null;
//		}
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
	
	public String getCodeSystemVersionName(){
		return this.codeSystemVersionName;
	}
	
	public boolean hasNameAndVersion(){
		return this.hasNameAndVersion;
	}
	
	public CodingSchemeVersionOrTag getVersionOrTag(){
		return this.versionOrTag;
	}

	public boolean isMapQuery(){
		return isMapQuery;
	}
	
	public void setVersionOrTag(VersionNameConverter versionNameConverter){
		if(codeSystemVersionName != null){
			NameVersionPair nameVersionPair =
					versionNameConverter.fromCts2VersionName(codeSystemVersionName);					
			versionOrTag = new CodingSchemeVersionOrTag();
			codingSchemeName = nameVersionPair.getName();
			versionOrTag.setTag(nameVersionPair.getVersion());
			versionOrTag.setVersion(nameVersionPair.getVersion());
	//		if(printObjects){
	//			System.out.println("CodingSchemeName: " + codingSchemeName);
	//			System.out.println("VersionOrTag: " + versionOrTag.getVersion());
	//		}
			if((codingSchemeName != null) && (versionOrTag.getVersion() != null || versionOrTag.getTag() != null)){
				hasNameAndVersion = true;
			}
		}
	}
}

