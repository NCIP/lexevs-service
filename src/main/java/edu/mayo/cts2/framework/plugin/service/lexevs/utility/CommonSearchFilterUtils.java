/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.valueSets.ValueSetDefinition;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.ActiveOrAll;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public final class CommonSearchFilterUtils {
	
	private CommonSearchFilterUtils(){
		super();
	}
	
	public static Set<MatchAlgorithmReference> getLexSupportedMatchAlgorithms() {

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		return new HashSet<MatchAlgorithmReference>(Arrays.asList(exactMatch,contains,startsWith));
	}

	public static Set<ComponentReference> getLexSupportedSearchReferences() {
		
		ComponentReference name = StandardModelAttributeReference.RESOURCE_NAME.getComponentReference();		
		ComponentReference about = StandardModelAttributeReference.ABOUT.getComponentReference();	
		ComponentReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference();
		
		return new HashSet<ComponentReference>(Arrays.asList(name,about,description));
	}


	public static <T extends ResourceQuery> boolean queryReturnsData(
			CodingSchemeRenderingList lexCodingSchemeRenderingList,
			QueryData<T> queryData){
		boolean found = false;
		String lexRenderingFormalName, lexRenderingVersion;
		CodingSchemeSummary lexRenderingSummary;
		
		int renderingCount = lexCodingSchemeRenderingList.getCodingSchemeRenderingCount();
		
		for(int index=0; index < renderingCount; index++){
			lexRenderingSummary = lexCodingSchemeRenderingList.getCodingSchemeRendering(index).getCodingSchemeSummary();
			lexRenderingFormalName = lexRenderingSummary.getFormalName() != null ? lexRenderingSummary.getFormalName() : lexRenderingSummary.getLocalName();
			lexRenderingVersion = lexRenderingSummary.getRepresentsVersion();
	
			if(lexRenderingFormalName.equals(queryData.getLexSchemeName()) && 
				lexRenderingVersion.equals(queryData.getLexVersionOrTag().getVersion())){
				found = true;
			}
		}		
			
		return found;
	}



	public static String determineSourceValue(String cts2SearchAttribute, CodingSchemeRendering lexSchemeRendering, VersionNameConverter nameConverter){
		String sourceValue = null;
		if(lexSchemeRendering == null){
			return sourceValue;
		}
		
		CodingSchemeSummary lexSchemeSummary = lexSchemeRendering.getCodingSchemeSummary();
		
		if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
			sourceValue = lexSchemeSummary.getCodingSchemeURI();
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
			if(lexSchemeSummary.getCodingSchemeDescription() != null){
				sourceValue = lexSchemeSummary.getCodingSchemeDescription().getContent();
			}
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
			sourceValue = 
				nameConverter.toCts2VersionName(
					lexSchemeSummary.getLocalName(), 
					lexSchemeSummary.getRepresentsVersion());
		} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_TAG)) {
				CodingSchemeTagList tags = lexSchemeRendering.getRenderingDetail().getVersionTags();
				if(tags != null && tags.getTagCount() == 1){
					sourceValue = tags.getTag(0);
				}
				
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
		
		List<CodingScheme> lexFilteredCodingSchemeList = lexCodingSchemeList;
		
		if(lexCodingSchemeList != null && cts2Filters != null){
			Iterator<ResolvedFilter> cts2FilterIterator = cts2Filters.iterator();
			while (cts2FilterIterator.hasNext() && (lexCodingSchemeList.size() > 0)) {
				ResolvedFilter cts2ResolvedFilter = cts2FilterIterator.next();
				lexFilteredCodingSchemeList = filterLexCodingSchemeList(lexCodingSchemeList, 
						cts2ResolvedFilter, 
						nameConverter);
			}
		}
		
		return lexFilteredCodingSchemeList;
	}
		
	
	public static List<CodingScheme> filterLexCodingSchemeList(
			List<CodingScheme> lexCodingSchemeList,
			ResolvedFilter cts2Filter, 
			VersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		List<CodingScheme> filteredLexCodingSchemeList = new ArrayList<CodingScheme>();
		
		// Collect Property References
		MatchAlgorithmReference cts2MatchAlgorithmReference = cts2Filter.getMatchAlgorithmReference();
		ComponentReference cts2ComponentReference = cts2Filter.getComponentReference();
		String cts2SearchAttribute = cts2ComponentReference.getAttributeReference();
		
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
	
	public static List<ValueSetDefinition> filterLexValueSetDefinitionList(
			List<ValueSetDefinition> lexVSDefinitionList,
			Set<ResolvedFilter> cts2Filters) {
		
		List<ValueSetDefinition> lexFilteredValueSetList = lexVSDefinitionList;
		
		if(lexVSDefinitionList != null && cts2Filters != null){
			Iterator<ResolvedFilter> cts2FilterIterator = cts2Filters.iterator();
			while (cts2FilterIterator.hasNext() && (lexVSDefinitionList.size() > 0)) {
				ResolvedFilter cts2ResolvedFilter = cts2FilterIterator.next();
				lexFilteredValueSetList = filterLexValueSetDefinitionList(lexVSDefinitionList, 
						cts2ResolvedFilter);
			}
		}
		
		return lexFilteredValueSetList;
	}
	public static List<ValueSetDefinition> filterLexValueSetDefinitionList(List<ValueSetDefinition> lexVSDefinitionList,
			ResolvedFilter cts2Filter){
		boolean caseSensitive = false;
		List<ValueSetDefinition> filteredVSDefinitionList = new ArrayList<ValueSetDefinition>();
		// Collect Property References
		MatchAlgorithmReference cts2MatchAlgorithmReference = cts2Filter.getMatchAlgorithmReference();
		ComponentReference cts2ComponentReference = cts2Filter.getComponentReference();
		String cts2SearchAttribute = cts2ComponentReference.getAttributeReference();
		
		String cts2MatchValue = cts2Filter.getMatchValue();	
		String sourceValue = null;
		
		for (ValueSetDefinition lexValueSet : lexVSDefinitionList) {
			sourceValue = CommonSearchFilterUtils.determineVSSourceValue(cts2SearchAttribute, lexValueSet);
			if (CommonStringUtils.executeMatchAlgorithm(sourceValue, cts2MatchValue, cts2MatchAlgorithmReference, caseSensitive)) {
				filteredVSDefinitionList.add(lexValueSet);
			} 
		}
		
		return filteredVSDefinitionList;
	}
	
	private static String determineVSSourceValue(String cts2SearchAttribute,
			ValueSetDefinition lexValueSet) {
			String sourceValue = null;
			if(lexValueSet == null){
				return sourceValue;
			}

			if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_ABOUT)) {
				sourceValue = lexValueSet.getValueSetDefinitionURI();
			} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_SYNOPSIS)) {
				sourceValue = lexValueSet.getValueSetDefinitionName();
			} else if (cts2SearchAttribute.equals(Constants.ATTRIBUTE_NAME_RESOURCE_NAME)) {
				sourceValue = 
						lexValueSet.getValueSetDefinitionName();
			}
			
			return sourceValue;
	}

	public static <T extends ResourceQuery> CodedNodeSet filterLexCodedNodeSet(CodedNodeSet lexCodedNodeSet, QueryData<T> queryData) throws  LBParameterException, LBInvocationException  {
		if(lexCodedNodeSet != null){
			// Apply restrictions if they exists
			Set<EntityNameOrURI> cts2Entities = queryData.getCts2Entities();
			lexCodedNodeSet = CommonSearchFilterUtils.filterLexCodedNodeSet(lexCodedNodeSet, cts2Entities);
			
			
			// Apply filters if they exist
			Set<ResolvedFilter> cts2Filters = queryData.getCts2Filters();
			if(cts2Filters != null){
				for(ResolvedFilter cts2Filter : cts2Filters){
					lexCodedNodeSet = CommonSearchFilterUtils.filterLexCodedNodeSet(lexCodedNodeSet, cts2Filter);
				}
			}
			
			ResolvedReadContext readContext = queryData.getReadContext();
			
			if(readContext != null) {
				 
					if(readContext.getActive() == null || 
					   readContext.getActive().equals(ActiveOrAll.ACTIVE_AND_INACTIVE)){
						
						// set to all
						lexCodedNodeSet.restrictToStatus(ActiveOption.ALL, null);
						
					}
					else if (readContext.getActive().equals(ActiveOrAll.ACTIVE_ONLY)){
						
						// set to active only
						lexCodedNodeSet.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
					}
			}
		}
		
		return lexCodedNodeSet;
	}
	
	public static CodedNodeSet filterLexCodedNodeSet(
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
			lexCodedNodeSet = lexCodedNodeSet.restrictToMatchingDesignations(cts2MatchValue, lexSearchOption, cts2MatchAlgorithm, lexLanguage);
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
		
		return lexCodedNodeSet;
	}

	
	public static CodedNodeSet filterLexCodedNodeSet(
			CodedNodeSet lexCodedNodeSet,
			Set<EntityNameOrURI> cts2EntitySet) {
		ConceptReferenceList lexConceptReferenceList = new ConceptReferenceList();
		String cts2EntityName;
		boolean listEmpty = true;
		
		if(cts2EntitySet != null){
			for(EntityNameOrURI cts2Entity : cts2EntitySet){
				cts2EntityName = cts2Entity.getUri();
				if(cts2EntityName == null && cts2Entity.getEntityName() != null){
					cts2EntityName = cts2Entity.getEntityName().getName();
				}
				
				if(cts2EntityName != null){
					ConceptReference lexConcpetReference = new ConceptReference();
					lexConcpetReference.setCode(cts2EntityName);
					lexConceptReferenceList.addConceptReference(lexConcpetReference);
					listEmpty = false;
				}					
				
			}
		}
		
		if(!listEmpty){
			try {
				lexCodedNodeSet = lexCodedNodeSet.restrictToCodes(lexConceptReferenceList);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}		
		}
		
		return lexCodedNodeSet;
	}

	public static CodingSchemeRenderingList filterLexCodingSchemeRenderingList(
			CodingSchemeRenderingList lexRenderingList, 
			String cts2SystemName, 
			MappingExtension lexMappingExtension) {
		
		if(lexRenderingList == null || (cts2SystemName == null && lexMappingExtension == null)){
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
			boolean localNameMatches = lexCodingSchemeSummary.getLocalName().equals(cts2SystemName); 
			if(restrictToBOTH){
				if(localNameMatches) {
					// Add if valid Mapping Coding Scheme
					if (CommonMapUtils.validateMappingCodingScheme(lexCodingSchemeURI, lexCodingSchemeVersion, lexMappingExtension)) {
						lexFilteredRenderingList.addCodingSchemeRendering(lexRendering);
					}
				}
			}
			else if(restrictToNAME){
				if(localNameMatches) {
					lexFilteredRenderingList.addCodingSchemeRendering(lexRendering);
				}
			}
			else if(restrictToMAP){ 
				if(CommonMapUtils.validateMappingCodingScheme(lexCodingSchemeURI, lexCodingSchemeVersion, lexMappingExtension)) {
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
		
		if(lexRenderingList == null || cts2Filters == null){
			return lexRenderingList;
		}
		
		CodingSchemeRenderingList lexFilteredRenderingList = new CodingSchemeRenderingList();
		CodingSchemeRendering[] lexCodingSchemeRenderings = lexRenderingList.getCodingSchemeRendering();
		
		for (CodingSchemeRendering lexCodingSchemeRendering : lexCodingSchemeRenderings) {
			if(CommonSearchFilterUtils.applyFiltersToRendering(lexCodingSchemeRendering, cts2Filters, nameConverter)){
				lexFilteredRenderingList.addCodingSchemeRendering(lexCodingSchemeRendering);
			}
		}
		return lexFilteredRenderingList;
	}
	
	/**
	 * @param lexCodingSchemeRendering
	 * @param cts2Filters
	 * @return
	 */
	private static boolean applyFiltersToRendering(
			CodingSchemeRendering lexCodingSchemeRendering,
			Set<ResolvedFilter> cts2Filters, 
			VersionNameConverter nameConverter) {
		boolean matches = true;
		Iterator<ResolvedFilter> filtersItr = cts2Filters.iterator();

		boolean caseSensitive = false;
		
		while (filtersItr.hasNext()) {
			ResolvedFilter cts2ResolvedFilter = filtersItr.next();
			
			// Collect Property References
			MatchAlgorithmReference cts2MatchAlgorithmReference = cts2ResolvedFilter.getMatchAlgorithmReference();
			ComponentReference cts2ComponentReference = cts2ResolvedFilter.getComponentReference();
			String cts2SearchAttribute = cts2ComponentReference.getAttributeReference();
			
			String cts2MatchValue = cts2ResolvedFilter.getMatchValue();	
			String sourceValue = CommonSearchFilterUtils.determineSourceValue(cts2SearchAttribute, lexCodingSchemeRendering, nameConverter);
			if (!CommonStringUtils.executeMatchAlgorithm(sourceValue, cts2MatchValue, cts2MatchAlgorithmReference, caseSensitive)) {
				matches = false;
			}
		}
		return matches;
	}

	public static CodingSchemeRenderingList filterLexCodingSchemeRenderingList(
			CodingSchemeRenderingList lexCodingSchemeRenderingList,
			ResolvedFilter cts2ResolvedFilter, 
			VersionNameConverter nameConverter) {
		
		boolean caseSensitive = false;
		CodingSchemeRenderingList lexFilteredRendering = new CodingSchemeRenderingList();
		
		// Collect Property References
		MatchAlgorithmReference cts2MatchAlgorithmReference = cts2ResolvedFilter.getMatchAlgorithmReference();
		ComponentReference cts2ComponentReference = cts2ResolvedFilter.getComponentReference();
		String cts2SearchAttribute = cts2ComponentReference.getPropertyReference().getName();
		
		String cts2MatchValue = cts2ResolvedFilter.getMatchValue();	
		String sourceValue = null;
		
		CodingSchemeRendering[] lexCodingSchemeRenderings = lexCodingSchemeRenderingList.getCodingSchemeRendering();
		for (CodingSchemeRendering lexCodingSchemeRendering : lexCodingSchemeRenderings) {
			sourceValue = CommonSearchFilterUtils.determineSourceValue(cts2SearchAttribute, lexCodingSchemeRendering, nameConverter);
			if (CommonStringUtils.executeMatchAlgorithm(sourceValue, cts2MatchValue, cts2MatchAlgorithmReference, caseSensitive)) {
				lexFilteredRendering.addCodingSchemeRendering(lexCodingSchemeRendering);
			} 
		}  
		
		return lexFilteredRendering;
	}

	public static CodingSchemeRenderingList filterLexCodingSchemeRenderingList(
			CodingSchemeRenderingList list,
			ResolvedReadContext readContext) {
		
		if(readContext.getActive() == null || 
				readContext.getActive().equals(ActiveOrAll.ACTIVE_AND_INACTIVE)){
			return list;
		}

		CodingSchemeRenderingList returnList = new CodingSchemeRenderingList();
		
		for(CodingSchemeRendering csr : list.getCodingSchemeRendering()){
			if(csr.getRenderingDetail().getVersionStatus().
						equals(CodingSchemeVersionStatus.ACTIVE)){
						returnList.addCodingSchemeRendering(csr);
			}
		}
		
		return returnList;
	}	
}
