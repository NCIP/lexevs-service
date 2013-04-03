package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
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
			results = CommonResourceSummaryUtils.getResolvedConceptReferenceResults(codedNodeSet, sortCriteria, page);
		}
		
		return results;
	}

	public static ResolvedConceptReferenceResults getResolvedConceptReferenceResults(
			CodedNodeSet codedNodeSet, 
			SortCriteria sortCriteria, 
			Page page){
		boolean atEnd = false;
		ResolvedConceptReference[] resolvedConceptReferences = null;
		ResolvedConceptReferencesIterator iterator;
		ResolvedConceptReferenceList resolvedConceptReferenceList = null;
		int start = 0, end = 0;
		try {
			iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, sortCriteria);
			
			if(iterator != null){
				// Get on requested "page" of entities.  
				// In this case we can get the "page" from the iterator, unlike in LexEvsCodeSystemVersionQueryService.
				start = page.getStart();
				end = page.getEnd();
				if(end > iterator.numberRemaining()){
					end = iterator.numberRemaining();
					atEnd = true;				
				}
				resolvedConceptReferenceList = iterator.get(start, end);
				// Get array of resolved concept references
				
				if(resolvedConceptReferenceList != null){
					resolvedConceptReferences = resolvedConceptReferenceList.getResolvedConceptReference();
//					if(printObjects){
//						System.out.println("resolvedConceptReferences: " + resolvedConceptReferences.length);
//					}
				}	
			}
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBResourceUnavailableException e) {
			throw new RuntimeException(e);
		}
		
		return new ResolvedConceptReferenceResults(resolvedConceptReferences, atEnd);
	}
	
	
	
	public static <T extends ResourceQuery> CodedNodeSet getCodedNodeSet(
			LexBIGService lexBigService, 
			QueryData<T> queryData,
			SortCriteria sortCriteria){
		CodedNodeSet codedNodeSet = null;
		
		if(queryData.hasNameAndVersion()){
			boolean found = false;
			
			try {
				// Get Code Node Set from LexBIG service for given coding scheme
				LocalNameList entityTypes = new LocalNameList();
				CodingSchemeRenderingList codingSchemeRenderingList = lexBigService.getSupportedCodingSchemes();
				int count = codingSchemeRenderingList.getCodingSchemeRenderingCount();
				for(int i=0; i < count; i++){
					CodingSchemeSummary codingSchemeSummary = codingSchemeRenderingList.getCodingSchemeRendering(i).getCodingSchemeSummary();
//					if(printObjects){
//						System.out.println("CodingSchemeRendering: ");
//						System.out.println(PrintUtility.codingSchemeSummary(codingSchemeSummary, 1));
//					}
					// TODO: not certain this is correct
					String codingSchemeSummaryName = codingSchemeSummary.getLocalName();
					String codingSchemeSummaryVersion = codingSchemeSummary.getRepresentsVersion();
					if(codingSchemeSummaryName.equals(queryData.getCodingSchemeName()) && 
							codingSchemeSummaryVersion.equals(queryData.getVersionOrTag().getVersion())){
						found = true;
					}
				}
				
				
				if(found){
					codedNodeSet = lexBigService.getNodeSet(queryData.getCodingSchemeName(), queryData.getVersionOrTag() , entityTypes);
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
				
			Set<ResolvedFilter> filters = queryData.getFilters();
			if(found && (filters != null)){
				for(ResolvedFilter filter : filters){
					CommonSearchFilterUtils.filterCodedNodeSetByResolvedFilter(filter, codedNodeSet);
				}
			}
		}
	
		
		return codedNodeSet;
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
			codingSchemeList = CommonSearchFilterUtils.filterByCodeSystemRestriction(lexBigService, csrFilteredList, queryData.getCodeSystemRestriction());
			resolvedToCodingSchemeFlag = true;
		}
		
		if (!resolvedToCodingSchemeFlag) {
			codingSchemeList = CommonCodingSchemeUtils.getCodingSchemeListFromCodingSchemeRenderings(lexBigService, csrFilteredList.getCodingSchemeRendering());
		}
					
		return codingSchemeList;
	}
	
	public static <T extends ResourceQuery> CodingSchemeRenderingList getCodingSchemeRenderingList(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter,
			MappingExtension mappingExtension,
			QueryData<T> queryData,
			SortCriteria sortCriteria) {
		try {
			CodingSchemeRenderingList csrFilteredList = lexBigService.getSupportedCodingSchemes();
			
			// Remove any items in above returned list that are not LexEVS MappingCodeScheme type CodeSchemes 
			if(queryData.isMapQuery()){
				csrFilteredList = CommonSearchFilterUtils.filterByMappingCodingSchemes(csrFilteredList, mappingExtension);
			}
			
			if (queryData.getCodingSchemeName() != null) {
				csrFilteredList = CommonSearchFilterUtils.filterResourceSummariesByCodingSchemeName(queryData.getCodingSchemeName(), csrFilteredList);
			}
			
			if (CommonResourceSummaryUtils.hasCodingSchemeRenderings(queryData, csrFilteredList)){ 
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
