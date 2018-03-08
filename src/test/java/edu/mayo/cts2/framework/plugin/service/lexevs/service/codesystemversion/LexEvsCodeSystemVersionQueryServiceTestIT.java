/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.LexGrid.util.assertedvaluesets.AssertedValueSetParameters;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lexevs.dao.index.service.search.SourceAssertedValueSetSearchIndexService;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexgrid.valuesets.sourceasserted.SourceAssertedValueSetService;
import org.lexgrid.valuesets.sourceasserted.impl.SourceAssertedValueSetServiceImpl;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.types.ActiveOrAll;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractQueryServiceTest;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.CodeSystemVersionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;
//import edu.mayo.cts2.framework.plugin.service.lexevs.utility.PrintUtility;
import edu.stanford.smi.protegex.owl.ui.individuals.AssertedTypesListPanel;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
	@LoadContent(contentPath="lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")})
public class LexEvsCodeSystemVersionQueryServiceTestIT
	extends AbstractQueryServiceTest<CodeSystemVersionCatalogEntryListEntry, 
		CodeSystemVersionCatalogEntrySummary, 
		CodeSystemVersionQuery> {
	
	private final static String ABOUT_CONTAINS = "11.11.0.1";
	private final static String RESOURCESYNOPSIS_STARTSWITH = "Auto";
	private final static String RESOURCENAME_EXACTMATCH = "Automobiles-1.0";
	
//	private static SourceAssertedValueSetService sourceAssertedValueSetService;
//	private static SourceAssertedValueSetSearchIndexService sourceAssertedValueSetSearchIndexService;
		
	@Resource
	private LexEvsCodeSystemVersionQueryService service;

//	@BeforeClass
//	public static void createIndex() throws Exception {
//		sourceAssertedValueSetSearchIndexService = 
//				LexEvsServiceLocator.getInstance().getIndexServiceManager().getAssertedValueSetIndexService();
//		sourceAssertedValueSetSearchIndexService.createIndex(Constructors.createAbsoluteCodingSchemeVersionReference(
//				"http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl", "0.1.5"));
//		
//		AssertedValueSetParameters params = new AssertedValueSetParameters.Builder("0.1.5").
//				assertedDefaultHierarchyVSRelation("Concept_In_Subset").
//				codingSchemeName("owl2lexevs").
//				codingSchemeURI("http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl")
//				.build();
//		sourceAssertedValueSetService = SourceAssertedValueSetServiceImpl.getDefaultValueSetServiceForVersion(params);
//	}
	
	
	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	
	@Test
	public void queryPropertyTest() throws ParseException {
		// Testing the indexing
		
		SourceAssertedValueSetSearchIndexService sourceAssertedValueSetSearchIndexService = 
				LexEvsServiceLocator.getInstance().getIndexServiceManager().getAssertedValueSetIndexService();
		
		assertTrue(sourceAssertedValueSetSearchIndexService != null);
		
//		sourceAssertedValueSetSearchIndexService.createIndex(Constructors.createAbsoluteCodingSchemeVersionReference(
//				"http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl", "0.1.5"));
				
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(new TermQuery(new Term("isParentDoc", "true")), Occur.MUST_NOT);
		builder.add(new TermQuery(new Term("code", "C99998")), Occur.MUST);
		builder.add(new TermQuery(new Term("propertyName", "Contributing_Source")), Occur.MUST);
		QueryParser propValueParser = new QueryParser("propertyValue", sourceAssertedValueSetSearchIndexService.getAnalyzer());
		builder.add(propValueParser.createBooleanQuery("propertyValue", "FDA"), Occur.MUST);
		Query query = builder.build();
		QueryBitSetProducer parentFilter;
		parentFilter = new QueryBitSetProducer(
					new QueryParser("isParentDoc", new StandardAnalyzer(new CharArraySet(0, true))).parse("true"));
		ToParentBlockJoinQuery blockJoinQuery = new ToParentBlockJoinQuery(query, parentFilter, ScoreMode.Total);

		List<ScoreDoc> docs = sourceAssertedValueSetSearchIndexService.query(null, blockJoinQuery);
		assertNotNull(docs);
		assertTrue(docs.size() > 0);
		ScoreDoc sd = docs.get(0);
		Document doc = sourceAssertedValueSetSearchIndexService.getById(sd.doc);
		assertNotNull(doc);
		
		boolean fieldFound = false;
		
		List<IndexableField> fields =  doc.getFields();
		for(IndexableField field: fields) {
			if (field.name().equals("entityCode")  &&
				field.stringValue().equals("C99998") ) {
				fieldFound = true;
			}
		}
			
		assertTrue(fieldFound);
	}
	
//	@Test
//	public void queryPublishPropertyTest() throws ParseException {
//		
//		SourceAssertedValueSetSearchIndexService sourceAssertedValueSetSearchIndexService = 
//				LexEvsServiceLocator.getInstance().getIndexServiceManager().getAssertedValueSetIndexService();
//		
//		BooleanQuery.Builder builder = new BooleanQuery.Builder();
//		builder.add(new TermQuery(new Term("isParentDoc", "true")), Occur.MUST_NOT);
//		builder.add(new TermQuery(new Term("code", "C99999")), Occur.MUST);
//		builder.add(new TermQuery(new Term("propertyName", "Publish_Value_Set")), Occur.MUST);
//		QueryParser propValueParser = new QueryParser("propertyValue", sourceAssertedValueSetSearchIndexService.getAnalyzer());
//		builder.add(propValueParser.createBooleanQuery("propertyValue", "Yes"), Occur.MUST);
//		Query query = builder.build();
//		QueryBitSetProducer parentFilter;
//		parentFilter = new QueryBitSetProducer(
//					new QueryParser("isParentDoc", new StandardAnalyzer(new CharArraySet(0, true))).parse("true"));
//		ToParentBlockJoinQuery blockJoinQuery = new ToParentBlockJoinQuery(query, parentFilter, ScoreMode.Total);
//
//		List<ScoreDoc> docs = sourceAssertedValueSetSearchIndexService.query(null, blockJoinQuery);
//		assertNotNull(docs);
//		assertTrue(docs.size() > 0);
//		ScoreDoc sd = docs.get(0);
//		Document doc = sourceAssertedValueSetSearchIndexService.getById(sd.doc);
//		assertNotNull(doc);
//		
//		boolean fieldFound = false;
//		
//		List<IndexableField> fields =  doc.getFields();
//		for(IndexableField field: fields) {
//			if (field.name().equals("entityCode")  &&
//				field.stringValue().equals("C99999") ) {
//				fieldFound = true;
//			}
//		}
//			
//		assertTrue(fieldFound);
//	}
	
	@Test
	public void testCountWithNullQuery() throws Exception {
		int expecting = 0;
		int actual = this.service.count(null);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	// -----------------------------
	// Count with All valid filters
	// -----------------------------
	@Test
	public void testCountWithValidFilterSet() throws Exception {
		// Call local method to create set of all filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	// -------------------------
	// Count with valid filters
	// -------------------------
	@Test
	public void testCountWithValidFilterOnAbout() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getComponentReference(), 
												  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
												  		  ABOUT_CONTAINS);

		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	

	@Test
	public void testOnlyActive() throws Exception {

		ResolvedReadContext readContext = new ResolvedReadContext();
		readContext.setActive(ActiveOrAll.ACTIVE_ONLY);
		
		AbsoluteCodingSchemeVersionReference autos = 
			Constructors.createAbsoluteCodingSchemeVersionReference("urn:oid:11.11.0.1", "1.0");
		
		LexBIGServiceImpl.defaultInstance().getServiceManager(null).
			deactivateCodingSchemeVersion(autos, null);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, readContext, null);

		int expecting = 2;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
		
		LexBIGServiceImpl.defaultInstance().getServiceManager(null).
			activateCodingSchemeVersion(autos);
	}
	
	@Test
	public void testOnlyProduction() throws Exception {

		ComponentReference tag = new ComponentReference();
		tag.setPropertyReference(new URIAndEntityName());
		tag.setAttributeReference("tag");
		
		ResolvedFilter filter = 
			CommonTestUtils.createFilter("exactMatch", "PRODUCTION", tag);
		
		AbsoluteCodingSchemeVersionReference autos = 
			Constructors.createAbsoluteCodingSchemeVersionReference("urn:oid:11.11.0.1", "1.0");
		
		AbsoluteCodingSchemeVersionReference gmp = 
				Constructors.createAbsoluteCodingSchemeVersionReference("urn:oid:11.11.0.2", "2.0");
		
		LexBIGServiceImpl.defaultInstance().getServiceManager(null).setVersionTag(autos, "PRODUCTION");
		LexBIGServiceImpl.defaultInstance().getServiceManager(null).setVersionTag(gmp, "NOT-PRODUCTION");
		
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>(Arrays.asList(filter));
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filters, null, null);

		try {
			int expecting = 2;
			int actual = this.service.getResourceSummaries(query, null, new Page()).getEntries().size();
			assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
		} finally {
			LexBIGServiceImpl.defaultInstance().getServiceManager(null).setVersionTag(autos, "PRODUCTION");
			LexBIGServiceImpl.defaultInstance().getServiceManager(null).setVersionTag(gmp, "PRODUCTION");
		}
	}
	
	@Test
	public void testActiveAndInactive() throws Exception {

		ResolvedReadContext readContext = new ResolvedReadContext();
		readContext.setActive(ActiveOrAll.ACTIVE_AND_INACTIVE);
		
		AbsoluteCodingSchemeVersionReference autos = 
			Constructors.createAbsoluteCodingSchemeVersionReference("urn:oid:11.11.0.1", "1.0");
		
		LexBIGServiceImpl.defaultInstance().getServiceManager(null).
			deactivateCodingSchemeVersion(autos, null);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, readContext, null);

		int expecting = 3;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
		
		LexBIGServiceImpl.defaultInstance().getServiceManager(null).
			activateCodingSchemeVersion(autos);
	}
		
	@Test
	public void testCountWithValidFilterOnResourceSynopsis() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference(), 
														  StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
														  RESOURCESYNOPSIS_STARTSWITH);

		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	@Test
	public void testCountWithValidFilterOnResourceName() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getComponentReference(), 
												  		  StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
												  		  RESOURCENAME_EXACTMATCH);

		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		

	// ---------------------------
	// Count with invalid filters
	// ---------------------------
	@Test
	public void testCountWithInvalidFilterOnAbout() throws Exception {

		// Call local method to create set of all filters, Create error in resource name
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS + "FOO", RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 0;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testCountWithInvalidFilterOnResourceName() throws Exception {

		// Call local method to create set of all filters, Create error in resource name
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH + "FOO");
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 0;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
		
	@Test
	public void testCountWithInvalidFilterOnResorceSynopsis() throws Exception {

		// Call local method to create set of all filters, Create error in resource name
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH + "FOO", RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		int expecting = 0;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	// ----------------------------------------
	// resourceSummaries test codeSetName
	// -----------------------------------------
	@Test
	public void testGetResourceSummariesWithValidRestriction() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		// Get Directory Results for given codeSystem (no restrictions and empty query so return all entities)
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);
		
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummariesPageLimit() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		Page page = new Page();
		page.setMaxToReturn(1);
		page.setPage(0);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, page);
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);

		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummariesPageLimitNoRestrictions() throws Exception {

		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, null);

		Page page = new Page();
		page.setMaxToReturn(1);
		page.setPage(0);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, page);
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);

		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceSummariesWithInvalidRestriction() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles" + "FOO"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);
		
		// Get Directory Results for given codeSystem
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, doesn't exist so will return list with no elements.
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	// ----------------------------------------
	// resourceSummaries with All valid filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummariesWithValidFiltersSet() throws Exception {

		// Call local method to create set of all filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
		
		// Build query using filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
