package edu.mayo.cts2.framework.plugin.service.lexevs.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public abstract class AbstractQueryServiceTest<R,S,Q extends ResourceQuery> 
	extends AbstractTestITBase {
	
	private Cts2Marshaller marshaller = new DelegatingMarshaller();
	
	protected abstract QueryService<R,S,Q> getService();
	
	protected abstract Q getQuery();

	@Test
	public void testQuerySummaries(){
		assertNotNull(this.getService().getResourceSummaries(this.getQuery(), null, new Page()));
	}
	
	@Test
	public void testQueryLists(){
		assertNotNull(this.getService().getResourceList(this.getQuery(), null, new Page()));
	}
	
	@Test
	public void testCount(){
		assertTrue(this.getService().count(this.getQuery()) > 0);
	}
	
	@Test
	public void testSummariesValidXml() throws Exception {
		DirectoryResult<S> summaries = this.getService().getResourceSummaries(this.getQuery(), null, new Page());

		for(S summary : summaries.getEntries()){
			this.marshaller.marshal(summary, new StreamResult(new StringWriter()));	
		}
	}
}
