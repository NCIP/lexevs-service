/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class FakeCodedNode {
	String codingScheme;
	String version;
	
	public FakeCodedNode(String codingScheme, String version) {
		this.codingScheme = codingScheme;
		this.version = version;
	}
	
	public String getCodingScheme(){
		return this.codingScheme;
	}
	
	public String getVersion(){
		return this.version;
	}

}
