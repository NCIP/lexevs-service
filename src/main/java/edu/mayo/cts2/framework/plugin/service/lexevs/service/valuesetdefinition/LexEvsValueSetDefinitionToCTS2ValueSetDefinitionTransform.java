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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import org.lexgrid.valuesets.dto.ResolvedValueSetCodedNodeSet;

import edu.mayo.cts2.framework.model.core.ValueSetReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry;

/**
 * Transforms a LexEVS ValueSetDefinition object into a CTS2 ValueSetDefinition object.
 * 
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform {

	public ValueSetDefinition transformToValueSetDefinition(org.LexGrid.valueSets.ValueSetDefinition lexEvsVSD) {
		
		ValueSetDefinition cts2VSD = new ValueSetDefinition();
		if (lexEvsVSD == null) {
			return cts2VSD;
		}
		
		cts2VSD.setAbout(lexEvsVSD.getValueSetDefinitionURI());
		cts2VSD.setFormalName(lexEvsVSD.getValueSetDefinitionURI());  // TODO Ok mapping???
		
		// TODO Need to transform LexEVS Definition Entry objects into CTS2 ValueSetDefinitionEntry objects ???
		ValueSetDefinitionEntry[] vEntryList;
//		cts2VSD.setEntry(vEntryList);
		
		ValueSetReference vsReference = new ValueSetReference();
		vsReference.setContent(lexEvsVSD.getValueSetDefinitionURI());
		cts2VSD.setDefinedValueSet(vsReference);
		
		// TODO status mapping ???
		lexEvsVSD.getStatus();
		
		// TODO owner mapping
		lexEvsVSD.getOwner();
		
		// TODO effective date mapping
		lexEvsVSD.getEffectiveDate();
		
		
		// TODO source mapping - need to map org.LexGrid.commonTypes.Source entries into CTS2 TBD objects ???
		lexEvsVSD.getSource();
				
		//return cts2VSD;
		
		throw new UnsupportedOperationException("Transform of LexEVS ValueSetDefinition into CTS2 ValueSetDefinition is under construction");		
	}
	
	// TODO Not sure if method is needed/useful. May be better to transform directly in LexValueSetDefinitionQueryService
	public ValueSetDefinitionDirectoryEntry transformToValueSetDefinitionDirectoryEntry(ResolvedValueSetCodedNodeSet resolvedValueSetCodedNodeSet) {
		ValueSetDefinitionDirectoryEntry vsdDirEntry = new ValueSetDefinitionDirectoryEntry();
		
		//return vsdDirEntry;
		throw new UnsupportedOperationException("Transform of LexEVS ValueSetDefinition into CTS2 ValueSetDefinitionDirectoryEntry is under construction");		
	}
	
}
