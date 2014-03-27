package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.ResourceVersionDescriptionDirectoryEntry;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.core.ValueSetReference;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntrySummary;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNamePair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

public class LexEVSValueSetDefinitionToValueSetEntryTransform
		extends
		AbstractBaseTransform<ValueSetCatalogEntryListEntry, 
		org.LexGrid.valueSets.ValueSetDefinition, ValueSetCatalogEntrySummary,org.LexGrid.valueSets.ValueSetDefinition> {
	
	@Resource
	private UriHandler uriHandler;
	
	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;
	
	@Resource
	private LexBIGService lexBigService;
	

	public LexEVSValueSetDefinitionToValueSetEntryTransform() {
		super();
	}

	@Override
	public ValueSetCatalogEntryListEntry transformFullDescription(
			ValueSetDefinition data) {
		
//		ValueSetCatalogEntryListEntry vsCatEntry = new ValueSetCatalogEntryListEntry();
//		ValueSetCatalogEntry entry = new ValueSetCatalogEntry();
//
//		vsCatEntry.setEntry(entry);

		throw new UnsupportedOperationException();

	}

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
		;
		vsCatEntry.setCurrentDefinition(vsdReference);
		vsCatEntry.setResourceName(data.getValueSetDefinitionName());

		return vsCatEntry;
	}




}
