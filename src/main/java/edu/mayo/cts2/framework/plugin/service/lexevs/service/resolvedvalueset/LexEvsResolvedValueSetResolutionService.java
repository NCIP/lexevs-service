/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.filter.match.ContainsMatcher;
import edu.mayo.cts2.framework.filter.match.ExactMatcher;
import edu.mayo.cts2.framework.filter.match.ResolvableMatchAlgorithmReference;
import edu.mayo.cts2.framework.filter.match.StartsWithMatcher;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSet;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetHeader;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ResolvedValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ResolvedValueSetNameTriple;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.LexEvsEntityQueryService;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetResolutionEntityRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResolutionEntityQuery;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResult;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.NotImplementedException;

@Component
public class LexEvsResolvedValueSetResolutionService extends AbstractLexEvsService implements
ResolvedValueSetResolutionService {

	
	@Resource
	private UrlConstructor urlConstructor;
	@Resource
	private ResolvedCodingSchemeTransform transform;
		
	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private LexEvsEntityQueryService lexEvsEntityQueryService;	
	
	@Resource
	private ResolvedValueSetNameTranslator resolvedValueSetNameTranslator;
	
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		Set<MatchAlgorithmReference> returnSet = new HashSet<MatchAlgorithmReference>();
		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(exactMatch,
						new ExactMatcher()));
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(contains,
						new ContainsMatcher()));
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
		returnSet.add(ResolvableMatchAlgorithmReference
				.toResolvableMatchAlgorithmReference(startsWith,
						new StartsWithMatcher()));

		return returnSet;
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		ComponentReference
		name = StandardModelAttributeReference.RESOURCE_NAME.getComponentReference();
		ComponentReference
			about = StandardModelAttributeReference.ABOUT.getComponentReference();
		ComponentReference
			description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference();
		
		return new HashSet<ComponentReference>(Arrays.asList(name,about,description));
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return null;
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return null;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return null;
	}

	@Override
	public ResolvedValueSetResult<URIAndEntityName> getResolution(
			ResolvedValueSetReadId identifier,
			Set<ResolvedFilter> filterComponent, 
			Page page) {
		NameVersionPair codingScheme = this.getNameVersionPair(identifier);
		
		if(codingScheme == null){
			return null;
		}
		
		String cts2VersionName = 
			this.nameConverter.toCts2VersionName(codingScheme.getName(), codingScheme.getVersion());
		
		EntityDescriptionQueryImpl query= new EntityDescriptionQueryImpl();
		query.setFilterComponent(filterComponent);
		EntityDescriptionQueryServiceRestrictions entityRestrictions =
				new EntityDescriptionQueryServiceRestrictions();
		entityRestrictions.getCodeSystemVersions().add(
				ModelUtils.nameOrUriFromName(cts2VersionName));

		query.setRestrictions(entityRestrictions);
		DirectoryResult<EntityDirectoryEntry> result = this.lexEvsEntityQueryService.getResourceSummaries(
				query, null, page);
		List<URIAndEntityName> transformedResult= transform.transform(result);
		
		return new ResolvedValueSetResult<URIAndEntityName>(
				this.getResolvedValueSetHeader(codingScheme), 
				transformedResult, 
				result.isAtEnd());
	}

	@Override
	public ResolvedValueSet getResolution(ResolvedValueSetReadId identifier) {
		Page page = new Page();
		page.setPage(0);
		page.setMaxToReturn(Integer.MAX_VALUE);
				
		ResolvedValueSetResult<URIAndEntityName> resolution = this.getResolution(identifier, null, page);
		if(resolution == null){
			return null;
		}
		
		ResolvedValueSet resolvedValueSet = new ResolvedValueSet();
		resolvedValueSet.setResolutionInfo(resolution.getResolvedValueSetHeader());
		for(URIAndEntityName synopsis : resolution.getEntries()){
			resolvedValueSet.addEntry(synopsis);
		}
		
		return resolvedValueSet;		
	}

	@Override
	public ResolvedValueSetResult<EntityDirectoryEntry> getEntities(
			ResolvedValueSetReadId identifier,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, Page page) {
		NameVersionPair codingScheme = this.getNameVersionPair(identifier);
		
		if(codingScheme == null){
			return null;
		}

		DirectoryResult<EntityDirectoryEntry> result = this.lexEvsEntityQueryService.getResourceSummaries(
				this.toEntityDescriptionQuery(codingScheme, query),
				sortCriteria, 
				page);
		
		return new ResolvedValueSetResult<EntityDirectoryEntry>(
				this.getResolvedValueSetHeader(codingScheme), 
				result.getEntries(), 
				result.isAtEnd());		
	}

	protected NameVersionPair getNameVersionPair(ResolvedValueSetReadId identifier){
		String valueSetId = identifier.getValueSet().getName();
		String definitionId = identifier.getValueSetDefinition().getName();
		String id = identifier.getLocalName();
	
		return this.resolvedValueSetNameTranslator.getNameVersionPair(
			new ResolvedValueSetNameTriple(valueSetId, definitionId, id));
	}
	
	protected ResolvedValueSetHeader getResolvedValueSetHeader(NameVersionPair versionNamePair){	
		CodingScheme cs = resolve(
			versionNamePair.getName(), 
			Constructors.createCodingSchemeVersionOrTagFromVersion(versionNamePair.getVersion()));
		
		if (cs !=null) {
			return transform.transformToResolvedValueSetHeader(cs);
		} else {
			throw new RuntimeException("Cannot find CodingScheme for ResolvedValueSet Header: " + versionNamePair.getName());
		}
	}
	
	@Override
	public DirectoryResult<EntityDescription> getEntityList(
			ResolvedValueSetReadId identifier,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, Page page) {
		NameVersionPair codingScheme = this.getNameVersionPair(identifier);
		
		if(codingScheme == null){
			return null;
		}
		
		DirectoryResult<EntityListEntry> result = this.lexEvsEntityQueryService.getResourceList(
				this.toEntityDescriptionQuery(codingScheme, query),
				sortCriteria, 
				page);
		
		return new ResolvedValueSetResult<EntityDescription>(
					this.getResolvedValueSetHeader(codingScheme), 
					this.listEntriesToDescriptions(result.getEntries()), 
					result.isAtEnd());
	}
	
	private List<EntityDescription> listEntriesToDescriptions(List<EntityListEntry> listEntries){
		List<EntityDescription> returnList = new ArrayList<EntityDescription>();
		for(EntityListEntry entry : listEntries){
			returnList.add(entry.getEntry());
		}
		
		return returnList;
	}
	
	protected CodingScheme resolve(String nameOrUri, CodingSchemeVersionOrTag versionIdOrTag){
		CodingScheme codingScheme;
		try {
			codingScheme = this.getLexBigService().resolveCodingScheme(nameOrUri, versionIdOrTag);
		} catch (LBException e) {
			//this could be just that LexEVS didn't find it. If so, return null.
			log.warn(e);
			return null;
		}
		return codingScheme;
	}
	
	private EntityDescriptionQuery toEntityDescriptionQuery(
			NameVersionPair codingScheme, final ResolvedValueSetResolutionEntityQuery query){
		
			final EntityDescriptionQueryServiceRestrictions entityRestrictions =
				new EntityDescriptionQueryServiceRestrictions();
			
			if(query != null && query.getResolvedValueSetResolutionEntityRestrictions() != null){
				ResolvedValueSetResolutionEntityRestrictions restrictions = 
						query.getResolvedValueSetResolutionEntityRestrictions();
				
				if (restrictions.getCodeSystemVersion() != null)
					entityRestrictions.getCodeSystemVersions().add(restrictions.getCodeSystemVersion());

				entityRestrictions.setEntities(restrictions.getEntities());
			}
		
			entityRestrictions.getCodeSystemVersions().add(
				ModelUtils.nameOrUriFromName(
					this.nameConverter.toCts2VersionName(
						codingScheme.getName(), 
						codingScheme.getVersion())));
		
		return new EntityDescriptionQuery(){

			@Override
			public Query getQuery() {
				if(query == null){
					return null;
				} else {
					return query.getQuery();
				}
			}

			@Override
			public Set<ResolvedFilter> getFilterComponent() {
				if(query == null){
					return null;
				} else {
					return query.getFilterComponent();
				}
			}

			@Override
			public ResolvedReadContext getReadContext() {
				return null;
			}

			@Override
			public EntitiesFromAssociationsQuery getEntitiesFromAssociationsQuery() {
				return null;
			}

			@Override
			public EntityDescriptionQueryServiceRestrictions getRestrictions() {
				return entityRestrictions;
			}
		};
	}
	
	@Override
	public EntityReferenceList contains(ResolvedValueSetReadId identifier, Set<EntityNameOrURI> entities) {
		return null;
	}
}
