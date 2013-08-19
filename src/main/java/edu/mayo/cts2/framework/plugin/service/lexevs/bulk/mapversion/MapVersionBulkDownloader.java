/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion;

import java.io.OutputStream;
import java.util.List;

import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;

/**
 * A Bulk Downloading interface for downloading large terminology content.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface MapVersionBulkDownloader {
	
	static final String SOURCE_CODE_FIELD = "source-code";
	static final String SOURCE_NAMESPACE_FIELD = "source-namespace";
	static final String SOURCE_DESCRIPTION_FIELD = "source-description";
	static final String SOURCE_CODINGSCHEME_NAME_FIELD = "source-codingschemename";
	static final String SOURCE_CODINGSCHEME_URI_FIELD = "source-codingschemeuri";
	static final String SOURCE_CODINGSCHEME_VERSION_FIELD = "source-codingschemeversion";
	
	static final String TARGET_CODE_FIELD = "target-code";
	static final String TARGET_NAMESPACE_FIELD = "target-namespace";
	static final String TARGET_DESCRIPTION_FIELD = "target-description";
	static final String TARGET_CODINGSCHEME_NAME_FIELD = "target-codingschemename";
	static final String TARGET_CODINGSCHEME_URI_FIELD = "target-codingschemeuri";
	static final String TARGET_CODINGSCHEME_VERSION_FIELD = "target-codingschemeversion";
	
	/**
	 * Send requested content to the provided {@link OutputStream}.
	 *
	 * @param outputStream the output stream
	 * @param codingScheme the coding scheme
	 * @param fields the fields
	 * @param separator the separator
	 */
	void download(OutputStream outputStream, CodingSchemeReference codingScheme, List<String> fields, char separator);
}
