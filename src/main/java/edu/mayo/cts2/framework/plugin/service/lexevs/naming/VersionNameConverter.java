/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * CTS2 CodeSystemVersionNames will generally be in the form:
 * 
 * {Name}-{VersionId}
 * 
 * For example, SNOMEDCT-20120101
 * 
 * LexEVS will need this broken apart, for example
 * CodingSchemeName: SNOMEDCT, VersionId: 20120101
 */
@Component
public class VersionNameConverter {

	private static final String SEPARATOR = "-";
	
	private static String SEPARATOR_ENCODE = "[:]";
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;
	
	public VersionNameConverter(){
		super();
	}
	
	public VersionNameConverter(CodingSchemeNameTranslator codingSchemeNameTranslator){
		super();
		this.codingSchemeNameTranslator = codingSchemeNameTranslator;
	}
	/**
	 * To cts2 code system version name.
	 *
	 * @param lexEvsCodingSchemeName the lex evs coding scheme name
	 * @param version the version
	 * @return the string
	 */
	public String toCts2VersionName(String lexEvsCodingSchemeName, String version){
		return 
			this.codingSchemeNameTranslator.translateFromLexGrid(lexEvsCodingSchemeName) 
			+ SEPARATOR
			+ this.escapeVersion(version);
	}
	
	/**
	 * From cts2 code system version name.
	 *
	 * @param cts2CodeSystemVersionName the cts2 code system version name
	 * @return the name version pair
	 * @throws InvaildVersionNameException 
	 */
	public NameVersionPair fromCts2VersionName(String cts2CodeSystemVersionName) throws InvaildVersionNameException{
		if(! this.isValidVersionName(cts2CodeSystemVersionName)){
			throw new InvaildVersionNameException(cts2CodeSystemVersionName);
		}
		
		String version = StringUtils.substringAfterLast(cts2CodeSystemVersionName, SEPARATOR);
		String name = StringUtils.substringBeforeLast(cts2CodeSystemVersionName, SEPARATOR);

		return new NameVersionPair(
			this.codingSchemeNameTranslator.translateToLexGrid(name), 
			this.unescapeVersion(version));
	}
	
	public boolean isValidVersionName(String cts2CodeSystemVersionName){
		String[] nameParts = StringUtils.split(cts2CodeSystemVersionName, SEPARATOR);
		return nameParts.length >= 2;
	}
	
	public String escapeVersion(String version){
		return StringUtils.replace(version, SEPARATOR, SEPARATOR_ENCODE);
	}
	
	public String unescapeVersion(String version){
		return StringUtils.replace(version, SEPARATOR_ENCODE, SEPARATOR);
	}

	public CodingSchemeNameTranslator getCodingSchemeNameTranslator() {
		return codingSchemeNameTranslator;
	}

	public void setCodingSchemeNameTranslator(
			CodingSchemeNameTranslator codingSchemeNameTranslator) {
		this.codingSchemeNameTranslator = codingSchemeNameTranslator;
	}
	
}
