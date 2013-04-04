package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;

public class CommonMapUtils {

	public static boolean validateMappingCodingScheme(String uri, String version, MappingExtension mappingExtension){
		try {
			if(mappingExtension != null){
				return mappingExtension.isMappingCodingScheme(uri, 
							Constructors.createCodingSchemeVersionOrTagFromVersion(version));
			}
			else {
				return false;
			}
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}

//	public static boolean validateMappingCodingScheme(MappingExtension mappingExtension, String uri, String version){
//		try {
//			if(mappingExtension != null){
//				return mappingExtension.
//					isMappingCodingScheme(
//							uri, 
//							Constructors.createCodingSchemeVersionOrTagFromVersion(version));
//			}
//			else {
//				return false;
//			}
//		} catch (LBParameterException e) {
//			throw new RuntimeException(e);
//		}
//	}
}
