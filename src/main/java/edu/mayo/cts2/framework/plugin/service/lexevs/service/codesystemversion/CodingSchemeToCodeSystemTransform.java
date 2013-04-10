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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
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
	extends AbstractBaseTransform<CodeSystemVersionCatalogEntry, CodingScheme, CodeSystemVersionCatalogEntrySummary, CodingSchemeRendering>{
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
	public CodeSystemVersionCatalogEntry transformDescription(CodingScheme codingScheme){
		CodeSystemVersionCatalogEntry codeSystemVersion = new CodeSystemVersionCatalogEntry();

		codeSystemVersion.setAbout(
				this.getUriHandler().getCodeSystemUri(codingScheme)
		);
		
		String name = this.getName(codingScheme);
		
		codeSystemVersion.setCodeSystemVersionName(name);
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
		
		CodeSystemReference codeSystemReference = new CodeSystemReference();
		codeSystemReference.setContent(codingScheme.getCodingSchemeName());
		codeSystemReference.setUri(codeSystemVersion.getAbout());
		codeSystemVersion.setVersionOf(codeSystemReference);
		
		codeSystemVersion.setEntityDescriptions(
			this.getUrlConstructor().createEntitiesOfCodeSystemVersionUrl(
				codingScheme.getCodingSchemeName(), 
				codingScheme.getRepresentsVersion()));
		
		return codeSystemVersion;
	}
	
	@Override
	public CodeSystemVersionCatalogEntrySummary transformDirectoryEntry(CodingSchemeRendering codingSchemeRendering){
		CodeSystemVersionCatalogEntrySummary summary = new CodeSystemVersionCatalogEntrySummary();
		
		CodingSchemeSummary codingSchemeSummary = codingSchemeRendering.getCodingSchemeSummary();
		
		String name = this.getName(codingSchemeRendering);
		
		summary.setCodeSystemVersionName(name);
		summary.setDocumentURI(
				this.getUriHandler().getCodeSystemVersionUri(codingSchemeSummary)
		);
		summary.setAbout(
				this.getUriHandler().getCodeSystemUri(codingSchemeSummary)
		);
		
		summary.setFormalName(codingSchemeSummary.getFormalName());
		
		if(codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription() != null && 
				codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription().getContent() != null) {
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(
					codingSchemeRendering.getCodingSchemeSummary().getCodingSchemeDescription().getContent()));
			summary.setResourceSynopsis(description);			
		}
		
		CodeSystemReference codeSystemReference = new CodeSystemReference();
		codeSystemReference.setContent(codingSchemeSummary.getLocalName());
		codeSystemReference.setUri(summary.getAbout());
		
		summary.setVersionOf(codeSystemReference);
		
		summary.setHref(
				this.getUrlConstructor().
					createEntitiesOfCodeSystemVersionUrl(
							codingSchemeSummary.getLocalName(), 
							codingSchemeSummary.getRepresentsVersion()));
		
		return summary;
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
		return this.getVersionNameConverter().
			toCts2VersionName(name, version);
	}

}
