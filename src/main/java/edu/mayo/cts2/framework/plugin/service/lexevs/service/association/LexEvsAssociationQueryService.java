package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.model.association.Association;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.association.GraphNode;
import edu.mayo.cts2.framework.model.association.types.GraphDirection;
import edu.mayo.cts2.framework.model.association.types.GraphFocus;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.StatementTarget;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.association.AssociationQuery;
import edu.mayo.cts2.framework.service.profile.association.AssociationQueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

@Component
public class LexEvsAssociationQueryService extends AbstractLexEvsService implements AssociationQueryService {

	private static class CodedNodeGraphCallback 
		implements Callback<CodedNodeGraph,AssociationDirectoryEntry> {

		@Override
		public DirectoryResult<AssociationDirectoryEntry> 
			execute(
				CodedNodeGraph state, 
				int start, 
				int maxResults) {
			
			return null;
		}

		@Override
		public int executeCount(CodedNodeGraph state) {
			return 0;
		}
		
	}
	
	
	@Override
	public DirectoryResult<AssociationDirectoryEntry> getResourceSummaries(
			AssociationQuery query, SortCriteria sortCriteria, Page page) {

		LexBIGService lbs = this.getLexBigService();

		String entityName = query.getRestrictions().getSourceEntity()
				.getEntityName().getName();

		String csvName = query.getRestrictions().getCodeSystemVersion()
				.getName();

		CodedNodeGraph cng;
		try {
			cng = lbs.getNodeGraph(csvName, null, null);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}

		// TODO Do we need to handle how the hard coded items below get managed in a dynamic manner?
		boolean resolveForward = true;
		boolean resolveBackward = false;
		int resolveCodedEntryDepth = 0;
		int resolveAssociationDepth = 1;
		LocalNameList propertyNames = null;
		PropertyType[] propertyTypes = null;
		SortOptionList sortOptions = null;
		
		int maxToReturn = -1;
		
		ResolvedConceptReferenceList rcrList;
		try {
			rcrList = cng.resolveAsList(
					Constructors.createConceptReference(entityName, null),
					resolveForward, resolveBackward, resolveCodedEntryDepth,
					resolveAssociationDepth, propertyNames, propertyTypes,
					sortOptions, maxToReturn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ResolvedConceptReference rcr = rcrList.getResolvedConceptReference(0);
		DirectoryResult<AssociationDirectoryEntry> directoryResult = null;
		List<AssociationDirectoryEntry> assnDirEntryList = new ArrayList<AssociationDirectoryEntry>();
		org.LexGrid.LexBIG.DataModel.Core.Association[] associations = rcr
				.getSourceOf().getAssociation();
		for (org.LexGrid.LexBIG.DataModel.Core.Association assoc : associations) {
			for (AssociatedConcept ac : assoc.getAssociatedConcepts()
					.getAssociatedConcept()) {

				AssociationDirectoryEntry entry = new AssociationDirectoryEntry();
				URIAndEntityName uriEntityName = new URIAndEntityName();
				uriEntityName.setName(ac.getCode());

				entry.setSubject(uriEntityName);

				PredicateReference predReference = new PredicateReference();
				predReference.setName(assoc.getAssociationName());

				entry.setPredicate(predReference);

				StatementTarget st = new StatementTarget();
				uriEntityName = new URIAndEntityName();
				uriEntityName.setName(ac.getCode());
				uriEntityName.setUri(ac.getCodeNamespace());
				st.setEntity(uriEntityName);
				entry.setTarget(st);

				assnDirEntryList.add(entry);
			}
		}

		// Limit results to return based on passed in Page settings
		List<AssociationDirectoryEntry> assnDirEntryAdjustedList = new ArrayList<AssociationDirectoryEntry>();
		boolean atEnd = false;
		int start = page.getStart();
		int end = page.getEnd();
		int i = 0;
		
		if ((start == 0) && ((end == assnDirEntryList.size()) || (end > assnDirEntryList.size()))) {
			i = assnDirEntryList.size();
			assnDirEntryAdjustedList = assnDirEntryList;
		} else {
			for(i = start; i < end && i < assnDirEntryList.size(); i++){
				assnDirEntryAdjustedList.add(assnDirEntryList.get(i));
			}
		}

		if(i == assnDirEntryList.size()){
			atEnd = true;
		}
		
		directoryResult = new DirectoryResult<AssociationDirectoryEntry>(
				assnDirEntryAdjustedList, atEnd);

		return directoryResult;
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
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
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
