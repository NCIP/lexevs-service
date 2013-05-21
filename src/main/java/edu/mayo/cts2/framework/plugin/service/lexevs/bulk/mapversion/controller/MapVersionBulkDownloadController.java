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
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.AbstractBulkDownloadController;
import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion.MapVersionBulkDownloader;

/**
 * A REST Controller for providing access to bulk downloads.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Controller("mapVersionBulkDownloadController")
public class MapVersionBulkDownloadController extends AbstractBulkDownloadController {

	private static final List<String> DEFAULT_FIELDS = Arrays.asList(
				MapVersionBulkDownloader.SOURCE_CODE_FIELD,
				MapVersionBulkDownloader.SOURCE_NAMESPACE_FIELD,
				MapVersionBulkDownloader.SOURCE_DESCRIPTION_FIELD,
				MapVersionBulkDownloader.SOURCE_CODINGSCHEME_NAME_FIELD,
				MapVersionBulkDownloader.SOURCE_CODINGSCHEME_URI_FIELD,
				MapVersionBulkDownloader.SOURCE_CODINGSCHEME_VERSION_FIELD,
				
				MapVersionBulkDownloader.TARGET_CODE_FIELD,
				MapVersionBulkDownloader.TARGET_NAMESPACE_FIELD,
				MapVersionBulkDownloader.TARGET_DESCRIPTION_FIELD,
				MapVersionBulkDownloader.TARGET_CODINGSCHEME_NAME_FIELD,
				MapVersionBulkDownloader.TARGET_CODINGSCHEME_URI_FIELD,
				MapVersionBulkDownloader.TARGET_CODINGSCHEME_VERSION_FIELD			
	);

	@Resource
	private MapVersionBulkDownloader mapVersionBulkDownloader;

	/**
	 * Download.
	 *
	 * @param response the response
	 * @param codingschemes the codingschemes
	 * @param fields the fields
	 * @param separator the separator
	 * @throws LBException the lB exception
	 */
	@RequestMapping(value="/download/map")
    public void download(
    		HttpServletResponse response,
    		@RequestParam(value="map", required=true) String map,
    		@RequestParam(value="fields", defaultValue="") String fields,
    		@RequestParam(value="separator", defaultValue=DEFAULT_SEPARATOR) char separator,
    		@RequestParam(value="filename", defaultValue=DEFAULT_FILE_NAME) String filename) throws LBException {
		
		List<String> fieldsList;
		if(StringUtils.isBlank(fields)){
			fieldsList = DEFAULT_FIELDS;
		} else {
			fieldsList = Arrays.asList(StringUtils.split(fields, ','));
		}
		
		this.setHeaders(response, filename);
		
		String[] parts = StringUtils.split(map, ':');
		
		CodingSchemeReference reference = new CodingSchemeReference();
		reference.setCodingScheme(parts[0]);

		if(parts.length == 2){
			reference.setVersionOrTag(
				Constructors.createCodingSchemeVersionOrTagFromVersion(parts[1]));
		} 


		try {
			this.mapVersionBulkDownloader.download(response.getOutputStream(), reference, fieldsList, separator);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	
        try {
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
	
	@Override
	public Object getController() {
		return this;
	}

}
