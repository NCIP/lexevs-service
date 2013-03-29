package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
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

	public static CodingSchemeRenderingList filterByMappingCodingSchemes(CodingSchemeRenderingList csrFilteredList, 
			MappingExtension mappingExtension) {
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = csrFilteredList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			
			String uri = codingSchemeSummary.getCodingSchemeURI();
			String version = codingSchemeSummary.getRepresentsVersion();
			
			if (validateMappingCodingScheme(uri, version, mappingExtension)) {
				temp.addCodingSchemeRendering(render);
			}
		}		
		return temp;		
	}


	
}
