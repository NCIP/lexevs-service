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

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsSystem;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class LexEvsCodeSystemVersionReadServiceTest {
	// Setup mocked environment
	// -------------------------
	public LexEvsCodeSystemVersionReadService createService(
			FakeLexEvsSystem<CodeSystemVersionCatalogEntry, CodeSystemVersionCatalogEntrySummary, CodeSystemVersionQueryImpl, LexEvsCodeSystemVersionQueryService> fakeLexEvs, 
			boolean withData) throws Exception{
		LexEvsCodeSystemVersionReadService service = new LexEvsCodeSystemVersionReadService();

		// Mock LexBIGService, overwrite return value for getSupportedCodingSchemes
		LexBIGService lexBigService = fakeLexEvs.createMockedLexBIGServiceWithFakeLexEvsData(service, withData);
		
		service.setLexBigService(lexBigService);

		// Overwrite objects in service object 
		VersionNameConverter converter = new VersionNameConverter();
		CodingSchemeToCodeSystemTransform transformer = new CodingSchemeToCodeSystemTransform(converter);
		service.setCodingSchemeToCodeSystemTransform(transformer);
		service.setCodeSystemVersionNameConverter(new VersionNameConverter());
		
		return service;
	}

//	QueryService<CodeSystemVersionCatalogEntry, 
//	CodeSystemVersionCatalogEntrySummary, 
//	CodeSystemVersionQuery>, Cts2Profile {

	// =============
	// Test methods
	// =============
	
	// Count with VALID and INVALID filters
	// ------------------------------------
//	@Test
//	public void testCount_Filter_About_Contains() throws Exception {
//		FakeLexEvsSystem<CodeSystemVersionCatalogEntry, CodeSystemVersionCatalogEntrySummary, CodeSystemVersionQueryImpl, LexEvsCodeSystemVersionQueryService> fakeLexEvs;
//		fakeLexEvs = new FakeLexEvsSystem<CodeSystemVersionCatalogEntry, CodeSystemVersionCatalogEntrySummary, CodeSystemVersionQueryImpl, LexEvsCodeSystemVersionQueryService>();
//		LexEvsCodeSystemVersionQueryService service = this.createService(fakeLexEvs, true); 
//		boolean testValidData = true;
//		
//		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
//		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);
//				
//		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, null, DataField.ABOUT, 
//				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), testValidData);		
//		fakeLexEvs.executeCountForEachExistingCodeSchemeWithSuppliedFilter(service, query, null, DataField.ABOUT, 
//				StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), !testValidData);		
//	}
	


}
