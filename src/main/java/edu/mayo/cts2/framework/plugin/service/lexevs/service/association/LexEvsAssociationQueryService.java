/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.filter.directory.DirectoryBuilder;
import edu.mayo.cts2.framework.model.association.Association;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.association.GraphNode;
import edu.mayo.cts2.framework.model.association.types.GraphDirection;
import edu.mayo.cts2.framework.model.association.types.GraphFocus;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.Property;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.association.ResolvedConceptReferenceAssociationPage.Direction;
import edu.mayo.cts2.framework.service.command.restriction.AssociationQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.association.AssociationQuery;
import edu.mayo.cts2.framework.service.profile.association.AssociationQueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

@Component
public class LexEvsAssociationQueryService extends AbstractLexEvsService implements AssociationQueryService {
	
	@Resource
	private AssociatedConceptToAssociationTransform transform;

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;
	
	private class CodedNodeGraphCallback 
		implements Callback<CodedNodeGraph,AssociationDirectoryEntry> {

		private ConceptReference focus;
		private Direction direction;
		
		private CodedNodeGraphCallback(ConceptReference focus, Direction direction){
			super();
			this.focus = focus;
			this.direction = direction;
		}
		
		@Override
		public DirectoryResult<AssociationDirectoryEntry> 
			execute(
				CodedNodeGraph state, 
				int start, 
				int maxResults) {
			
			boolean forward = this.direction.equals(Direction.SOURCEOF);
			boolean reverse = this.direction.equals(Direction.TARGETOF);
			
			try {
				ResolvedConceptReferenceList resultList = 
					state.resolveAsList(this.focus, forward, reverse, 0, 1, null, null, null, null, -1);
				
				if(resultList.getResolvedConceptReferenceCount() == 0){
					return new DirectoryResult<AssociationDirectoryEntry>(
							new ArrayList<AssociationDirectoryEntry>(), true);
				} 
				if(resultList.getResolvedConceptReferenceCount() > 1){
					throw new IllegalStateException("With a focus, this can never be more than 1.");
				}
	
				List<AssociationDirectoryEntry> results = 
					transform.transformSummaryDescription(
						new ResolvedConceptReferenceAssociationPage(
								this.direction, 
								start, 
								start + maxResults + 1, 
								resultList.getResolvedConceptReference(0)));
				
				boolean atEnd = true;
				if(results.size() == maxResults + 1){
					atEnd = false;
					results.remove(results.size() - 1);
				}
				
				return new DirectoryResult<AssociationDirectoryEntry>(results, atEnd);
				
			} catch (LBException e) {
				throw new RuntimeException(e);
			} 
		}

		@Override
		public int executeCount(CodedNodeGraph state) {
			return 0;
		}
		
	}
	
	protected <T> List<T> slice(Iterator<T> itr, int start, int end){
		List<T> returnList = new ArrayList<T>();
		int counter = 0;
		while(counter++ < start){
			itr.next();
		}
		while(itr.hasNext() && counter++ < end){
			returnList.add(itr.next());
		}
		
		return returnList;
	}
	
	@Override
	public DirectoryResult<AssociationDirectoryEntry> getResourceSummaries(
			AssociationQuery query, SortCriteria sortCriteria, Page page) {

		return getResourceSummaries(query, sortCriteria, page, null);
	}
	
	@Override
	public DirectoryResult<AssociationDirectoryEntry> getResourceSummaries(
			AssociationQuery query, SortCriteria sortCriteria, Page page, String uri) {

		if(! this.validateQuery(query)){
			throw new UnsupportedOperationException();
		}
		
		AssociationQueryServiceRestrictions restrictions = query.getRestrictions();
		
		NameOrURI codeSystemVersion = restrictions.getCodeSystemVersion();
		
		NameVersionPair versionName = this.nameConverter.fromCts2VersionName(codeSystemVersion.getName());
		
		EntityNameOrURI focus;
		Direction direction;
		if(restrictions.getSourceEntity() != null){
			direction = Direction.SOURCEOF;
			focus = restrictions.getSourceEntity();
		} else {
			direction = Direction.TARGETOF;
			focus = restrictions.getTargetEntity();
		}
		
		CodedNodeGraph initialState;
		try {
			initialState = 
				this.getLexBigService().getNodeGraph(
						versionName.getName(), 
						Constructors.createCodingSchemeVersionOrTagFromVersion(versionName.getVersion()), 
						null);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
		ConceptReference ref = 
			Constructors.createConceptReference(
				focus.getEntityName().getName(), 
				this.codingSchemeNameTranslator.translateToLexGrid(focus.getEntityName().getNamespace()), 
				null);
		
		DirectoryBuilder<AssociationDirectoryEntry> builder = 
			new CodedNodeGraphDirectoryBuilder(
				initialState, 
				new CodedNodeGraphCallback(ref, direction),
				null, 
				null).
				restrict(restrictions).
				addStart(page.getStart()).
				addMaxToReturn(page.getMaxToReturn());
		
		return builder.resolve();
	}
	
	protected boolean validateQuery(AssociationQuery query){
		return query != null && 
				query.getRestrictions() != null &&
				query.getRestrictions().getCodeSystemVersion() != null &&
				(query.getRestrictions().getSourceEntity() != null || 
					query.getRestrictions().getTargetEntity() != null);
	}

	@Override
	public DirectoryResult<Association> getResourceList(AssociationQuery query,
			SortCriteria sortCriteria, Page page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int count(AssociationQuery query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DirectoryResult<GraphNode> getAssociationGraph(GraphFocus focusType,
			EntityDescriptionReadId focusEntity, GraphDirection direction,
			long depth) {
		throw new UnsupportedOperationException();
	}

}

