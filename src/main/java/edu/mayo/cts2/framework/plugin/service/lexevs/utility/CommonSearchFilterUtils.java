package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;

public class CommonSearchFilterUtils {

	private CommonSearchFilterUtils() {
		super();
	}
	
	/**
	 * Common filter routine needed for specialized filtering that cannot leverage existing LexEVS filter extensions.
	 * 
	 * @param resolvedFilter
	 * @param renderingList
	 * @param nameConverter
	 * @return
	 */
	public static CodingSchemeRenderingList filterRenderingListByResolvedFilter(ResolvedFilter resolvedFilter, 
			CodingSchemeRenderingList renderingList,
			CodeSystemVersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		MatchAlgorithmReference matchAlgorithmReference = resolvedFilter.getMatchAlgorithmReference();
		
		PropertyReference propertyReference = resolvedFilter.getPropertyReference();
		String searchAttribute = propertyReference.getReferenceTarget().getName();
		
		String matchStr = resolvedFilter.getMatchValue();	
		
		CodingSchemeRendering[] csRendering = renderingList.getCodingSchemeRendering();
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
			if ((retrievedAttrValue != null) && (matchStr != null) && 
				(CommonStringUtils.executeMatchAlgorithm(retrievedAttrValue, matchStr, matchAlgorithmReference, caseSensitive))) {
					temp.addCodingSchemeRendering(render);
			} 
		}  
		
		return temp;
	}
	
	public static void filterCodedNodeSetByResolvedFilter(ResolvedFilter filter, CodedNodeSet codedNodeSet){
		if(codedNodeSet != null){
			try {
				String matchText = null;
				String matchAlgorithm = null;
			
				if(filter != null){
					matchText = filter.getMatchValue();										// Value to search with 
					matchAlgorithm = filter.getMatchAlgorithmReference().getContent();		// Extract from filter the match algorithm to use
				}	
				SearchDesignationOption option = SearchDesignationOption.ALL;					// Other options: PREFERRED_ONLY, NON_PREFERRED_ONLY, ALL 
				String language = null;															// This field is not really used, uses default "en"
				
				codedNodeSet.restrictToMatchingDesignations(matchText, option, matchAlgorithm, language);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
		}
	}
	

	
	public static List<CodingScheme> filterByRenderingListAndMappingExtension(
			LexBIGService lexBigService, 
			CodingSchemeRenderingList renderingList, 
			CodeSystemRestriction mappingRestriction) {

		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();

		Set<NameOrURI> codeSystemSet = null;
		MapRole mapRole = null;
		CodingScheme codingScheme;
		
		if (mappingRestriction != null) {
			codeSystemSet = mappingRestriction.getCodeSystems();
			if(haveMapRoleAndCodeSystems(mappingRestriction, codeSystemSet)){
				mapRole = mappingRestriction.getMapRole();
				// Get array of CodingSchemeRendering object and loop checking each item in array
				CodingSchemeRendering[] csRendering = renderingList.getCodingSchemeRendering();
				for (CodingSchemeRendering render : csRendering) {
					codingScheme = CommonCodingSchemeUtils.getMappedCodingSchemeForCodeSystemRestriction(lexBigService, render, codeSystemSet, mapRole.value()); 
					if (codingScheme != null) {
						codingSchemeList.add(codingScheme);
					}			
				} 
			}
		}
		
		return codingSchemeList;		
	}
	
	public static boolean haveMapRoleAndCodeSystems(CodeSystemRestriction mappingRestriction, Set<NameOrURI> codeSystemSet){
		boolean answer = false;
		String mapRoleValue = null;
		MapRole mapRole = mappingRestriction.getMapRole();
		if (mapRole != null) {
			mapRoleValue = mapRole.value();
			if (mapRoleValue != null && codeSystemSet != null && codeSystemSet.size() > 0) {
				answer = true;
			}
		}
		return answer;
	}
	
	/**
	 * Common filter routine needed for specialized CodingScheme name filtering that cannot leverage existing LexEVS filter extensions.
	 * 
	 * @param codingSchemeName
	 * @param renderingList
	 * @return
	 */
	public static CodingSchemeRenderingList filterIfCodingSchemeNameValid(String codingSchemeName, CodingSchemeRenderingList renderingList) {
		if(codingSchemeName == null || renderingList == null){
			return renderingList;
		}
		
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = renderingList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			
			// Add if names match
			if (codingSchemeSummary.getLocalName().equals(codingSchemeName)) {
				temp.addCodingSchemeRendering(render);
			}
		}
		
		return temp;
	}
	
	public static CodingSchemeRenderingList filterIfMappingExtensionValid(
			MappingExtension mappingExtension,
			CodingSchemeRenderingList renderingList) {
		if(mappingExtension == null || renderingList == null){
			return renderingList;
		}
		
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] csRendering = renderingList.getCodingSchemeRendering();
		for(CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			
			// Add if valid Mapping Coding Scheme
			String uri = codingSchemeSummary.getCodingSchemeURI();
			String version = codingSchemeSummary.getRepresentsVersion();
			if (CommonMapUtils.validateMappingCodingScheme(uri, version, mappingExtension)) {
				temp.addCodingSchemeRendering(render);
			}
		}		
		return temp;		
	}


	


}
