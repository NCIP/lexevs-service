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
