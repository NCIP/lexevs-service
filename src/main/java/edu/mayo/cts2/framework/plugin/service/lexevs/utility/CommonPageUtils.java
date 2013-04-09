/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

/**
 * @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class CommonPageUtils {
	public static <T extends ResourceQuery> ResolvedConceptReferenceResults getResolvedConceptReferenceResultsPage(
			LexBIGService lexBigService, 
			QueryData<T> queryData,
			SortCriteria sortCriteria, 
			Page page){
		ResolvedConceptReferenceResults results = null;
		ResolvedConceptReferencesIterator iterator;
		CodedNodeSet codedNodeSet;
		
		codedNodeSet = CommonResourceSummaryUtils.getCodedNodeSet(lexBigService, queryData, sortCriteria);
		if(codedNodeSet != null){
			iterator = CommonUtils.getResolvedConceptReferencesIterator(codedNodeSet, sortCriteria);
			results = CommonPageUtils.getPageFromIterator(iterator, page);
		}
		
		return results;
	}
	
	public static ResolvedConceptReferenceResults getPageFromIterator(ResolvedConceptReferencesIterator iterator,
			Page page) {
		boolean atEnd = false;
		ResolvedConceptReference[] resolvedConceptReferences = null;
		ResolvedConceptReferenceList resolvedConceptReferenceList = null;
		int start = 0, end = 0;
		try {
			if(iterator != null){
				if(page != null){
					start = page.getStart();
					end = page.getEnd();
				}
				else{
					end = iterator.numberRemaining();
				}
				
				if(end > iterator.numberRemaining()){
					end = iterator.numberRemaining();
					atEnd = true;				
				}
				resolvedConceptReferenceList = iterator.get(start, end);
				// Get array of resolved concept references
				
				if(resolvedConceptReferenceList != null){
					resolvedConceptReferences = resolvedConceptReferenceList.getResolvedConceptReference();
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
	
	
	
	public static  <T> List<T> getRenderingList(List<T> list, Page page){
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
	

	
	public static <T> Object[] getRenderingPage(List<T> list, Page page){
		Object [] renderedArray = list.toArray(new CodingScheme[0]);
		Object [] renderedPage = CommonPageUtils.getPageFromArray(renderedArray, page);		
		return renderedPage;
	}
	
	public static <T> Object[] getPageFromArray(T[] data, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
		Object [] csPage = null;
		
		if(end > data.length){
			end = data.length;
		}
		
		if ((start == 0) && (end == data.length)) {
			csPage = data.clone();
		} 
		else if(start < end){
			
			int size = end - start;
			csPage = new Object [size];
			
			for (int i = 0; i < csPage.length; i++) {
				csPage[i] = data[start + i];
			}
		}
	
		return csPage;
	}

	

}
