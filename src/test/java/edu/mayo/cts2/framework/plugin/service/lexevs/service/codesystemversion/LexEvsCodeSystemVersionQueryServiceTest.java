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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import static org.junit.Assert.*;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.easymock.EasyMock;
import org.junit.Test;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionQuery;


public class LexEvsCodeSystemVersionQueryServiceTest {

	LexEvsCodeSystemVersionQueryService service = null;
	
	@Test
	public void testQueryByResourceSummaries() throws Exception {
		Page page = new Page();
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
		LexEvsCodeSystemVersionQueryService service = 
			new LexEvsCodeSystemVersionQueryService();
		
		LexBIGService lexBigService = 
			EasyMock.createMock(LexBIGService.class);
		
		CodingSchemeRenderingList list = new CodingSchemeRenderingList();
		EasyMock.expect(lexBigService.getSupportedCodingSchemes()).andReturn(list);
		
		EasyMock.replay(lexBigService);
		
		service.setLexBigService(lexBigService);

		assertNotNull(service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page));
	}

	@Test
	public void testQueryByResourceSummaries_count() throws Exception {
		Page page = new Page();
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 
		assertNotNull(directoryResult);
		assertEquals(1, directoryResult.getEntries().size());
	}
	
	@Test
	public void testQueryByResourceSummaries_returnZero() throws Exception {
		Page page = new Page();
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
		page.setPage(3);
		page.setMaxToReturn(10);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 
		assertNotNull(directoryResult);
		assertEquals(0, directoryResult.getEntries().size());
	}

	@Test
	public void testQueryByResourceSummaries_returnOne() throws Exception {
		Page page = new Page();
		CodeSystemVersionQuery codeSystemVersionQuery = null;
		SortCriteria sortCriteria = null;		
		
		page.setPage(0);
		page.setMaxToReturn(1);
		DirectoryResult<CodeSystemVersionCatalogEntrySummary> directoryResult = 
				this.service.getResourceSummaries(codeSystemVersionQuery, sortCriteria, page); 
		assertNotNull(directoryResult);
		assertEquals(1, directoryResult.getEntries().size());
	}

}
