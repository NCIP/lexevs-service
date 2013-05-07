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
