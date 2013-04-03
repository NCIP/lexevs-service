package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;

public class CommonSearchFilterUtils {

	private CommonSearchFilterUtils() {
		super();
	}
	
	/**
	 * Common filter routine needed for specialized CodingScheme name filtering that cannot leverage existing LexEVS filter extensions.
	 * 
	 * @param searchCodingSchemeName
	 * @param csrFilteredList
	 * @return
	 */
	public static CodingSchemeRenderingList filterResourceSummariesByCodingSchemeName(String searchCodingSchemeName, CodingSchemeRenderingList csrFilteredList) {
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			if (codingSchemeSummary.getLocalName().equals(searchCodingSchemeName)) {
				temp.addCodingSchemeRendering(render);
			}
		}
		
		return temp;
	}
	
	
	/**
	 * Common filter routine needed for specialized filtering that cannot leverage existing LexEVS filter extensions.
	 * 
	 * @param resolvedFilter
	 * @param csrFilteredList
	 * @param nameConverter
	 * @return
	 */
	public static CodingSchemeRenderingList filterResourceSummariesByResolvedFilter(ResolvedFilter resolvedFilter, 
			CodingSchemeRenderingList csrFilteredList,
			CodeSystemVersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		MatchAlgorithmReference matchAlgorithmReference = resolvedFilter.getMatchAlgorithmReference();
		
		PropertyReference propertyReference = resolvedFilter.getPropertyReference();
		String searchAttribute = propertyReference.getReferenceTarget().getName();
		
		String matchStr = resolvedFilter.getMatchValue();	
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for (CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			if(codingSchemeSummary == null){
				break;
			}
			String retrievedAttrValue = null;
			if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
				retrievedAttrValue = codingSchemeSummary.getCodingSchemeURI();
			} else if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
				retrievedAttrValue = codingSchemeSummary.getCodingSchemeDescription().getContent();
			} else if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
				retrievedAttrValue = 
					nameConverter.toCts2CodeSystemVersionName(
						codingSchemeSummary.getLocalName(), 
						codingSchemeSummary.getRepresentsVersion());
			}
			if ((retrievedAttrValue != null) && (matchStr != null) && 
				(isFoundForSearchUsingSearchType(retrievedAttrValue, matchStr, matchAlgorithmReference, caseSensitive))) {
					temp.addCodingSchemeRendering(render);
			} 
		}  
		
		return temp;
	}
	
	public static boolean isFoundForSearchUsingSearchType(String sourceValue, String searchValue, 
			MatchAlgorithmReference matchAlgorithmReference, boolean caseSensitive) {
		
		String searchType = matchAlgorithmReference.getContent();
		
		if (searchType.equals(Constants.SEARCH_TYPE_EXACT_MATCH)) {
			return CommonStringUtils.searchExactMatch(sourceValue, searchValue, caseSensitive);
		} else if (searchType.equals(Constants.SEARCH_TYPE_CONTAINS)) {
			return CommonStringUtils.searchContains(sourceValue, searchValue, caseSensitive);
		} else if (searchType.equals(Constants.SEARCH_TYPE_STARTS_WITH)) {
			return CommonStringUtils.searchStartsWith(sourceValue, searchValue, caseSensitive);
		}  
		
		return false;
	}

	public static void filterCodedNodeSetByResolvedFilter(ResolvedFilter filter, CodedNodeSet codedNodeSet){
		if(codedNodeSet != null){
			try {
				String matchText = null;
				String matchAlgorithm = null;
			
				if(filter != null){
					matchText = filter.getMatchValue();										// Value to search with 
					matchAlgorithm = filter.getMatchAlgorithmReference().getContent();		// Extract from filter the match algorithm to use
				}	
				SearchDesignationOption option = SearchDesignationOption.ALL;					// Other options: PREFERRED_ONLY, NON_PREFERRED_ONLY, ALL 
				String language = null;															// This field is not really used, uses default "en"
				
				codedNodeSet.restrictToMatchingDesignations(matchText, option, matchAlgorithm, language);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
		}
	}
	

	
	public static List<CodingScheme> filterByCodeSystemRestriction(LexBIGService lexBigService, CodingSchemeRenderingList csrFilteredList, 
			CodeSystemRestriction codeSystemRestriction) {

		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();

		MapRole codeSystemRestrictionMapRole = null;
		Set<NameOrURI> codeSystemSet = null;
		if (codeSystemRestriction != null) {
			codeSystemRestrictionMapRole = codeSystemRestriction.getMapRole();
			codeSystemSet = codeSystemRestriction.getCodeSystems();
		}
		
		String csrMapRoleValue = null;
		if (codeSystemRestrictionMapRole != null) {
			csrMapRoleValue = codeSystemRestrictionMapRole.value();
		}
		
		if (csrMapRoleValue != null && codeSystemSet != null && codeSystemSet.size() > 0) {
			// Get array of CodingSchemeRendering object and loop checking each item in array
			CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
			for (CodingSchemeRendering render : csRendering) {
				CodingScheme codingScheme = CommonUtils.getCodingSchemeForCodeSystemRestriction(lexBigService, render, codeSystemSet, csrMapRoleValue); 
				if (codingScheme != null) {
					codingSchemeList.add(codingScheme);
				}
			}			
		} 
		
		return codingSchemeList;		
	}
	
	public static CodingSchemeRenderingList filterByMappingCodingSchemes(CodingSchemeRenderingList csrFilteredList, 
			MappingExtension mappingExtension) {
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			
			String uri = codingSchemeSummary.getCodingSchemeURI();
			String version = codingSchemeSummary.getRepresentsVersion();
			
			if (CommonMapUtils.validateMappingCodingScheme(uri, version, mappingExtension)) {
				temp.addCodingSchemeRendering(render);
			}
		}		
		return temp;		
	}


	


}
