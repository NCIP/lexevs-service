package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.core.types.EntryState;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNamePair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

@Component
public class ValueSetTransform
		extends
		AbstractBaseTransform<ValueSetCatalogEntry, org.LexGrid.valueSets.ValueSetDefinition, ValueSetCatalogEntry, org.LexGrid.valueSets.ValueSetDefinition> {
	
	@Resource
	private UriHandler uriHandler;
	
	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;
	
	@Resource
	private LexBIGService lexBigService;
	
	@Override
	public ValueSetCatalogEntry transformFullDescription(ValueSetDefinition data) {
		ValueSetCatalogEntry vsce = new ValueSetCatalogEntry();
		vsce.setAbout(data.getValueSetDefinitionURI());
		if(data.getIsActive()){
			vsce.setEntryState(EntryState.ACTIVE);
		}
		vsce.setFormalName(data.getValueSetDefinitionName());
		//vsce.setSourceAndRole(vSourceAndRoleList);
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
		//vsce.setCurrentResolution(currentResolution);
		
		
		return vsce;
	}

	@Override
	public ValueSetCatalogEntry transformSummaryDescription(
			ValueSetDefinition data) {
		// TODO Auto-generated method stub
		return null;
	}

}
