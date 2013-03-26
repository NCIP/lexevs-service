package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;

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
			return searchExactMatch(sourceValue, searchValue, caseSensitive);
		} else if (searchType.equals(Constants.SEARCH_TYPE_CONTAINS)) {
			return searchContains(sourceValue, searchValue, caseSensitive);
		} else if (searchType.equals(Constants.SEARCH_TYPE_STARTS_WITH)) {
			return searchStartsWith(sourceValue, searchValue, caseSensitive);
		}  
		
		return false;
	}
	
	public static boolean searchContains(String sourceValue, String searchValue, boolean caseSensitive) {
		if (caseSensitive) {
			if (sourceValue.indexOf(searchValue) != -1) {
				return true;
			}
		} else {
			if (sourceValue.toLowerCase().indexOf(searchValue.toLowerCase()) != -1) {
				return true;
			}						
		}
		return false;
	}

	public static boolean searchExactMatch(String sourceValue, String searchValue, boolean caseSensitive) {
		if (caseSensitive) {
			if (sourceValue.equals(searchValue)) {
				return true;
			}
		} else {
			if (sourceValue.equalsIgnoreCase(searchValue)) {
				return true;
			}						
		}
		return false;
	}


	public static boolean searchStartsWith(String sourceValue, String searchValue, boolean caseSensitive) {
		if (caseSensitive) {
			if (sourceValue.startsWith(searchValue)) {
				return true;
			}
		} else {
			if (sourceValue.toLowerCase().startsWith(searchValue.toLowerCase())) {
				return true;
			}						
		}
		return false;
	}

}
