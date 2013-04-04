package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

public class ResolvedConceptReferenceResults {
	private boolean atEnd;
	private ResolvedConceptReference [] resolvedConceptReference;
	
	public ResolvedConceptReferenceResults(ResolvedConceptReference [] resolvedConceptReference, boolean atEnd){	
		this.resolvedConceptReference = null;
		if(resolvedConceptReference != null){
			this.resolvedConceptReference = resolvedConceptReference.clone();
		}
		this.atEnd = atEnd;
	}

	public boolean isAtEnd() {
		return atEnd;
	}

	public void setAtEnd(boolean atEnd) {
		this.atEnd = atEnd;
	}

	public ResolvedConceptReference[] getResolvedConceptReference() {
		return resolvedConceptReference;
	}

	public void setResolvedConceptReference(
			ResolvedConceptReference[] resolvedConceptReference) {
		this.resolvedConceptReference = resolvedConceptReference.clone();
	}
	
}
	
