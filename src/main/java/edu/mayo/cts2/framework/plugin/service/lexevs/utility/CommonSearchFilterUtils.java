package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.command.restriction.MapVersionQueryServiceRestrictions.EntitiesRestriction;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public class CommonSearchFilterUtils {

	public static Set<MatchAlgorithmReference> getLexSupportedMatchAlgorithms() {

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		return new HashSet<MatchAlgorithmReference>(Arrays.asList(exactMatch,contains,startsWith));
	}

	public static Set<? extends PropertyReference> getLexSupportedSearchReferences() {
		
		PropertyReference name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();	
		PropertyReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		
		return new HashSet<PropertyReference>(Arrays.asList(name,about,description));
	}


	public static <T extends ResourceQuery> boolean queryReturnsData(
			CodingSchemeRenderingList lexCodingSchemeRenderingList,
			QueryData<T> queryData){
		boolean found = false;
		String lexRenderingLocalName, lexRenderingVersion;
		CodingSchemeSummary lexRenderingSummary;
		
		int renderingCount = lexCodingSchemeRenderingList.getCodingSchemeRenderingCount();
		
		for(int index=0; index < renderingCount; index++){
			lexRenderingSummary = lexCodingSchemeRenderingList.getCodingSchemeRendering(index).getCodingSchemeSummary();
			lexRenderingLocalName = lexRenderingSummary.getLocalName();
			lexRenderingVersion = lexRenderingSummary.getRepresentsVersion();
	
			if(lexRenderingLocalName.equals(queryData.getLexSchemeName()) && 
				lexRenderingVersion.equals(queryData.getLexVersionOrTag().getVersion())){
				found = true;
			}
		}		
			
		return found;
	}



	public static CodingSchemeRenderingList filterLexCodingSchemeRenderingList(
			CodingSchemeRenderingList lexCodingSchemeRenderingList,
			ResolvedFilter cts2ResolvedFilter, 
			VersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		CodingSchemeRenderingList lexFilteredRendering = new CodingSchemeRenderingList();
		
		// Collect Property References
		MatchAlgorithmReference cts2MatchAlgorithmReference = cts2ResolvedFilter.getMatchAlgorithmReference();
		PropertyReference cts2PropertyReference = cts2ResolvedFilter.getPropertyReference();
		String cts2SearchAttribute = cts2PropertyReference.getReferenceTarget().getName();
		
		String cts2MatchValue = cts2ResolvedFilter.getMatchValue();	
		String sourceValue = null;
		
		CodingSchemeRendering[] lexCodingSchemeRenderings = lexCodingSchemeRenderingList.getCodingSchemeRendering();
		for (CodingSchemeRendering lexCodingSchemeRendering : lexCodingSchemeRenderings) {
			CodingSchemeSummary lexCodingSchemeSummary = lexCodingSchemeRendering.getCodingSchemeSummary();
			sourceValue = CommonSearchFilterUtils.determineSourceValue(cts2SearchAttribute, lexCodingSchemeSummary, nameConverter);
			if (CommonStringUtils.executeMatchAlgorithm(sourceValue, cts2MatchValue, cts2MatchAlgorithmReference, caseSensitive)) {
				lexFilteredRendering.addCodingSchemeRendering(lexCodingSchemeRendering);
			} 
		}  
		
		return lexFilteredRendering;
	}
	
	public static String determineSourceValue(String cts2SearchAttribute, CodingSchemeSummary lexSchemeSummary, VersionNameConverter nameConverter){
		String sourceValue = null;
		if(lexSchemeSummary == null){
			return sourceValue;
		}
		if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
			sourceValue = lexSchemeSummary.getCodingSchemeURI();
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
			sourceValue = lexSchemeSummary.getCodingSchemeDescription().getContent();
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
			sourceValue = 
				nameConverter.toCts2VersionName(
					lexSchemeSummary.getLocalName(), 
					lexSchemeSummary.getRepresentsVersion());
		}
		
		return sourceValue;
	}

	public static String determineSourceValue(String cts2SearchAttribute, CodingScheme lexCodingScheme, VersionNameConverter nameConverter){
		String sourceValue = null;
		if(lexCodingScheme == null){
			return sourceValue;
		}
		if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
			sourceValue = lexCodingScheme.getCodingSchemeURI();
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
			sourceValue = lexCodingScheme.getEntityDescription().getContent();
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
			sourceValue = 
				nameConverter.toCts2VersionName(
					lexCodingScheme.getCodingSchemeName(), 
					lexCodingScheme.getRepresentsVersion());
		}
		
		return sourceValue;
	}	
	
	
	public static List<CodingScheme> filterLexCodingSchemeList(
			List<CodingScheme> lexCodingSchemeList,
			Set<ResolvedFilter> cts2Filters, 
			VersionNameConverter nameConverter) {
		
		if(lexCodingSchemeList != null && cts2Filters != null){
			Iterator<ResolvedFilter> cts2FilterIterator = cts2Filters.iterator();
			while (cts2FilterIterator.hasNext() && (lexCodingSchemeList.size() > 0)) {
				ResolvedFilter cts2ResolvedFilter = cts2FilterIterator.next();
				lexCodingSchemeList = filterLexCodingSchemeList(lexCodingSchemeList, 
						cts2ResolvedFilter, 
						nameConverter);
			}
		}
		
		return lexCodingSchemeList;
	}
		
	
	public static List<CodingScheme> filterLexCodingSchemeList(
			List<CodingScheme> lexCodingSchemeList,
			ResolvedFilter cts2Filter, 
			VersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		List<CodingScheme> filteredLexCodingSchemeList = new ArrayList<CodingScheme>();
		
		// Collect Property References
		MatchAlgorithmReference cts2MatchAlgorithmReference = cts2Filter.getMatchAlgorithmReference();
		PropertyReference cts2PropertyReference = cts2Filter.getPropertyReference();
		String cts2SearchAttribute = cts2PropertyReference.getReferenceTarget().getName();
		
		String cts2MatchValue = cts2Filter.getMatchValue();	
		String sourceValue = null;
		
		for (CodingScheme lexCodingScheme : lexCodingSchemeList) {
			sourceValue = CommonSearchFilterUtils.determineSourceValue(cts2SearchAttribute, lexCodingScheme, nameConverter);
			if (CommonStringUtils.executeMatchAlgorithm(sourceValue, cts2MatchValue, cts2MatchAlgorithmReference, caseSensitive)) {
				filteredLexCodingSchemeList.add(lexCodingScheme);
			} 
		}  
		
		return filteredLexCodingSchemeList;
	}
	
	public static <T extends ResourceQuery> void filterLexCodedNodeSet(CodedNodeSet lexCodedNodeSet, QueryData<T> queryData){
		if(lexCodedNodeSet != null){
			// Apply restrictions if they exists
			Set<EntityNameOrURI> cts2Entities = queryData.getCts2Entities();
			CommonSearchFilterUtils.filterLexCodedNodeSet(lexCodedNodeSet, cts2Entities);
			
			
			// Apply filters if they exist
			Set<ResolvedFilter> cts2Filters = queryData.getCts2Filters();
			if(cts2Filters != null){
				for(ResolvedFilter cts2Filter : cts2Filters){
					CommonSearchFilterUtils.filterLexCodedNodeSet(lexCodedNodeSet, cts2Filter);
				}
			}
		}
	}
	
	public static void filterLexCodedNodeSet(
			CodedNodeSet lexCodedNodeSet,
			ResolvedFilter cts2Filter){
		String cts2MatchValue = null;
		String cts2MatchAlgorithm = null;
	
		if(cts2Filter != null){
			cts2MatchValue = cts2Filter.getMatchValue();										// Value to search with 
			cts2MatchAlgorithm = cts2Filter.getMatchAlgorithmReference().getContent();			// Extract from filter the match algorithm to use
		}	
		SearchDesignationOption lexSearchOption = SearchDesignationOption.ALL;					// Other options: PREFERRED_ONLY, NON_PREFERRED_ONLY, ALL 
		String lexLanguage = null;																// This field is not really used, uses default "en"
		
		try {
			lexCodedNodeSet.restrictToMatchingDesignations(cts2MatchValue, lexSearchOption, cts2MatchAlgorithm, lexLanguage);
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}

	
	public static void filterLexCodedNodeSet(
			CodedNodeSet lexCodedNodeSet,
			Set<EntityNameOrURI> cts2EntitySet) {
		ConceptReferenceList lexConceptReferenceList = new ConceptReferenceList();
		ConceptReference lexConcpetReference = new ConceptReference();
		String cts2EntityName;
		boolean listEmpty = true;
		
		if(cts2EntitySet != null){
			for(EntityNameOrURI cts2Entity : cts2EntitySet){
				cts2EntityName = cts2Entity.getUri();
				if(cts2EntityName == null){
					if(cts2Entity.getEntityName() != null){
						cts2EntityName = cts2Entity.getEntityName().getName();
					}
				}
				
				if(cts2EntityName != null){
					lexConcpetReference = new ConceptReference();
					lexConcpetReference.setCode(cts2EntityName);
					lexConceptReferenceList.addConceptReference(lexConcpetReference);
					listEmpty = false;
				}					
				
			}
		}
		
		if(!listEmpty){
			try {
				lexCodedNodeSet.restrictToCodes(lexConceptReferenceList);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}		
		}
	}

	public static CodingSchemeRenderingList filterLexCodingSchemeRenderingList(
			CodingSchemeRenderingList lexRenderingList, 
			String cts2SystemName, 
			MappingExtension lexMappingExtension) {
		
		if(lexRenderingList == null){
			return lexRenderingList;
		}

		boolean restrictToBOTH = (cts2SystemName != null && lexMappingExtension != null);
		boolean restrictToNAME = (!restrictToBOTH && cts2SystemName != null);
		boolean restrictToMAP = (!restrictToBOTH && lexMappingExtension != null);
		
		CodingSchemeRenderingList lexFilteredRenderingList = new CodingSchemeRenderingList();
		
		CodingSchemeRendering[] lexRenderings = lexRenderingList.getCodingSchemeRendering();
		for(CodingSchemeRendering lexRendering : lexRenderings) {
			CodingSchemeSummary lexCodingSchemeSummary = lexRendering.getCodingSchemeSummary();
			String lexCodingSchemeURI = lexCodingSchemeSummary.getCodingSchemeURI();
			String lexCodingSchemeVersion = lexCodingSchemeSummary.getRepresentsVersion();
			
			if(restrictToBOTH){
				if (lexCodingSchemeSummary.getLocalName().equals(cts2SystemName)) {
					// Add if valid Mapping Coding Scheme
					if (CommonMapUtils.validateMappingCodingScheme(lexCodingSchemeURI, lexCodingSchemeVersion, lexMappingExtension)) {
						lexFilteredRenderingList.addCodingSchemeRendering(lexRendering);
					}
				}
			}
			else if(restrictToNAME){
				if (lexCodingSchemeSummary.getLocalName().equals(cts2SystemName)) {
					lexFilteredRenderingList.addCodingSchemeRendering(lexRendering);
				}
			}
			else if(restrictToMAP){
				if (CommonMapUtils.validateMappingCodingScheme(lexCodingSchemeURI, lexCodingSchemeVersion, lexMappingExtension)) {
					lexFilteredRenderingList.addCodingSchemeRendering(lexRendering);
				}
			}
			else{
				lexFilteredRenderingList.addCodingSchemeRendering(lexRendering);
			}
			
		}
		
		return lexFilteredRenderingList;
	}

	public static CodingSchemeRenderingList filterLexCodingSchemeRenderingList(
			CodingSchemeRenderingList lexRenderingList, 
			Set<ResolvedFilter> cts2Filters,
			VersionNameConverter nameConverter) {
		
		if(lexRenderingList != null && cts2Filters != null){
			Iterator<ResolvedFilter> filtersItr = cts2Filters.iterator();
			while (filtersItr.hasNext() && (lexRenderingList.getCodingSchemeRenderingCount() > 0)) {
				ResolvedFilter resolvedFilter = filtersItr.next();
				lexRenderingList = CommonSearchFilterUtils.filterLexCodingSchemeRenderingList(lexRenderingList, resolvedFilter, nameConverter);
			}
		}
		
		return lexRenderingList;
	}
	



	
	public static List<CodingScheme> filterLexCodingSchemeList(
			List<CodingScheme> lexCodingSchemeList, 
			EntitiesRestriction cts2EntitiesRestriction) {
		
		Set<EntityNameOrURI> cts2EntitySet = null;
		MapRole cts2MapRole = null;
		
		cts2EntitySet = cts2EntitiesRestriction.getEntities();
		cts2MapRole = cts2EntitiesRestriction.getMapRole();
		if(haveMapRoleAndSetNotEmpty(cts2MapRole, cts2EntitySet)){
			//for (CodingScheme scheme : codingSchemeList) {
				//Entities entities = scheme.getEntities();
				// TODO: need to see if entity exists in given scheme?? if not remove scheme from codingSchemeList
			//} 
		}
		
		return lexCodingSchemeList;		
	}
	
	private static <T> boolean haveMapRoleAndSetNotEmpty(
			MapRole cts2MapRole,
			Set<T> cts2DataSet) {
		boolean answer = false;
		String mapRoleValue = null;
		if (cts2MapRole != null) {
			mapRoleValue = cts2MapRole.value();
			if (mapRoleValue != null && cts2DataSet != null && cts2DataSet.size() > 0) {
				answer = true;
			}
		}
		return answer;
	}




	
}
