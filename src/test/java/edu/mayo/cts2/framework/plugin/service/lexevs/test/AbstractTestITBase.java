/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.test;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.BaseContentLoadingInMemoryTest;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventHandler;

@ContextConfiguration("/test-lexevs-context.xml")
public abstract class AbstractTestITBase extends BaseContentLoadingInMemoryTest {
	
	@Resource
	private LexEvsChangeEventHandler lexEvsChangeEventHandler;
	
	@Before
	public void clearCaches(){
		this.lexEvsChangeEventHandler.fireChange();
	}
	
}