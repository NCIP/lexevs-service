/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.extension.LocalIdValueSetDefinition;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetDefinitionUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractReadServiceTest;
import edu.mayo.cts2.framework.service.profile.ReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@LoadContent(contentPath="lexevs/test-content/valueset/VSDOnlyTest.xml")
public class LexEvsValueSetDefinitionReadByNameServiceTestIT
	extends AbstractReadServiceTest<LocalIdValueSetDefinition, ValueSetDefinitionReadId> {

	@Resource
	private LexEvsValueSetDefinitionReadService service;
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	@Override
	protected ReadService<LocalIdValueSetDefinition, ValueSetDefinitionReadId> getService() {
		return this.service;
	}

	@Override
	protected ValueSetDefinitionReadId getGoodIdentifier() {
		return new ValueSetDefinitionReadId("75d925fb", ModelUtils.nameOrUriFromName("Property Reference Test 1"));
	}

	@Override
	protected ValueSetDefinitionReadId getBadIdentifier() {
		return new ValueSetDefinitionReadId("__NONE__", ModelUtils.nameOrUriFromName("Property Reference Test 1"));
	}
	
	public static void main(String[] args){
		System.out.println(ValueSetDefinitionUtils.getValueSetDefinitionLocalId("SRITEST:AUTO:AllDomesticANDGM"));
	}

}

