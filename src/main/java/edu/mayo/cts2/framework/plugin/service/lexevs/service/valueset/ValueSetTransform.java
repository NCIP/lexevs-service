/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import javax.annotation.Resource;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.core.types.EntryState;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntrySummary;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNamePair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;


/**
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 */
@Component
public class ValueSetTransform
		extends
		AbstractBaseTransform<ValueSetCatalogEntry, org.LexGrid.valueSets.ValueSetDefinition, ValueSetCatalogEntrySummary, org.LexGrid.valueSets.ValueSetDefinition> {

	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.transform.LexEvsToCTS2Transformer#transformFullDescription(java.lang.Object)
	 */
	@Override
	public ValueSetCatalogEntry transformFullDescription(ValueSetDefinition data) {
		ValueSetCatalogEntry vsce = new ValueSetCatalogEntry();
		vsce.setAbout(data.getValueSetDefinitionURI());
		if(data.getIsActive()){
			vsce.setEntryState(EntryState.ACTIVE);
		}
		vsce.setFormalName(data.getValueSetDefinitionName());
		if(data.getEntityDescription() != null){
			String content = data.getEntityDescription().getContent();
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(content));
			
			vsce.setResourceSynopsis(description);
		}
		
		ValueSetNamePair pair = this.valueSetNameTranslator
				.getDefinitionNameAndVersion(data.getValueSetDefinitionURI());
		vsce.setValueSetName(pair.getValueSetName());
		vsce.addAlternateID(pair.getValueSetName());

		ValueSetDefinitionReference vsdReference = null;
		vsdReference = this.getTransformUtils().toValueSetDefinitionReference(
				data.getValueSetDefinitionName(),
				data.getValueSetDefinitionURI());
		vsce.setCurrentDefinition(vsdReference);
		return vsce;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.transform.LexEvsToCTS2Transformer#transformSummaryDescription(java.lang.Object)
	 */
	@Override
	public ValueSetCatalogEntrySummary transformSummaryDescription(
			ValueSetDefinition data) {

		ValueSetCatalogEntrySummary vsCatEntry = new ValueSetCatalogEntrySummary();

		vsCatEntry.setAbout(data.getValueSetDefinitionURI());
		vsCatEntry.setFormalName(data.getValueSetDefinitionName());

		if (data.getEntityDescription() != null) {
			String content = data.getEntityDescription().getContent();
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(content));

			vsCatEntry.setResourceSynopsis(description);
		}

		ValueSetNamePair pair = this.valueSetNameTranslator
				.getDefinitionNameAndVersion(data.getValueSetDefinitionURI());
		vsCatEntry.setValueSetName(pair.getValueSetName());
		vsCatEntry.setHref(this.getUrlConstructor().createValueSetUrl(
				pair.getValueSetName()));
		ValueSetDefinitionReference vsdReference = null;
		vsdReference = this.getTransformUtils().toValueSetDefinitionReference(
				data.getValueSetDefinitionName(),
				data.getValueSetDefinitionURI());
		vsCatEntry.setCurrentDefinition(vsdReference);
		vsCatEntry.setResourceName(data.getValueSetDefinitionName());

		return vsCatEntry;
	}

}
