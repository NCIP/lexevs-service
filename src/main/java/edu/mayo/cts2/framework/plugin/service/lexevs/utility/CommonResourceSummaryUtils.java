package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public class CommonResourceSummaryUtils{
	
	public static <T extends ResourceQuery> boolean hasCodingSchemeRenderings(QueryData<T> queryData, CodingSchemeRenderingList csrFilteredList){
		boolean answer = false;
		if((queryData.getFilters() != null) && (csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)){
			answer = true;
		}
		return answer;
	}

	public static <T extends ResourceQuery> ResolvedConceptReferenceResults getResolvedConceptReferenceResults(
			LexBIGService lexBigService, 
			QueryData<T> queryData,
			SortCriteria sortCriteria, 
			Page page){
		ResolvedConceptReferenceResults results = null;
		CodedNodeSet codedNodeSet;
		
		codedNodeSet = CommonResourceSummaryUtils.getCodedNodeSet(lexBigService, queryData, sortCriteria);
		if(codedNodeSet != null){
			results = CommonUtils.getReferenceResultPage(codedNodeSet, sortCriteria, page);
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
//				if(printObjects){
//					System.out.println("CodingSchemeRendering: ");
//					System.out.println(PrintUtility.codingSchemeSummary(codingSchemeSummary, 1));
//				}
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
		
//		QueryData<MapQuery> queryData = new QueryData<MapQuery>(query);
		CodingSchemeRenderingList csrFilteredList;
		csrFilteredList = getCodingSchemeRenderingList(lexBigService, nameConverter, mappingExtension, queryData, sortCriteria);

		// NOTE:  Logic requires the processing of CodeSystemRestrictions to be last in order to save on 
		//   the resolving to a list of CodingScheme objects.  Filter items based on the CodingScheme Relations 
		//   sourceCodingScheme and/or targetCodingScheme string values.
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
