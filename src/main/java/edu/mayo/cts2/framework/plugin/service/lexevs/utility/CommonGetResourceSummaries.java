package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;

public class CommonGetResourceSummaries {
	
	@SuppressWarnings("rawtypes")
	public static boolean hasCodingSchemeRenderings(QueryData queryData, CodingSchemeRenderingList csrFilteredList){
		boolean answer = false;
		if((queryData.getFilters() != null) && (csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)){
			answer = true;
		}
		return answer;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<CodingScheme> getCodingSchemeList(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter,
			MappingExtension mappingExtension,
			QueryData queryData,
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
			codingSchemeList = CommonSearchFilterUtils.filterByCodeSystemRestriction(lexBigService, csrFilteredList, queryData.getCodeSystemRestriction());
			resolvedToCodingSchemeFlag = true;
		}
		
		if (!resolvedToCodingSchemeFlag) {
			codingSchemeList = CommonUtils.resolveToCodingSchemeList(lexBigService, csrFilteredList.getCodingSchemeRendering());
		}
					
		return codingSchemeList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static CodingSchemeRenderingList getCodingSchemeRenderingList(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter,
			MappingExtension mappingExtension,
			QueryData queryData,
			SortCriteria sortCriteria) {
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			// Remove any items in above returned list that are not LexEVS MappingCodeScheme type CodeSchemes 
			if(queryData.isMapQuery()){
				csrFilteredList = CommonMapUtils.filterByMappingCodingSchemes(csrFilteredList, mappingExtension);
			}
			
			if (queryData.getCodingSchemeName() != null) {
				csrFilteredList = CommonSearchFilterUtils.filterResourceSummariesByCodingSchemeName(queryData.getCodingSchemeName(), csrFilteredList);
			}
			
			if (CommonGetResourceSummaries.hasCodingSchemeRenderings(queryData, csrFilteredList)){ 
				Iterator<ResolvedFilter> filtersItr = queryData.getFilters().iterator();
				while (filtersItr.hasNext() && (csrFilteredList.getCodingSchemeRenderingCount() > 0)) {
						ResolvedFilter resolvedFilter = filtersItr.next();
						csrFilteredList = CommonSearchFilterUtils.filterResourceSummariesByResolvedFilter(resolvedFilter, 
								csrFilteredList, nameConverter);
				}
			}
						
			return csrFilteredList;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
