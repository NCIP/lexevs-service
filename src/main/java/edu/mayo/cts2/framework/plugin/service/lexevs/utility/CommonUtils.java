package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;

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
		CodingSchemeRendering [] csRenderingPage = null;
		
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
	
		return csRenderingPage;
	}
	
}
