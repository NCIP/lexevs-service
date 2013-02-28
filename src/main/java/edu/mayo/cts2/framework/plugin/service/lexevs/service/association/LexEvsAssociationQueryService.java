package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.model.association.Association;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.association.GraphNode;
import edu.mayo.cts2.framework.model.association.types.GraphDirection;
import edu.mayo.cts2.framework.model.association.types.GraphFocus;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.OpaqueData;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.SourceReference;
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
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	
	@Override
	public DirectoryResult<AssociationDirectoryEntry> getResourceSummaries(
			AssociationQuery query, 
			SortCriteria sortCriteria, 
			Page page) {
		
		LexBIGService lbs = this.getLexBigService();
		
		String csvName = 
			query.getRestrictions().getCodeSystemVersion().getName();
		
		CodedNodeGraph cng;
		try {
			cng = 
				lbs.getNodeGraph(csvName, null, null);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

	@Override
	public DirectoryResult<Association> getResourceList(AssociationQuery query,
			SortCriteria sortCriteria, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count(AssociationQuery query) {
		// TODO Auto-generated method stub
		return 0;
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
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectoryResult<GraphNode> getAssociationGraph(GraphFocus focusType,
			EntityDescriptionReadId focusEntity, GraphDirection direction,
			long depth) {
		// TODO Auto-generated method stub
		return null;
	}

}
