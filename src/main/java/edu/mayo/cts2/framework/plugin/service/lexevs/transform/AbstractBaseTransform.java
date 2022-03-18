/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.transform;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.XmlUtils;

public abstract class AbstractBaseTransform<
	DescriptionDataType, 
	DescriptionDataIN, 
	DirectoryEntryDataType, 
	DirectoryEntryDataIN>
	implements LexEvsToCTS2Transformer<
		DescriptionDataType, 
		DescriptionDataIN, 
		DirectoryEntryDataType, 
		DirectoryEntryDataIN> {
	
	protected Logger log = LogManager.getLogger(this.getClass());
	
	@Resource
	private TransformUtils transformUtils;
	
	@Resource
	private UrlConstructor urlConstructor;
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;
	
	@Resource
	private UriHandler uriHandler;

	public TransformUtils getTransformUtils() {
		return transformUtils;
	}

	public void setTransformUtils(TransformUtils transformUtils) {
		this.transformUtils = transformUtils;
	}

	public UrlConstructor getUrlConstructor() {
		return urlConstructor;
	}

	public void setUrlConstructor(UrlConstructor urlConstructor) {
		this.urlConstructor = urlConstructor;
	}

	public VersionNameConverter getVersionNameConverter() {
		return versionNameConverter;
	}

	public void setVersionNameConverter(VersionNameConverter versionNameConverter) {
		this.versionNameConverter = versionNameConverter;
	}

	public UriHandler getUriHandler() {
		return uriHandler;
	}

	public void setUriHandler(UriHandler uriHandler) {
		this.uriHandler = uriHandler;
	}

	public CodingSchemeNameTranslator getCodingSchemeNameTranslator() {
		return codingSchemeNameTranslator;
	}

	public void setCodingSchemeNameTranslator(
			CodingSchemeNameTranslator codingSchemeNameTranslator) {
		this.codingSchemeNameTranslator = codingSchemeNameTranslator;
	}
	
	protected String sanitizeNamespace(String namespace){
		namespace = this.getCodingSchemeNameTranslator().translateFromLexGrid(namespace);

		boolean isNamespaceValidNCName = XmlUtils.isNCName(namespace);
		if(isNamespaceValidNCName){
			return namespace;
		} else {
			//Last ditch effort... generate a random namespace.
			//If it gets here, it probably needs to be added
			//to the UriResolver.
			return "ns" + Integer.toString(namespace.hashCode());
		}
	}

}
