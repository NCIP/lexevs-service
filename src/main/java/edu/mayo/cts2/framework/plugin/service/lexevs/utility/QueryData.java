package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
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
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.TaggedCodeSystemRestriction;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.map.MapQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQuery;

public final class QueryData <Query extends ResourceQuery>{
	private VersionNameConverter nameConverter;
	
	private Set<ResolvedFilter> cts2Filters = null;							// Used by all
	private Object cts2Restrictions = null;									// Used by all
	
	private boolean isMapQuery = false;										// Used by ALL Map Queries
	
	private NameOrURI cts2Map = null;										// Used by MapVersionQuery
	private String cts2MapName = null;											// Used by MapVersionQuery 
	private EntitiesRestriction cts2EntitiesRestriction = null;				// Used in MapVersionQuery
	
	private NameOrURI cts2CodeSystem = null;								// Used by CodeSystemVersionQuery 
	private String cts2CodeSystemName = null;									// Used by CodeSystemVersionQuery
	private EntityRestriction cts2EntityRestriction = null;					// Used in CodeSystemVersionQuery
	
	private CodeSystemRestriction cts2CodeSystemRestriction = null;			// Used by MapQuery and MapVersionQuery
	
	// Following are set in setVersionOrTag method							// Used by EntityDescriptionQuery and MapEntryQuery
	private NameOrURI cts2CodeSystemVersion = null;
	private NameOrURI cts2MapVersion = null;
	private String lexSchemeName = null;
	private CodingSchemeVersionOrTag lexVersionOrTag = null;
	private boolean hasNameAndVersion = false;
	
	private Set<EntityNameOrURI> cts2Entities = null;								// Used in EntityDescriptionQuery
	private TaggedCodeSystemRestriction cts2TaggedCodeSystemRestriction = null;		// Used in EntityDescriptionQuery
	
	private Set<EntityNameOrURI> cts2TargetEntities = null;					// Used in MapEntryQuery
	
	private ResolvedReadContext readContext;

	public Set<ResolvedFilter> getCts2Filters(){
		return this.cts2Filters;
	}
	
	public Object getCts2Restrictions(){
		return this.cts2Restrictions;
	}
	
	public CodingSchemeVersionOrTag getLexVersionOrTag(){
		return this.lexVersionOrTag;
	}

	public CodeSystemRestriction getCts2CodeSystemRestriction(){
		return this.cts2CodeSystemRestriction;
	}
	
	public NameOrURI getCts2CodeSystem(){
		return this.cts2CodeSystem;
	}
	
	public NameOrURI getCts2SystemVersion(){
		if(this.cts2MapVersion == null){
			return this.cts2CodeSystemVersion;
		}
		
		return this.cts2MapVersion;
	}
	
	public String getCts2SystemName(){
		if(this.cts2MapName == null){
			return this.cts2CodeSystemName;
		}
		return this.cts2MapName;
	}

	public String getLexSchemeName(){
		return this.lexSchemeName;
	}
	
	public Set<EntityNameOrURI> getCts2TargetEntities() {
		return cts2TargetEntities;
	}

	public Set<EntityNameOrURI> getCts2Entities() {
		return cts2Entities;
	}

	public TaggedCodeSystemRestriction getCts2TaggedCodeSystemRestriction() {
		return cts2TaggedCodeSystemRestriction;
	}

	public EntitiesRestriction getCts2EntitiesRestriction() {
		return cts2EntitiesRestriction;
	}

	public EntityRestriction getCts2EntityRestriction() {
		return cts2EntityRestriction;
	}

	public boolean isMapQuery(){
		return isMapQuery;
	}
	
	public boolean hasNameAndVersion(){
		return this.hasNameAndVersion;
	}

