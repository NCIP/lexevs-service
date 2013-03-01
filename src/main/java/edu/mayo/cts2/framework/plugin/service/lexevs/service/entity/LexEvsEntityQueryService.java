package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
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
			CodedNodeSet codeNodeSet = lexBigService.getNodeSet(query.getRestrictions().getCodeSystemVersion().getName(), null, null);
			//Look at LexEVS test to see how to filter using following command
//			codeNodeSet.restrictToMatchingDesignations(matchText, preferredOnly, matchAlgorithm, language)
			ResolvedConceptReferencesIterator iterator = codeNodeSet.resolve(null, null, null);
			for(ResolvedConceptReference reference : iterator.get(page.getStart(),  page.getEnd()).getResolvedConceptReference()){
				list.add(entityTransform.transform(reference));
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
