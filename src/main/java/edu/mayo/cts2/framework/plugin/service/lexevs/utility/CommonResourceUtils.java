package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.LexEvsToCTS2Transformer;
import edu.mayo.cts2.framework.service.command.restriction.MapEntryQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.MapQueryServiceRestrictions.CodeSystemRestriction;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQuery;

public class CommonResourceUtils{
	private final static String UNCHECKED = "unchecked";
	private final static String RAWTYPES = "rawtypes";
	
	@SuppressWarnings({ RAWTYPES, UNCHECKED })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResults(
			LexEvsToCTS2Transformer transformer,
			ResolvedConceptReferenceResults lexResolvedConceptReferenceResults,
			String transformerType) {
		
		List<EntryType> cts2EntryList = new ArrayList<EntryType>();
		DirectoryResult<EntryType> cts2DirectoryResult = new DirectoryResult<EntryType>(cts2EntryList, true);
		EntryType cts2Entry = null;
		if(lexResolvedConceptReferenceResults != null){
			ResolvedConceptReference[] lexResolvedConceptReferences = lexResolvedConceptReferenceResults.getLexResolvedConceptReference();
			if(lexResolvedConceptReferences != null){
				for(ResolvedConceptReference lexResolvedConceptReference : lexResolvedConceptReferences){
					if(transformerType.equals(Constants.FULL_DESCRIPTION)){
						cts2Entry = (EntryType) transformer.transformFullDescription(lexResolvedConceptReference);
					}
					else if(transformerType.equals(Constants.SUMMARY_DESCRIPTION)){
						cts2Entry = (EntryType) transformer.transformSummaryDescription(lexResolvedConceptReference);
					}
					cts2EntryList.add(cts2Entry);
				}
			}			
			cts2DirectoryResult = new DirectoryResult<EntryType>(cts2EntryList, lexResolvedConceptReferenceResults.isAtEnd());
		}

		return cts2DirectoryResult;
	}	

	@SuppressWarnings({ UNCHECKED, RAWTYPES })
	public static <EntryType, LexData> DirectoryResult<EntryType> createDirectoryResultsWithSummaryDescriptions(
			LexEvsToCTS2Transformer transformer,
			LexData[] lexData, 
			boolean atEnd,
			String transformerType) {
		List<EntryType> cts2EntryList = new ArrayList<EntryType>();
		DirectoryResult<EntryType> cts2DirectoryResult = new DirectoryResult<EntryType>(cts2EntryList, true);
		EntryType cts2Entry = null;
		
		if(lexData != null){
			for (LexData lexDataItem : lexData) {
				if(transformerType.equals(Constants.FULL_DESCRIPTION)){
					cts2Entry = (EntryType) transformer.transformFullDescription(lexDataItem);
				}
				else if(transformerType.equals(Constants.SUMMARY_DESCRIPTION)){
					cts2Entry = (EntryType) transformer.transformSummaryDescription(lexDataItem);
				}
				
				cts2EntryList.add(cts2Entry);
			}
			cts2DirectoryResult = new DirectoryResult<EntryType>(cts2EntryList, atEnd);
		}
		
		return cts2DirectoryResult;
	}
	
	@SuppressWarnings({ RAWTYPES, UNCHECKED })
	public static <EntryType> DirectoryResult<EntryType> createDirectoryResultWithEntryFullVersionDescriptions(
			LexBIGService lexBigService, 
			LexEvsToCTS2Transformer transformer, 
			CodingSchemeRendering[] lexCodeSchemeRenderings, 
			boolean atEnd2){
		List<EntryType> cts2EntryList = new ArrayList<EntryType>();
		boolean atEnd = true;
		DirectoryResult<EntryType> cts2DirectoryResult = new DirectoryResult<EntryType>(cts2EntryList, atEnd);
		
		if(lexCodeSchemeRenderings != null){
			for (CodingSchemeRendering lexCodingSchemeRendering : lexCodeSchemeRenderings) {
				String lexCodingSchemeName = lexCodingSchemeRendering.getCodingSchemeSummary().getCodingSchemeURI();			
				String lexCodingSchemeVersion = lexCodingSchemeRendering.getCodingSchemeSummary().getRepresentsVersion();
				CodingSchemeVersionOrTag lexTagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(lexCodingSchemeVersion);
				CodingScheme lexCodingScheme;
				try {
					lexCodingScheme = lexBigService.resolveCodingScheme(lexCodingSchemeName, lexTagOrVersion);
					cts2EntryList.add((EntryType) transformer.transformFullDescription(lexCodingScheme));
				} catch (LBException e) {
					throw new RuntimeException(e);
				}
			}
			cts2DirectoryResult = new DirectoryResult<EntryType>(cts2EntryList, atEnd);
		}
		
		return cts2DirectoryResult;
	}
	
