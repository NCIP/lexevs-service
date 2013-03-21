package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Utility.Constructors;

import edu.mayo.cts2.framework.model.core.VersionTagReference;

public final class Constants {
	
	private static final String CURRENT_TAG_TEXT = "CURRENT";
	
	public static final VersionTagReference CURRENT_TAG = new VersionTagReference(CURRENT_TAG_TEXT);
	
	private static final String CURRENT_LEXEVS_TAG_TEXT = "PRODUCTION";
	
	public static final CodingSchemeVersionOrTag CURRENT_LEXEVS_TAG = 
		Constructors.createCodingSchemeVersionOrTagFromTag(CURRENT_LEXEVS_TAG_TEXT);
	
	// MatchAlgorithmReference type searches
	public final static String SEARCH_TYPE_CONTAINS = "contains";
	public final static String SEARCH_TYPE_EXACT_MATCH = "exactMatch";
	public final static String SEARCH_TYPE_STARTS_WITH = "startsWith";

	// PropertyReference search attributes
	public final static String ATTRIBUTE_NAME_ABOUT = "about";
	public final static String ATTRIBUTE_NAME_RESOURCE_SYNOPSIS = "resourceSynopsis";
	public final static String ATTRIBUTE_NAME_RESOURCE_NAME = "resourceName";

	
	private Constants(){
		super();
	}
}
