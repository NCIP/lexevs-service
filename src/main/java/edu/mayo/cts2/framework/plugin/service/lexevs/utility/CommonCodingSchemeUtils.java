package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.Iterator;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.relations.Relations;

import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;

public class CommonCodingSchemeUtils {


	public static boolean containsCodingScheme(String nameOrUriOne, String nameOrUriTwo, Set<NameOrURI> codeSystemSet) {

		boolean returnFlag = false;
		Iterator<NameOrURI> iterator = codeSystemSet.iterator();
		while (iterator.hasNext() && returnFlag == false) {
			NameOrURI nameOrURI = iterator.next();
			if (nameOrURI.getName() != null){
				if ((nameOrURI.getName().equals(nameOrUriOne) || nameOrURI.getName().equals(nameOrUriTwo))) {
					returnFlag = true;
				}
			}
			else if (nameOrURI.getUri() != null){
				if(nameOrURI.getUri().equals(nameOrUriOne) || nameOrURI.getUri().equals(nameOrUriTwo)) {
					returnFlag = true;
				}
			}
		}
		return returnFlag;
	}
	
	public static CodingScheme getCodingSchemeFromCodingSchemeRendering(
			LexBIGService lexBigService, 
			CodingSchemeRendering codingSchemeRendering) {
		CodingScheme codingScheme = null;
		String codingSchemeName = null;
		String version = null;
		CodingSchemeVersionOrTag tagOrVersion = null;
		try {
			if(codingSchemeRendering != null){
				codingSchemeName = codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeURI();			
				version = codingSchemeRendering.getCodingSchemeSummary().getRepresentsVersion();
				tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);			
				codingScheme = lexBigService.resolveCodingScheme(codingSchemeName, tagOrVersion);
			}
			else{
				codingScheme = lexBigService.resolveCodingScheme(codingSchemeName, tagOrVersion);			
			}
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		return codingScheme;
	}
	
	public static CodingScheme getMappedCodingSchemeForCodeSystemRestriction(
			LexBIGService lexBigService, 
			CodingSchemeRendering codingSchemeRendering, 
			Set<NameOrURI> codeSystemSet, 
			String mapRoleValue) {

		CodingScheme notFoundCodingScheme = null;

		CodingScheme codingScheme = CommonCodingSchemeUtils.getCodingSchemeFromCodingSchemeRendering(lexBigService, codingSchemeRendering);
		
		// Assuming format of Map has only has 1 relations section/1 relations element in xml file
		if (codingScheme.getRelationsCount() != 1) {
			throw new UnsupportedOperationException("Invalid format for Map. Expecting only one metadata section for Relations.");
		}
		Relations relations = codingScheme.getRelations(0);
		String sourceCodingScheme = relations.getSourceCodingScheme();
		String targetCodingScheme = relations.getTargetCodingScheme();
		
		if (mapRoleValue.equals(Constants.MAP_TO_ROLE) && CommonCodingSchemeUtils.containsCodingScheme(targetCodingScheme, null, codeSystemSet)) {
			return codingScheme;
		}
		
		if (mapRoleValue.equals(Constants.MAP_FROM_ROLE) && CommonCodingSchemeUtils.containsCodingScheme(sourceCodingScheme, null, codeSystemSet)) { 
			return codingScheme;
		}
		
		if (mapRoleValue.equals(Constants.BOTH_MAP_ROLES) && 
				CommonCodingSchemeUtils.containsCodingScheme(targetCodingScheme, sourceCodingScheme, codeSystemSet)) {
			return codingScheme;
		}
		
		return notFoundCodingScheme;
	}


	public static CodingScheme getMappedCodingSchemeForEntitiesRestriction(
			LexBIGService lexBigService, CodingSchemeRendering render,
			Set<EntityNameOrURI> entitiesSet, String value) {

		
//		Iterator<EntityNameOrURI> iterator = entitiesSet.iterator();
//		while(iterator.hasNext()){
//			EntityNameOrURI entity = iterator.next();
//			entity.getEntityName();
//			entity.getUri();
//		}
		return null;
	}
}