//	@Test
//	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
//	public void testGetResourceSummariesWithValidFilterSetAndVerifyTransformation() throws Exception {
//
//		// Call local method to create set of all filters
//		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(ABOUT_CONTAINS, RESOURCESYNOPSIS_STARTSWITH, RESOURCENAME_EXACTMATCH);
//		
//		// Build query using filters
//		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);
//
//		// Call getResourceSummaries with query created.
//		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
//		
//		// Test results, should return one entity
//		assertNotNull(dirResult);
//		int expecting = 1;
//		int actual = dirResult.getEntries().size();
//		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
//		
//		// Verify LexEVS to CTS2 transform worked 
//		CodeSystemVersionCatalogEntrySummary csvCatalogEntrySummary = dirResult.getEntries().get(0);
//		assertNotNull(csvCatalogEntrySummary.getFormalName());
//		assertEquals("Formal name not transformed - ", "autos", csvCatalogEntrySummary.getFormalName());
//		
//		assertNotNull(csvCatalogEntrySummary.getCodeSystemVersionName());
//		assertEquals("CodeSystemVersionName not transformed - ","Automobiles-1.0",csvCatalogEntrySummary.getCodeSystemVersionName());
//
//		assertNotNull(csvCatalogEntrySummary.getDocumentURI());
//		assertEquals("DocumentURI not transformed - ","urn:oid:11.11.0.1/1.0",csvCatalogEntrySummary.getDocumentURI());		
//
//		assertNotNull(csvCatalogEntrySummary.getAbout());
//		assertEquals("About not transformed - ","urn:oid:11.11.0.1",csvCatalogEntrySummary.getAbout());		
//
//		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis());
//		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis().getValue());
//		assertNotNull(csvCatalogEntrySummary.getResourceSynopsis().getValue().getContent());
//		assertEquals("Resource Synopsis not transformed - ","Automobiles", csvCatalogEntrySummary.getResourceSynopsis().getValue().getContent());						
//	}
	
	// ----------------------------------------
	// resourceSummaries with individual filters
	// -----------------------------------------
	@Test
	public void testGetResourceSummariesWithValidFilterOnAbout() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getComponentReference(), 
				   										  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
				   										  ABOUT_CONTAINS);
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummariesWithValidFilterOnResourceSynopsis() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference(), 
														  StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
				   										  RESOURCESYNOPSIS_STARTSWITH);
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummariesWithValidFilterOnResourceName() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getComponentReference(), 
														  StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
														  RESOURCENAME_EXACTMATCH);
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummariesWithInvalidFilterOnAbout() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.ABOUT.getComponentReference(), 
														  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
														  ABOUT_CONTAINS + "FOO");
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummariesWithInValidFilterOnResourceSynopsis() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference(), 
														  StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference(), 
														  RESOURCESYNOPSIS_STARTSWITH + "FOO");
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		
		// Test results, should return one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testGetResourceSummariesWithInvalidFilterOnResourceName() throws Exception {

		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getComponentReference(), 
														  StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference(), 
														  RESOURCENAME_EXACTMATCH + "FOO");
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, filter, null, null);

		// Call getResourceSummaries with query created.
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult = service.getResourceSummaries(query, null, new Page());
		// Test results, should return one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntrySummary(dirResult) + "\n");
		
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}	


	// ----------------------------------------
	// resourceList test codeSetName
	// -----------------------------------------
	@Test
	public void testGetResourceListWithValidRestriction() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		// Get Directory Results for given codeSystem (no restrictions and empty query so return all entities)
		DirectoryResult<CodeSystemVersionCatalogEntryListEntry> dirResult = service.getResourceList(query, null, new Page());
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntry(dirResult) + "\n");
		
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testGetResourceListWithInvalidRestriction() throws Exception {

		// Restrict to given codeSystem
		CodeSystemVersionQueryServiceRestrictions restrictions = new CodeSystemVersionQueryServiceRestrictions();
		restrictions.setCodeSystem(ModelUtils.nameOrUriFromName("Automobiles" + "FOO"));
		
		// Create query with restriction
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, restrictions);

		// Get Directory Results for given codeSystem (no restrictions and empty query so return all entities)
		DirectoryResult<CodeSystemVersionCatalogEntryListEntry> dirResult = service.getResourceList(query, null, new Page());
		
		// Test results, Automobiles has one entity
		assertNotNull(dirResult);
//		System.out.println(PrintUtility.createStringFromDirectoryResultWithEntry(dirResult) + "\n");
		
		int expecting = 0;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}

	@Test
	public void testOwl2CodeSystemVersionQuery() throws Exception {
		// Build query using no filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, null);

		List<CodeSystemVersionCatalogEntryListEntry> entries = this.service.getResourceList(query, null, new Page()).getEntries();
		
		int expecting = 3;
		int actual = entries.size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);

		boolean found = false;
				
		for(CodeSystemVersionCatalogEntryListEntry entry :entries) {
			if (entry.getEntry().getCodeSystemVersionName().equals("owl2lexevs-0.1.5") && 
				entry.getEntry().getDocumentURI().equals("http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl/0.1.5")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
	@Override
	protected QueryService<CodeSystemVersionCatalogEntryListEntry, CodeSystemVersionCatalogEntrySummary, CodeSystemVersionQuery> getService() {
		return this.service;
	}

	@Override
	protected CodeSystemVersionQuery getQuery() {
		return new CodeSystemVersionQueryImpl(null, null, null, null);
	}

}
