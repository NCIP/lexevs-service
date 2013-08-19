/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;

public final class CommonStringUtils {
	
	private CommonStringUtils(){
		super();
	}
	
	public static boolean compareStrings(String compareTo, String match1, String match2){
		boolean matches = false;
		if (compareTo != null){
			if ((compareTo.equals(match1) || compareTo.equals(match2))) {
				matches = true;
			}
		}
		
		return matches;
	}
	

	public static boolean executeMatchAlgorithm(
			String sourceValue, 
			String searchValue, 
			MatchAlgorithmReference matchAlgorithmReference, 
			boolean caseSensitive) {
		
		if(sourceValue == null || searchValue == null){
			return false;
		}
		
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
