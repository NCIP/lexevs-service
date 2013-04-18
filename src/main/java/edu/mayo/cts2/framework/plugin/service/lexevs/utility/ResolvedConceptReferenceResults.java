package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

public class ResolvedConceptReferenceResults {
	private boolean atEnd;
	private ResolvedConceptReference [] lexResolvedConceptReference;
	
	public ResolvedConceptReferenceResults(ResolvedConceptReference [] lexResolvedConceptReference, boolean atEnd){	
		this.lexResolvedConceptReference = null;
		if(lexResolvedConceptReference != null){
			this.lexResolvedConceptReference = lexResolvedConceptReference.clone();
		}
		this.atEnd = atEnd;
	}

	public boolean isAtEnd() {
		return atEnd;
	}

	public void setAtEnd(boolean atEnd) {
		this.atEnd = atEnd;
	}

	public ResolvedConceptReference[] getLexResolvedConceptReference() {
		return lexResolvedConceptReference;
	}

	public void setLexResolvedConceptReference(
			ResolvedConceptReference[] resolvedConceptReference) {
		this.lexResolvedConceptReference = resolvedConceptReference.clone();
	}
	
}
	
