/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.map;

import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.relations.Relations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.core.constants.URIHelperInterface;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.map.MapCatalogEntry;
import edu.mayo.cts2.framework.model.map.MapCatalogEntryListEntry;
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
	extends AbstractBaseTransform<MapCatalogEntryListEntry, CodingScheme, MapCatalogEntrySummary, CodingScheme> {

	@Override
	public MapCatalogEntryListEntry transformFullDescription(CodingScheme codingScheme) {
		if(codingScheme == null){
			return null;
		}
		
		MapCatalogEntry mapCatalogEntry = new MapCatalogEntry();

		mapCatalogEntry.setAbout(
				this.getUriHandler().getCodeSystemUri(codingScheme)
		);
		
		String mapName = codingScheme.getCodingSchemeName();
		
		mapCatalogEntry.setMapName(mapName);
		mapCatalogEntry.setVersions(
			this.getUrlConstructor().createMapUrl(mapName + "/" + URIHelperInterface.VERSIONS));
		
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
		
		MapCatalogEntryListEntry listEntry = new MapCatalogEntryListEntry();
		listEntry.setEntry(mapCatalogEntry);
		listEntry.setHref(this.getUrlConstructor().createMapUrl(mapName));
		listEntry.setResourceName(mapCatalogEntry.getMapName());
		
		return listEntry;
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
		
		String mapName = codingScheme.getCodingSchemeName();
		
		mapCatalogEntrySummary.setMapName(mapName);
		mapCatalogEntrySummary.setHref(this.getUrlConstructor().createMapUrl(mapName));
		
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
		
		//TODO:
		return new Tuple<CodeSystemReference>(
				this.getTransformUtils().toCodeSystemReference(source, null),
				this.getTransformUtils().toCodeSystemReference(target, null)
		);
	}
}
