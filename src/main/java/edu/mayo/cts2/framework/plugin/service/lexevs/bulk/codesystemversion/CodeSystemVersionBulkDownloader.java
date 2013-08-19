/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.codesystemversion;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;

/**
 * A Bulk Downloading interface for downloading large terminology content.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface CodeSystemVersionBulkDownloader {
	
	static final String ALL_CODINGSCHEMES = "all";
	
	static final String CODE_FIELD = "code";
	static final String NAMESPACE_FIELD = "namespace";
	static final String DESCRIPTION_FIELD = "description";
	static final String CODINGSCHEME_NAME_FIELD = "codingschemename";
	static final String CODINGSCHEME_URI_FIELD = "codingschemeuri";
	static final String CODINGSCHEME_VERSION_FIELD = "codingschemeversion";
	
	/**
	 * Send requested content to the provided {@link OutputStream}.
	 *
	 * @param outputStream the output stream
	 * @param codingSchemes the coding schemes
	 * @param fields the fields
	 * @param separator the separator
	 */
	void download(OutputStream outputStream, Set<CodingSchemeReference> codingSchemes, Set<CodingSchemeReference> excludedCodingSchemes, List<String> fields, char separator);
}
