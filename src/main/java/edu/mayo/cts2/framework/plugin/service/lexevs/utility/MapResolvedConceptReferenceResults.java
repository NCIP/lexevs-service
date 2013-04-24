package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;

public class MapResolvedConceptReferenceResults extends ResolvedConceptReferenceResults {
	
	public MapResolvedConceptReferenceResults(
			ResolvedConceptReference[] lexResolvedConceptReference,
			boolean atEnd) {
		super(lexResolvedConceptReference, atEnd);
	}

	public MapResolvedConceptReferenceResults(
			NameVersionPair mapVersion,
			ResolvedConceptReferenceResults lexResolvedConceptReferenceResults) {
		super(transform(mapVersion, lexResolvedConceptReferenceResults.getLexResolvedConceptReference()), 
				lexResolvedConceptReferenceResults.isAtEnd());
	}

	private static ResolvedConceptReference[] transform(
			NameVersionPair mapVersion,
			ResolvedConceptReference[] refs) {
		
		List<MapResolvedConceptReference> returnList = new ArrayList<MapResolvedConceptReference>();
		for(ResolvedConceptReference ref : refs){
			returnList.add(new MapResolvedConceptReference(mapVersion, ref));
		}
		
		return returnList.toArray(new ResolvedConceptReference[returnList.size()]);
	}

}
	
