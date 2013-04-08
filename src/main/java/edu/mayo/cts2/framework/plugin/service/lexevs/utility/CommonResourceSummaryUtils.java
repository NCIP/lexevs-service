package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.LexEvsToCTS2Transformer;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public class CommonResourceSummaryUtils{
	
	/// create DirectoryResults with EntrySummaryData
	// ---------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <SummaryType, DataType> DirectoryResult<SummaryType> createDirectoryResultWithResolvedEntrySummaryData(
			LexBIGService lexBigService, 
			LexEvsToCTS2Transformer transformer,
			ResolvedConceptReferenceResults resolvedConceptReferenceResults) {
		
		List<SummaryType> list = new ArrayList<SummaryType>();
		DirectoryResult<SummaryType> directoryResult = new DirectoryResult<SummaryType>(list, true);
		
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			if(resolvedConceptReferences != null){
				for(ResolvedConceptReference reference : resolvedConceptReferences){
					SummaryType entry = (SummaryType) transformer.transformDescription(reference);
					list.add(entry);
				}
			}			
			directoryResult = new DirectoryResult<SummaryType>(list, resolvedConceptReferenceResults.isAtEnd());
		}

		return directoryResult;
	}	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <SummaryType, DataType> DirectoryResult<SummaryType> createDirectoryResultWithEntrySummaryData(
			LexBIGService lexBigService,
			LexEvsToCTS2Transformer transformer,
			DataType[] dataCollection, boolean atEnd) {
		List<SummaryType> list = new ArrayList<SummaryType>();

		if(dataCollection != null){
			for (DataType data : dataCollection) {
				list.add((SummaryType) transformer.transformDirectoryEntry(data));
			}
		}
		
		return new DirectoryResult<SummaryType>(list, atEnd);
	}
	
	
	/// create DirectoryResults with EntryData
	// ---------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResultWithEntryData(
			LexBIGService lexBigService,
			LexEvsToCTS2Transformer transformer,
			CodingScheme[] codingSchemes, boolean atEnd) {
		List<EntryType> list = new ArrayList<EntryType>();

		if(codingSchemes != null){
			for (CodingScheme codingScheme : codingSchemes) {
				list.add((EntryType) transformer.transformDescription(codingScheme));
			}
		}
		
		return new DirectoryResult<EntryType>(list, atEnd);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResultWithResolvedEntryData(
			LexBIGService lexBigService, 
			LexEvsToCTS2Transformer transformer,
			ResolvedConceptReferenceResults resolvedConceptReferenceResults) {
		
		List<EntryType> list = new ArrayList<EntryType>();
		DirectoryResult<EntryType> directoryResult = new DirectoryResult<EntryType>(list, true);
		
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			if(resolvedConceptReferences != null){
				for(ResolvedConceptReference reference : resolvedConceptReferences){
					EntryType entry = (EntryType) transformer.transformDirectoryEntry(reference);
					list.add(entry);
				}
			}			
			directoryResult = new DirectoryResult<EntryType>(list, resolvedConceptReferenceResults.isAtEnd());
		}

		return directoryResult;
	}	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResultWithRenderedEntryData(
			LexBIGService lexBigService, 
			LexEvsToCTS2Transformer transformer, 
			CodingSchemeRendering[] csRendering, boolean atEnd2){
		
		List<EntryType> list = new ArrayList<EntryType>();
		boolean atEnd = true;
		if(csRendering != null){
			for (CodingSchemeRendering render : csRendering) {
				String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
				String version = render.getCodingSchemeSummary().getRepresentsVersion();
				CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
				CodingScheme codingScheme;
				try {
					codingScheme = lexBigService.resolveCodingScheme(codingSchemeName, tagOrVersion);
					list.add((EntryType) transformer.transformDescription(codingScheme));
				} catch (LBException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return new DirectoryResult<EntryType>(list, atEnd);
	}

	
	// --------------------------------------------
	public static <T extends ResourceQuery> CodingSchemeRendering[] getCodingSchemeRendering(
			LexBIGService lexBigService, 
			VersionNameConverter nameConverter, 
			QueryData<T> queryData,
			MappingExtension mappingExtension,
			SortCriteria sortCriteria){
		
		CodingSchemeRenderingList csrFilteredList;
		csrFilteredList = CommonResourceSummaryUtils.getCodingSchemeRenderingList(lexBigService, nameConverter, mappingExtension, queryData, sortCriteria);
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();

		return csRendering;
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
				containsData = CommonUtils.queryReturnsData(queryData, codingSchemeRenderingList);			
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
	
	public static <T extends ResourceQuery> CodingSchemeRenderingList getCodingSchemeRenderingList(
			LexBIGService lexBigService, 
			VersionNameConverter nameConverter,
			MappingExtension mappingExtension,
			QueryData<T> queryData,
			SortCriteria sortCriteria) {
		try {
			CodingSchemeRenderingList renderingList = lexBigService.getSupportedCodingSchemes();
			
			renderingList = CommonSearchFilterUtils.filterIfMappingExtensionValid(mappingExtension, renderingList);
			renderingList = CommonSearchFilterUtils.filterIfCodingSchemeNameValid(queryData.getCodingSchemeName(), renderingList);
			
			if (CommonUtils.hasCodingSchemeRenderings(queryData, renderingList)){ 
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
			VersionNameConverter nameConverter,
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

}
