/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.mapversion.MapEntryDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.MapEntryQueryServiceRestrictions;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */	
@LoadContents(
	{
		@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
		@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
		@LoadContent(contentPath="lexevs/test-content/testMapping.xml")
	}
)
public class LexEvsMapEntryQueryServiceTestIT extends AbstractTestITBase {

	@Resource
	private LexEvsMapEntryQueryService service;
	
	@Resource
	private Cts2Marshaller marshaller;

	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Test
	public void testGetResourceSummaries() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntryDirectoryEntry> list = this.service.getResourceSummaries(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(6,list.getEntries().size());
	}
	
	@Test
	public void testGetResourceSummariesTargetRestriction() throws Exception {

		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
		
		EntityNameOrURI target = new EntityNameOrURI();
		target.setEntityName(ModelUtils.createScopedEntityName("E0001", "GermanMadePartsNamespace"));
		restrictions.getTargetEntities().add(target);
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntryDirectoryEntry> list = this.service.getResourceSummaries(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(3,list.getEntries().size());
	}
	
	@Test
	public void testGetResourceSummariesValidXml() throws Exception {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0");
		restrictions.setMapVersion(mapVersion);
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<MapEntryDirectoryEntry> list = this.service.getResourceSummaries(mapEntryQueryImpl, sortCriteria, page);
		assertNotNull(list);
		assertEquals(6,list.getEntries().size());
		
		for(MapEntryDirectoryEntry entry : list.getEntries()){
			this.marshaller.marshal(entry, new StreamResult(new StringWriter()));	
		}
	}
	
	@Test
	public void testGetResourceSummariesInvalidMapVersion() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		// Note:  a bad map version name having more than one "-" will cause an exception to be thrown
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0BAD");
		restrictions.setMapVersion(mapVersion);
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		assertEquals(0, service.getResourceSummaries(mapEntryQueryImpl, sortCriteria, page).getEntries().size());
	}
	
	@Test
	public void testGetResourceSummariesInvalidMapVersionTwoHyphens() {
		
		MapEntryQueryServiceRestrictions restrictions = new MapEntryQueryServiceRestrictions();
		// Note:  a bad map version name having more than one "-" will cause an exception to be thrown
		NameOrURI mapVersion = ModelUtils.nameOrUriFromName("MappingSample-1.0-BAD");
		restrictions.setMapVersion(mapVersion);
		
		MapEntryQueryImpl mapEntryQueryImpl = new MapEntryQueryImpl(null,null,null,restrictions);
		
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		assertEquals(0, service.getResourceSummaries(mapEntryQueryImpl, sortCriteria, page).getEntries().size());
	}

}
