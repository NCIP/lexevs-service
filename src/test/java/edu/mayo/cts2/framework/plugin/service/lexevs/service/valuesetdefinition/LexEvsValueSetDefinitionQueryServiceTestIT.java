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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.ValueSetDefinitionQueryServiceRestrictions;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
public class LexEvsValueSetDefinitionQueryServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private LexEvsValueSetDefinitionQueryService service;
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/VSDOnlyTest.xml")
	public void testResourceSummaries() throws Exception {
		String uri = "SRITEST:AUTO:PropertyRefTest1-VSDONLY";
		String name = "Automobiles";
		//NameOrURI uriName = ModelUtils.nameOrUriFromUri(uri);
		NameOrURI codeSystemName = ModelUtils.nameOrUriFromName(name);
		
		//uriName.setName("Automobiles");  // LexEVS defaultCodingScheme of ValueSetDefinition
		
		Query query = null;
		Set<ResolvedFilter> filterComponent = null;
		ResolvedReadContext readContext = null;		
		ValueSetDefinitionQueryServiceRestrictions vsdQueryServiceRestrictions = new ValueSetDefinitionQueryServiceRestrictions();
		vsdQueryServiceRestrictions.setValueSet(codeSystemName);
		
		ValueSetDefinitionQueryImpl vsdQuery = new ValueSetDefinitionQueryImpl(query,filterComponent,readContext,vsdQueryServiceRestrictions);
						
		SortCriteria sortCriteria = null;
		Page page = new Page();
		
		DirectoryResult<ValueSetDefinitionDirectoryEntry> resourceSummaries = service.getResourceSummaries(vsdQuery, sortCriteria, page);
		
		assertNotNull(resourceSummaries);
	}

}