	private void initializeClassMemberFields(){
		cts2Filters = null;							
		cts2Restrictions = null;									
		isMapQuery = false;										
		cts2Map = null;										
		cts2MapName = null;											
		cts2EntitiesRestriction = null;				
		cts2CodeSystem = null;								
		cts2CodeSystemName = null;									
		cts2EntityRestriction = null;					
		cts2CodeSystemRestriction = null;			
		cts2CodeSystemVersion = null;
		cts2MapVersion = null;
		lexSchemeName = null;
		lexVersionOrTag = null;
		hasNameAndVersion = false;
		cts2Entities = null;								
		cts2TaggedCodeSystemRestriction = null;		
		cts2TargetEntities = null;				
	}
	
	
	public QueryData(VersionNameConverter nameConverter){
		super();
		this.nameConverter = nameConverter;
		
		this.initializeClassMemberFields();
	}
	
	public QueryData(Query cts2Query, VersionNameConverter nameConverter){
		super();
		this.nameConverter = nameConverter;
		
		this.initializeClassMemberFields();
		if (cts2Query != null) {
			if(cts2Query instanceof CodeSystemVersionQuery){
				this.extractCodeSystemVersionQueryData((CodeSystemVersionQuery) cts2Query);
			}
			else if(cts2Query instanceof EntityDescriptionQuery){
				this.extractEntityDescriptionQueryData((EntityDescriptionQuery) cts2Query);
			}
			else if(cts2Query instanceof MapQuery){
				this.extractMapQueryData((MapQuery) cts2Query);
			}
			else if(cts2Query instanceof MapVersionQuery){
				this.extractMapVersionQueryData((MapVersionQuery) cts2Query);
			}
			else if(cts2Query instanceof MapEntryQuery){
				this.extractMapEntryQueryData((MapEntryQuery) cts2Query);				
			}
			else if(cts2Query instanceof ResolvedValueSetQuery){
				this.extractResolvedValueSetQuery((ResolvedValueSetQuery) cts2Query);
			}
		}
	}
	
	/**
	 * @param cts2Query
	 * @param nameConverter
	 */
	private void extractResolvedValueSetQuery(ResolvedValueSetQuery cts2Query) {
		ResolvedValueSetQueryServiceRestrictions localCts2Restrictions = cts2Query.getResolvedValueSetQueryServiceRestrictions();
		this.cts2Restrictions = localCts2Restrictions;
		this.cts2Filters = cts2Query.getFilterComponent();
		
//		if(localCts2Restrictions != null){
			// Do we want to pull more data from the restrictions?
//			localCts2Restrictions.getCodeSystems();
//			localCts2Restrictions.getCodeSystemVersions();
//			localCts2Restrictions.getEntities();
//			localCts2Restrictions.getValueSetDefinitions();
//			localCts2Restrictions.getValueSets();
			//this.convertCts2Name(nameConverter);
//		}
		
	}

	private void extractMapEntryQueryData(MapEntryQuery cts2Query) {
		MapEntryQueryServiceRestrictions localCts2Restrictions = cts2Query.getRestrictions();
		this.cts2Restrictions = localCts2Restrictions;
		this.cts2Filters = cts2Query.getFilterComponent();
		

		// Not needed?
//		query.getReadContext();
		
		if(localCts2Restrictions != null){
			this.cts2MapVersion = localCts2Restrictions.getMapVersion();
			this.convertCts2Name();
			this.cts2TargetEntities = localCts2Restrictions.getTargetEntities();
		}
		
		
		this.isMapQuery = true;		
	}

	private void extractEntityDescriptionQueryData(EntityDescriptionQuery cts2Query) {
		EntityDescriptionQueryServiceRestrictions localCts2Restrictions = cts2Query.getRestrictions();
		this.cts2Restrictions = localCts2Restrictions;
		this.cts2Filters = cts2Query.getFilterComponent();
				
		// Not needed?
//		query.getEntitiesFromAssociationsQuery();
//		restrictions.getHierarchyRestriction();
//		query.getReadContext();
		
		if(localCts2Restrictions != null){
			this.cts2CodeSystemVersion = localCts2Restrictions.getCodeSystemVersion();
			this.convertCts2Name();
			this.cts2Entities = localCts2Restrictions.getEntities();
			this.cts2TaggedCodeSystemRestriction = localCts2Restrictions.getTaggedCodeSystem();
		}
		
	}