	// --------------------------------------------
	public static <T extends ResourceQuery> CodingSchemeRendering[] getLexCodingSchemeRenderings(
			LexBIGService lexBigService, 
			VersionNameConverter nameConverter, 
			QueryData<T> queryData,
			MappingExtension lexMappingExtension,
			SortCriteria cts2SortCriteria){
		
		CodingSchemeRenderingList lexRenderingList = null;
		String cts2SystemName = queryData.getCts2SystemName();
		
		try {
			lexRenderingList = lexBigService.getSupportedCodingSchemes();
		} catch (LBInvocationException e) {
			throw new RuntimeException();
		}
		
		lexRenderingList = CommonSearchFilterUtils.filterLexCodingSchemeRenderingList(lexRenderingList, cts2SystemName, lexMappingExtension);
		lexRenderingList = CommonSearchFilterUtils.filterLexCodingSchemeRenderingList(lexRenderingList, queryData.getCts2Filters(), nameConverter);
		// TODO: Need to filter further for restrictions
		
		return lexRenderingList.getCodingSchemeRendering();
	}
	
	
	public static <T extends ResourceQuery> List<CodingScheme> getLexCodingSchemeList(
			LexBIGService lexBigService, 
			VersionNameConverter nameConverter,
			MappingExtension lexMappingExtension,
			QueryData<T> queryData,
			SortCriteria cts2SortCriteria) {

		List<CodingScheme> lexCodingSchemeList = new ArrayList<CodingScheme>();
		
		CodingSchemeRendering[] lexCodingSchemeRendering;
		lexCodingSchemeRendering = CommonResourceUtils.getLexCodingSchemeRenderings(lexBigService, nameConverter, queryData, lexMappingExtension, cts2SortCriteria); 

		if(lexCodingSchemeRendering != null){
			CodeSystemRestriction cts2CodeSystemRestriction = queryData.getCts2CodeSystemRestriction();
			lexCodingSchemeList = CommonResourceUtils.getLexCodingSchemeList(lexBigService, lexCodingSchemeRendering, cts2CodeSystemRestriction);
		}
		
		return lexCodingSchemeList;
	}

