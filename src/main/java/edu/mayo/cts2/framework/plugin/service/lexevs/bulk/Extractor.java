/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

/**
 * Interface for extracting String information from a ResolvedConceptReference.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface Extractor {
	
	/**
	 * Extract.
	 *
	 * @param ref the ref
	 * @return the string
	 */
	public String extract(ResolvedConceptReference ref);
	
}