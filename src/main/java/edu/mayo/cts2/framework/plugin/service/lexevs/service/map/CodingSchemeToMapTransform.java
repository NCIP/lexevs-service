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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.relations.Relations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntrySummary;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.TransformUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Tuple;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class CodingSchemeToMapTransform 
	extends AbstractBaseTransform<MapCatalogEntry, CodingScheme, MapCatalogEntrySummary, CodingScheme> {

	@Override
	public MapCatalogEntry transformFullDescription(CodingScheme codingScheme) {
		if(codingScheme == null){
			return null;
		}
		
		MapCatalogEntry mapCatalogEntry = new MapCatalogEntry();

		mapCatalogEntry.setAbout(
				this.getUriHandler().getCodeSystemUri(codingScheme)
		);
		
		mapCatalogEntry.setMapName(codingScheme.getCodingSchemeName());
		
		mapCatalogEntry.setFormalName(codingScheme.getFormalName());
		
		if(codingScheme.getProperties() != null){
			for(Property property : codingScheme.getProperties().getProperty()){
				mapCatalogEntry.addProperty(TransformUtils.toProperty(property));
			}
		}
		
		for(String localName : codingScheme.getLocalName()){
			mapCatalogEntry.addKeyword(localName);
		}
		
		EntityDescription entityDescription = codingScheme.getEntityDescription();
		if(entityDescription != null){
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(entityDescription.getContent()));
			mapCatalogEntry.setResourceSynopsis(description);
		}

		Tuple<CodeSystemReference> fromAndTo = this.getFromToCodingSchemes(codingScheme);
		mapCatalogEntry.setFromCodeSystem(fromAndTo.getOne());
		mapCatalogEntry.setToCodeSystem(fromAndTo.getTwo());
		
		return mapCatalogEntry;
	}
	
	@Override
	public MapCatalogEntrySummary transformSummaryDescription(CodingScheme codingScheme) {
		if(codingScheme == null){
			return null;
		}
		
		MapCatalogEntrySummary mapCatalogEntrySummary = new MapCatalogEntrySummary();

		mapCatalogEntrySummary.setAbout(
				this.getUriHandler().getCodeSystemUri(codingScheme)
		);
		
		mapCatalogEntrySummary.setMapName(codingScheme.getCodingSchemeName());
		
		mapCatalogEntrySummary.setFormalName(codingScheme.getFormalName());
		
		EntityDescription entityDescription = codingScheme.getEntityDescription();
		if(entityDescription != null){
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(entityDescription.getContent()));
			mapCatalogEntrySummary.setResourceSynopsis(description);
		}

		Tuple<CodeSystemReference> fromAndTo = this.getFromToCodingSchemes(codingScheme);
		mapCatalogEntrySummary.setFromCodeSystem(fromAndTo.getOne());
		mapCatalogEntrySummary.setToCodeSystem(fromAndTo.getTwo());
		
		return mapCatalogEntrySummary;
	}
	
	protected Tuple<CodeSystemReference> getFromToCodingSchemes(CodingScheme codingScheme){
		Assert.isTrue(
			codingScheme.getRelationsCount() == 1,
			"Only ONE Relations container is allowed in a Mapping Coding Scheme.");
		
		Relations relations = codingScheme.getRelations(0);
		String source = relations.getSourceCodingScheme();
		String target = relations.getTargetCodingScheme();
		
		return new Tuple<CodeSystemReference>(
				this.getTransformUtils().toCodeSystemReference(source),
				this.getTransformUtils().toCodeSystemReference(target)
		);
	}
}
