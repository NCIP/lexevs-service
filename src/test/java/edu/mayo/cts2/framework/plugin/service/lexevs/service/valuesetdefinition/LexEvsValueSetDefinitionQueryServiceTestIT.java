/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionListEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/valueset/vdTestData.xml")
public class LexEvsValueSetDefinitionQueryServiceTestIT extends
		AbstractTestITBase {

	@Resource
	private LexEvsValueSetDefinitionQueryService service;
	
	private Cts2Marshaller marshaller = new DelegatingMarshaller();
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}
	
	@Test
	public void testResourceSummaries() throws Exception {						
		Page page = new Page();
		
		DirectoryResult<ValueSetDefinitionDirectoryEntry> summaries = 
			service.getResourceSummaries(null, null, page);
		
		assertNotNull(summaries);
		assertEquals(20, summaries.getEntries().size());
	}
	
	@Test
	public void testResourceList() throws Exception {						
		Page page = new Page();
		
		DirectoryResult<ValueSetDefinitionListEntry> summaries = 
			service.getResourceList(null, null, page);
		
		assertNotNull(summaries);
		assertEquals(20, summaries.getEntries().size());
	}
	
	@Test
	public void testResourceSummariesValidXml() throws Exception {						
		Page page = new Page();
		
		DirectoryResult<ValueSetDefinitionDirectoryEntry> summaries = 
			service.getResourceSummaries(null, null, page);
		
		assertNotNull(summaries);
		assertTrue(summaries.getEntries().size() > 0);
		
		for(ValueSetDefinitionDirectoryEntry summary : summaries.getEntries()){
			this.marshaller.marshal(summary, new StreamResult(new StringWriter()));	
		}
	}
	
	@Test
	public void testResourceListValidXml() throws Exception {						
		Page page = new Page();
		
		DirectoryResult<ValueSetDefinitionListEntry> summaries = 
			service.getResourceList(null, null, page);
		
		assertNotNull(summaries);
		assertTrue(summaries.getEntries().size() > 0);
		
		for(ValueSetDefinitionListEntry summary : summaries.getEntries()){
			this.marshaller.marshal(summary, new StreamResult(new StringWriter()));	
		}
	}

}
