package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.relations.Relations;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

public class CommonUtils {

	// Private constructor - case where every method in class is static
	private CommonUtils() {
		super();
	}
	
//	public static <T> DirectoryResult<T> getSublist(List<T> list, Page page){
//		List<T> sublist = new ArrayList<T>();
//		boolean atEnd = false;
//		int start = page.getStart();
//		int end = page.getEnd();
//		int i = 0;
//		if ((start == 0) && ((end == list.size()) || (end > list.size()))) {
//			i = list.size();
//			sublist = list;
//		} else {
//			for (i = start; i < end && i < list.size(); i++) {
//				sublist.add(list.get(i));
//			}
//		}
//	
//		if (i == list.size()) {
//			atEnd = true;
//		}
//	
//		DirectoryResult<T> directoryResult = new DirectoryResult<T>(
//				sublist, atEnd);
//		
//		return directoryResult;
//	}

	public static CodingSchemeRendering[] getRenderingPage(CodingSchemeRendering[] csRendering, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
		CodingSchemeRendering [] csRenderingPage = new CodingSchemeRendering[0];
		
		if(csRendering != null){
			if(end > csRendering.length){
				end = csRendering.length;
			}
			
			if ((start == 0) && (end == csRendering.length)) {
				csRenderingPage = csRendering;
			} 
			else if(start < end){
				
				int size = end - start;
				csRenderingPage = new CodingSchemeRendering [size];
				
				for (int i = 0; i < csRenderingPage.length; i++) {
					csRenderingPage[i] = csRendering[start + i];
				}
			}
		}
		
		return csRenderingPage;
	}

	public static CodingScheme[] getRenderingPage(CodingScheme[] codingScheme, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
		CodingScheme [] csPage = null;
		
		if(end > codingScheme.length){
			end = codingScheme.length;
		}
		
		if ((start == 0) && (end == codingScheme.length)) {
			csPage = codingScheme.clone();
		} 
		else if(start < end){
			
			int size = end - start;
			csPage = new CodingScheme [size];
			
			for (int i = 0; i < csPage.length; i++) {
				csPage[i] = codingScheme[start + i];
			}
		}
	
		return csPage;
	}


	public static boolean isCodingSchemeFound(String relationCodingScheme, Set<NameOrURI> codeSystemSet) {

		boolean returnFlag = false;
		Iterator<NameOrURI> iterator = codeSystemSet.iterator();
		while (iterator.hasNext() && returnFlag == false) {
			NameOrURI nameOrURI = iterator.next();
			if (nameOrURI.getName() != null && nameOrURI.getName().equals(relationCodingScheme)) {
				returnFlag = true;
			}
			if (nameOrURI.getUri() != null && nameOrURI.getUri().equals(relationCodingScheme)) {
				returnFlag = true;
			}
		}
		return returnFlag;
	}
	
	
	public static boolean isCodingSchemeFound(String targetCodingScheme, String srcCodingScheme, Set<NameOrURI> codeSystemSet) {

		boolean returnFlag = false;
		Iterator<NameOrURI> iterator = codeSystemSet.iterator();
		while (iterator.hasNext() && returnFlag == false) {
			NameOrURI nameOrURI = iterator.next();
			if (nameOrURI.getName() != null && (nameOrURI.getName().equals(srcCodingScheme) || 
					nameOrURI.getName().equals(targetCodingScheme))) {
				returnFlag = true;
			}
			if (nameOrURI.getUri() != null && (nameOrURI.getUri().equals(srcCodingScheme) || 
					nameOrURI.getUri().equals(targetCodingScheme))) {
				returnFlag = true;
			}
		}
		return returnFlag;
	}
	
