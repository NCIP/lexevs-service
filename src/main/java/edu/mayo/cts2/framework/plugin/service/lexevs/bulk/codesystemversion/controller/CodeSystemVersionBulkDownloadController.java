/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.codesystemversion.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.AbstractBulkDownloadController;
import edu.mayo.cts2.framework.plugin.service.lexevs.bulk.codesystemversion.CodeSystemVersionBulkDownloader;
import edu.mayo.cts2.framework.plugin.service.lexevs.security.msso.MssoUserValidator;

/**
 * A REST Controller for providing access to bulk downloads.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Controller("codeSystemVersionBulkDownloadController")
public class CodeSystemVersionBulkDownloadController extends AbstractBulkDownloadController implements InitializingBean {
	
	private static final String DEFAULT_SEPARATOR = "|";
	
	private static final String DEFAULT_CODING_SCHEMES = CodeSystemVersionBulkDownloader.ALL_CODINGSCHEMES;
	
	private static final String DEFAULT_FILE_NAME = "terminology-bulk-download.txt";
	
	private static final String MEDDRA_NAME = "MedDRA";
	
	private static final String NCI_META_NAME = "NCI Metathesaurus";
	
	private static final List<String> DEFAULT_FIELDS = Arrays.asList(
				CodeSystemVersionBulkDownloader.CODE_FIELD,
				CodeSystemVersionBulkDownloader.NAMESPACE_FIELD,
				CodeSystemVersionBulkDownloader.DESCRIPTION_FIELD,
				CodeSystemVersionBulkDownloader.CODINGSCHEME_NAME_FIELD,
				CodeSystemVersionBulkDownloader.CODINGSCHEME_URI_FIELD,
				CodeSystemVersionBulkDownloader.CODINGSCHEME_VERSION_FIELD);
	
	@Resource
	private LexBIGService lexBigService;
	
	@Resource
	private CodeSystemVersionBulkDownloader codeSystemVersionBulkDownloader;

	@Resource
	private MssoUserValidator mssoUserValidator;
	
	private Set<CodingSchemeReference> meddraExclusions;
	
	private Set<CodingSchemeReference> nciMetaExclusions;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.meddraExclusions = this.getMeddraCodingSchemes();
		this.nciMetaExclusions = this.getNciMetaCodingSchemes();
	}
	
	/**
	 * Download.
	 *
	 * @param response the response
	 * @param codingschemes the codingschemes
	 * @param fields the fields
	 * @param separator the separator
	 * @throws LBException the lB exception
	 */
	@RequestMapping(value="/exporter/codingscheme")
    public void download(
    		HttpServletResponse response,
    		@RequestParam(value="meddratoken", defaultValue="") String meddraToken,
    		@RequestParam(value="codingschemes", defaultValue="") String codingschemes,
    		@RequestParam(value="fields", defaultValue="") String fields,
    		@RequestParam(value="separator", defaultValue=DEFAULT_SEPARATOR) char separator,
    		@RequestParam(value="filename", defaultValue=DEFAULT_FILE_NAME) String filename) throws LBException {
		
		if(StringUtils.isBlank(codingschemes)){
			throw new UserInputException("'codingschemes' parameter is required.");
		}
		
		boolean isValidMeddraToken = false;
		if(StringUtils.isNotBlank(meddraToken)){
			boolean validates = this.mssoUserValidator.isValid(meddraToken);
			if(! validates){
		        try {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid MedDRA token.");
					return;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				isValidMeddraToken = true;
			}
		}

		List<String> fieldsList;
		if(StringUtils.isBlank(fields)){
			fieldsList = DEFAULT_FIELDS;
		} else {
			fieldsList = Arrays.asList(StringUtils.split(fields, ','));
		}
		
		this.setHeaders(response, filename);
		
		Set<CodingSchemeReference> references = new HashSet<CodingSchemeReference>();
		
		for(String codingScheme : StringUtils.split(codingschemes, ',')){
			if(codingScheme.equals(CodeSystemVersionBulkDownloader.ALL_CODINGSCHEMES)){
				continue;
			}
			String[] parts = StringUtils.split(codingScheme, ':');
			
			CodingSchemeReference reference = new CodingSchemeReference();
			reference.setCodingScheme(parts[0]);

			if(parts.length == 2){
				reference.setVersionOrTag(
					Constructors.createCodingSchemeVersionOrTagFromVersion(parts[1]));
			} 
			
			references.add(reference);
		}
		
		Set<CodingSchemeReference> exclusions = new HashSet<CodingSchemeReference>();
		exclusions.addAll(this.nciMetaExclusions);
		if(! isValidMeddraToken){
			exclusions.addAll(this.meddraExclusions);
		}

		try {
			this.codeSystemVersionBulkDownloader.download(
					response.getOutputStream(), 
					references, 
					exclusions,
					fieldsList, 
					separator);
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
	protected String getValidParametersMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("codingschemes - (Optional) CodingSchemes to include (comma-separated). Default: " + DEFAULT_CODING_SCHEMES +"\n");
		sb.append("\tFormat: codingSchemeName[:version]  - example: 'MyCodingScheme' or 'MyCodingScheme:1.0'\n");
		sb.append("\tAvailable: all," + this.getAvailableCodingSchemesString() +"\n");
		sb.append("fields - (Optional) Content fields to output. Default: "+ DEFAULT_FIELDS + "\n");
		sb.append("separator -(Optional) One character field separator. Default: " + DEFAULT_SEPARATOR +"\n");
		sb.append("filename - (Optional) Output file name. Default: " + DEFAULT_FILE_NAME);
		
		return sb.toString();
	}
	
	private Set<CodingSchemeReference> getMeddraCodingSchemes(){
		return this.doGetCodingSchemeReferences(MEDDRA_NAME);
	}
	
	private Set<CodingSchemeReference> getNciMetaCodingSchemes(){
		return this.doGetCodingSchemeReferences(NCI_META_NAME);
	}
	
	private Set<CodingSchemeReference> doGetCodingSchemeReferences(String name){
		Set<CodingSchemeReference> references = new HashSet<CodingSchemeReference>();
		try {
			for(CodingSchemeRendering scheme : lexBigService.getSupportedCodingSchemes().getCodingSchemeRendering()){
				if(scheme.getCodingSchemeSummary().getLocalName().equals(name)){
					CodingSchemeReference reference = new CodingSchemeReference();
					reference.setCodingScheme(
							scheme.getCodingSchemeSummary().getCodingSchemeURI());
					reference.setVersionOrTag(
							Constructors.createCodingSchemeVersionOrTagFromVersion(scheme.getCodingSchemeSummary().getRepresentsVersion()));
				
					references.add(reference);
				}
			}
		} catch (LBInvocationException e) {
			return null;
		}
		
		return references;
	}
	
	private String getAvailableCodingSchemesString(){
		List<String> schemes = new ArrayList<String>();
		try {
			for(CodingSchemeRendering scheme : lexBigService.getSupportedCodingSchemes().getCodingSchemeRendering()){
				if(scheme.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE)){
					String name = scheme.getCodingSchemeSummary().getLocalName();
					String version = scheme.getCodingSchemeSummary().getRepresentsVersion();
					schemes.add(name + "[:" + version + "]");
				}
			}
		} catch (LBInvocationException e) {
			return "";
		}
		
		return StringUtils.join(schemes, ",");
	}

	@Override
	public Object getController() {
		return this;
	}

}
