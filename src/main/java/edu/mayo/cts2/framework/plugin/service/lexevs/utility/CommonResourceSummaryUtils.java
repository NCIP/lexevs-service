package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.CodingSchemeToCodeSystemTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.EntityTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.map.CodingSchemeToMapTransform;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public class CommonResourceSummaryUtils{
	
	public static <T extends ResourceQuery> boolean hasCodingSchemeRenderings(QueryData<T> queryData, CodingSchemeRenderingList csrFilteredList){
		boolean answer = false;
		if((queryData.getFilters() != null) && (csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)){
			answer = true;
		}
		return answer;
	}
	
	public static DirectoryResult<MapCatalogEntry> createDirectoryResultWithEntryData(
			LexBIGService lexBigService,
			CodingSchemeToMapTransform transformer,
			CodingScheme[] codingSchemes, boolean atEnd) {
		List<MapCatalogEntry> list = new ArrayList<MapCatalogEntry>();

		if(codingSchemes != null){
			for (CodingScheme codingScheme : codingSchemes) {
				list.add(transformer.transformToMapCatalogEntry(codingScheme));
			}
		}
		
		return new DirectoryResult<MapCatalogEntry>(list, atEnd);
	}

	public static DirectoryResult<MapCatalogEntrySummary> createDirectoryResultWithEntrySummaryData(
			LexBIGService lexBigService,
			CodingSchemeToMapTransform transformer,
			CodingScheme[] codingSchemes, boolean atEnd) {
		List<MapCatalogEntrySummary> list = new ArrayList<MapCatalogEntrySummary>();

		if(codingSchemes != null){
			for (CodingScheme codingScheme : codingSchemes) {
				list.add(transformer.transformToMapCatalogEntrySummary(codingScheme));
			}
		}
		
		return new DirectoryResult<MapCatalogEntrySummary>(list, atEnd);
	}
	
	
	
	public static DirectoryResult<CodeSystemVersionCatalogEntrySummary> createDirectoryResultWithEntrySummaryData(
			LexBIGService lexBigService,
			CodingSchemeToCodeSystemTransform transformer,
			CodingSchemeRendering[] csRendering, boolean atEnd) {
		List<CodeSystemVersionCatalogEntrySummary> list = new ArrayList<CodeSystemVersionCatalogEntrySummary>();

		if(csRendering != null){
			for (CodingSchemeRendering render : csRendering) {
				list.add(transformer.transform(render));
			}
		}
		
		return new DirectoryResult<CodeSystemVersionCatalogEntrySummary>(list, atEnd);
	}


	public static <Query extends ResourceQuery> CodingSchemeRendering[] getCodingSchemeRendering(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter, 
			Query query, 
			MappingExtension mappingExtension,
			SortCriteria sortCriteria){
		
		QueryData<Query> queryData = new QueryData<Query>(query);
		CodingSchemeRenderingList csrFilteredList;
		csrFilteredList = CommonResourceSummaryUtils.getCodingSchemeRenderingList(lexBigService, nameConverter, mappingExtension, queryData, sortCriteria);
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();

		return csRendering;
	}
	
	public static <Entry extends edu.mayo.cts2.framework.model.core.ResourceDescription> DirectoryResult<Entry> createDirectoryResultWithEntryData(
			LexBIGService lexBigService, 
			CodingSchemeToCodeSystemTransform transformer, 
			CodingSchemeRendering[] csRendering, boolean atEnd2){
		
		
//		List<CodeSystemVersionCatalogEntry> list = new ArrayList<CodeSystemVersionCatalogEntry>();
		List<Entry> list = new ArrayList<Entry>();
		boolean atEnd = true;
		if(csRendering != null){
			for (CodingSchemeRendering render : csRendering) {
				String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
				String version = render.getCodingSchemeSummary().getRepresentsVersion();
				CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
				CodingScheme codingScheme;
				try {
					codingScheme = lexBigService.resolveCodingScheme(codingSchemeName, tagOrVersion);
					list.add((Entry) transformer.transform(codingScheme));
				} catch (LBException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return new DirectoryResult<Entry>(list, atEnd);
	}

	
	public static <T extends ResourceQuery> ResolvedConceptReferenceResults getResolvedConceptReferenceResultsPage(
			LexBIGService lexBigService, 
			QueryData<T> queryData,
			SortCriteria sortCriteria, 
			Page page){
		ResolvedConceptReferenceResults results = null;
		ResolvedConceptReferencesIterator iterator;
		CodedNodeSet codedNodeSet;
		
		codedNodeSet = CommonResourceSummaryUtils.getCodedNodeSet(lexBigService, queryData, sortCriteria);
		if(codedNodeSet != null){
			iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, sortCriteria);
			results = CommonUtils.getPageFromIterator(iterator, page);
		}
		
		return results;
	}

	public static <T extends ResourceQuery> CodedNodeSet getCodedNodeSet(
			LexBIGService lexBigService, 
			QueryData<T> queryData,
			SortCriteria sortCriteria){
		CodedNodeSet codedNodeSet = null;
		boolean containsData = false;
		if(queryData.hasNameAndVersion()){
			try {
				// Get Code Node Set from LexBIG service for given coding scheme
				LocalNameList entityTypes = new LocalNameList();
				CodingSchemeRenderingList codingSchemeRenderingList = lexBigService.getSupportedCodingSchemes();
				containsData = queryReturnsData(queryData, codingSchemeRenderingList);			
				if(containsData){
					codedNodeSet = lexBigService.getNodeSet(queryData.getCodingSchemeName(), queryData.getVersionOrTag() , entityTypes);
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
			Set<ResolvedFilter> filters = queryData.getFilters();
			if(containsData && (filters != null)){
				for(ResolvedFilter filter : filters){
					CommonSearchFilterUtils.filterCodedNodeSetByResolvedFilter(filter, codedNodeSet);
				}
			}
		}
		
		return codedNodeSet;
	}
	
	private static <T extends ResourceQuery> boolean queryReturnsData(
			QueryData<T> queryData,
			CodingSchemeRenderingList codingSchemeRenderingList){
		boolean found = false;
		String localName, version;
		int count = codingSchemeRenderingList.getCodingSchemeRenderingCount();
		for(int index=0; index < count; index++){
			CodingSchemeSummary codingSchemeSummary;
			codingSchemeSummary = codingSchemeRenderingList.getCodingSchemeRendering(index).getCodingSchemeSummary();
			localName = codingSchemeSummary.getLocalName();
			version = codingSchemeSummary.getRepresentsVersion();
			if(localName.equals(queryData.getCodingSchemeName()) && 
				version.equals(queryData.getVersionOrTag().getVersion())){
				found = true;
			}
		}		
			
		return found;
	}

	public static <T extends ResourceQuery> CodingSchemeRenderingList getCodingSchemeRenderingList(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter,
			MappingExtension mappingExtension,
			QueryData<T> queryData,
			SortCriteria sortCriteria) {
		try {
			CodingSchemeRenderingList renderingList = lexBigService.getSupportedCodingSchemes();
			
			renderingList = CommonSearchFilterUtils.filterIfMappingExtensionValid(mappingExtension, renderingList);
			renderingList = CommonSearchFilterUtils.filterIfCodingSchemeNameValid(queryData.getCodingSchemeName(), renderingList);
			
			if (CommonResourceSummaryUtils.hasCodingSchemeRenderings(queryData, renderingList)){ 
				Iterator<ResolvedFilter> filtersItr = queryData.getFilters().iterator();
				while (filtersItr.hasNext() && (renderingList.getCodingSchemeRenderingCount() > 0)) {
						ResolvedFilter resolvedFilter = filtersItr.next();
						renderingList = CommonSearchFilterUtils.filterRenderingListByResolvedFilter(resolvedFilter, 
								renderingList, nameConverter);
				}
			}
						
			return renderingList;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	

	public static <T extends ResourceQuery> List<CodingScheme> getCodingSchemeList(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter,
			MappingExtension mappingExtension,
			QueryData<T> queryData,
			SortCriteria sortCriteria) {

		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();
		boolean resolvedToCodingSchemeFlag = false;
		
		CodingSchemeRenderingList csrFilteredList;
		csrFilteredList = getCodingSchemeRenderingList(lexBigService, nameConverter, mappingExtension, queryData, sortCriteria);

		if (queryData.getCodeSystemRestriction() != null) {
			codingSchemeList = CommonSearchFilterUtils.filterByRenderingListAndMappingExtension(lexBigService, csrFilteredList, queryData.getCodeSystemRestriction());
			resolvedToCodingSchemeFlag = true;
		}
		
		if (!resolvedToCodingSchemeFlag) {
			codingSchemeList = CommonCodingSchemeUtils.getCodingSchemeListFromCodingSchemeRenderings(lexBigService, csrFilteredList.getCodingSchemeRendering());
		}
					
		return codingSchemeList;
	}

	public static DirectoryResult<EntityDirectoryEntry> createDirectoryResultWithEntryData(
			LexBIGService lexBigService, EntityTransform transformer,
			ResolvedConceptReferenceResults resolvedConceptReferenceResults) {
		
		List<EntityDirectoryEntry> list = new ArrayList<EntityDirectoryEntry>();
		DirectoryResult<EntityDirectoryEntry> directoryResult = new DirectoryResult<EntityDirectoryEntry>(list, true);
		
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			if(resolvedConceptReferences != null){
				for(ResolvedConceptReference reference : resolvedConceptReferences){
					EntityDirectoryEntry entry = transformer.transformToEntry(reference);
					list.add(entry);
				}
			}			
			directoryResult = new DirectoryResult<EntityDirectoryEntry>(list, resolvedConceptReferenceResults.isAtEnd());
		}

		return directoryResult;
	}	

	public static DirectoryResult<EntityDescription> createDirectoryResultWithEntrySummaryData(
			LexBIGService lexBigService, EntityTransform transformer,
			ResolvedConceptReferenceResults resolvedConceptReferenceResults) {
		
		List<EntityDescription> list = new ArrayList<EntityDescription>();
		DirectoryResult<EntityDescription> directoryResult = new DirectoryResult<EntityDescription>(list, true);
		
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			if(resolvedConceptReferences != null){
				for(ResolvedConceptReference reference : resolvedConceptReferences){
					EntityDescription entry = transformer.transformToEntity(reference);
					list.add(entry);
				}
			}			
			directoryResult = new DirectoryResult<EntityDescription>(list, resolvedConceptReferenceResults.isAtEnd());
		}

		return directoryResult;
	}	
}
