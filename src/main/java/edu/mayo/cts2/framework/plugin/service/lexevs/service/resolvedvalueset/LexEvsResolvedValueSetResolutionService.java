package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.core.constants.URIHelperInterface;
import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.core.util.EncodingUtils;
import edu.mayo.cts2.framework.filter.match.ContainsMatcher;
import edu.mayo.cts2.framework.filter.match.ExactMatcher;
import edu.mayo.cts2.framework.filter.match.ResolvableMatchAlgorithmReference;
import edu.mayo.cts2.framework.filter.match.StartsWithMatcher;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.DescriptionInCodeSystem;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSet;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetHeader;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.LexEvsEntityQueryService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceSummaryUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.ResolvedConceptReferenceResults;
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
@Component
public class LexEvsResolvedValueSetResolutionService extends AbstractLexEvsService implements
ResolvedValueSetResolutionService {

	
	@Resource
	private UrlConstructor urlConstructor;
	@Resource
	private ResolvedCodingSchemeTransform transform;
		
	@Resource
	private LexEvsEntityQueryService lexEvsEntityQueryService;	
	
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
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		PropertyReference
		name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();
	PropertyReference
		about = StandardModelAttributeReference.ABOUT.getPropertyReference();
	PropertyReference
		description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
	return new HashSet<PropertyReference>(Arrays.asList(name,about,description));

	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedValueSetResult<EntitySynopsis> getResolution(
			ResolvedValueSetReadId identifier,
			Set<ResolvedFilter> filterComponent, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedValueSet getResolution(ResolvedValueSetReadId identifier) {
		throw new UnsupportedOperationException("Cannot resolve the complete ResolvedValueSet yet...");

	}

	@Override
	public ResolvedValueSetResult<EntityDirectoryEntry> getEntities(
			ResolvedValueSetReadId identifier,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, Page page) {
		String id = identifier.getLocalName();

		DirectoryResult<EntityDirectoryEntry> result = this.lexEvsEntityQueryService.getResourceSummaries(
				this.toEntityDescriptionQuery(identifier, query),
				sortCriteria, 
				page);
		return new ResolvedValueSetResult<EntityDirectoryEntry>(
				this.getResolvedValueSetHeader(id), 
				result.getEntries(), 
				result.isAtEnd());
		
	}

	
	protected ResolvedValueSetHeader getResolvedValueSetHeader(String id){
		CodingScheme cs= resolve(id, null);
		if (cs !=null) {
		return transform.transformToResolvedValueSetHeader(cs);
		} else {
			return null;
		}
	}
	
	
	@Override
	public DirectoryResult<EntityDescription> getEntityList(
			ResolvedValueSetReadId identifier,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, Page page) {
        String id = identifier.getLocalName();
		
		DirectoryResult<EntityDescription> result = this.lexEvsEntityQueryService.getResourceList(
				this.toEntityDescriptionQuery(identifier, query),
				sortCriteria, 
				page);
		
		return new ResolvedValueSetResult<EntityDescription>(
					this.getResolvedValueSetHeader(id), 
					result.getEntries(), 
					result.isAtEnd());
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
	
	private ResolvedValueSetResult<EntityDirectoryEntry> addInEntitiesHrefs(
			ResolvedValueSetResult<EntityDirectoryEntry> result) {
		if(result == null || result.getEntries() == null){
			return null;
		}
		
		CodeSystemVersionReference ref = 
				result.getResolvedValueSetHeader().getResolvedUsingCodeSystem(0);
		
		for(EntityDirectoryEntry entry : result.getEntries()){
			
			
		
			entry.setHref(
					this.urlConstructor.getServerRootWithAppName() + "/" + URIHelperInterface.ENTITY + "/" + 
							EncodingUtils.encodeScopedEntityName(entry.getName()));
			
			ValueSetDefinitionReference resolutionOf = result.getResolvedValueSetHeader().getResolutionOf();
			
			for(DescriptionInCodeSystem description : entry.getKnownEntityDescription()){
				description.setHref(this.urlConstructor.createEntityUrl(
						resolutionOf.getValueSet().getContent(),
						resolutionOf.getValueSetDefinition().getContent(),
						entry.getName()));
				
				description.setDescribingCodeSystemVersion(ref);
			}
			
		}
		
		return result;
	}

	private ResolvedValueSetResult<EntitySynopsis> addInHrefs(
			ResolvedValueSetResult<EntitySynopsis> result) {
		if(result == null || result.getEntries() == null){
			return null;
		}
		
		for(EntitySynopsis entry : result.getEntries()){
	
			ValueSetDefinitionReference resolutionOf = result.getResolvedValueSetHeader().getResolutionOf();
			
			entry.setHref(this.urlConstructor.createEntityUrl(
				resolutionOf.getValueSet().getContent(),
				resolutionOf.getValueSetDefinition().getContent(),
				EncodingUtils.encodeScopedEntityName(entry)));
		}
		
		return result;
	}	
	private EntityDescriptionQuery toEntityDescriptionQuery(
			ResolvedValueSetReadId identifier, final ResolvedValueSetResolutionEntityQuery query){
		
			final EntityDescriptionQueryServiceRestrictions entityRestrictions =
				new EntityDescriptionQueryServiceRestrictions();
			
			if(query != null && query.getResolvedValueSetResolutionEntityRestrictions() != null){
				ResolvedValueSetResolutionEntityRestrictions restrictions = 
						query.getResolvedValueSetResolutionEntityRestrictions();
				
				entityRestrictions.setCodeSystemVersion(restrictions.getCodeSystemVersion());
				entityRestrictions.setEntities(restrictions.getEntities());
			}
			
			entityRestrictions.setCodeSystemVersion(
				ModelUtils.nameOrUriFromName(identifier.getValueSetDefinition().getName()));
		
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
	
}
