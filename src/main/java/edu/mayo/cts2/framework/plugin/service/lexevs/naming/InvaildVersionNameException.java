/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public class InvaildVersionNameException extends RuntimeException {

	private static final long serialVersionUID = -765984153939066266L;
	
	protected InvaildVersionNameException(String name){
		super("Version name: " + name + " is not a valid CTS2 Version name.");
	}

}
