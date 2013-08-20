/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import java.util.zip.CRC32;

public final class ValueSetDefinitionUtils {
	
	private ValueSetDefinitionUtils(){
		super();
	}

	public static String getValueSetDefinitionLocalId(String definitionUri){
	    CRC32 crc = new CRC32();
	    crc.update(definitionUri.getBytes());
	    
	    return Long.toHexString(crc.getValue());
	}
	
}
