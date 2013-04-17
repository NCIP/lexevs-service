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
	
	private boolean isMapQuery = false;									// Used by ALL Map Queries
	
	private NameOrURI map = null;										// Used by MapVersionQuery
	private String mapName = null;										// Used by MapVersionQuery 
	private EntitiesRestriction entitiesRestriction = null;				// Used in MapVersionQuery
	
	private NameOrURI codeSystem = null;								// Used by CodeSystemVersionQuery 
	private String codeSystemName = null;								// Used by CodeSystemVersionQuery
	private EntityRestriction entityRestriction = null;					// Used in CodeSystemVersionQuery
	
	private CodeSystemRestriction codeSystemRestriction = null;			// Used by MapQuery and MapVersionQuery
	
	// Following are set in setVersionOrTag method						// Used by EntityDescriptionQuery and MapEntryQuery
	private NameOrURI nameOrURI = null;
	private String versionName = null;
	private CodingSchemeVersionOrTag versionOrTag = null;
	private boolean hasNameAndVersion = false;
	
	private Set<EntityNameOrURI> entities = null;						// Used in EntityDescriptionQuery
	private TaggedCodeSystemRestriction taggedCodeSystem = null;		// Used in EntityDescriptionQuery
	
	private Set<EntityNameOrURI> targetEntities = null;					// Used in MapEntryQuery
	
	
	
	
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
	
	public String getSystemName(){
		if(this.mapName == null){
			return this.codeSystemName;
		}
		return this.mapName;
	}
	
	public String getMapName(){
		return this.mapName;
	}
	
	public String getCodeSystemName() {
		return codeSystemName;
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

	// Map Catalog (MapQueryService) (i.e. Eclipse "the idea of", or "Mapping Sample")
	// -----------------------------------------------------------
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

	// Map Version (MapVersionQueryService) (i.e. Eclipse Juno, or "Mapping Sample-1.0")
	// ---------------------------------------------------------
	private void extractMapVersionQueryData(MapVersionQuery query) {
		MapVersionQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
//		restrictions.getValueSetRestriction();
		
		
		if(restrictions != null){
			this.codeSystemRestriction = restrictions.getCodeSystemRestriction();  	// Restrict to source or target fields containing this value.
			this.entitiesRestriction = restrictions.getEntitiesRestriction();		// Restrict to maps containing these entities in a relation.
			
			this.map = restrictions.getMap(); 										// Restrict to given map with this codeScheme Name
			this.mapName = this.getName(this.map);
		}
		this.isMapQuery = true;
	}

	private String getName(NameOrURI nameOrURI) {
		String name = null;
		if(nameOrURI != null){
			name = (nameOrURI.getUri() != null) ? nameOrURI.getUri() : nameOrURI.getName();
		}
		return name;
	}

	private void extractCodeSystemVersionQueryData(CodeSystemVersionQuery query) {
		CodeSystemVersionQueryServiceRestrictions restrictions = query.getRestrictions();
		this.restrictions = restrictions;
		this.filters = query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
		
		if(restrictions != null){
			this.entityRestriction = restrictions.getEntityRestriction();
			
			this.codeSystem = restrictions.getCodeSystem();
			this.codeSystemName = this.getName(this.codeSystem);
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
			
			this.mapName = nameVersionPair.getName();
			this.checkNameAndVersion();
		}
	}

	private void checkNameAndVersion() {
		if((mapName != null) && (versionOrTag.getVersion() != null || versionOrTag.getTag() != null)){
			hasNameAndVersion = true;
		}
	}
}

