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

	private Constants(){
		super();
	}
}
