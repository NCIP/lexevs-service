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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.MapReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.SourceAndNotation;
import edu.mayo.cts2.framework.model.core.StatementTarget;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsToCTS2Transformer;

/**
 * Transforms a LexGrid CodingScheme into a CTS2 MayVersion.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class CodingSchemeToMapVersionTransform implements LexEvsToCTS2Transformer <MapVersion, CodingScheme, MapVersionDirectoryEntry, CodingSchemeRendering> {
	
	@Resource
	private CodeSystemVersionNameConverter codeSystemVersionNameConverter;
	
	public CodingSchemeToMapVersionTransform(){
		super();
	}
	
	public CodingSchemeToMapVersionTransform(
		CodeSystemVersionNameConverter codeSystemVersionNameConverter){
		super();
		this.codeSystemVersionNameConverter = codeSystemVersionNameConverter;
	}

	@Override
	public MapVersion transformDescription(CodingScheme codingScheme){
		MapVersion mapVersion = new MapVersion();

		mapVersion.setAbout(codingScheme.getCodingSchemeURI());
		
		String name = this.getName(codingScheme);
		
		mapVersion.setMapVersionName(name);
		mapVersion.setDocumentURI(codingScheme.getCodingSchemeURI());
		mapVersion.setFormalName(codingScheme.getFormalName());
		
		if(codingScheme.getProperties() != null){
			for(Property property : codingScheme.getProperties().getProperty()){
				mapVersion.addProperty(this.toProperty(property));
			}
		}
		
		for(String localName : codingScheme.getLocalName()){
			mapVersion.addKeyword(localName);
		}
		
		EntityDescription entityDescription = codingScheme.getEntityDescription();
		if(entityDescription != null){
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(entityDescription.getContent()));
			mapVersion.setResourceSynopsis(description);
		}
		
		SourceAndNotation sourceAndNotation = new SourceAndNotation();
		sourceAndNotation.setSourceAndNotationDescription("LexEVS");
		
		mapVersion.setSourceAndNotation(sourceAndNotation);
		
		MapReference mapReference = new MapReference();
		mapReference.setContent(codingScheme.getCodingSchemeName());
		mapReference.setUri(codingScheme.getCodingSchemeURI());
		mapVersion.setVersionOf(mapReference);
		
		return mapVersion;
	}
	
	@Override
	public MapVersionDirectoryEntry transformDirectoryEntry(CodingSchemeRendering codingSchemeRendering){
		MapVersionDirectoryEntry summary = new MapVersionDirectoryEntry();
		
		CodingSchemeSummary codingSchemeSummary = codingSchemeRendering.getCodingSchemeSummary();
		
		String name = this.getName(codingSchemeRendering);
		
		summary.setMapVersionName(name);
		summary.setDocumentURI(codingSchemeSummary.getCodingSchemeURI());
		summary.setAbout(codingSchemeSummary.getCodingSchemeURI());
		
		summary.setFormalName(codingSchemeSummary.getFormalName());
		
		if (codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription() != null && 
				codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription().getContent() != null) {
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(
					codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription().getContent()));
			summary.setResourceSynopsis(description);			
		}
		
		return summary;
	}
	
	protected edu.mayo.cts2.framework.model.core.Property toProperty(Property property){
		edu.mayo.cts2.framework.model.core.Property cts2Prop = 
			new edu.mayo.cts2.framework.model.core.Property();
		
		PredicateReference predicateRef = new PredicateReference();
		predicateRef.setName(property.getPropertyName());
		predicateRef.setUri(property.getPropertyName());
		
		cts2Prop.setPredicate(predicateRef);
		
		StatementTarget target = new StatementTarget();
		target.setLiteral(ModelUtils.createOpaqueData(property.getValue().getContent()));
		
		cts2Prop.addValue(target);
		
		return cts2Prop;
	}
	
	private String getName(CodingScheme codingScheme){
		return this.getName(codingScheme.getCodingSchemeName(), 
					codingScheme.getRepresentsVersion());
	}
	
	private String getName(CodingSchemeRendering codingScheme){
		return this.getName(codingScheme.getCodingSchemeSummary().getLocalName(), 
					codingScheme.getCodingSchemeSummary().getRepresentsVersion());
	}
	
	private String getName(String name, String version){
		return this.codeSystemVersionNameConverter.
				toCts2CodeSystemVersionName(name, version);
	}
	
}
