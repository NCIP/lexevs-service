/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public interface ResolvedValueSetNameTranslator {

	public static final String RESOLVED_VS_LOCAL_ID = "1";
	
	public NameVersionPair getNameVersionPair(ResolvedValueSetNameTriple resolvedValueSetNameTriple);

	public ResolvedValueSetNameTriple getResolvedValueSetNameTriple(
			String codingSchemeURI);

}
