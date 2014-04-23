/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public interface ValueSetNameTranslator {
	
	public static final String UNNAMED_VALUESET = "Unnamed";
	
	public String getDefinitionUri(String valueSetName, String definitionLocalId);
	
	public ValueSetNamePair getDefinitionNameAndVersion(String uri);

	public ValueSetNamePair getCurrentDefinition(String valueSetName);
	
	public String getRegularValueSetCurrentDefinition(String valueSetName);

}
