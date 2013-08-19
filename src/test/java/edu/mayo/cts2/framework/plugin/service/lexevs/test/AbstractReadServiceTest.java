/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
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
		
		assertNotNull(resource);

		this.marshaller.marshal(resource, new StreamResult(new StringWriter()));	
	}
}
