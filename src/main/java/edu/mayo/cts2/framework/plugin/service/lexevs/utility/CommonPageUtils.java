/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

/**
 * @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public final class CommonPageUtils {
	
	private CommonPageUtils(){
		super();
	}
	
	public static <T extends ResourceQuery> ResolvedConceptReferenceResults getPage(
			LexBIGService lexBigService, 
			LexEVSValueSetDefinitionServices vsDefinitionServices,
			LexEVSResolvedValueSetService resolvedVSService,
			QueryData<T> queryData,
			SortCriteria cts2SortCriteria, 
			Page page){
		ResolvedConceptReferenceResults lexResolvedConceptResults = null;
		ResolvedConceptReferencesIterator lexResolvedConceptIterator;
		CodedNodeSet lexCodedNodeSet;
		
		lexCodedNodeSet = CommonResourceUtils.getLexCodedNodeSet(lexBigService, vsDefinitionServices, 
				resolvedVSService, queryData, cts2SortCriteria);
				
		if(lexCodedNodeSet != null){
			lexResolvedConceptIterator = CommonUtils.getLexResolvedConceptIterator(lexCodedNodeSet, cts2SortCriteria);
			lexResolvedConceptResults = CommonPageUtils.getPage(lexResolvedConceptIterator, page);
		}
		
		return lexResolvedConceptResults;
	}

	/**
	 * A way of telling whether or not a source is exhausted or not is
	 * to ask for one more result than you want, and if you get it, you
	 * will know that there are more results left. This will assume the
	 * list passed in contains '#resultsWanted + 1' -- and return back
	 * a list of '#resultsWanted' plus a boolean flag.
	 *
	 * @param list the list
	 * @param expectec the actual expected number of results
	 * @return true if 'atEnd' - false if not
	 */
	public static <T> boolean adjustForOnExtraResult(List<T> list, int expected){
		if(list.size() <= expected){
			return true;
		} else {
			list.remove(list.size() - 1);
			return false;
		}
	}
	
	public static ResolvedConceptReferenceResults getPage(
			ResolvedConceptReferencesIterator iterator,
			Page page) {
			
		List<ResolvedConceptReference> list = new ArrayList<ResolvedConceptReference>();
		try {
			if(iterator != null){
				int start = page.getStart();
				int max = page.getMaxToReturn();

				int i = 0;
				while(iterator.hasNext() && list.size() < max){
					ResolvedConceptReference ref = iterator.next();
					if(i >= start){
						list.add(ref);
					}
					i++;
				}
				
				return new ResolvedConceptReferenceResults(
					list.toArray(new ResolvedConceptReference[list.size()]), 
					!iterator.hasNext());
			} else {
				return new ResolvedConceptReferenceResults(
					list.toArray(new ResolvedConceptReference[0]), true);
			}
	
		} catch (LBException e) {
			throw new RuntimeException(e);
		}	
	}

	public static  <T> List<T> getPage(List<T> list, Page page){
		int start = page.getStart();
		int end = page.getEnd();
		
		
		if(end > list.size()){
			end = list.size();
		}
		if (list.size() > start ) {
			return list.subList(start, end);
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public static <T> T[] getPage(T[] data, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
	    T typeVar = null;
	    T[] csPage = null;
		
		if(end > data.length){
			end = data.length;
		}
		
		if ((start == 0) && (end == data.length)) {
			csPage = data.clone();
		} 
		else if(start < end){
			
			int size = end - start;
			List<T> arrayList = new ArrayList<T>(); 
			for (int i = 0; i < size; i++) {
				typeVar = data[start + i];
				arrayList.add(typeVar);
			}		
			csPage = arrayList.toArray((T[]) Array.newInstance(typeVar.getClass(),0));
		}
	
		return csPage;
	}

	

}
