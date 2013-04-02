package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsSystem;

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
		CodeSystemVersionNameConverter converter = new CodeSystemVersionNameConverter();
		CodingSchemeToCodeSystemTransform transformer = new CodingSchemeToCodeSystemTransform(converter);
		service.setCodingSchemeToCodeSystemTransform(transformer);
		service.setCodeSystemVersionNameConverter(new CodeSystemVersionNameConverter());
		
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
