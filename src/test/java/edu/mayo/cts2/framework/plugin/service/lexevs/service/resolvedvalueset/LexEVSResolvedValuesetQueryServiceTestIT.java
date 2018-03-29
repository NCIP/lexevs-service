/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
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

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

@LoadContents({
		@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml", loader = "LexGrid_ResolvedValueSetLoader"),
		@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosButGM.xml", loader = "LexGrid_ResolvedValueSetLoader"),
		@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
public class LexEVSResolvedValuesetQueryServiceTestIT extends
		AbstractTestITBase {

	private Cts2Marshaller marshaller = new DelegatingMarshaller();
	private static SourceAssertedValueSetSearchIndexService sourceAssertedValueSetSearchIndexService;
	
	@Resource
	private LexEvsResolvedValueSetQueryService service;
	
	@BeforeClass
	public static void createIndex() throws Exception {
		// index the owl2lexevs coding scheme
		sourceAssertedValueSetSearchIndexService = 
				LexEvsServiceLocator.getInstance().getIndexServiceManager().getAssertedValueSetIndexService();
		sourceAssertedValueSetSearchIndexService.createIndex(Constructors.createAbsoluteCodingSchemeVersionReference(
				"http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl", "0.1.5"));
	}
	
	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

//	@Test
//	public void testGetResourceSummaries_Restriction_AssertedValueSetDefinitionName()
//			throws Exception {
//
//		// Restrict to given codeSystem
//		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
//		Set<NameOrURI> valueSetDefinitions = new HashSet<NameOrURI>();
//		valueSetDefinitions.add(ModelUtils.nameOrUriFromName("owl2lexevs"));	
//		restrictions.setValueSetDefinitions(valueSetDefinitions);
//
//		// Create query with restriction
//		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
//				null, restrictions);
//		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
//				.getResourceSummaries(query, null, new Page());
//
//		assertNotNull(dirResult);
//		int expecting = 1;
//		int actual = dirResult.getEntries().size();
//		assertEquals("Expecting " + expecting + " but got " + actual,
//				expecting, actual);
//	}
	
	@Test
	public void testGetResourceSummaries() throws Exception {

		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
				.getResourceSummaries(null, null, new Page());

		assertNotNull(dirResult);
		int expecting = 10;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Restriction_ValueSetDefinitionName()
			throws Exception {

		// Restrict to given codeSystem
		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
		Set<NameOrURI> valueSetDefinitions = new HashSet<NameOrURI>();
		valueSetDefinitions.add(ModelUtils
				.nameOrUriFromName("571eb4e6"));
		restrictions.setValueSetDefinitions(valueSetDefinitions);

		// Create query with restriction
		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
				null, restrictions);
		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
				.getResourceSummaries(query, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	
	@Test
	public void testGetResourceSummaries_Restriction_ValueSetDefinitionUri()
			throws Exception {

		// Restrict to given codeSystem
		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
		Set<NameOrURI> valueSetDefinitions = new HashSet<NameOrURI>();
		valueSetDefinitions.add(ModelUtils
				.nameOrUriFromUri("SRITEST:AUTO:AllDomesticANDGM"));
		restrictions.setValueSetDefinitions(valueSetDefinitions);

		// Create query with restriction
		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
				null, restrictions);
		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
				.getResourceSummaries(query, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Restriction_CodeSystemVersions()
			throws Exception {

		// Restrict to given codeSystem
		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
		Set<NameOrURI> codeSystemVersions = new HashSet<NameOrURI>();
		codeSystemVersions
				.add(ModelUtils.nameOrUriFromUri("urn:oid:11.11.0.1"));
		restrictions.setCodeSystemVersions(codeSystemVersions);

		// Create query with restriction
		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
				null, restrictions);
		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
				.getResourceSummaries(query, null, new Page());

		assertNotNull(dirResult);
		int expecting = 2;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

// Test broken by Asserted Resolved Value Sets
//	@Test
//	public void testGetResourceSummaries_Restriction_Entity() throws Exception {
//
//		// Restrict to given codeSystem
//		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
//		Set<EntityNameOrURI> entities = new HashSet<EntityNameOrURI>();
//		entities.add(ModelUtils.entityNameOrUriFromName(ModelUtils
//				.createScopedEntityName("GM", "Automobiles")));
//		restrictions.setEntities(entities);
//
//		// Create query with restriction
//		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
//				null, restrictions);
//		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
//				.getResourceSummaries(query, null, new Page());
//
//		assertNotNull(dirResult);
//		int expecting = 1;
//		int actual = dirResult.getEntries().size();
//		assertEquals("Expecting " + expecting + " but got " + actual,
//				expecting, actual);
//	}

	// -------------------------
	// Count with valid filters
	// -------------------------
	@Test
	public void testGetCountWithValidFilterOnAbout() throws Exception {
		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(
				StandardModelAttributeReference.ABOUT.getComponentReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(),
				"SRITEST:AUTO:AllDomesticButGM");

		// Build query using filters
		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
				filter, null);

		int expecting = 1;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	// -------------------------
	// Count with valid filters
	// -------------------------
	@Test
	public void testGetCountWithValidFilterOnResourceName() throws Exception {
		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(
				StandardModelAttributeReference.RESOURCE_NAME
						.getComponentReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(), "All");

		// Build query using filters
		ResolvedValueSetQueryImpl query = new ResolvedValueSetQueryImpl(null,
				filter, null);

		int expecting = 2;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	@Test
	public void testSummariesValidXml() throws Exception {
		DirectoryResult<ResolvedValueSetDirectoryEntry> summaries = this.service.getResourceSummaries
				(new ResolvedValueSetQueryImpl(null, null, null), null, new Page());

		for(ResolvedValueSetDirectoryEntry summary : summaries.getEntries()){
			this.marshaller.marshal(summary, new StreamResult(new StringWriter()));	
		}
	}
	
	@Test
	public void queryPropertyTest() throws ParseException {				
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
	
	@Test
	public void queryPublishPropertyTest() throws ParseException {
		
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(new TermQuery(new Term("isParentDoc", "true")), Occur.MUST_NOT);
		builder.add(new TermQuery(new Term("code", "C99999")), Occur.MUST);
		builder.add(new TermQuery(new Term("propertyName", "Publish_Value_Set")), Occur.MUST);
		QueryParser propValueParser = new QueryParser("propertyValue", sourceAssertedValueSetSearchIndexService.getAnalyzer());
		builder.add(propValueParser.createBooleanQuery("propertyValue", "Yes"), Occur.MUST);
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
				field.stringValue().equals("C99999") ) {
				fieldFound = true;
			}
		}
			
		assertTrue(fieldFound);
	}
	

}
