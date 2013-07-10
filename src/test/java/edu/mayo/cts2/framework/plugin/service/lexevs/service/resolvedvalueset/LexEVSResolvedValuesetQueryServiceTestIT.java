package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

@LoadContents({
		@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml"),
		@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosButGM.xml"), })
public class LexEVSResolvedValuesetQueryServiceTestIT extends
		AbstractTestITBase {

	private Cts2Marshaller marshaller = new DelegatingMarshaller();

	@Resource
	private LexEvsResolvedValueSetQueryService service;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetResourceSummaries() throws Exception {

		DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
				.getResourceSummaries(null, null, new Page());

		assertNotNull(dirResult);
		int expecting = 2;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	@Test
	public void testGetResourceSummaries_Restriction_ValueSetDefinitions()
			throws Exception {

		// Restrict to given codeSystem
		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
		Set<NameOrURI> valueSetDefinitions = new HashSet<NameOrURI>();
		valueSetDefinitions.add(ModelUtils
				.nameOrUriFromName("SRITEST:AUTO:AllDomesticANDGM"));
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

	@Test
	public void testGetResourceSummaries_Restriction_Entity() throws Exception {

		// Restrict to given codeSystem
		ResolvedValueSetQueryServiceRestrictions restrictions = new ResolvedValueSetQueryServiceRestrictions();
		Set<EntityNameOrURI> entities = new HashSet<EntityNameOrURI>();
		entities.add(ModelUtils.entityNameOrUriFromName(ModelUtils
				.createScopedEntityName("GM", "Automobiles")));
		restrictions.setEntities(entities);

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

	// -------------------------
	// Count with valid filters
	// -------------------------
	@Test
	public void testGetCountWithValidFilterOnAbout() throws Exception {
		// Build query using filters
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(
				StandardModelAttributeReference.ABOUT.getPropertyReference(),
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
						.getPropertyReference(),
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

}
