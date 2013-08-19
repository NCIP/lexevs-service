/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
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
		uriEntityName.setHref(this.getTransformUtils().createEntityHref(subject));
		
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
				//We can reuse these in each entry
				PredicateReference predReference = new PredicateReference();
				predReference.setName(association.getAssociationName());
				predReference.setUri(
						this.getUriHandler().
							getPredicateUri(
									subject.getCodingSchemeURI(),
									subject.getCodingSchemeVersion(),
									association.getAssociationName()));
				CodeSystemVersionReference codeSystemVersionReference = this.getTransformUtils().toCodeSystemVersionReference(
						this.getCodingSchemeNameTranslator().translateFromLexGrid(subject.getCodingSchemeName()), 
						subject.getCodingSchemeVersion(),
						subject.getCodingSchemeURI());
	
				for (AssociatedConcept target : association.getAssociatedConcepts()
						.getAssociatedConcept()) {
					if(counter++ >= ref.getStart()){
						AssociationDirectoryEntry entry = new AssociationDirectoryEntry();
						entry.setSubject(uriEntityName);
						entry.setAssertedBy(codeSystemVersionReference);
						entry.setPredicate(predReference);
						StatementTarget st = new StatementTarget();
						uriEntityName = new URIAndEntityName();
						uriEntityName.setName(target.getCode());
						uriEntityName.setNamespace(target.getCodeNamespace());
						uriEntityName.setUri(this.getUriHandler().getEntityUri(target));
						uriEntityName.setHref(this.getTransformUtils().createEntityHref(target));
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
