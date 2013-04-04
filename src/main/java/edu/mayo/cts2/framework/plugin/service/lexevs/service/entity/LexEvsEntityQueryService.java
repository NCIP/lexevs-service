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

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.match.ContainsMatcher;
import edu.mayo.cts2.framework.filter.match.ExactMatcher;
import edu.mayo.cts2.framework.filter.match.ResolvableMatchAlgorithmReference;
import edu.mayo.cts2.framework.filter.match.StartsWithMatcher;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceSummaryUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.PrintUtility;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.ResolvedConceptReferenceResults;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQueryService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsEntityQueryService extends AbstractLexEvsService 
		implements EntityDescriptionQueryService {

	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	@Resource
	private EntityTransform transformer;
	
	private boolean printObjects = false;
	
	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(
			CodeSystemVersionNameConverter codeSystemVersionNameConverter) {
		this.nameConverter = codeSystemVersionNameConverter;
	}
	
	public void setEntityTransformer(
			EntityTransform entityTransform) {
		this.transformer = entityTransform;
	}
	
	public void setPrintObject(boolean print){
		this.printObjects = print;
	}


	// -------- Implemented methods ----------------
	@Override
	public int count(EntityDescriptionQuery query) {
		int count = 0;
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query);
		queryData.setVersionOrTag(nameConverter);
		CodedNodeSet codedNodeSet;
		codedNodeSet = CommonResourceSummaryUtils.getCodedNodeSet(lexBigService, queryData, null);
		
		if(codedNodeSet != null){
			ResolvedConceptReferencesIterator iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, null);
			if(iterator != null){
				try {
					return iterator.numberRemaining();
				} catch (LBResourceUnavailableException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return count;
	}

	@Override
	public DirectoryResult<EntityDescription> getResourceList(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		DirectoryResult<EntityDescription> directoryResult = null;
		List<EntityDescription> list = new ArrayList<EntityDescription>();
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query);
		queryData.setVersionOrTag(nameConverter);
		ResolvedConceptReferenceResults resolvedConceptReferenceResults;
		resolvedConceptReferenceResults = CommonResourceSummaryUtils.getResolvedConceptReferenceResults(lexBigService, queryData, sortCriteria, page);
		
		if(resolvedConceptReferenceResults != null){
			// Transform each reference into a CTS2 entry and add to list
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			for(ResolvedConceptReference reference : resolvedConceptReferences){
				if(printObjects){
					System.out.println("ResolvedConceptReference:\n" + PrintUtility.resolvedConceptReference_toString(reference, 1));
				}
				EntityDescription entry = transformer.transformToEntity(reference);
				list.add(entry);
			}
			
			directoryResult = new DirectoryResult<EntityDescription>(list, resolvedConceptReferenceResults.isAtEnd());
		}
		else{
			directoryResult = new DirectoryResult<EntityDescription>(list, true);
		}
		
		return directoryResult;
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {	
		List<EntityDirectoryEntry> list = new ArrayList<EntityDirectoryEntry>();
		DirectoryResult<EntityDirectoryEntry> directoryResult = new DirectoryResult<EntityDirectoryEntry>(list, true);
		
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query);
		queryData.setVersionOrTag(nameConverter);
		ResolvedConceptReferenceResults resolvedConceptReferenceResults;
		resolvedConceptReferenceResults = CommonResourceSummaryUtils.getResolvedConceptReferenceResults(lexBigService, queryData, sortCriteria, page);
		
		// Transform each reference into a CTS2 entry and add to list
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			if(resolvedConceptReferences != null){
				for(ResolvedConceptReference reference : resolvedConceptReferences){
					if(printObjects){
						System.out.println("ResolvedConceptReference:\n" + PrintUtility.resolvedConceptReference_toString(reference, 1));
					}
					EntityDirectoryEntry entry = transformer.transformToEntry(reference);
					list.add(entry);
				}
			}			
			directoryResult = new DirectoryResult<EntityDirectoryEntry>(list, resolvedConceptReferenceResults.isAtEnd());
		}
		
		return directoryResult;
	}
	
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

//	@Override
//	public boolean isEntityInSet(EntityNameOrURI entity,
//			EntityDescriptionQuery restrictions, ResolvedReadContext readContext) {
//		boolean answer = false;
//		CodedNodeSet codedNodeSet = this.getCodedNodeSet(restrictions, null);
//		
//		ConceptReference code = new ConceptReference();
//		code.setCode(entity.getEntityName().getName());
//		code.setCodeNamespace(entity.getEntityName().getNamespace());
//	
//		try {
//			answer = codedNodeSet.isCodeInSet(code);
//		} catch (LBInvocationException e) {
//			throw new UnsupportedOperationException();
//		} catch (LBParameterException e) {
//			throw new UnsupportedOperationException();
//		}
//		
//		return answer;
//	}

//	@Override
//	public EntityReferenceList resolveAsEntityReferenceList(
//			EntityDescriptionQuery restrictions, ResolvedReadContext readContext) {
//		
//		EntityReferenceList entityReferenceList = new EntityReferenceList();		
//		
//		ResolvedConceptReferenceResults resolvedConceptReferenceResults = this.doGetResourceSummaryResults(restrictions, null, null);
//		
//		// Transform each reference into a CTS2 entry and add to list
//		ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.resolvedConceptReference;
//		for(ResolvedConceptReference reference : resolvedConceptReferences){
//			if(printObjects){
//				System.out.println("ResolvedConceptReference:\n" + PrintUtility.resolvedConceptReference_toString(reference, 1));
//			}
//			EntityReference entry = entityTransform.transform_EntityReference(reference);
//			entityReferenceList.addEntry(entry);
//		}
//		
//		return entityReferenceList;
//	}
//
	@Override
	public EntityNameOrURIList intersectEntityList(
			Set<EntityNameOrURI> entities,
			EntityDescriptionQuery restrictions, 
			ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}


	@Override
	public Set<VersionTagReference> getSupportedTags() {
		return new HashSet<VersionTagReference>(Arrays.asList(Constants.CURRENT_TAG));
	}

	@Override
	public boolean isEntityInSet(EntityNameOrURI arg0,
			EntityDescriptionQuery arg1, ResolvedReadContext arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityReferenceList resolveAsEntityReferenceList(
			EntityDescriptionQuery arg0, ResolvedReadContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}

