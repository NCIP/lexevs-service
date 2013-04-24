package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResolutionEntityQuery;

@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml")
public class LexEVSResolvedValuesetResolutionServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private ResolvedValueSetResolutionService service;
	@Resource
	Cts2Marshaller marshaller;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetRolution() throws Exception {

		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("SRITEST:AUTO:AllDomesticANDGM-06736a30878a0f8bd0ea83196732380a",
				ModelUtils.nameOrUriFromName("SRITEST:AUTO:AllDomesticANDGM"),
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM-06736a30878a0f8bd0ea83196732380a"));
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	@Test
	public void testGetResolutionEntitiesNoFilter() {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("SRITEST:AUTO:AllDomesticANDGM-06736a30878a0f8bd0ea83196732380a",
				ModelUtils.nameOrUriFromName("SRITEST:AUTO:AllDomesticANDGM"),
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM-06736a30878a0f8bd0ea83196732380a"));
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, null, null, new Page());
		assertTrue(dirResult.getEntries().size() > 0);

	}

	
	@Test
	public void testGetResolutionEntitiesWithFilter() {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("SRITEST:AUTO:AllDomesticANDGM-06736a30878a0f8bd0ea83196732380a",
				ModelUtils.nameOrUriFromName("SRITEST:AUTO:AllDomesticANDGM"),
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM-06736a30878a0f8bd0ea83196732380a"));
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
		  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
		  		"GM");
		
		ResolvedValueSetResolutionQueryImpl query= new ResolvedValueSetResolutionQueryImpl();
		query.setFilterComponent(filter);
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, query, null, new Page());
		assertTrue(dirResult.getEntries().size() > 0);

	}	
	@Test
	public void testGetResolutionValidXML() throws Exception {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("SRITEST:AUTO:AllDomesticANDGM-06736a30878a0f8bd0ea83196732380a",
				ModelUtils.nameOrUriFromName("SRITEST:AUTO:AllDomesticANDGM"),
				ModelUtils.nameOrUriFromName("All Domestic Autos AND GM-06736a30878a0f8bd0ea83196732380a"));
		
		Set<ResolvedFilter> filter = CommonTestUtils.createFilterSet(StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference(), 
		  		  StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference(), 
		  		"GM");

		
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, filter, new Page());


		
		
		for (EntitySynopsis synopsis: dirResult.getEntries()) {
			StreamResult result= new StreamResult(new StringWriter());
			marshaller.marshal(synopsis, result);
			System.out.println(result.getWriter().toString());
		}
		
	}

}
