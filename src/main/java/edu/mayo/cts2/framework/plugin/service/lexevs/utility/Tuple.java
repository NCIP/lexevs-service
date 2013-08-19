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
 * A simple tuple.
 *
 * @param <T> the tuple type
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class Tuple<T> {
	
	private T one;
	private T two;
	
	/**
	 * Instantiates a new tuple.
	 *
	 * @param one the one
	 * @param two the two
	 */
	public Tuple(T one, T two) {
		super();
		this.one = one;
		this.two = two;
	}
	
	public T getOne() {
		return one;
	}
	
	public T getTwo() {
		return two;
	}
	
}
