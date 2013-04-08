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
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;



/**
 * @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public interface LexEvsToCTS2Transformer <DescriptionDataType, DescriptionDataIN, DirectoryEntryDataType, DirectoryEntryDataIN> {
	// ResourceList
	// Description
	/*
	public CodeSystemVersionCatalogEntry transform(CodingScheme codingScheme);
	public MapCatalogEntry transformToMapCatalogEntry(CodingScheme codingScheme);
	public MapVersion transform(CodingScheme codingScheme);
	
	public MapEntry transformToMapEntry(ResolvedConceptReference resolvedConceptReference);
	public EntityDescription transformToEntity(ResolvedConceptReference reference);
*/
	
//	public Description transformDescription(CodingScheme codingScheme);
//	public Description transformDescription(ResolvedConceptReference conceptReference);

	public DescriptionDataType transformDescription(DescriptionDataIN data);

	
	// ResourceSummaries
	// DirectoryEntry
	/*
	public CodeSystemVersionCatalogEntrySummary transform(CodingSchemeRendering codingSchemeRendering);
	public MapVersionDirectoryEntry transform(CodingSchemeRendering codingSchemeRendering);
	
	public EntityDirectoryEntry transformToEntry(ResolvedConceptReference reference);
	public MapEntryDirectoryEntry transform(ResolvedConceptReference resolvedConceptReference);
	
	public MapCatalogEntrySummary transformToMapCatalogEntrySummary(CodingScheme codingScheme);
	*/
	
//	public DirectoryEntry transformDirectoryEntry(CodingSchemeRendering codingSchemeRendering);
//	public DirectoryEntry transformDirectoryEntry(ResolvedConceptReference reference);
//	public DirectoryEntry transformDirectoryEntry(CodingScheme codingScheme);
	public DirectoryEntryDataType transformDirectoryEntry(DirectoryEntryDataIN data);
}
