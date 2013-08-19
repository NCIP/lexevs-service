/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
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
	private LexBIGService lexBigService;

	@Resource
	private MapVersionBulkDownloader mapVersionBulkDownloader;
	
	@Resource
	private MappingExtension mappingExtension;

	/**
	 * Download.
	 *
	 * @param response the response
	 * @param codingschemes the codingschemes
	 * @param fields the fields
	 * @param separator the separator
	 * @throws LBException the lB exception
	 */
	@RequestMapping(value="/exporter/map")
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
	
	@Override
	protected String getValidParametersMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("map - (Optional) the Map to export\n");
		sb.append("\tFormat: mapName[:version]  - example: 'MyMap' or 'MyMap:1.0'\n");
		sb.append("\tAvailable: " + this.getAvailableCodingSchemesString() + "\n");
		sb.append("fields - (Optional) Content fields to output. Default: "+ DEFAULT_FIELDS + "\n");
		sb.append("separator -(Optional) One character field separator. Default: " + DEFAULT_SEPARATOR +"\n");
		sb.append("filename - (Optional) Output file name. Default: " + DEFAULT_FILE_NAME);
		
		return sb.toString();
	}

	private String getAvailableCodingSchemesString(){
		List<String> schemes = new ArrayList<String>();
		try {
			for(CodingSchemeRendering scheme : lexBigService.getSupportedCodingSchemes().getCodingSchemeRendering()){
				boolean isMapping = 
						mappingExtension.isMappingCodingScheme(
								scheme.getCodingSchemeSummary().getCodingSchemeURI(),
								Constructors.createCodingSchemeVersionOrTagFromVersion(
										scheme.getCodingSchemeSummary().getRepresentsVersion()));
				if(isMapping && scheme.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE)){
					String name = scheme.getCodingSchemeSummary().getLocalName();
					String version = scheme.getCodingSchemeSummary().getRepresentsVersion();
					schemes.add(name + "[:" + version + "]");
				}
			}
		} catch (LBException e) {
			return "";
		}
		
		return StringUtils.join(schemes, ",");
	}
}
