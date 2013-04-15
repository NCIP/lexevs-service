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
	private Set<ResolvedFilter> filters = null;
	private Object restrictions = null;
	
	private CodingSchemeVersionOrTag versionOrTag = null;
	private CodeSystemRestriction codeSystemRestriction = null;
	private NameOrURI codeSystem = null;
	private NameOrURI codeSystemVersion = null;
	private String nameVersionPairName = null;
	private String codeSystemVersionName = null;
	
	private boolean isMapQuery = false;
	private boolean hasNameAndVersion = false;

	
	private Set<EntityNameOrURI> targetEntities = null;
	private Set<EntityNameOrURI> entities = null;
	private TaggedCodeSystemRestriction taggedCodeSystem = null;
	private EntitiesRestriction entitiesRestriction = null;
	private EntityRestriction entityRestriction = null;
	
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
	
	public NameOrURI getCodeSystemVersion(){
		return this.codeSystemVersion;
	}
	
	public String getNameVersionPairName(){
		return this.nameVersionPairName;
	}
	
	public String getCodeSystemVersionName(){
		return this.codeSystemVersionName;
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
			this.codeSystemVersion = restrictions.getMapVersion();
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
			this.codeSystemVersion = restrictions.getCodeSystemVersion();
			this.setVersionOrTag(nameConverter);
			this.entities = restrictions.getEntities();
			this.taggedCodeSystem = restrictions.getTaggedCodeSystem();
		}
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
		if(this.codeSystemVersion != null){
//			this.codeSystemVersionName = this.codeSystemVersion.getName();
//			if(this.codeSystemVersionName != null){
				nameVersionPair = nameConverter.fromCts2VersionName(this.codeSystemVersion.getName());	
				this.codeSystemVersionName = nameVersionPair.getName();
				this.versionOrTag = new CodingSchemeVersionOrTag();
				this.nameVersionPairName = nameVersionPair.getName();
				this.versionOrTag.setTag(nameVersionPair.getVersion());
				this.versionOrTag.setVersion(nameVersionPair.getVersion());
				this.checkNameAndVersion();
//			}
		}
	}

	private void checkNameAndVersion() {
		if((nameVersionPairName != null) && (versionOrTag.getVersion() != null || versionOrTag.getTag() != null)){
			hasNameAndVersion = true;
		}
	}
}

