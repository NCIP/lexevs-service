package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.mapversion.MapEntryDirectoryEntry;

@Component
public class MappingToMapEntryTransform {

	public MappingToMapEntryTransform() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MapEntry transform(Mapping mapping){
		throw new UnsupportedOperationException("Transform to MapEntry is under construction");
	}
	
	
	public MapEntryDirectoryEntry transform(ResolvedConceptReference resolvedConceptReference) {
		throw new UnsupportedOperationException("Transform to MapEntryDirectoryEntry is under construction");
	}
	
	
	
}
