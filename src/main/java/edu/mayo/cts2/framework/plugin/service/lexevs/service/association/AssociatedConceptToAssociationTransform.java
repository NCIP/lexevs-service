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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.association;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.StatementTarget;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;

/**
 * An AssociatedConcept -> Association Transform.
 */
@Component
public class AssociatedConceptToAssociationTransform
		extends
		AbstractBaseTransform<Void, Void, List<AssociationDirectoryEntry>, ResolvedConceptReferenceAssociationPage> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsToCTS2Transformer
	 * #transformDescription(java.lang.Object)
	 */
	@Override
	public Void transformFullDescription(Void data) {
		throw new UnsupportedOperationException(
				"Not reading Associations by ID.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsToCTS2Transformer
	 * #transformDirectoryEntry(java.lang.Object)
	 */
	@Override
	public List<AssociationDirectoryEntry> transformSummaryDescription(
			ResolvedConceptReferenceAssociationPage ref) {
		
		ResolvedConceptReference subject = ref.getResolvedConceptReference();

		List<AssociationDirectoryEntry> returnList = new ArrayList<AssociationDirectoryEntry>();

		URIAndEntityName uriEntityName = new URIAndEntityName();
		uriEntityName.setName(subject.getCode());
		uriEntityName.setNamespace(subject.getCodeNamespace());
		uriEntityName.setUri(this.getUriHandler().getEntityUri(subject));
		
		AssociationList associations;
		switch(ref.getDirection()){
			case SOURCEOF:{
				associations = subject.getSourceOf();
				break;
			}
			case TARGETOF: {
				associations = subject.getTargetOf();
				break;
			}
			default : throw new IllegalStateException();
		}
		int counter = 0;
		
		if(associations != null){
			for (Association association : associations.getAssociation()) {
				AssociationDirectoryEntry entry = new AssociationDirectoryEntry();
				entry.setSubject(uriEntityName);
				entry.setAssertedBy(
					this.getTransformUtils().toCodeSystemVersionReference(
							subject.getCodingSchemeName(), 
							subject.getCodingSchemeVersion(),
							subject.getCodingSchemeURI()));

				PredicateReference predReference = new PredicateReference();
				predReference.setName(association.getAssociationName());
				predReference.setUri(
						this.getUriHandler().
							getPredicateUri(
									subject.getCodingSchemeURI(),
									subject.getCodingSchemeVersion(),
									association.getAssociationName()));
	
				entry.setPredicate(predReference);
	
				for (AssociatedConcept target : association.getAssociatedConcepts()
						.getAssociatedConcept()) {
					if(counter++ >= ref.getStart()){
						StatementTarget st = new StatementTarget();
						uriEntityName = new URIAndEntityName();
						uriEntityName.setName(target.getCode());
						uriEntityName.setNamespace(target.getCodeNamespace());
						uriEntityName.setUri(this.getUriHandler().getEntityUri(target));
						st.setEntity(uriEntityName);
						entry.setTarget(st);
		
						returnList.add(entry);
						
						if(returnList.size() == ref.getEnd()){
							return returnList;
						}
					}
				}
	
			}
		}

		return returnList;
	}
}
