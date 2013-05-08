/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
