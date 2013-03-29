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

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
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
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.PrintUtility;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQueryService;

@Component
public class LexEvsEntityQueryService extends AbstractLexEvsService 
		implements EntityDescriptionQueryService {

	// Local class defined
	private class ResolvedConceptReferenceResults{
		private boolean atEnd;
		private ResolvedConceptReference [] resolvedConceptReference;
		
		public ResolvedConceptReferenceResults(ResolvedConceptReference [] resolvedConceptReference, boolean atEnd){			
			this.resolvedConceptReference = resolvedConceptReference.clone();
			this.atEnd = atEnd;
		}

		public boolean isAtEnd() {
			return atEnd;
		}

		@SuppressWarnings("unused")
		public void setAtEnd(boolean atEnd) {
			this.atEnd = atEnd;
		}

		public ResolvedConceptReference[] getResolvedConceptReference() {
			return resolvedConceptReference;
		}

		@SuppressWarnings("unused")
		public void setResolvedConceptReference(
				ResolvedConceptReference[] resolvedConceptReference) {
			this.resolvedConceptReference = resolvedConceptReference.clone();
		}
		
	}

	// Local variables
	private EntityTransform entityTransform = new EntityTransform();
	private boolean printObjects = false;

	@Resource
	private CodeSystemVersionNameConverter codeSystemVersionNameConverter;
	
	
	// ------ Local methods ----------------------
	public CodeSystemVersionNameConverter getCodeSystemVersionNameConverter() {
		return codeSystemVersionNameConverter;
	}

	public void setCodeSystemVersionNameConverter(
			CodeSystemVersionNameConverter codeSystemVersionNameConverter) {
		this.codeSystemVersionNameConverter = codeSystemVersionNameConverter;
	}
	
	public EntityTransform getEntityTransformer() {
		return entityTransform;
	}

	public void setEntityTransformer(
			EntityTransform entityTransform) {
		this.entityTransform = entityTransform;
	}
	
	public void setPrintObject(boolean print){
		this.printObjects = print;
	}

	protected ResolvedConceptReferenceResults doGetResourceSummaryResults(EntityDescriptionQuery query, SortCriteria sortCriteria, Page page){
		ResolvedConceptReferenceResults results = null;
		
		// * if codingSchemeName exists within the query, get CodedNodeSet
		// * for each filter existing within the query, execute restrictToMatchingDesignations on the codedNodeSet
		CodedNodeSet codedNodeSet = this.getCodedNodeSet(query, sortCriteria);
		
		if(codedNodeSet != null){
			// Using filtered codeNodeSet get ResolvedConceptReferenceResults
			// -- contains an array of ResolvedConceptReference and a boolean indicating if at end of resultSet
			results = this.getResolvedConceptReferenceResults(codedNodeSet, sortCriteria, page);
		}
		
		return results;
	}

	protected ResolvedConceptReferenceResults getResolvedConceptReferenceResults(CodedNodeSet codedNodeSet, SortCriteria sortCriteria, Page page){
		boolean atEnd = false;
		ResolvedConceptReference[] resolvedConceptReferences = null;
		ResolvedConceptReferencesIterator iterator;
		ResolvedConceptReferenceList resolvedConceptReferenceList = null;
		int start = 0, end = 0;
		try {
			iterator = this.getResolvedConceptReferencesIterator(codedNodeSet, sortCriteria);
			
			// Get on requested "page" of entities.  
			// In this case we can get the "page" from the iterator, unlike in LexEvsCodeSystemVersionQueryService.
			start = page.getStart();
			end = page.getEnd();
			if(end > iterator.numberRemaining()){
				end = iterator.numberRemaining();
				atEnd = true;				
			}
			resolvedConceptReferenceList = iterator.get(start, end);
			// Get array of resolved concept references
			
			if(resolvedConceptReferenceList != null){
				resolvedConceptReferences = resolvedConceptReferenceList.getResolvedConceptReference();
				if(printObjects){
					System.out.println("resolvedConceptReferences: " + resolvedConceptReferences.length);
				}
			}	
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBResourceUnavailableException e) {
			throw new RuntimeException(e);
		}
		
		return new ResolvedConceptReferenceResults(resolvedConceptReferences, atEnd);
	}
	
	protected ResolvedConceptReferencesIterator getResolvedConceptReferencesIterator(CodedNodeSet codedNodeSet, SortCriteria sortCriteria){
		ResolvedConceptReferencesIterator iterator = null;
		try {
			// With all null arguments the iterator will access the entire codeNodeSet
			// This call will execute the set of filters determined in loop above
			SortOptionList sortOptions = null;
			LocalNameList propertyNames = null;
			PropertyType [] propertyTypes = null; 
			
			iterator = codedNodeSet.resolve(sortOptions, propertyNames, propertyTypes);
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
		
		return iterator;
	}
	
	protected CodedNodeSet getCodedNodeSet(EntityDescriptionQuery query, SortCriteria sortCriteria){
		CodedNodeSet codedNodeSet = null;
		Set<ResolvedFilter> filters = null; 		
		String codeSystem = null;
		EntityDescriptionQueryServiceRestrictions entityDescriptionQueryServiceRestrictions = null;
		String codingSchemeName = null;
		CodingSchemeVersionOrTag versionOrTag = null;
		boolean haveSchemeName = false;
		
		if (query != null) {
			entityDescriptionQueryServiceRestrictions = query.getRestrictions();
			filters = query.getFilterComponent();
			if (entityDescriptionQueryServiceRestrictions != null) {
				codeSystem = entityDescriptionQueryServiceRestrictions.getCodeSystemVersion().getName();
				
				if(codeSystem != null){
					NameVersionPair nameVersionPair =
							this.codeSystemVersionNameConverter.fromCts2CodeSystemVersionName(codeSystem);					
					versionOrTag = new CodingSchemeVersionOrTag();
					codingSchemeName = nameVersionPair.getName();
					versionOrTag.setTag(nameVersionPair.getVersion());
					versionOrTag.setVersion(nameVersionPair.getVersion());
					if(printObjects){
						System.out.println("CodingSchemeName: " + codingSchemeName);
						System.out.println("VersionOrTag: " + versionOrTag.getVersion());
					}
					if((codingSchemeName != null) && (versionOrTag.getVersion() != null || versionOrTag.getTag() != null)){
						haveSchemeName = true;
					}
				}
			}
		}		
				

		if(haveSchemeName){
			LexBIGService lexBigService = getLexBigService();
			boolean found = false;
			
			try {
				// Get Code Node Set from LexBIG service for given coding scheme
				LocalNameList entityTypes = new LocalNameList();
				CodingSchemeRenderingList codingSchemeRenderingList = lexBigService.getSupportedCodingSchemes();
				int count = codingSchemeRenderingList.getCodingSchemeRenderingCount();
				for(int i=0; i < count; i++){
					CodingSchemeSummary codingSchemeSummary = codingSchemeRenderingList.getCodingSchemeRendering(i).getCodingSchemeSummary();
					if(printObjects){
						System.out.println("CodingSchemeRendering: ");
						System.out.println(PrintUtility.codingSchemeSummary(codingSchemeSummary, 1));
					}
					
					if(codingSchemeSummary.getLocalName().equals(codingSchemeName)){
						found = true;
					}
				}
				
				
				if(found){
					codedNodeSet = lexBigService.getNodeSet(codingSchemeName, versionOrTag , entityTypes);
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
				
			if(found && (filters != null)){
				for(ResolvedFilter filter : filters){
					filterCodedNodeSetByResolvedFilter(filter, codedNodeSet);
				}
			}
		}
	
		
		return codedNodeSet;
	}

	protected void filterCodedNodeSetByResolvedFilter(ResolvedFilter filter, CodedNodeSet codedNodeSet){
		if(codedNodeSet != null){
			try {
				String matchText = filter.getMatchValue();										// Value to search with 
				SearchDesignationOption option = SearchDesignationOption.ALL;					// Other options: PREFERRED_ONLY, NON_PREFERRED_ONLY, ALL 
				String matchAlgorithm = filter.getMatchAlgorithmReference().getContent();		// Extract from filter the match algorithm to use
				String language = null;															// This field is not really used, uses default "en"
				
				codedNodeSet.restrictToMatchingDesignations(matchText, option, matchAlgorithm, language);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	// -------- Implemented methods ----------------
	@Override
	public int count(EntityDescriptionQuery query) {
		CodedNodeSet codedNodeSet = this.getCodedNodeSet(query, null);
		ResolvedConceptReferencesIterator iterator = this.getResolvedConceptReferencesIterator(codedNodeSet, null);
		if(iterator == null){
			return 0;
		}
		
		try {
			return iterator.numberRemaining();
		} catch (LBResourceUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DirectoryResult<EntityDescription> getResourceList(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		DirectoryResult<EntityDescription> directoryResult = null;
		List<EntityDescription> list = new ArrayList<EntityDescription>();
		
		ResolvedConceptReferenceResults resolvedConceptReferenceResults = this.doGetResourceSummaryResults(query, sortCriteria, page);
		
		// Transform each reference into a CTS2 entry and add to list
		ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
		for(ResolvedConceptReference reference : resolvedConceptReferences){
			if(printObjects){
				System.out.println("ResolvedConceptReference:\n" + PrintUtility.resolvedConceptReference_toString(reference, 1));
			}
			EntityDescription entry = entityTransform.transformToEntity(reference);
			list.add(entry);
		}
		
		directoryResult = new DirectoryResult<EntityDescription>(list, resolvedConceptReferenceResults.isAtEnd());
		
		return directoryResult;
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {	
		DirectoryResult<EntityDirectoryEntry> directoryResult = null;
		List<EntityDirectoryEntry> list = new ArrayList<EntityDirectoryEntry>();
		
		ResolvedConceptReferenceResults resolvedConceptReferenceResults = this.doGetResourceSummaryResults(query, sortCriteria, page);
		
		// Transform each reference into a CTS2 entry and add to list
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			for(ResolvedConceptReference reference : resolvedConceptReferences){
				if(printObjects){
					System.out.println("ResolvedConceptReference:\n" + PrintUtility.resolvedConceptReference_toString(reference, 1));
				}
				EntityDirectoryEntry entry = entityTransform.transformToEntry(reference);
				list.add(entry);
			}
			
			directoryResult = new DirectoryResult<EntityDirectoryEntry>(list, resolvedConceptReferenceResults.isAtEnd());
		}
		
		return directoryResult;
	}
	
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
//		for(ExtensionDescription desc :
//			this.getLexBigService().getFilterExtensions().getExtensionDescription()){
//			
//		}
		// TODO
		return null;
	}
	
	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		PropertyReference ref = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		// presentation
		// resource_name - code (restricted code method)
		return new HashSet<PropertyReference>(Arrays.asList(ref)); 
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

