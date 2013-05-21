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
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk;

import javax.servlet.http.HttpServletResponse;

import edu.mayo.cts2.framework.webapp.rest.extensions.controller.ControllerProvider;


/**
 * An abstract Controller for any bulk downloads.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractBulkDownloadController implements ControllerProvider {
	
	protected static final String DEFAULT_SEPARATOR = "|";
	
	protected static final String DEFAULT_FILE_NAME = "terminology-bulk-download.txt";

	/**
	 * Sets the headers.
	 *
	 * @param response the response
	 * @param filename the filename
	 */
	protected void setHeaders(HttpServletResponse response, String filename){
		String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", filename);
        response.setHeader(headerKey, headerValue);
		response.setContentType("text/plain; charset=utf-8");
	}

}
