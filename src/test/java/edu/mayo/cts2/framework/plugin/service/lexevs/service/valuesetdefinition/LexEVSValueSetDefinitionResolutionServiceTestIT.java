package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset.ResolvedValueSetResolutionQueryImpl;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetResolutionEntityRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;

@LoadContent(contentPath = "lexevs/test-content/valueset/ResolvedAllDomesticAutosAndGM.xml")
public class LexEVSValueSetDefinitionResolutionServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private LexEvsValueSetDefinitionResolutionService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	// ---- Test methods ----
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetResolution() throws Exception {

		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("All Domestic Autos AND GM-06736a30878a0f8bd0ea83196732380a", ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.
				resolveDefinition(defintionId, null, null, null, null, null, new Page());

		assertNotNull(dirResult);
		int expecting = 1;
		int actual = dirResult.getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual,
				expecting, actual);
	}
	
	@Test
	public void testGetResolutionInvalidId() throws Exception {

		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("INVALID", ModelUtils.nameOrUriFromName("All Domestic Autos AND GM"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.
				resolveDefinition(defintionId, null, null, null, null, null, new Page());

		assertNull(dirResult);
	}
	
	@Test
	public void testGetResolutionInvalidBoth() throws Exception {

		ValueSetDefinitionReadId defintionId = 
			new ValueSetDefinitionReadId("INVALID", ModelUtils.nameOrUriFromName("INVALID"));
		
		DirectoryResult<EntitySynopsis> dirResult = service.
				resolveDefinition(defintionId, null, null, null, null, null, new Page());

		assertNull(dirResult);
	}

}
