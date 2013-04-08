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

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.relations.Relations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.MapReference;
import edu.mayo.cts2.framework.model.core.SourceAndNotation;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.TransformUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Tuple;

/**
 * Transforms a LexGrid CodingScheme into a CTS2 MapVersion.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class CodingSchemeToMapVersionTransform extends AbstractBaseTransform <MapVersion, CodingScheme, MapVersionDirectoryEntry, CodingSchemeRendering> {

	public CodingSchemeToMapVersionTransform(){
		super();
	}
	
	public CodingSchemeToMapVersionTransform(
		VersionNameConverter versionNameConverter){
		super();
		this.setVersionNameConverter(versionNameConverter);
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
				mapVersion.addProperty(TransformUtils.toProperty(property));
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
		
	
		Tuple<CodeSystemVersionReference> fromAndTo = this.getFromToCodingSchemes(codingScheme);
		mapVersion.setFromCodeSystemVersion(fromAndTo.getOne());
		mapVersion.setToCodeSystemVersion(fromAndTo.getTwo());
		
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
	
	private String getName(CodingScheme codingScheme){
		return this.getName(codingScheme.getCodingSchemeName(), 
					codingScheme.getRepresentsVersion());
	}
	
	protected Tuple<CodeSystemVersionReference> getFromToCodingSchemes(CodingScheme codingScheme){
		Assert.isTrue(
			codingScheme.getRelationsCount() == 1,
			"Only ONE Relations container is allowed in a Mapping Coding Scheme.");
		
		Relations relations = codingScheme.getRelations(0);
		String source = relations.getSourceCodingScheme();
		String sourceVersion = relations.getSourceCodingSchemeVersion();
		String target = relations.getTargetCodingScheme();
		String targetVersion = relations.getTargetCodingSchemeVersion();
		
		return new Tuple<CodeSystemVersionReference>(
				this.getTransformUtils().toCodeSystemVersionReference(source, sourceVersion),
				this.getTransformUtils().toCodeSystemVersionReference(target, targetVersion)
		);
	}
	
	private String getName(CodingSchemeRendering codingScheme){
		return this.getName(codingScheme.getCodingSchemeSummary().getLocalName(), 
					codingScheme.getCodingSchemeSummary().getRepresentsVersion());
	}
	
	private String getName(String name, String version){
		return this.getVersionNameConverter().
				toCts2VersionName(name, version);
	}
	
}
