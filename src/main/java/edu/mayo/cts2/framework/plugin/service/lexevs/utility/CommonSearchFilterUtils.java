package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.Entities;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions.EntitiesRestriction;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

public class CommonSearchFilterUtils {

	private CommonSearchFilterUtils() {
		super();
	}
	

	/**
	 * @return
	 */
	public static Set<MatchAlgorithmReference> createSupportedMatchAlgorithms() {

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		return new HashSet<MatchAlgorithmReference>(Arrays.asList(exactMatch,contains,startsWith));
	}

	public static Set<? extends PropertyReference> createSupportedSearchReferences() {
		
		PropertyReference name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();	
		PropertyReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		
		return new HashSet<PropertyReference>(Arrays.asList(name,about,description));
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
			VersionNameConverter nameConverter) {
		
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
					nameConverter.toCts2VersionName(
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
	

	public static List<CodingScheme> filterByRenderingList(
			LexBIGService lexBigService, 
			CodingSchemeRendering[] codingSchemeRenderings) {
		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();
		
		if (codingSchemeRenderings != null) {
			for (int i = 0; i < codingSchemeRenderings.length; i++) {
				CodingScheme codingScheme = CommonCodingSchemeUtils.getCodingSchemeFromCodingSchemeRendering(lexBigService, codingSchemeRenderings[i]);
				codingSchemeList.add(codingScheme);
			}
		}
		return codingSchemeList;
	}
	
	/**
	 * @param lexBigService
	 * @param entitiesRestriction
	 * @return
	 */
	public static List<CodingScheme> filterCodingSchemeListByEntitiesRestriction(
			List<CodingScheme> codingSchemeList, 
			EntitiesRestriction entitiesRestriction) {
		
		Set<EntityNameOrURI> entitiesSet = null;
		MapRole mapRole = null;
		CodingScheme codingScheme;
		
		if (entitiesRestriction != null) {
			entitiesSet = entitiesRestriction.getEntities();
			mapRole = entitiesRestriction.getMapRole();
			if(haveMapRoleAndSetNotEmpty(mapRole, entitiesSet)){
				for (CodingScheme scheme : codingSchemeList) {
					Entities entities = scheme.getEntities();
					// TODO: need to see if entity exists in given scheme?? if not remove scheme from codingSchemeList
				} 
			}
		}
		
		return codingSchemeList;		
	}
	
	public static List<CodingScheme> filterByRenderingListAndCodeSystemRestrictions(
			LexBIGService lexBigService, 
			CodingSchemeRendering[] codingSchemeRendering, 
			CodeSystemRestriction codeSystemRestriction) {

		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();

		Set<NameOrURI> codeSystemSet = null;
		MapRole mapRole = null;
		CodingScheme codingScheme;
		
		if (codeSystemRestriction != null) {
			codeSystemSet = codeSystemRestriction.getCodeSystems();
			mapRole = codeSystemRestriction.getMapRole();
			if(haveMapRoleAndSetNotEmpty(mapRole, codeSystemSet)){
				for (CodingSchemeRendering render : codingSchemeRendering) {
					codingScheme = CommonCodingSchemeUtils.getMappedCodingSchemeForCodeSystemRestriction(lexBigService, render, codeSystemSet, mapRole.value()); 
					if (codingScheme != null) {
						codingSchemeList.add(codingScheme);
					}			
				} 
			}
		}
		
		return codingSchemeList;		
	}
	
	private static <T> boolean haveMapRoleAndSetNotEmpty(
			MapRole mapRole,
			Set<T> set) {
		boolean answer = false;
		String mapRoleValue = null;
		if (mapRole != null) {
			mapRoleValue = mapRole.value();
			if (mapRoleValue != null && set != null && set.size() > 0) {
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
