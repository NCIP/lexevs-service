/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

public class ResolvedConceptReferenceAssociationPage {
	
	protected enum Direction {SOURCEOF, TARGETOF}
	
	private Direction direction;
	
	private int start;
	
	private int end;
	
	private ResolvedConceptReference resolvedConceptReference;

	protected ResolvedConceptReferenceAssociationPage(Direction direction,
			int start, int end,
			ResolvedConceptReference resolvedConceptReference) {
		super();
		this.direction = direction;
		this.start = start;
		this.end = end;
		this.resolvedConceptReference = resolvedConceptReference;
	}

	public ResolvedConceptReference getResolvedConceptReference() {
		return resolvedConceptReference;
	}

	public void setResolvedConceptReference(ResolvedConceptReference resolvedConceptReference) {
		this.resolvedConceptReference = resolvedConceptReference;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}