	public static <T extends ResourceQuery> CodedNodeSet getLexCodedNodeSet(
			LexBIGService lexBigService, 
			QueryData<T> queryData,
			SortCriteria cts2SortCriteria){
		CodedNodeSet lexCodedNodeSet = null;
		boolean dataExists = false;
		
		if(queryData.hasNameAndVersion()){
			try {
				LocalNameList lexLocalNameList = new LocalNameList();
				
				CodingSchemeRenderingList lexCodingSchemeRenderingList = lexBigService.getSupportedCodingSchemes();
				dataExists = CommonSearchFilterUtils.queryReturnsData(lexCodingSchemeRenderingList, queryData);			
				if(dataExists){
					// Get Code Node Set from LexBIG service for given coding scheme
					lexCodedNodeSet = lexBigService.getNodeSet(queryData.getLexSchemeName(), queryData.getLexVersionOrTag() , lexLocalNameList);
					CommonSearchFilterUtils.filterLexCodedNodeSet(lexCodedNodeSet, queryData);
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
		
		return lexCodedNodeSet;
	}
	
	public static ResolvedConceptReferenceResults getLexMapReferenceResults(
			MapEntryQuery cts2Query, SortCriteria cts2SortCriteria, Page page,
			VersionNameConverter nameConverter, MappingExtension lexMappingExtension) {
		
		ResolvedConceptReferenceResults lexResolvedConceptReferenceResults = null;
		ResolvedConceptReferencesIterator lexMapIterator;
		QueryData<MapEntryQuery> queryData;

		queryData = new QueryData<MapEntryQuery>(cts2Query, nameConverter);
		
		String lexSchemeName = queryData.getLexSchemeName();  // Will get codeSchemeName or mapName depending on query type
	
		CodingSchemeVersionOrTag lexVersionOrTag = queryData.getLexVersionOrTag();
		String lexRelationsContainerName = null;
		List<MappingSortOption> lexSortOptionList = null;
		MapEntryQueryServiceRestrictions cts2Restrictions = (MapEntryQueryServiceRestrictions) queryData.getCts2Restrictions();
		
		try {
			if(cts2Restrictions != null){
				Mapping lexMapping = lexMappingExtension.getMapping(lexSchemeName, lexVersionOrTag, lexRelationsContainerName);
				Set<EntityNameOrURI> cts2TargetEntities = cts2Restrictions.getTargetEntities();
				for(EntityNameOrURI cts2EntityNameOrURI : cts2TargetEntities){
					String cts2EntityName = cts2EntityNameOrURI.getEntityName().getName();
					lexMapping = lexMapping.restrictToCodes(Constructors.createConceptReferenceList(cts2EntityName), SearchContext.SOURCE_OR_TARGET_CODES);
				}
				
				lexMapIterator = lexMapping.resolveMapping();
			}
			else{
				lexMapIterator = lexMappingExtension.resolveMapping(lexSchemeName, lexVersionOrTag, lexRelationsContainerName, lexSortOptionList);
				
			}
			
			lexResolvedConceptReferenceResults = CommonPageUtils.getPage(lexMapIterator, page);
			
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lexResolvedConceptReferenceResults;			
	}

//	public static ResolvedConceptReference [] getLexEntityList(
//			MapEntryQueryServiceRestrictions cts2Restrictions,
//			ResolvedConceptReferencesIterator lexMapIterator) {
//		ResolvedConceptReferenceList lexResolvedConceptReferenceList = new ResolvedConceptReferenceList();
//		ResolvedConceptReference lexResolvedConceptReference;
//		Set<EntityNameOrURI> cts2TargetEntitySet = cts2Restrictions.getTargetEntities();
//	
//		try {
//			while(lexMapIterator.hasNext()){
//				lexResolvedConceptReference  = lexMapIterator.next();
//				
//				String lexEntityCode = lexResolvedConceptReference.getCode();
//				for(EntityNameOrURI cts2Entity : cts2TargetEntitySet){
//					String cts2EntityName = cts2Entity.getEntityName().getName();
//					if(cts2EntityName.equals(lexEntityCode)){
//						lexResolvedConceptReferenceList.addResolvedConceptReference(lexResolvedConceptReference);
//					}
//				}
//			}
//		} catch (LBResourceUnavailableException e) {
//			throw new RuntimeException();
//		} catch (LBInvocationException e) {
//			throw new RuntimeException();
//		}
//		
//		return lexResolvedConceptReferenceList.getResolvedConceptReference();
//	}

	public static CodingScheme getMappedCodingScheme(
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
	
	public static List<CodingScheme> getLexCodingSchemeList(
			LexBIGService lexBigService, 
			CodingSchemeRendering[] lexCodingSchemeRenderings, 
			CodeSystemRestriction cts2CodeSystemRestriction) {

		List<CodingScheme> lexCodingSchemeList = new ArrayList<CodingScheme>();
		Set<NameOrURI> cts2CodeSystemSet = null;
		MapRole cts2MapRole = null;
		
		CodingScheme lexCodingScheme;

		if(cts2CodeSystemRestriction != null){
			cts2CodeSystemSet = cts2CodeSystemRestriction.getCodeSystems();
			cts2MapRole = cts2CodeSystemRestriction.getMapRole();
		}
		
		for (CodingSchemeRendering lexCodingSchemeRendering : lexCodingSchemeRenderings) {
			lexCodingScheme = CommonResourceUtils.getLexCodingScheme(lexBigService, lexCodingSchemeRendering);
			if(cts2MapRole != null && cts2CodeSystemSet != null){
				if(CommonCodingSchemeUtils.checkIfCts2MapExists(lexCodingScheme, cts2CodeSystemSet, cts2MapRole.value())){
					lexCodingSchemeList.add(lexCodingScheme);
				}			
			}
			else{
				lexCodingSchemeList.add(lexCodingScheme);
			}
		} 
		
		return lexCodingSchemeList;		
	}
	
	public static CodingScheme getLexCodingScheme(
			LexBIGService lexBigService, 
			CodingSchemeRendering lexCodingSchemeRendering) {
		
		CodingScheme lexCodingScheme = null;
		String lexCodingSchemeName = null;
		String lexVersion = null;
		CodingSchemeVersionOrTag lexTagOrVersion = null;
		try {
			if(lexCodingSchemeRendering != null){
				lexCodingSchemeName = lexCodingSchemeRendering.getCodingSchemeSummary().getCodingSchemeURI();			
				lexVersion = lexCodingSchemeRendering.getCodingSchemeSummary().getRepresentsVersion();
				lexTagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(lexVersion);			
			}
			
			lexCodingScheme = lexBigService.resolveCodingScheme(lexCodingSchemeName, lexTagOrVersion);			
			
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		return lexCodingScheme;
	}
}
