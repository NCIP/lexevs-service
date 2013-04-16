package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
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


	public static CodingSchemeRenderingList filterRenderingList(ResolvedFilter resolvedFilter, 
			CodingSchemeRenderingList renderingList,
			VersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		CodingSchemeRenderingList temp = new CodingSchemeRenderingList();
		
		// Collect Property References
		MatchAlgorithmReference matchAlgorithmReference = resolvedFilter.getMatchAlgorithmReference();
		PropertyReference propertyReference = resolvedFilter.getPropertyReference();
		String searchAttribute = propertyReference.getReferenceTarget().getName();
		
		String matchStr = resolvedFilter.getMatchValue();	
		String sourceValue = null;
		
		CodingSchemeRendering[] csRendering = renderingList.getCodingSchemeRendering();
		for (CodingSchemeRendering render : csRendering) {
			CodingSchemeSummary codingSchemeSummary = render.getCodingSchemeSummary();
			sourceValue = CommonSearchFilterUtils.determineSourceValue(searchAttribute, codingSchemeSummary, nameConverter);
			if (CommonStringUtils.executeMatchAlgorithm(sourceValue, matchStr, matchAlgorithmReference, caseSensitive)) {
				temp.addCodingSchemeRendering(render);
			} 
		}  
		
		return temp;
	}
	
	public static String determineSourceValue(String searchAttribute, CodingSchemeSummary summary, VersionNameConverter nameConverter){
		String sourceValue = null;
		if(summary == null){
			return sourceValue;
		}
		if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
			sourceValue = summary.getCodingSchemeURI();
		} else if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
			sourceValue = summary.getCodingSchemeDescription().getContent();
		} else if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
			sourceValue = 
				nameConverter.toCts2VersionName(
					summary.getLocalName(), 
					summary.getRepresentsVersion());
		}
		
		return sourceValue;
	}

	
	public static List<CodingScheme> filterCodingSchemeList(
			Set<ResolvedFilter> filters, List<CodingScheme> codingSchemeList,
			VersionNameConverter nameConverter) {
		
		if(codingSchemeList != null && filters != null){
			Iterator<ResolvedFilter> filtersItr = filters.iterator();
			while (filtersItr.hasNext() && (codingSchemeList.size() > 0)) {
				ResolvedFilter resolvedFilter = filtersItr.next();
				codingSchemeList = filterCodingSchemeList(resolvedFilter, 
						codingSchemeList, nameConverter);
			}
		}
		
		return codingSchemeList;
	}
		
	
	public static List<CodingScheme> filterCodingSchemeList(ResolvedFilter resolvedFilter, 
			List<CodingScheme> codingSchemeList,
			VersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		List<CodingScheme> temp = new ArrayList<CodingScheme>();
		
		// Collect Property References
		MatchAlgorithmReference matchAlgorithmReference = resolvedFilter.getMatchAlgorithmReference();
		PropertyReference propertyReference = resolvedFilter.getPropertyReference();
		String searchAttribute = propertyReference.getReferenceTarget().getName();
		
		String matchStr = resolvedFilter.getMatchValue();	
		String sourceValue = null;
		
		for (CodingScheme codingScheme : codingSchemeList) {
			sourceValue = CommonSearchFilterUtils.determineSourceValue(searchAttribute, codingScheme, nameConverter);
			if (CommonStringUtils.executeMatchAlgorithm(sourceValue, matchStr, matchAlgorithmReference, caseSensitive)) {
				temp.add(codingScheme);
			} 
		}  
		
		return temp;
	}
	
	public static String determineSourceValue(String searchAttribute, CodingScheme codingScheme, VersionNameConverter nameConverter){
		String sourceValue = null;
		if(codingScheme == null){
			return sourceValue;
		}
		if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
			sourceValue = codingScheme.getCodingSchemeURI();
		} else if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
			sourceValue = codingScheme.getEntityDescription().getContent();
		} else if (searchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
			sourceValue = 
				nameConverter.toCts2VersionName(
					codingScheme.getCodingSchemeName(), 
					codingScheme.getRepresentsVersion());
		}
		
		return sourceValue;
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
	
	
	public static List<CodingScheme> getCodingSchemeListFromCodeSchemeRenderings(
			LexBIGService lexBigService, 
			CodingSchemeRendering[] codingSchemeRenderings, 
			CodeSystemRestriction codeSystemRestriction) {

		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();
		Set<NameOrURI> codeSystemSet = null;
		MapRole mapRole = null;
		
		CodingScheme codingScheme;

		if(codeSystemRestriction != null){
			codeSystemSet = codeSystemRestriction.getCodeSystems();
			mapRole = codeSystemRestriction.getMapRole();
		}
		
		for (CodingSchemeRendering render : codingSchemeRenderings) {
			codingScheme = CommonCodingSchemeUtils.getCodingSchemeFromRendering(lexBigService, render);
			if(mapRole != null && codeSystemSet != null){
				if(CommonCodingSchemeUtils.validateMapRole(codingScheme, codeSystemSet, mapRole.value())){
					codingSchemeList.add(codingScheme);
				}			
			}
			else{
				codingSchemeList.add(codingScheme);
			}
		} 
		
		return codingSchemeList;		
	}
	

	public static List<CodingScheme> filterCodingSchemeListByEntitiesRestriction(
			List<CodingScheme> codingSchemeList, 
			EntitiesRestriction entitiesRestriction) {
		
		Set<EntityNameOrURI> entitiesSet = null;
		MapRole mapRole = null;
		
		entitiesSet = entitiesRestriction.getEntities();
		mapRole = entitiesRestriction.getMapRole();
		if(haveMapRoleAndSetNotEmpty(mapRole, entitiesSet)){
			for (CodingScheme scheme : codingSchemeList) {
				Entities entities = scheme.getEntities();
				// TODO: need to see if entity exists in given scheme?? if not remove scheme from codingSchemeList
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


	
}
