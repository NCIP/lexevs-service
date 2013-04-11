package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;

@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml")
public class LexEVSResolvedValuesetResolutionServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private ResolvedValueSetResolutionService service;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetRolution() throws Exception {

		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("41011",
				ModelUtils.nameOrUriFromName("BRO"),
				ModelUtils.nameOrUriFromName("BRO-41011"));
		DirectoryResult<EntitySynopsis> dirResult = service.getResolution(
				identifier, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}

	@Test
	void TestGetResolutionEntitiesNoFilter() {
		ResolvedValueSetReadId identifier = new ResolvedValueSetReadId("41011",
				ModelUtils.nameOrUriFromName("BRO"),
				ModelUtils.nameOrUriFromName("BRO-41011"));
		DirectoryResult<EntityDirectoryEntry> dirResult = service.getEntities(
				identifier, null, null, new Page());
		assertTrue(dirResult.getEntries().size() > 0);

	}

}
