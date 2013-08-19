/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.EntityReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriUtils.BadUriException;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionReadService;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

/**
 * LexEVS CodedNodeSet implementation of EntityDescriptionReadService.
 * 
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsEntityReadService extends AbstractLexEvsService 
	implements EntityDescriptionReadService, InitializingBean {
	
	@Resource
	private EntityNameQueryBuilder entityNameQueryBuilder;

	@Resource
	private EntityTransform transformer;
	
	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private EntityUriResolver entityUriResolver;
	
	private SearchExtension searchExtension;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
		this.searchExtension = 
			(SearchExtension) this.getLexBigService().getGenericExtension("SearchExtension");
		} catch (Exception e){
			log.warn("SearchExtension is not available.");
		}
	}

	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(
			VersionNameConverter versionNameConverter) {
		this.nameConverter = versionNameConverter;
	}

	public void setEntityTransform(EntityTransform entityTransform) {
		this.transformer = entityTransform;
	}

	// -------- Implemented methods ----------------
	@Override
	public EntityDescription read(
			EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {
		LexBIGService lexBigService = this.getLexBigService();
		ResolvedConceptReference entity = CommonUtils.getLexResolvedConceptReference(lexBigService, nameConverter, identifier, readContext);
		
		if(entity == null){
			return null;
		} else {
			EntityListEntry listEntry = this.transformer.transformFullDescription(entity);
			
			return listEntry == null ? null : listEntry.getEntry();
		}
	}

	@Override
	public boolean exists(EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {
		LexBIGService lexBigService = this.getLexBigService();
		
		ResolvedConceptReference entity = CommonUtils.getLexResolvedConceptReference(lexBigService, nameConverter, identifier,	readContext);
		return (entity == null) ? false : true;
	}
	
	@Override
	public List<VersionTagReference> getSupportedVersionTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}	

	// Not going to implement following methods
	// ----------------------------------------
	@Override
	public DirectoryResult<EntityListEntry> readEntityDescriptions(
			EntityNameOrURI entityId, SortCriteria sortCriteria,
			ResolvedReadContext readContext, Page page) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EntityReference availableDescriptions(EntityNameOrURI entityId,
			ResolvedReadContext readContext) {
		ScopedEntityName name;
		if(entityId.getEntityName() != null){
			name = entityId.getEntityName();
		} else {
			try {
				name = this.entityUriResolver.resolveUri(entityId.getUri());
			} catch (BadUriException e){
				return null;
			}
		}
		
		String searchString = this.entityNameQueryBuilder.buildQuery(name);
		
		try {
			return this.transformer.transformEntityReference(
				this.searchExtension.search(searchString, MatchAlgorithm.LUCENE));
		} catch (LBParameterException e) {
			//Exception on the LexEVS side dealing with an invalid input. Return null.
			return null;
		}	
	}

	@Override
	public List<EntityListEntry> readEntityDescriptions(EntityNameOrURI entityId,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<CodeSystemVersionReference> getKnownCodeSystemVersions() {
		return new ArrayList<CodeSystemVersionReference>();
	}

	@Override
	public List<CodeSystemReference> getKnownCodeSystems() {
		return new ArrayList<CodeSystemReference>();
	}
	
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}
}