	public static CodingScheme getCodingScheme(LexBIGService lexBigService, CodingSchemeRendering render) {
		String codingSchemeName = render.getCodingSchemeSummary().getCodingSchemeURI();			
		String version = render.getCodingSchemeSummary().getRepresentsVersion();
		CodingSchemeVersionOrTag tagOrVersion = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
		CodingScheme codingScheme;
		try {
			codingScheme = lexBigService.resolveCodingScheme(codingSchemeName, tagOrVersion);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		return codingScheme;
	}
	
	public static List<CodingScheme> resolveToCodingSchemeList(LexBIGService lexBigService, CodingSchemeRendering[] codingSchemeRenderingArray) {
		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();
		
		if (codingSchemeRenderingArray != null && codingSchemeRenderingArray.length > 0) {
			for (int i=0; i<codingSchemeRenderingArray.length; i++) {
				CodingScheme codingScheme = CommonUtils.getCodingScheme(lexBigService, codingSchemeRenderingArray[i]);
				codingSchemeList.add(codingScheme);
			}
		}
		return codingSchemeList;
	}
	
	public static CodingScheme getCodingSchemeForCodeSystemRestriction(LexBIGService lexBigService, CodingSchemeRendering render, 
			Set<NameOrURI> codeSystemSet, 
			String csrMapRoleValue) {

		CodingScheme notFoundCodingScheme = null;

		CodingScheme codingScheme = CommonUtils.getCodingScheme(lexBigService, render);
		
		// Assuming format of Map has only has 1 relations section/1 relations element in xml file
		if (codingScheme.getRelationsCount() != 1) {
			throw new UnsupportedOperationException("Invalid format for Map. Expecting only one metadata section for Relations.");
		}
		Relations relations = codingScheme.getRelations(0);
		String sourceCodingScheme = relations.getSourceCodingScheme();
		String targetCodingScheme = relations.getTargetCodingScheme();
		
		if (csrMapRoleValue.equals(Constants.MAP_TO_ROLE) && CommonUtils.isCodingSchemeFound(targetCodingScheme, codeSystemSet)) {
			return codingScheme;
		}
		
		if (csrMapRoleValue.equals(Constants.MAP_FROM_ROLE) && CommonUtils.isCodingSchemeFound(sourceCodingScheme, codeSystemSet)) { 
			return codingScheme;
		}
		
		if (csrMapRoleValue.equals(Constants.BOTH_MAP_ROLES) && 
				CommonUtils.isCodingSchemeFound(targetCodingScheme, sourceCodingScheme, codeSystemSet)) {
			return codingScheme;
		}
		
		return notFoundCodingScheme;
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
	
	public static ResolvedConceptReferencesIterator getResolvedConceptReferencesIterator(CodedNodeSet codedNodeSet, SortCriteria sortCriteria){
		ResolvedConceptReferencesIterator iterator = null;
		if(codedNodeSet != null){
			try {
				// With all null arguments the iterator will access the entire codeNodeSet
				// This call will execute the set of filters determined in loop above
				SortOptionList sortOptions = null;
				LocalNameList propertyNames = null;
				PropertyType [] propertyTypes = null; 
				
				iterator = codedNodeSet.resolve(sortOptions, propertyNames, propertyTypes);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
		}		
		return iterator;
	}
	
	public static ResolvedConceptReferenceResults doGetResourceSummaryResults(LexBIGService lexBigService, CodeSystemVersionNameConverter codeSystemVersionNameConverter, EntityDescriptionQuery query, SortCriteria sortCriteria, Page page){
		ResolvedConceptReferenceResults results = null;
		
		// * if codingSchemeName exists within the query, get CodedNodeSet
		// * for each filter existing within the query, execute restrictToMatchingDesignations on the codedNodeSet
		CodedNodeSet codedNodeSet = CommonUtils.getCodedNodeSet(lexBigService, codeSystemVersionNameConverter, query, sortCriteria);
		
		if(codedNodeSet != null){
			// Using filtered codeNodeSet get ResolvedConceptReferenceResults
			// -- contains an array of ResolvedConceptReference and a boolean indicating if at end of resultSet
			results = CommonUtils.getResolvedConceptReferenceResults(codedNodeSet, sortCriteria, page);
		}
		
		return results;
	}

	public static ResolvedConceptReferenceResults getResolvedConceptReferenceResults(CodedNodeSet codedNodeSet, SortCriteria sortCriteria, Page page){
		boolean atEnd = false;
		ResolvedConceptReference[] resolvedConceptReferences = null;
		ResolvedConceptReferencesIterator iterator;
		ResolvedConceptReferenceList resolvedConceptReferenceList = null;
		int start = 0, end = 0;
		try {
			iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, sortCriteria);
			
			if(iterator != null){
				// Get on requested "page" of entities.  
				// In this case we can get the "page" from the iterator, unlike in LexEvsCodeSystemVersionQueryService.
				start = page.getStart();
				end = page.getEnd();
				if(end > iterator.numberRemaining()){
					end = iterator.numberRemaining();
					atEnd = true;				
				}
				resolvedConceptReferenceList = iterator.get(start, end);
				// Get array of resolved concept references
				
				if(resolvedConceptReferenceList != null){
					resolvedConceptReferences = resolvedConceptReferenceList.getResolvedConceptReference();
//					if(printObjects){
//						System.out.println("resolvedConceptReferences: " + resolvedConceptReferences.length);
//					}
				}	
			}
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBResourceUnavailableException e) {
			throw new RuntimeException(e);
		}
		
		return new ResolvedConceptReferenceResults(resolvedConceptReferences, atEnd);
	}
	
	public static CodedNodeSet getCodedNodeSet(LexBIGService lexBigService, CodeSystemVersionNameConverter codeSystemVersionNameConverter, EntityDescriptionQuery query, SortCriteria sortCriteria){
		CodedNodeSet codedNodeSet = null;
		Set<ResolvedFilter> filters = null; 		
		String codeSystem = null;
		EntityDescriptionQueryServiceRestrictions entityDescriptionQueryServiceRestrictions = null;
		String codingSchemeName = null;
		CodingSchemeVersionOrTag versionOrTag = null;
		boolean haveSchemeName = false;
		
		if (query != null) {
			entityDescriptionQueryServiceRestrictions = query.getRestrictions();
			filters = query.getFilterComponent();
			if (entityDescriptionQueryServiceRestrictions != null) {
				codeSystem = entityDescriptionQueryServiceRestrictions.getCodeSystemVersion().getName();
				
				if(codeSystem != null){
					NameVersionPair nameVersionPair =
							codeSystemVersionNameConverter.fromCts2CodeSystemVersionName(codeSystem);					
					versionOrTag = new CodingSchemeVersionOrTag();
					codingSchemeName = nameVersionPair.getName();
					versionOrTag.setTag(nameVersionPair.getVersion());
					versionOrTag.setVersion(nameVersionPair.getVersion());
//					if(printObjects){
//						System.out.println("CodingSchemeName: " + codingSchemeName);
//						System.out.println("VersionOrTag: " + versionOrTag.getVersion());
//					}
					if((codingSchemeName != null) && (versionOrTag.getVersion() != null || versionOrTag.getTag() != null)){
						haveSchemeName = true;
					}
				}
			}
		}		
				

		if(haveSchemeName){
//			LexBIGService lexBigService = getLexBigService();
			boolean found = false;
			
			try {
				// Get Code Node Set from LexBIG service for given coding scheme
				LocalNameList entityTypes = new LocalNameList();
				CodingSchemeRenderingList codingSchemeRenderingList = lexBigService.getSupportedCodingSchemes();
				int count = codingSchemeRenderingList.getCodingSchemeRenderingCount();
				for(int i=0; i < count; i++){
					CodingSchemeSummary codingSchemeSummary = codingSchemeRenderingList.getCodingSchemeRendering(i).getCodingSchemeSummary();
//					if(printObjects){
//						System.out.println("CodingSchemeRendering: ");
//						System.out.println(PrintUtility.codingSchemeSummary(codingSchemeSummary, 1));
//					}
					// TODO: not certain this is correct
					if(codingSchemeSummary.getLocalName().equals(codingSchemeName) && codingSchemeSummary.getRepresentsVersion().equals(versionOrTag.getVersion())){
						found = true;
					}
				}
				
				
				if(found){
					codedNodeSet = lexBigService.getNodeSet(codingSchemeName, versionOrTag , entityTypes);
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
				
			if(found && (filters != null)){
				for(ResolvedFilter filter : filters){
					CommonUtils.filterCodedNodeSetByResolvedFilter(filter, codedNodeSet);
				}
			}
		}
	
		
		return codedNodeSet;
	}
	
	
	public static boolean validateMappingCodingScheme(MappingExtension mappingExtension, String uri, String version){
		try {
			if(mappingExtension != null){
				return mappingExtension.
					isMappingCodingScheme(
							uri, 
							Constructors.createCodingSchemeVersionOrTagFromVersion(version));
			}
			else {
				return false;
			}
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}
	
		
}
