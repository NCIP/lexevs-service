/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.SourceAndNotation;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.TransformUtils;

/**
 * Transforms a LexGrid CodingScheme into a CTS2 CodeSystemVersion CatalogEntry.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class CodingSchemeToCodeSystemTransform 
	extends AbstractBaseTransform<CodeSystemVersionCatalogEntryListEntry, CodingScheme, CodeSystemVersionCatalogEntrySummary, CodingSchemeRendering>{
	//DescriptionDataType, DescriptionDataIN, DirectoryEntryDataType, DirecotoryEntryDataIN> 
	
	public CodingSchemeToCodeSystemTransform(){
		super();
	}
	
	public CodingSchemeToCodeSystemTransform(
		VersionNameConverter versionNameConverter){
		super();
		this.setVersionNameConverter(versionNameConverter);
	}
	
	/**
	 * Transform.
	 *
	 * @param codingScheme the coding scheme
	 * @return the code system version catalog entry
	 */
	@Override
	public CodeSystemVersionCatalogEntryListEntry transformFullDescription(CodingScheme codingScheme){
		CodeSystemVersionCatalogEntry codeSystemVersion = new CodeSystemVersionCatalogEntry();

		codeSystemVersion.setAbout(
				this.getUriHandler().getCodeSystemVersionUri(codingScheme)
		);
		
		String codingSchemeName = 
			this.getCodingSchemeNameTranslator().translateFromLexGrid(codingScheme.getCodingSchemeName());
		
		String name = this.getName(codingSchemeName, codingScheme.getRepresentsVersion());
		
		codeSystemVersion.setCodeSystemVersionName(name);
		codeSystemVersion.setOfficialResourceVersionId(codingScheme.getRepresentsVersion());
		codeSystemVersion.setDocumentURI(
				this.getUriHandler().getCodeSystemVersionUri(codingScheme)
		);
		
		codeSystemVersion.setFormalName(codingScheme.getFormalName());
		
		if(codingScheme.getProperties() != null){
			for(Property property : codingScheme.getProperties().getProperty()){
				codeSystemVersion.addProperty(TransformUtils.toProperty(property));
			}
		}
		
		for(String localName : codingScheme.getLocalName()){
			codeSystemVersion.addKeyword(localName);
		}
		
		EntityDescription entityDescription = codingScheme.getEntityDescription();
		if(entityDescription != null){
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(entityDescription.getContent()));
			codeSystemVersion.setResourceSynopsis(description);
		}
		
		SourceAndNotation sourceAndNotation = new SourceAndNotation();
		sourceAndNotation.setSourceAndNotationDescription("LexEVS");
		
		codeSystemVersion.setSourceAndNotation(sourceAndNotation);

		CodeSystemReference codeSystemReference = 
			this.getTransformUtils().toCodeSystemReference(codingSchemeName, codingScheme.getCodingSchemeURI());

		codeSystemVersion.setVersionOf(codeSystemReference);
		
		codeSystemVersion.setEntityDescriptions(
			this.getUrlConstructor().createEntitiesOfCodeSystemVersionUrl(
				codingSchemeName, 
				codingScheme.getRepresentsVersion()));
		
		CodeSystemVersionCatalogEntryListEntry listEntry = new CodeSystemVersionCatalogEntryListEntry();
		listEntry.setEntry(codeSystemVersion);
		listEntry.setResourceName(codeSystemVersion.getCodeSystemVersionName());
		
		listEntry.setHref(
				this.getUrlConstructor().
					createCodeSystemVersionUrl(
							codingSchemeName, 
							codingScheme.getRepresentsVersion()));
		
		return listEntry;
	}
	
	@Override
	public CodeSystemVersionCatalogEntrySummary transformSummaryDescription(CodingSchemeRendering codingSchemeRendering){
		CodeSystemVersionCatalogEntrySummary summary = new CodeSystemVersionCatalogEntrySummary();
		
		CodingSchemeSummary codingSchemeSummary = codingSchemeRendering.getCodingSchemeSummary();
		
		String codingSchemeName = 
			this.getCodingSchemeNameTranslator().translateFromLexGrid(codingSchemeSummary.getLocalName());
		
		String name = this.getName(codingSchemeName, codingSchemeSummary.getRepresentsVersion());
		
		summary.setCodeSystemVersionName(name);
		summary.setOfficialResourceVersionId(codingSchemeSummary.getRepresentsVersion());
		summary.setDocumentURI(
				this.getUriHandler().getCodeSystemVersionUri(codingSchemeSummary)
		);
		summary.setAbout(
				this.getUriHandler().getCodeSystemVersionUri(codingSchemeSummary)
		);
		
		summary.setFormalName(codingSchemeSummary.getFormalName());
		
		if(codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription() != null && 
				codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription().getContent() != null) {
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(
					codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription().getContent()));
			summary.setResourceSynopsis(description);			
		}
		
		CodeSystemReference codeSystemReference =
			this.getTransformUtils().toCodeSystemReference(
					codingSchemeName, 
					codingSchemeSummary.getCodingSchemeURI());
		
		summary.setVersionOf(codeSystemReference);
		
		summary.setHref(
				this.getUrlConstructor().
					createCodeSystemVersionUrl(
							codingSchemeName, 
							codingSchemeSummary.getRepresentsVersion()));
		
		return summary;
	}
	
	private String getName(String name, String version){
		return this.getVersionNameConverter().
			toCts2VersionName(name, version);
	}

}
