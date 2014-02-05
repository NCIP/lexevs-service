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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.DelegatingEntityQueryService.QueryType;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions.HierarchyRestriction;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions.HierarchyRestriction.HierarchyType;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

@Component
public class LexEvsAssociationEntityQueryService extends AbstractLexEvsService 
		implements DelegateEntityQueryService, InitializingBean {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private EntityTransform transformer;
	
	private LexBIGServiceConvenienceMethods lbscm;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.lbscm = (LexBIGServiceConvenienceMethods) 
				this.getLexBigService().getGenericExtension("LexBIGServiceConvenienceMethods");
	}
		
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
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEntityInSet(EntityNameOrURI nameOrUri,
			EntityDescriptionQuery query, ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}
	
	public <T> DirectoryResult<T> doQuery(
			EntityDescriptionQuery query, 
			SortCriteria sortCriteria, 
			TransformClosure<T> closure, 
			Page page) {
		NameVersionPair codesystemVersion = 
			this.nameConverter.fromCts2VersionName(
				query.getRestrictions().getCodeSystemVersions().iterator().next().getName());

		HierarchyRestriction restriction = query.getRestrictions().getHierarchyRestriction();
		
		if(restriction.getHierarchyType().equals(HierarchyType.CHILDREN)){
			AssociationList list;
			try {
				list = this.lbscm.getHierarchyLevelNext(
					codesystemVersion.getName(), 
					Constructors.createCodingSchemeVersionOrTagFromVersion(codesystemVersion.getVersion()), 
					null, 
					restriction.getEntity().getEntityName().getName(), 
					false, 
					null);
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
			
			List<T> transformedList = this.associationListToDirectoryEntry(
					list, 
					closure,
					page.getStart(), 
					page.getMaxToReturn() + 1);
			
			boolean atEnd = CommonPageUtils.adjustForOnExtraResult(transformedList, page.getMaxToReturn());
			
			return new DirectoryResult<T>(transformedList, atEnd);
		} else {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {	
		return this.doQuery(query, sortCriteria, this.entryClosure, page);
	}

	@Override
	public DirectoryResult<EntityListEntry> getResourceList(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		return this.doQuery(query, sortCriteria, this.listClosure, page);
	}

	private interface TransformClosure<T> {
		T transform(AssociatedConcept ac);
	}
	
	private TransformClosure<EntityListEntry> listClosure = new TransformClosure<EntityListEntry>(){
		@Override
		public EntityListEntry transform(AssociatedConcept ac) {
			return transformer.transformFullDescription(ac);
		}
	};
	
	private TransformClosure<EntityDirectoryEntry> entryClosure = new TransformClosure<EntityDirectoryEntry>(){
		@Override
		public EntityDirectoryEntry transform(AssociatedConcept ac) {
			return transformer.transformSummaryDescription(ac);
		}
	};
	
	protected <T> List<T> associationListToDirectoryEntry(
			AssociationList list, TransformClosure<T> closure, int start, int max){
		List<T> returnList = new ArrayList<T>();
		int counter = 0;
		
		if(list != null){
			for(Association association : list.getAssociation()){
				AssociatedConceptList conceptList = association.getAssociatedConcepts();
				if(conceptList != null){
					for(AssociatedConcept ac : conceptList.getAssociatedConcept()){
						if(counter >= start && returnList.size() < max){
							returnList.add(closure.transform(ac));
						}
						counter++;
					}
				}
			}
		}
		
		return returnList;
	}
	
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}
	
	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.getLexSupportedSearchReferences();
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
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return new HashSet<ComponentReference>();
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
	public boolean canHandle(EntityDescriptionQuery query, QueryType queryType) {
		return query.getRestrictions().getHierarchyRestriction() != null;
	}

	@Override
	public int getOrder() {
		return 2;
	}

}