	// Map Catalog (MapQueryService) (i.e. Eclipse "the idea of", or "Mapping Sample")
	// -----------------------------------------------------------
	private void extractMapQueryData(MapQuery cts2Query) {
		MapQueryServiceRestrictions localCts2Restrictions = cts2Query.getRestrictions();
		this.cts2Restrictions = localCts2Restrictions;
		this.cts2Filters = cts2Query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
//		restrictions.getValueSetRestriction();
		
		if(localCts2Restrictions != null){
			this.cts2CodeSystemRestriction = localCts2Restrictions.getCodeSystemRestriction();
		}
		this.isMapQuery = true;
	}

	// Map Version (MapVersionQueryService) (i.e. Eclipse Juno, or "Mapping Sample-1.0")
	// ---------------------------------------------------------
	private void extractMapVersionQueryData(MapVersionQuery cts2Query) {
		MapVersionQueryServiceRestrictions localCts2Restrictions = cts2Query.getRestrictions();
		this.cts2Restrictions = localCts2Restrictions;
		this.cts2Filters = cts2Query.getFilterComponent();

		// Not needed?
//		query.getReadContext();
//		restrictions.getValueSetRestriction();
		
		
		if(localCts2Restrictions != null){
			this.cts2CodeSystemRestriction = localCts2Restrictions.getCodeSystemRestriction();  	// Restrict to source or target fields containing this value.
			this.cts2EntitiesRestriction = localCts2Restrictions.getEntitiesRestriction();		// Restrict to maps containing these entities in a relation.
			
			this.cts2Map = localCts2Restrictions.getMap(); 										// Restrict to given map with this codeScheme Name
			this.cts2MapName = this.getName(this.cts2Map);
		}
		this.isMapQuery = true;
	}

	private String getName(NameOrURI cts2NameOrURI) {
		String cts2Name = null;
		if(cts2NameOrURI != null){
			cts2Name = (cts2NameOrURI.getUri() != null) ? cts2NameOrURI.getUri() : cts2NameOrURI.getName();
		}
		return cts2Name;
	}

	private void extractCodeSystemVersionQueryData(CodeSystemVersionQuery cts2Query) {
		CodeSystemVersionQueryServiceRestrictions localCts2Restrictions = cts2Query.getRestrictions();
		this.cts2Restrictions = localCts2Restrictions;
		this.cts2Filters = cts2Query.getFilterComponent();
		this.setReadContext(cts2Query.getReadContext());

		// Not needed?
//		query.getReadContext();
		
		if(localCts2Restrictions != null){
			this.cts2EntityRestriction = localCts2Restrictions.getEntityRestriction();
			
			this.cts2CodeSystem = localCts2Restrictions.getCodeSystem();
			this.cts2CodeSystemName = this.getName(this.cts2CodeSystem);
		}
	}

	private void convertCts2Name(){
		NameVersionPair nameVersionPair;
		NameOrURI cts2SystemVersion = this.getCts2SystemVersion();
		
		if(cts2SystemVersion != null){
			nameVersionPair = this.fromCts2VersionName(cts2SystemVersion.getName());	
			this.lexSchemeName = nameVersionPair.getName();
			
			this.lexVersionOrTag = new CodingSchemeVersionOrTag();
			this.lexVersionOrTag.setTag(nameVersionPair.getVersion());
			this.lexVersionOrTag.setVersion(nameVersionPair.getVersion());
			
			this.checkNameAndVersion();
		}
	}

	private void checkNameAndVersion() {
		if((this.lexSchemeName != null) && (lexVersionOrTag.getVersion() != null || lexVersionOrTag.getTag() != null)){
			hasNameAndVersion = true;
		}
	}
	
	public NameVersionPair fromCts2VersionName(String cts2CodeSystemVersionName){
		return this.nameConverter.fromCts2VersionName(cts2CodeSystemVersionName);
	}

	public ResolvedReadContext getReadContext() {
		return readContext;
	}

	public void setReadContext(ResolvedReadContext readContext) {
		this.readContext = readContext;
	}
	
}

