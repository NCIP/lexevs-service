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
