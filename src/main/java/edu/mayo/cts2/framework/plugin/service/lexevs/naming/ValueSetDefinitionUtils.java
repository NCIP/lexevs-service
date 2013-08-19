/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public final class ValueSetDefinitionUtils {
	
	private ValueSetDefinitionUtils(){
		super();
	}

	public static String getValueSetDefinitionLocalId(String definitionUri){
		String hash = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(definitionUri.getBytes());
		return 
			org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(definitionUri.getBytes()).
				substring(hash.length() - 5, hash.length() - 1);
	}
	
}
