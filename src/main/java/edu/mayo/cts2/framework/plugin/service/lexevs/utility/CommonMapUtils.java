package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;

public final class CommonMapUtils {
	private CommonMapUtils(){
		super();
	}
	
	public static boolean validateMappingCodingScheme(String cts2URI, String version, MappingExtension lexMappingExtension){
		try {
			if(lexMappingExtension != null){
				return lexMappingExtension.isMappingCodingScheme(cts2URI, 
							Constructors.createCodingSchemeVersionOrTagFromVersion(version));
			}
			else {
				return false;
			}
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}

}