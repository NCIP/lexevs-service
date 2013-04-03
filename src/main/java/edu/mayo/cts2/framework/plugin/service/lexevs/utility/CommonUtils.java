package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.relations.Relations;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;

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

	/*
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
*/
	public static <T> Object[] getRenderingPage(T[] codingScheme, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
		Object [] csPage = null;
		
		if(end > codingScheme.length){
			end = codingScheme.length;
		}
		
		if ((start == 0) && (end == codingScheme.length)) {
			csPage = codingScheme.clone();
		} 
		else if(start < end){
			
			int size = end - start;
			csPage = new Object [size];
			
			for (int i = 0; i < csPage.length; i++) {
				csPage[i] = codingScheme[start + i];
			}
		}
	
		return csPage;
	}

	public static boolean containsCodingScheme(String relationCodingScheme, Set<NameOrURI> codeSystemSet) {

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
	
	public static List<CodingScheme> resolveToCodingSchemeList(
			LexBIGService lexBigService, 
			CodingSchemeRendering[] codingSchemeRenderingArray) {
		List<CodingScheme> codingSchemeList = new ArrayList<CodingScheme>();
		
		if (codingSchemeRenderingArray != null && codingSchemeRenderingArray.length > 0) {
			for (int i=0; i<codingSchemeRenderingArray.length; i++) {
				CodingScheme codingScheme = CommonUtils.getCodingScheme(lexBigService, codingSchemeRenderingArray[i]);
				codingSchemeList.add(codingScheme);
			}
		}
		return codingSchemeList;
	}
	
	public static CodingScheme getCodingSchemeForCodeSystemRestriction(
			LexBIGService lexBigService, 
			CodingSchemeRendering render, 
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
		
		if (csrMapRoleValue.equals(Constants.MAP_TO_ROLE) && CommonUtils.containsCodingScheme(targetCodingScheme, codeSystemSet)) {
			return codingScheme;
		}
		
		if (csrMapRoleValue.equals(Constants.MAP_FROM_ROLE) && CommonUtils.containsCodingScheme(sourceCodingScheme, codeSystemSet)) { 
			return codingScheme;
		}
		
		if (csrMapRoleValue.equals(Constants.BOTH_MAP_ROLES) && 
				CommonUtils.isCodingSchemeFound(targetCodingScheme, sourceCodingScheme, codeSystemSet)) {
			return codingScheme;
		}
		
		return notFoundCodingScheme;
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
	

		
}
