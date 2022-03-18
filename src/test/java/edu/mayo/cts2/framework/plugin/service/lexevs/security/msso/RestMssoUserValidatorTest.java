/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.security.msso;

import static org.junit.Assert.*;

import org.junit.Test;

public class RestMssoUserValidatorTest {
	
//	@Test
//	public void testValid(){
//		RestMssoUserValidator validator = new RestMssoUserValidator();
//		
//		//dummy token -- insert a valid one here
//		//assertTrue(validator.isValid("11111"));
//	}

	
	@Test
	public void testInValid(){
		RestMssoUserValidator validator = new RestMssoUserValidator();
		
		assertFalse(validator.isValid("12345"));
	}
	
	@Test
	public void testInValidNonNumber(){
		RestMssoUserValidator validator = new RestMssoUserValidator();
		
		assertFalse(validator.isValid("__INVALID__"));
	}

}
