package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;

public class CommonUtils {

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
		else {
			
			int size = end - start;
			csRenderingPage = new CodingSchemeRendering [size];
			
			for (int i = 0; i < csRenderingPage.length; i++) {
				csRenderingPage[i] = csRendering[start + i];
			}
		}
	
		return csRenderingPage;
	}
}
