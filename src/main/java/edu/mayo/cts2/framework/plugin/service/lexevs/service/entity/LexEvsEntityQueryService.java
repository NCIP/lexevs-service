/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.ResolvedConceptReferenceResults;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Primary
@Component
public class LexEvsEntityQueryService extends AbstractLexEvsService 
		implements DelegateEntityQueryService {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private EntityTransform transformer;
		
	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(
			VersionNameConverter versionNameConverter) {
		this.nameConverter = versionNameConverter;
	}
	
	public void setEntityTransformer(
			EntityTransform entityTransform) {
		this.transformer = entityTransform;
	}
	

	// -------- Implemented methods ----------------
	@Override
	public int count(EntityDescriptionQuery query) {
		int count = 0;
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		CodedNodeSet codedNodeSet;
		codedNodeSet = CommonResourceUtils.getCodedNodeSet(lexBigService, queryData, null);
		
		if(codedNodeSet != null){
			ResolvedConceptReferencesIterator iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, null);
			if(iterator != null){
				try {
					count = iterator.numberRemaining();
				} catch (LBResourceUnavailableException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return count;
	}

	@Override
	public boolean isEntityInSet(EntityNameOrURI nameOrUri,
			EntityDescriptionQuery query, ResolvedReadContext readContext) {
		SortCriteria sortCriteria = null;
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		CodedNodeSet codedNodeSet = CommonResourceUtils.getCodedNodeSet(lexBigService, queryData, sortCriteria);
		
		ScopedEntityName entityName = nameOrUri.getEntityName();
		String uri = nameOrUri.getUri();
		
		ConceptReference cts2Code = new ConceptReference();
		try {
			if(entityName != null){
				String code = entityName.getName();
				String nameSpace = entityName.getNamespace();
				
				cts2Code.setCode(code);
				cts2Code.setCodeNamespace(nameSpace);
				return codedNodeSet.isCodeInSet(cts2Code);
			}
			else if(uri != null){
				// TODO need to lookup in LexEVS, turn uri into name LexEvs can understand then update cts2Code object
				return codedNodeSet.isCodeInSet(cts2Code);
			}
		
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
		
		return false;
	}

	@Override
	public DirectoryResult<EntityDescription> getResourceList(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		ResolvedConceptReferenceResults resolvedConceptReferenceResultsPage;
		resolvedConceptReferenceResultsPage = CommonPageUtils.getResolvedConceptReferenceResultsPage(lexBigService, queryData, sortCriteria, page);

		DirectoryResult<EntityDescription> directoryResult;
		directoryResult = CommonResourceUtils.createDirectoryResultWithEntryDescriptions(this.transformer, resolvedConceptReferenceResultsPage, Constants.FULL_DESCRIPTION);

		return directoryResult;
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {	
		
		LexBIGService lexBigService = this.getLexBigService();
		
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		ResolvedConceptReferenceResults resolvedConceptReferenceResults;
		resolvedConceptReferenceResults = CommonPageUtils.getResolvedConceptReferenceResultsPage(lexBigService, queryData, sortCriteria, page);
		
		DirectoryResult<EntityDirectoryEntry> directoryResult;
		directoryResult = CommonResourceUtils.createDirectoryResultWithEntryDescriptions(this.transformer, resolvedConceptReferenceResults, Constants.SUMMARY_DESCRIPTION);
			
		return directoryResult;
	}
	
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.createSupportedMatchAlgorithms();
	}
	
	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.createSupportedSearchReferences();
	}

	@Override
	public Set<VersionTagReference> getSupportedTags() {
		return new HashSet<VersionTagReference>(Arrays.asList(Constants.CURRENT_TAG));
	}

	
	// Not going to implement following methods
	// ----------------------------------------
	@Override
	public EntityReferenceList resolveAsEntityReferenceList(
			EntityDescriptionQuery arg0, ResolvedReadContext arg1) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EntityNameOrURIList intersectEntityList(
			Set<EntityNameOrURI> entities,
			EntityDescriptionQuery restrictions, 
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	@Override
	public boolean canHandle(EntityDescriptionQuery query) {
		return query.getEntitiesFromAssociationsQuery() == null &&
				query.getRestrictions().getHierarchyRestriction() == null &&
				query.getRestrictions().getCodeSystemVersion() != null;
	}

	@Override
	public int getOrder() {
		return 1;
	}

}

