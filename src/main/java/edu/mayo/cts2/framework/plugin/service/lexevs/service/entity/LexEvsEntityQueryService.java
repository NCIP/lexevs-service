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
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Entity;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.OpaqueData;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQueryService;

@Component
public class LexEvsEntityQueryService extends AbstractLexEvsService 
		implements EntityDescriptionQueryService {

	// ------ Local methods ----------------------
	private EntityTransform entityTransform = new EntityTransform();

	public EntityTransform getEntityTransformer() {
		return entityTransform;
	}

	public void setEntityTransformer(
			EntityTransform entityTransform) {
		this.entityTransform = entityTransform;
	}

	
	// -------- Implemented methods ----------------
	@Override
	public int count(EntityDescriptionQuery query) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DirectoryResult<EntityDescription> getResourceList(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		
		LexBIGService lexBigService = getLexBigService();
		ArrayList<EntityDirectoryEntry> list = new ArrayList<EntityDirectoryEntry>();
		DirectoryResult<EntityDirectoryEntry> directoryResult = null;
		boolean atEnd = false;
		
		try {			
			String codingScheme = query.getRestrictions().getCodeSystemVersion().getName();			
			
			CodingSchemeVersionOrTag versionOrTag = null;
			LocalNameList entityTypes = new LocalNameList();
		//	entityTypes.addEntry("context"); 
		//	entityTypes.addEntry("instance");
			CodedNodeSet codeNodeSet = lexBigService.getNodeSet(codingScheme, versionOrTag, entityTypes);
			System.out.println("#_#_#_#_CodeNodeSet: " + codeNodeSet.toString());
			
			//Look at LexEVS test to see how to filter using following command
//			codeNodeSet.restrictToMatchingDesignations(matchText, preferredOnly, matchAlgorithm, language)
			
//			String matchText = "";
//			// PREFERRED_ONLY, NON_PREFERRED_ONLY, ALL
//			SearchDesignationOption preferredOnly = SearchDesignationOption.ALL;
//			String matchAlgorithm = "";
//			String language = "";
//			codeNodeSet.restrictToMatchingDesignations(matchText, preferredOnly, matchAlgorithm, language);
			
			
			SortOptionList sortOptions = null;
			LocalNameList propertyNames = null;
			PropertyType [] propertyTypes = null; 
			ResolvedConceptReferencesIterator iterator = codeNodeSet.resolve(sortOptions, propertyNames, propertyTypes);
			
			int start = page.getStart();
			int end = page.getEnd();
			System.out.println("Start: " + start + ", End: " + end);
			ResolvedConceptReferenceList resolvedConceptReferenceList = iterator.get(start, end);
			System.out.println("resolvedConcpetReferenceList: " + resolvedConceptReferenceList.getResolvedConceptReferenceCount());
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceList.getResolvedConceptReference();
			System.out.println("resolvedConceptReferences: " + resolvedConceptReferences.length);
			for(ResolvedConceptReference reference : resolvedConceptReferences){
				System.out.println("ResolvedConceptReference:\n" + resolvedConceptReference_toString(reference));
				EntityDirectoryEntry entry = entityTransform.transform(reference);
				
				list.add(entry);
			}

			if(!iterator.hasNext()){
				atEnd = true;
			}
			
			directoryResult = new DirectoryResult<EntityDirectoryEntry>(list, atEnd);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return directoryResult;
	}
	
	private String resolvedConceptReference_toString(ResolvedConceptReference reference){
		String results = "";
		
		results += "\t Code: " + reference.getCode() + "\n";
		results += "\t CodeNamespace: " + reference.getCodeNamespace() + "\n";
		results += "\t CodingSchemeName: " + reference.getCodingSchemeName() + "\n";
		results += "\t CodingSchemeURI: " + reference.getCodingSchemeURI() + "\n";
		results += "\t CodingSchemeVersion: " + reference.getCodingSchemeVersion() + "\n";
		results += "\t ConceptCode: " + reference.getConceptCode() + "\n";

		results += "\t Entities: \n";
		results += "\t\t " + entity_toString(reference.getEntity()) + "\n";
				
		results += "\t Entity: " + reference.getEntity().getEntityCode() + "\n";
		results += "\t EntityDescription: " + reference.getEntityDescription().getContent() + "\n";
		results += "\t SourceOf: " + reference.getSourceOf() + "\n";
		results += "\t TargetOf: " + reference.getTargetOf() + "\n";
		
		return results;
	}
	
	private String entity_toString(Entity entity){
		String results = "EntityCode = " + entity.getEntityCode() + ", ";
		results += "EntityCodeNamespace = " + entity.getEntityCodeNamespace() + ", ";
		results += "Owner = " + entity.getOwner() + ", ";
		results += "Status = " + entity.getStatus() + "\n";

		results += "\t\t EntityTypeCount = " + entity.getEntityTypeCount() + ", ";
		results += "EntityTypes = " + entityTypes_toString(entity) + "\n";
		
		results += "\t\t CommentCount = " + entity.getCommentCount() + ", ";
		results += "Comments = " + comments_toString(entity) + "\n";
		
		results += "\t\t DefinitionCount = " + entity.getDefinitionCount() + ", ";
		results += "Definitions = " + definitions_toString(entity) + "\n";
		
		return results;
	}
	
	private String definitions_toString(Entity entity){
		String results = "";
		int definitionCount = entity.getDefinitionCount();
		for(int i=0; i < definitionCount; i++){
			results += entity.getDefinition(i) + ", ";
		}
		return results;
	}
	
	private String comments_toString(Entity entity){
		String results = "";
		int commentCount = entity.getCommentCount();
		for(int i=0; i < commentCount; i++){
			results += entity.getComment(i) + ", ";
		}
		return results;
	}

	private String entityTypes_toString(Entity entity){
		String results = "";
		int typeCount = entity.getEntityTypeCount();
		for(int i=0; i < typeCount; i++){
			results += entity.getEntityType(i) + ", ";
		}
		return results;
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		// TODO Auto-generated method stub
		return null;
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
	public String getServiceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OpaqueData getServiceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SourceReference getServiceProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEntityInSet(EntityNameOrURI entity, Query query,
			Set<ResolvedFilter> filterComponent,
			EntityDescriptionQuery restrictions, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityReferenceList resolveAsEntityReferenceList(Query query,
			Set<ResolvedFilter> filterComponent,
			EntityDescriptionQuery restrictions, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityNameOrURIList intersectEntityList(
			Set<EntityNameOrURI> entities, Query query,
			Set<ResolvedFilter> filterComponent,
			EntityDescriptionQuery restrictions, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends VersionTagReference> getSupportedTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
