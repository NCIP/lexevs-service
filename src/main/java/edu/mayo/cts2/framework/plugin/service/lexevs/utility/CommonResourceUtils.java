package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
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
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
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

public final class CommonResourceUtils{
	private static final String UNCHECKED = "unchecked";
	private static final String RAWTYPES = "rawtypes";
	
	private CommonResourceUtils(){
		super();
	}
	
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

	public static <I,O> DirectoryResult<O> createDirectoryResultsWithSummary(
			final LexEvsToCTS2Transformer<?,?,O,I> transformer,
			I[] lexData, 
			boolean atEnd) {
		List<O> results = doInLoop(lexData,new Closure<I,O>(){
			@Override
			public O forEach(I in) {
				return transformer.transformSummaryDescription(in);
			}
		});

		return new DirectoryResult<O>(results, atEnd);
	}
	
	public static <I,O> DirectoryResult<O> createDirectoryResultsWithList(
			final LexEvsToCTS2Transformer<O,I,?,?> transformer,
			I[] lexData, 
			boolean atEnd) {
		List<O> results = doInLoop(lexData,new Closure<I,O>(){
			@Override
			public O forEach(I in) {
				return transformer.transformFullDescription(in);
			}
		});

		return new DirectoryResult<O>(results, atEnd);
	}
	
	private interface Closure<I,O>{
		O forEach(I in);
	}
	
	protected static <I,O> List<O> doInLoop(I[] lexData, Closure<I,O> closure){
		List<O> output = new ArrayList<O>();
		if(lexData != null){
			for (I in : lexData) {
				output.add(closure.forEach(in));
			}
		}
		return output;
	}

	public static <T> DirectoryResult<T> createDirectoryResultWithEntryFullVersionDescriptions(
			LexBIGService lexBigService, 
			LexEvsToCTS2Transformer<T,CodingScheme,?,?> transformer, 
			CodingSchemeRendering[] lexCodeSchemeRenderings, 
			boolean atEnd2){
		List<T> cts2EntryList = new ArrayList<T>();
		boolean atEnd = true;
		DirectoryResult<T> cts2DirectoryResult = new DirectoryResult<T>(cts2EntryList, atEnd);
		
		if(lexCodeSchemeRenderings != null){
			for (CodingSchemeRendering lexCodingSchemeRendering : lexCodeSchemeRenderings) {
				String lexCodingSchemeName = lexCodingSchemeRendering.getCodingSchemeSummary().getCodingSchemeURI();			
				String lexCodingSchemeVersion = lexCodingSchemeRendering.getCodingSchemeSummary().getRepresentsVersion();
				CodingSchemeVersionOrTag lexTagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(lexCodingSchemeVersion);
				try {
					CodingScheme lexCodingScheme = lexBigService.resolveCodingScheme(lexCodingSchemeName, lexTagOrVersion);
					cts2EntryList.add(transformer.transformFullDescription(lexCodingScheme));
				} catch (LBException e) {
					throw new RuntimeException(e);
				}
			}
			cts2DirectoryResult = new DirectoryResult<T>(cts2EntryList, atEnd);
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
		Set<ResolvedFilter> cts2Filters = queryData.getCts2Filters();
		
		try {
			lexRenderingList = lexBigService.getSupportedCodingSchemes();
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		}
		
		lexRenderingList = CommonSearchFilterUtils.filterLexCodingSchemeRenderingList(lexRenderingList, cts2SystemName, lexMappingExtension);
		lexRenderingList = CommonSearchFilterUtils.filterLexCodingSchemeRenderingList(lexRenderingList, cts2Filters, nameConverter);
		
		if(queryData.getReadContext() != null){
			lexRenderingList = CommonSearchFilterUtils.filterLexCodingSchemeRenderingList(lexRenderingList, queryData.getReadContext());
		}
		
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
		
		if(queryData.hasNameAndVersion()){
			try {
				LocalNameList lexLocalNameList = new LocalNameList();
				
				CodingSchemeRenderingList lexCodingSchemeRenderingList = lexBigService.getSupportedCodingSchemes();
				boolean dataExists = CommonSearchFilterUtils.queryReturnsData(lexCodingSchemeRenderingList, queryData);			
				if(dataExists){
					// Get Code Node Set from LexBIG service for given coding scheme
					lexCodedNodeSet = lexBigService.getNodeSet(queryData.getLexSchemeName(), queryData.getLexVersionOrTag() , lexLocalNameList);
					lexCodedNodeSet = CommonSearchFilterUtils.filterLexCodedNodeSet(lexCodedNodeSet, queryData);
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
		
		return lexCodedNodeSet;
	}
	
	public static MapResolvedConceptReferenceResults getLexMapReferenceResults(
			MapEntryQuery cts2Query, SortCriteria cts2SortCriteria, Page page,
			VersionNameConverter nameConverter, MappingExtension lexMappingExtension) {
		
		ResolvedConceptReferenceResults lexResolvedConceptReferenceResults = new ResolvedConceptReferenceResults(null, true);
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
					String cts2EntityName = null;
					String cts2EntityNamespace = null;
					if(cts2EntityNameOrURI.getEntityName() != null){
						cts2EntityName = cts2EntityNameOrURI.getEntityName().getName();
						cts2EntityNamespace = cts2EntityNameOrURI.getEntityName().getNamespace();
					}
					ConceptReferenceList reference = Constructors.createConceptReferenceList(cts2EntityName, cts2EntityNamespace, null);
					lexMapping = lexMapping.restrictToCodes(reference, SearchContext.TARGET_CODES);
				}
				
				lexMapIterator = lexMapping.resolveMapping();
			}
			else{
				lexMapIterator = lexMappingExtension.resolveMapping(lexSchemeName, lexVersionOrTag, lexRelationsContainerName, lexSortOptionList);
				
			}
			
			lexResolvedConceptReferenceResults = CommonPageUtils.getPage(lexMapIterator, page);
			
		} catch (LBParameterException e) {
			return new MapResolvedConceptReferenceResults(null,true);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}

		return new MapResolvedConceptReferenceResults(
				nameConverter.fromCts2VersionName(queryData.getCts2SystemVersion().getName()), 
				lexResolvedConceptReferenceResults);			
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
