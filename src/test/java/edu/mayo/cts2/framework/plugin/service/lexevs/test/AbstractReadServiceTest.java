package edu.mayo.cts2.framework.plugin.service.lexevs.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.junit.Test;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.service.profile.ReadService;

public abstract class AbstractReadServiceTest<R,I> extends AbstractTestITBase {
	
	protected Cts2Marshaller marshaller = new DelegatingMarshaller();
	
	protected abstract ReadService<R,I> getService();
	
	protected abstract I getGoodIdentifier();
	
	protected abstract I getBadIdentifier();
	
	@Test
	public void testRead(){
		assertNotNull(this.getService().read(this.getGoodIdentifier(), null));
	}
	
	@Test
	public void testReadNotFound(){
		assertNull(this.getService().read(this.getBadIdentifier(), null));
	}

	@Test
	public void testReadValidXml() throws Exception {
		R resource = this.getService().read(this.getGoodIdentifier(), null);

		this.marshaller.marshal(resource, new StreamResult(new StringWriter()));	
	}
}
