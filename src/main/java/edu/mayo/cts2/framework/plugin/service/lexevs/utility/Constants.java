/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Utility.Constructors;

import edu.mayo.cts2.framework.model.core.VersionTagReference;

public final class Constants {
	
	public static final String CURRENT_TAG_TEXT = "CURRENT";
	
	public static final VersionTagReference CURRENT_TAG = new VersionTagReference(CURRENT_TAG_TEXT);
	
	private static final String CURRENT_LEXEVS_TAG_TEXT = "PRODUCTION";
	
	public static final CodingSchemeVersionOrTag CURRENT_LEXEVS_TAG = 
		Constructors.createCodingSchemeVersionOrTagFromTag(CURRENT_LEXEVS_TAG_TEXT);
	
	// MatchAlgorithmReference type searches
	public static final String SEARCH_TYPE_CONTAINS = "contains";
	public static final String SEARCH_TYPE_EXACT_MATCH = "exactMatch";
	public static final String SEARCH_TYPE_STARTS_WITH = "startsWith";

	// PropertyReference search attributes
	public static final String ATTRIBUTE_NAME_ABOUT = "about";
	public static final String ATTRIBUTE_NAME_RESOURCE_SYNOPSIS = "resourceSynopsis";
	public static final String ATTRIBUTE_NAME_RESOURCE_NAME = "resourceName";
	public static final Object ATTRIBUTE_TAG = "tag";	

	// MapRole values
	public static final String MAP_TO_ROLE = "MAP_TO_ROLE"; // represents target mapping
	public static final String MAP_FROM_ROLE = "MAP_FROM_ROLE"; // represents source mapping
	public static final String BOTH_MAP_ROLES = "BOTH_MAP_ROLES"; // represents source or target mapping
		
	public static final String MAPPING_EXTENSION = "MappingExtension";

	public static final String FULL_DESCRIPTION = "FULL_DESCRIPTIONS";
	public static final String SUMMARY_DESCRIPTION = "SUMMARY_DESCRIPTIONS";
	
	private Constants(){
		super();
	}
}
