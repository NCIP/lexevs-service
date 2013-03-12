package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import edu.mayo.cts2.framework.model.core.VersionTagReference;

public final class Constants {
	
	private static final String CURRENT_TAG_TEXT = "CURRENT";
	
	public static final VersionTagReference CURRENT_TAG = new VersionTagReference(CURRENT_TAG_TEXT);

	private Constants(){
		super();
	}
}
