/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import java.util.Set;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingPropertyReference;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;

public class CodedNodeGraphDirectoryBuilder 
	extends 
	AbstractStateBuildingDirectoryBuilder<CodedNodeGraph,AssociationDirectoryEntry>{

	public CodedNodeGraphDirectoryBuilder(
			CodedNodeGraph initialState,
			Callback<CodedNodeGraph, AssociationDirectoryEntry> callback,
			Set<MatchAlgorithmReference> matchAlgorithmReferences,
			Set<StateAdjustingPropertyReference<CodedNodeGraph>> stateAdjustingPropertyReferences) {
		super(initialState, 
				callback, 
				matchAlgorithmReferences,
				stateAdjustingPropertyReferences);
	}

}
