/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.CodeSystemVersionQueryImpl;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.LexEvsCodeSystemVersionQueryService;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion.LexEvsCodeSystemVersionReadService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonTestUtils;

@Ignore
public class RemoteCodeSystemVersionTest extends AbstractRemoteTestBase {

	@Resource
	private LexEvsCodeSystemVersionQueryService queryService;
	
	@Resource
	private LexEvsCodeSystemVersionReadService readService;
	
	@Test
	public void testQuery() throws Exception {
		// Build query using no filters
		CodeSystemVersionQueryImpl query = new CodeSystemVersionQueryImpl(null, null, null, null);

		int expecting = 1;
		int actual = this.queryService.getResourceList(query, null, new Page()).getEntries().size();
		assertEquals("Expecting " + expecting + " but got " + actual, expecting, actual);
	}
	
	@Test
	public void testRead() throws Exception {
		ResolvedReadContext readContext = null;
		String nameOrUri = "LOINC-236";
		NameOrURI identifier = ModelUtils.nameOrUriFromName(nameOrUri);
		
		assertNotNull(CommonTestUtils.createValidValuesMessage(nameOrUri), this.readService.read(identifier, readContext));
	}
}
