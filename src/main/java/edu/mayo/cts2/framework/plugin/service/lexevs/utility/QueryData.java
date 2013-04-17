package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions.EntityRestriction;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapEntryQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions.EntitiesRestriction;
import edu.mayo.cts2.framework.service.command.restriction.TaggedCodeSystemRestriction;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;

public class QueryData <Query extends ResourceQuery>{
	private Set<ResolvedFilter> filters = null;							// Used by all
	private Object restrictions = null;									// Used by all
	private String nameVersionPairName = null;							// Used by CodeSystemVersionQuery, MapVersionQuery
		
	private boolean isMapQuery = false;									// Used by all Map Queries
	
	// Following are set in setVersionOrTag method						// Used by EntityDescriptionQuery and MapEntryQuery
	private NameOrURI nameOrURI = null;
	private String versionName = null;
	private CodingSchemeVersionOrTag versionOrTag = null;
	private boolean hasNameAndVersion = false;
	
	
	private CodeSystemRestriction codeSystemRestriction = null;			// Used by MapQuery and MapVersionQuery
	private NameOrURI codeSystem = null;								// Used by CodeSystemVersionQuery and MapVersionQuery
		
	private EntitiesRestriction entitiesRestriction = null;				// Used in MapVersionQuery
	private EntityRestriction entityRestriction = null;					// Used in CodeSystemVersionQuery
	
	
	private Set<EntityNameOrURI> targetEntities = null;					// Used in MapEntryQuery
	
	private Set<EntityNameOrURI> entities = null;						// Used in EntityDescriptionQuery
	private TaggedCodeSystemRestriction taggedCodeSystem = null;		// Used in EntityDescriptionQuery
	
	
	
	public Set<ResolvedFilter> getFilters(){
		return this.filters;
	}
	
	public Object getRestrictions(){
		return this.restrictions;
	}
	
	public CodingSchemeVersionOrTag getVersionOrTag(){
		return this.versionOrTag;
	}

	public CodeSystemRestriction getCodeSystemRestriction(){
		return this.codeSystemRestriction;
	}
	
	public NameOrURI getCodeSystem(){
		return this.codeSystem;
	}
	
	public NameOrURI getNameOrURI(){
		return this.nameOrURI;
	}
	
	public String getNameVersionPairName(){
		return this.nameVersionPairName;
	}
	
	public String getVersionName(){
		return this.versionName;
	}
	
	public Set<EntityNameOrURI> getTargetEntities() {
		return targetEntities;
	}

	public Set<EntityNameOrURI> getEntities() {
		return entities;
	}

	public TaggedCodeSystemRestriction getTaggedCodeSystem() {
		return taggedCodeSystem;
	}

	public EntitiesRestriction getEntitiesRestriction() {
		return entitiesRestriction;
	}

	public EntityRestriction getEntityRestriction() {
		return entityRestriction;
	}

	public boolean isMapQuery(){
		return isMapQuery;
	}
	
	public boolean hasNameAndVersion(){
		return this.hasNameAndVersion;
	}

	
	public QueryData(Query query, VersionNameConverter nameConverter){
		super();
		if (query != null) {
			if(query instanceof CodeSystemVersionQuery){
				this.extractCodeSystemVersionQueryData((CodeSystemVersionQuery) query);
			}
			else if(query instanceof EntityDescriptionQuery){
				this.extractEntityDescriptionQueryData((EntityDescriptionQuery) query, nameConverter);
			}
			else if(query instanceof MapQuery){
				this.extractMapQueryData((MapQuery) query);
			}
			else if(query instanceof MapVersionQuery){
				this.extractMapVersionQueryData((MapVersionQuery) query);
			}
			else if(query instanceof MapEntryQuery){
				this.extractMapEntryQueryData((MapEntryQuery) query, nameConverter);				
			}
		}
	}
	
	private void extractMapEntryQueryData(MapEntryQuery query,
			VersionNameConverter nameConverter) {
		MapEntryQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();
		

		// Not needed?
//		query.getReadContext();
		
		if(restrictions != null){
			this.nameOrURI = restrictions.getMapVersion();
			this.setVersionOrTag(nameConverter);
			this.targetEntities = restrictions.getTargetEntities();
		}
		
		
		this.isMapQuery = true;		
	}

	private void extractEntityDescriptionQueryData(EntityDescriptionQuery query,
			VersionNameConverter nameConverter) {
		EntityDescriptionQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();
				
		// Not needed?
//		query.getEntitiesFromAssociationsQuery();
//		restrictions.getHierarchyRestriction();
//		query.getReadContext();
		
		if(restrictions != null){
			this.nameOrURI = restrictions.getCodeSystemVersion();
			this.setVersionOrTag(nameConverter);
			this.entities = restrictions.getEntities();
			this.taggedCodeSystem = restrictions.getTaggedCodeSystem();
		}
		
	}

	private void extractMapQueryData(MapQuery query) {
		MapQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
//		restrictions.getValueSetRestriction();
		
		if(restrictions != null){
			this.codeSystemRestriction = restrictions.getCodeSystemRestriction();
		}
		this.isMapQuery = true;
	}

	private void extractMapVersionQueryData(MapVersionQuery query) {
		MapVersionQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
//		restrictions.getValueSetRestriction();
		
		
		if(restrictions != null){
			this.codeSystemRestriction = restrictions.getCodeSystemRestriction();
			this.codeSystem = restrictions.getMap(); 
			this.entitiesRestriction = restrictions.getEntitiesRestriction();
			
			if(this.codeSystem != null){
				this.nameVersionPairName = (this.codeSystem.getUri() != null) ? this.codeSystem.getUri() : this.codeSystem.getName();
			}
		}
		this.isMapQuery = true;
	}

	private void extractCodeSystemVersionQueryData(CodeSystemVersionQuery query) {
		CodeSystemVersionQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
		
		
		if(restrictions != null){
			this.codeSystem = restrictions.getCodeSystem();
			this.entityRestriction = restrictions.getEntityRestriction();
			
			if(this.codeSystem != null){
				this.nameVersionPairName = (this.codeSystem.getUri() != null) ? this.codeSystem.getUri() : this.codeSystem.getName();
			}
		}
	}

	private void setVersionOrTag(VersionNameConverter nameConverter){
		NameVersionPair nameVersionPair;
		if(this.nameOrURI != null){
			nameVersionPair = nameConverter.fromCts2VersionName(this.nameOrURI.getName());	
			this.versionName = nameVersionPair.getName();
			
			this.versionOrTag = new CodingSchemeVersionOrTag();
			this.versionOrTag.setTag(nameVersionPair.getVersion());
			this.versionOrTag.setVersion(nameVersionPair.getVersion());
			
			this.nameVersionPairName = nameVersionPair.getName();
			this.checkNameAndVersion();
		}
	}

	private void checkNameAndVersion() {
		if((nameVersionPairName != null) && (versionOrTag.getVersion() != null || versionOrTag.getTag() != null)){
			hasNameAndVersion = true;
		}
	}
}

