package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;

public class CommonUtils {

	// Private constructor - case where every method in class is static
	private CommonUtils() {
		super();
	}
	
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
	
	public static ResolvedConceptReferenceResults getReferenceResultPage(
			CodedNodeSet codedNodeSet, 
			SortCriteria sortCriteria, 
			Page page){
		boolean atEnd = false;
		ResolvedConceptReference[] resolvedConceptReferences = null;
		ResolvedConceptReferencesIterator iterator;
		ResolvedConceptReferenceList resolvedConceptReferenceList = null;
		int start = 0, end = 0;
		try {
			iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, sortCriteria);
			
			if(iterator != null){
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
