/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.codingSchemes.CodingScheme;

/**
 * Responsible for constructing URIs from LexEVS resources.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface UriHandler {

	/**
	 * Gets the entity uri.
	 *
	 * @param reference the reference
	 * @return the entity uri
	 */
	public String getEntityUri(ResolvedCodedNodeReference reference);
	
	/**
	 * Gets the code system uri.
	 *
	 * @param codingScheme the coding scheme
	 * @return the code system uri
	 */
	public String getCodeSystemUri(CodingScheme codingScheme);
	
	public String getCodeSystemUri(CodingSchemeSummary codingSchemeSummary);

	/**
	 * Gets the code system version uri.
	 *
	 * @param codingScheme the coding scheme
	 * @return the code system version uri
	 */
	public String getCodeSystemVersionUri(CodingScheme codingScheme);

	public String getCodeSystemVersionUri(CodingSchemeSummary codingSchemeSummary);

	public String getPredicateUri(String codingSchemeUri,
			String codingSchemeVersion, String associationName);
	
}
