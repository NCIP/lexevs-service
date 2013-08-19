/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import edu.mayo.cts2.framework.model.mapversion.MapVersionListEntry;
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
public class CodingSchemeToMapVersionTransform extends AbstractBaseTransform <MapVersionListEntry, CodingScheme, MapVersionDirectoryEntry, CodingSchemeRendering> {

	public CodingSchemeToMapVersionTransform(){
		super();
	}
	
	public CodingSchemeToMapVersionTransform(
		VersionNameConverter versionNameConverter){
		super();
		this.setVersionNameConverter(versionNameConverter);
	}

	@Override
	public MapVersionListEntry transformFullDescription(CodingScheme codingScheme){
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
		
		String mapName = codingScheme.getCodingSchemeName();
		MapReference mapReference = new MapReference();
		mapReference.setContent(mapName);
		mapReference.setUri(codingScheme.getCodingSchemeURI());
		mapReference.setHref(this.getUrlConstructor().createMapUrl(mapName));
		mapVersion.setVersionOf(mapReference);

		Tuple<CodeSystemVersionReference> fromAndTo = this.getFromToCodingSchemes(codingScheme);
		mapVersion.setFromCodeSystemVersion(fromAndTo.getOne());
		mapVersion.setToCodeSystemVersion(fromAndTo.getTwo());
		
		MapVersionListEntry listEntry = new MapVersionListEntry();
		listEntry.setEntry(mapVersion);
		listEntry.setResourceName(mapVersion.getMapVersionName());
		listEntry.setHref(
				this.getUrlConstructor().
					createMapVersionUrl(
						mapName, name));
		
		return listEntry;
	}
	
	@Override
	public MapVersionDirectoryEntry transformSummaryDescription(CodingSchemeRendering codingSchemeRendering){
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
		
		String mapName = codingSchemeSummary.getLocalName();
		MapReference mapReference = new MapReference();
		mapReference.setContent(mapName);
		mapReference.setUri(codingSchemeSummary.getCodingSchemeURI());
		mapReference.setHref(this.getUrlConstructor().createMapUrl(mapName));
		summary.setVersionOf(mapReference);
		
		summary.setHref(
			this.getUrlConstructor().
				createMapVersionUrl(
					mapName, name));
		
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
		
		//TODO
		return new Tuple<CodeSystemVersionReference>(
				this.getTransformUtils().toCodeSystemVersionReference(source, sourceVersion, null),
				this.getTransformUtils().toCodeSystemVersionReference(target, targetVersion, null)
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
