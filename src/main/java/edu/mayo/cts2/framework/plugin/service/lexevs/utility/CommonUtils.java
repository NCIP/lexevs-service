package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;

public class CommonUtils {

//	public static <T> DirectoryResult<T> getSublist(List<T> list, Page page){
//		List<T> sublist = new ArrayList<T>();
//		boolean atEnd = false;
//		int start = page.getStart();
//		int end = page.getEnd();
//		int i = 0;
//		if ((start == 0) && ((end == list.size()) || (end > list.size()))) {
//			i = list.size();
//			sublist = list;
//		} else {
//			for (i = start; i < end && i < list.size(); i++) {
//				sublist.add(list.get(i));
//			}
//		}
//	
//		if (i == list.size()) {
//			atEnd = true;
//		}
//	
//		DirectoryResult<T> directoryResult = new DirectoryResult<T>(
//				sublist, atEnd);
//		
//		return directoryResult;
//	}

	public static CodingSchemeRendering[] getRenderingPage(CodingSchemeRendering[] csRendering, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
		CodingSchemeRendering [] csRenderingPage = null;
		
		if(end > csRendering.length){
			end = csRendering.length;
		}
		
		if ((start == 0) && (end == csRendering.length)) {
			csRenderingPage = csRendering;
		} 
		else if(start < end){
			
			int size = end - start;
			csRenderingPage = new CodingSchemeRendering [size];
			
			for (int i = 0; i < csRenderingPage.length; i++) {
				csRenderingPage[i] = csRendering[start + i];
			}
		}
	
		return csRenderingPage;
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
		String searchType = matchAlgorithmReference.getContent();
		
		PropertyReference propertyReference = resolvedFilter.getPropertyReference();
		String searchAttribute = propertyReference.getReferenceTarget().getName();
		
		String matchStr = resolvedFilter.getMatchValue();	
		String lowerCaseMatchStr = matchStr.toLowerCase(); // Assuming default Locale is ok to use
		
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
			if (retrievedAttrValue != null) {
				if (searchType.equals(Constants.SEARCH_TYPE_EXACT_MATCH)) {
					if (caseSensitive) {
						if (retrievedAttrValue.equals(matchStr)) {
							temp.addCodingSchemeRendering(render);
						}
					} else {
						if (retrievedAttrValue.equalsIgnoreCase(matchStr)) {
							temp.addCodingSchemeRendering(render);
						}						
					}
				} else if (searchType.equals(Constants.SEARCH_TYPE_CONTAINS)) {
					if (caseSensitive) {
						if (retrievedAttrValue.indexOf(matchStr) != -1) {
							temp.addCodingSchemeRendering(render);
						}
					} else {
						retrievedAttrValue = retrievedAttrValue.toLowerCase(); // Assuming default Locale is ok to use
						if (retrievedAttrValue.indexOf(lowerCaseMatchStr) != -1) {
							temp.addCodingSchemeRendering(render);
						}						
					}
				} else if (searchType.equals(Constants.SEARCH_TYPE_STARTS_WITH)) {
					if (caseSensitive) {
						if (retrievedAttrValue.startsWith(matchStr)) {
							temp.addCodingSchemeRendering(render);
						}
					} else {
						retrievedAttrValue = retrievedAttrValue.toLowerCase(); // Assuming default Locale is ok to use
						if (retrievedAttrValue.startsWith(lowerCaseMatchStr)) {
							temp.addCodingSchemeRendering(render);
						}						
					}
				}  
			} // end brace retrievedAttr != null
		}  // end brace for loop
		
		return temp;
	}

	
}
