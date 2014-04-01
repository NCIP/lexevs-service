/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Before;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntrySummary;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset.ResolvedValueSetQueryImpl;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.command.restriction.ValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

/**
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 */
@LoadContents({
	@LoadContent(contentPath = "lexevs/test-content/valueset/vdTestData.xml") })
public class LexEVSValuesetQueryServiceTestIT extends AbstractTestITBase {

	private Cts2Marshaller marshaller = new DelegatingMarshaller();

	@Resource
	private LexEVSValueSetQueryService service;
	
	
	@Before
	public void setUp() throws Exception {
		assertNotNull(this.service);
	}

	@Test
	public void testGetResourceSummaries() throws Exception {

		DirectoryResult<ValueSetCatalogEntrySummary> dirResult = service
				.getResourceSummaries(null, null, new Page());

		assertNotNull(dirResult);
		int expecting = 20;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	@Test
	public void testGetResourceSummaries_Restriction_ValueSetDefinitionName()
			throws Exception {

		// Restrict to given codeSystem
		ValueSetQueryServiceRestrictions restrictions = new ValueSetQueryServiceRestrictions();
		List<String> codingSchemes = new ArrayList<String>();
		codingSchemes.add("Automobiles");
		restrictions.setCodesystem(codingSchemes);

		// Create query with restriction
		ValueSetQueryImpl query = new ValueSetQueryImpl(null,
				null, restrictions, null);
		DirectoryResult<ValueSetCatalogEntrySummary> dirResult = service
				.getResourceSummaries(query, null, new Page());

		assertNotNull(dirResult);
		int expecting = 14;
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
				StandardModelAttributeReference.ABOUT.getComponentReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(),
				"SRITEST:AUTO:AllDomesticButGM");

		// Build query using filters
		ValueSetQueryImpl query = new ValueSetQueryImpl(null,
				filter, null, null);

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
		ValueSetQueryImpl query = new ValueSetQueryImpl(null,
				filter, null, null);

		int expecting = 4;
		int actual = this.service.count(query);
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	@Test
	public void testSummariesValidXml() throws Exception {
		DirectoryResult<ValueSetCatalogEntrySummary> summaries = this.service.getResourceSummaries
				(new ValueSetQueryImpl(null, null, null, null), null, new Page());

		for(ValueSetCatalogEntrySummary summary : summaries.getEntries()){
			this.marshaller.marshal(summary, new StreamResult(new StringWriter()));	
		}
	}

}
