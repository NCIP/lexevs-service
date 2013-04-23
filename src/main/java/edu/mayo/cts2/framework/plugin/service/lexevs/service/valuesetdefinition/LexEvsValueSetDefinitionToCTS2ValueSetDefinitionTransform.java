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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import org.LexGrid.valueSets.CodingSchemeReference;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.PropertyReference;
import org.LexGrid.valueSets.ValueSetDefinitionReference;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.ValueSetReference;
import edu.mayo.cts2.framework.model.core.types.AssociationDirection;
import edu.mayo.cts2.framework.model.core.types.SetOperator;
import edu.mayo.cts2.framework.model.valuesetdefinition.AssociatedEntitiesReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.CompleteCodeSystemReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.CompleteValueSetReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.PropertyQueryReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.SpecificEntityList;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.types.LeafOrAll;
import edu.mayo.cts2.framework.model.valuesetdefinition.types.TransitiveClosure;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;

/**
 * Transforms a LexEVS ValueSetDefinition object into a CTS2 ValueSetDefinition object.
 * 
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform 
	extends AbstractBaseTransform<
		ValueSetDefinition, 
		org.LexGrid.valueSets.ValueSetDefinition, 
		ValueSetDefinitionDirectoryEntry, 
		org.LexGrid.valueSets.ValueSetDefinition> {

	public ValueSetDefinition transformFullDescription(org.LexGrid.valueSets.ValueSetDefinition lexEvsVSD) {
		if (lexEvsVSD == null) {
			return null;
		}
		
		ValueSetDefinition cts2VSD = new ValueSetDefinition();

		cts2VSD.setAbout(lexEvsVSD.getValueSetDefinitionURI());
		cts2VSD.setFormalName(lexEvsVSD.getValueSetDefinitionURI());

		ValueSetReference vsReference = new ValueSetReference();
		vsReference.setContent(lexEvsVSD.getValueSetDefinitionURI());
		cts2VSD.setDefinedValueSet(vsReference);
		
		for(DefinitionEntry entry : lexEvsVSD.getDefinitionEntry()){
			ValueSetDefinitionEntry cts2Entry = new ValueSetDefinitionEntry();
			
			if(entry.getCodingSchemeReference() != null){
				this.add(entry.getCodingSchemeReference(), cts2Entry);
			}
			if(entry.getEntityReference() != null){
				this.add(entry.getEntityReference(), cts2Entry);
			}
			if(entry.getPropertyReference() != null){
				this.add(entry.getPropertyReference(), cts2Entry);
			}
			if(entry.getPropertyReference() != null){
				this.add(entry.getValueSetDefinitionReference(), cts2Entry);
			}
			
			cts2Entry.setEntryOrder(entry.getRuleOrder());
			cts2Entry.setOperator(this.toSetOperator(entry.getOperator()));
		}
	
		return cts2VSD;
	}
	
	private SetOperator toSetOperator(DefinitionOperator operator){
		switch(operator) {
			case OR : return SetOperator.UNION;
			case AND : return SetOperator.INTERSECT;
			case SUBTRACT : return SetOperator.SUBTRACT;
			default : throw new IllegalStateException();
		}
	}
	
	private void add(
			ValueSetDefinitionReference valueSetDefinitionReference,
			ValueSetDefinitionEntry cts2Entry) {
		CompleteValueSetReference vsr = new CompleteValueSetReference();
		
		cts2Entry.setCompleteValueSet(vsr);
	}

	private void add(
			PropertyReference propertyReference,
			ValueSetDefinitionEntry cts2Entry) {
		PropertyQueryReference pqr = new PropertyQueryReference();
		
		cts2Entry.setPropertyQuery(pqr);
	}

	private void add(
			EntityReference entityReference,
			ValueSetDefinitionEntry cts2Entry) {
		URIAndEntityName uriAndName = new URIAndEntityName();
		uriAndName.setName(entityReference.getEntityCode());
		uriAndName.setNamespace(entityReference.getEntityCodeNamespace());
		
		if(entityReference.getReferenceAssociation() != null){
			AssociatedEntitiesReference ref = new AssociatedEntitiesReference();
			ref.setReferencedEntity(uriAndName);
			ref.setDirection(
				entityReference.getTargetToSource() ? 
					AssociationDirection.TARGET_TO_SOURCE : 
					AssociationDirection.SOURCE_TO_TARGET);
			
			ref.setTransitivity(
					entityReference.getTransitiveClosure() ? 
						TransitiveClosure.TRANSITIVE_CLOSURE : 
						TransitiveClosure.DIRECTLY_ASSOCIATED);
			
			ref.setLeafOnly(
					entityReference.getLeafOnly() ?
						LeafOrAll.LEAF_ONLY :
						LeafOrAll.ALL_INTERMEDIATE_NODES);
			
			PredicateReference predicate = new PredicateReference();
			predicate.setName(entityReference.getReferenceAssociation());
			
			//namespace for a predicate is not guaranteed in LexEVS... not sure 
			//how we want to handle this. For now, assume the same namespace
			//as the starting entity.
			predicate.setNamespace(entityReference.getEntityCodeNamespace());
			ref.setPredicate(predicate);
			
			cts2Entry.setAssociatedEntities(ref);
		} else {
			SpecificEntityList sel = new SpecificEntityList();
			
			sel.addReferencedEntity(uriAndName);
			
			cts2Entry.setEntityList(sel);
		}
	}

	private void add(
			CodingSchemeReference codingSchemeReference,
			ValueSetDefinitionEntry cts2Entry) {
		CompleteCodeSystemReference ccr = new CompleteCodeSystemReference();

		String codeSystemName = codingSchemeReference.getCodingScheme();
		
		ccr.setCodeSystem(
			this.getTransformUtils().toCodeSystemReference(codeSystemName, null));
		
		cts2Entry.setCompleteCodeSystem(ccr);
	}

	@Override
	public ValueSetDefinitionDirectoryEntry transformSummaryDescription(org.LexGrid.valueSets.ValueSetDefinition lexEvsVSD) {
		if (lexEvsVSD == null) {
			return null;
		}
		
		ValueSetDefinitionDirectoryEntry vsdDirEntry = new ValueSetDefinitionDirectoryEntry();
		
		vsdDirEntry.setAbout(lexEvsVSD.getValueSetDefinitionURI());
		vsdDirEntry.setFormalName(lexEvsVSD.getValueSetDefinitionURI());

		ValueSetReference vsReference = new ValueSetReference();
		vsReference.setContent(lexEvsVSD.getValueSetDefinitionURI());
		vsdDirEntry.setDefinedValueSet(vsReference);
		
		return vsdDirEntry;
	}
	
}
