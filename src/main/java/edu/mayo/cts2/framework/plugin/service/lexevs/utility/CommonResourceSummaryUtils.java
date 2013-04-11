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
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBRuntimeException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.LexEvsToCTS2Transformer;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQuery;

public class CommonResourceSummaryUtils{
	private final static String UNCHECKED = "unchecked";
	private final static String RAWTYPES = "rawtypes";
	
	@SuppressWarnings({ RAWTYPES, UNCHECKED })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResultWithEntryDescriptions(
			LexEvsToCTS2Transformer transformer,
			ResolvedConceptReferenceResults resolvedConceptReferenceResults,
			String descriptionType) {
		
		List<EntryType> list = new ArrayList<EntryType>();
		DirectoryResult<EntryType> directoryResult = new DirectoryResult<EntryType>(list, true);
		EntryType entry = null;
		if(resolvedConceptReferenceResults != null){
			ResolvedConceptReference[] resolvedConceptReferences = resolvedConceptReferenceResults.getResolvedConceptReference();
			if(resolvedConceptReferences != null){
				for(ResolvedConceptReference reference : resolvedConceptReferences){
					if(descriptionType.equals(Constants.FULL_DESCRIPTION)){
						entry = (EntryType) transformer.transformFullDescription(reference);
					}
					else if(descriptionType.equals(Constants.SUMMARY_DESCRIPTION)){
						entry = (EntryType) transformer.transformSummaryDescription(reference);
					}
					list.add(entry);
				}
			}			
			directoryResult = new DirectoryResult<EntryType>(list, resolvedConceptReferenceResults.isAtEnd());
		}

		return directoryResult;
	}	

	@SuppressWarnings({ UNCHECKED, RAWTYPES })
	public static <EntryType, DataType> DirectoryResult<EntryType> createDirectoryResultWithEntryDescriptions(
			LexEvsToCTS2Transformer transformer,
			DataType[] dataCollection, 
			boolean atEnd,
			String descriptionType) {
		List<EntryType> list = new ArrayList<EntryType>();
		DirectoryResult<EntryType> directoryResult = new DirectoryResult<EntryType>(list, true);
		EntryType entry = null;
		
		if(dataCollection != null){
			for (DataType data : dataCollection) {
				if(descriptionType.equals(Constants.FULL_DESCRIPTION)){
					entry = (EntryType) transformer.transformFullDescription(data);
				}
				else if(descriptionType.equals(Constants.SUMMARY_DESCRIPTION)){
					entry = (EntryType) transformer.transformSummaryDescription(data);
				}
				
				list.add(entry);
			}
			directoryResult = new DirectoryResult<EntryType>(list, atEnd);
		}
		
		return directoryResult;
	}
	
	@SuppressWarnings({ RAWTYPES, UNCHECKED })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResultWithEntryFullVersionDescriptions(
			LexBIGService lexBigService, 
			LexEvsToCTS2Transformer transformer, 
			CodingSchemeRendering[] csRendering, 
			boolean atEnd2){
		List<EntryType> list = new ArrayList<EntryType>();
		boolean atEnd = true;
		DirectoryResult<EntryType> directoryResult = new DirectoryResult<EntryType>(list, atEnd);
		
		if(csRendering != null){
			for (CodingSchemeRendering render : csRendering) {
				String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
				String version = render.getCodingSchemeSummary().getRepresentsVersion();
				CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
				CodingScheme codingScheme;
				try {
					codingScheme = lexBigService.resolveCodingScheme(codingSchemeName, tagOrVersion);
					list.add((EntryType) transformer.transformFullDescription(codingScheme));
				} catch (LBException e) {
					throw new RuntimeException(e);
				}
			}
			directoryResult = new DirectoryResult<EntryType>(list, atEnd);
		}
		
		return directoryResult;
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
					codedNodeSet = lexBigService.getNodeSet(queryData.getNameVersionPairName(), queryData.getVersionOrTag() , entityTypes);
				}
			} catch (LBException e) {
				throw new LBRuntimeException(e.getMessage());
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
		
			CodingSchemeRenderingList renderingList;
			try {
				renderingList = lexBigService.getSupportedCodingSchemes();
			
				renderingList = CommonSearchFilterUtils.filterIfMappingExtensionValid(mappingExtension, renderingList);
				renderingList = CommonSearchFilterUtils.filterIfCodingSchemeNameValid(queryData.getNameVersionPairName(), renderingList);
			
				if (CommonUtils.hasCodingSchemeRenderings(queryData, renderingList)){ 
					Iterator<ResolvedFilter> filtersItr = queryData.getFilters().iterator();
					while (filtersItr.hasNext() && (renderingList.getCodingSchemeRenderingCount() > 0)) {
							ResolvedFilter resolvedFilter = filtersItr.next();
							renderingList = CommonSearchFilterUtils.filterRenderingListByResolvedFilter(resolvedFilter, 
									renderingList, nameConverter);
					}
				}
							
				return renderingList;
			} catch (LBInvocationException e) {
				throw new LBRuntimeException(e.getMessage());
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

	public static ResolvedConceptReferenceResults getMapReferenceResults(
			MapEntryQuery query, SortCriteria sortCriteria, Page page,
			VersionNameConverter nameConverter, MappingExtension mappingExtension) {
		
		ResolvedConceptReferencesIterator iterator;
		QueryData<MapEntryQuery> queryData;

		queryData = new QueryData<MapEntryQuery>(query, nameConverter);
		
		String codingScheme = queryData.getCodeSystemVersionName();
		CodingSchemeVersionOrTag versionOrTag = queryData.getVersionOrTag();
		String relationsContainerName = null;
		List<MappingSortOption> sortOptionList = null;
		
		try {
			iterator = mappingExtension.resolveMapping(codingScheme, versionOrTag, relationsContainerName, sortOptionList);
		} catch (LBParameterException e) {
			throw new LBRuntimeException(e.getMessage());
		}
		
		return CommonPageUtils.getPageFromIterator(iterator, page);	
	}

}
