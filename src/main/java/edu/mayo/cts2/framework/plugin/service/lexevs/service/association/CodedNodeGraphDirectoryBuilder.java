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

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.Utility.Constructors;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingPropertyReference;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.service.command.restriction.AssociationQueryServiceRestrictions;

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
	
	public CodedNodeGraphDirectoryBuilder restrict(AssociationQueryServiceRestrictions restrictions){
		if(restrictions != null && 
				restrictions.getPredicateEntity() != null &&
				restrictions.getPredicateEntity().getEntityName() != null &&
				restrictions.getPredicateEntity().getEntityName().getName() != null){
			String predicateName = restrictions.getPredicateEntity().getEntityName().getName();
			
			try {
				this.updateState(
						this.getState().restrictToAssociations(Constructors.createNameAndValueList(predicateName), null));
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
		
		return this;
	}

}
