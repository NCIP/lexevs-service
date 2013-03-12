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

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;

/**
 * Transforms a LexGrid CodingScheme into a CTS2 CodeSystemVersion CatalogEntry.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class CodingSchemeToCodeSystemTransform {
	
	@Resource
	private CodeSystemVersionNameConverter codeSystemVersionNameConverter;
	
	/**
	 * Transform.
	 *
	 * @param codingScheme the coding scheme
	 * @return the code system version catalog entry
	 */
	public CodeSystemVersionCatalogEntry transform(CodingScheme codingScheme){
		CodeSystemVersionCatalogEntry codeSystem = new CodeSystemVersionCatalogEntry();

		codeSystem.setAbout(codingScheme.getCodingSchemeURI());
		
		String name = this.getName(codingScheme);
		
		codeSystem.setCodeSystemVersionName(name);
		codeSystem.setDocumentURI(codingScheme.getCodingSchemeURI());
		
		for(String localName : codingScheme.getLocalName()){
			codeSystem.addKeyword(localName);
		}
		
		EntryDescription description = new EntryDescription();
		description.setValue(ModelUtils.toTsAnyType(
				codingScheme.getEntityDescription().getContent()));
		codeSystem.setResourceSynopsis(description);
		
		return codeSystem;
	}
	
	public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering){
		CodeSystemVersionCatalogEntrySummary summary = new CodeSystemVersionCatalogEntrySummary();
		
		CodingSchemeSummary codingSchemeSummary = codingSchemeRendering.getCodingSchemeSummary();
		
		String name = this.getName(codingSchemeRendering);
		
		summary.setCodeSystemVersionName(name);
		summary.setDocumentURI(codingSchemeSummary.getCodingSchemeURI());
		
		summary.setFormalName(codingSchemeSummary.getFormalName());
		
		EntryDescription description = new EntryDescription();
		description.setValue(ModelUtils.toTsAnyType(
				summary.getResourceSynopsis().getValue().getContent()));
		summary.setResourceSynopsis(description);
		
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
		return this.codeSystemVersionNameConverter.
				toCts2CodeSystemVersionName(name, version);
	}
	
}
