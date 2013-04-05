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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.mapversion.MapEntryDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsToCTS2Transformer;

@Component
public class MappingToMapEntryTransform implements LexEvsToCTS2Transformer <MapEntry, ResolvedConceptReference, MapEntryDirectoryEntry, ResolvedConceptReference>  {

	public MappingToMapEntryTransform() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public MapEntry transformDescription(ResolvedConceptReference resolvedConceptReference) {
//		MapEntry mapEntry = new MapEntry();
//
//		String code = resolvedConceptReference.getCode();
//		String codeNameSpace = resolvedConceptReference.getCodeNamespace();
//		String codingSchemeName = resolvedConceptReference.getCodingSchemeName();
//		String codingSchemeURI = resolvedConceptReference.getCodingSchemeURI();
//		String codingSchemeVersion = resolvedConceptReference.getCodingSchemeVersion();
//		String entityDescription = resolvedConceptReference.getEntityDescription().getContent();
//		
//		resolvedConceptReference.getConceptCode();
//		
		//mapEntry.

		throw new UnsupportedOperationException("Transform to MapEntry is under construction");
	}
	
	
	@Override
	public MapEntryDirectoryEntry transformDirectoryEntry(ResolvedConceptReference resolvedConceptReference) {
		
//		MapEntryDirectoryEntry mapEntryDirectoryEntry = new MapEntryDirectoryEntry();
//		
//		String code = resolvedConceptReference.getCode();
//		String codeNameSpace = resolvedConceptReference.getCodeNamespace();
//		String codingSchemeName = resolvedConceptReference.getCodingSchemeName();
//		String codingSchemeURI = resolvedConceptReference.getCodingSchemeURI();
//		String codingSchemeVersion = resolvedConceptReference.getCodingSchemeVersion();
//		String entityDescription = resolvedConceptReference.getEntityDescription().getContent();

		throw new UnsupportedOperationException("Transform to MapEntryDirectoryEntry is under construction");
	}

}
