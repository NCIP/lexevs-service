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
package edu.mayo.cts2.framework.plugin.service.lexevs.service;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;

/**
 * A base service for all services needing to deal with LexEVS CodingSchemes.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractLexEvsCodeSystemService<T> extends AbstractLexEvsService {
	
	@Resource
	private UriResolver uriResolver;
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	protected abstract T transform(CodingScheme codingScheme);

	protected T getByVersionIdOrTag(
			NameOrURI parentIdentifier,
			CodingSchemeVersionOrTag convertTag) {
		String id;
		if(parentIdentifier.getName() != null){
			id = parentIdentifier.getName();
		} else {
			id = parentIdentifier.getUri();
		}
		
		CodingSchemeVersionOrTag csvt;
		if(convertTag.getTag() != null){
			csvt = Constructors.createCodingSchemeVersionOrTagFromTag(convertTag.getTag());
		} else {
			csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(convertTag.getVersion());
		}
		CodingScheme codingScheme = this.resolve(id,csvt);

		if(codingScheme != null && this.isValidCodingScheme(codingScheme)){
			return this.transform(codingScheme);
		} else {
			return null;
		}
	}
	
	/**
	 * Allow subclasses to validate the CodingScheme before transforming.
	 *
	 * @param codingScheme the coding scheme
	 * @return true, if is valid coding scheme
	 */
	protected boolean isValidCodingScheme(CodingScheme codingScheme){
		return true;
	}
	
	/**
	 * Gets the code system by version id or tag.
	 *
	 * @param codeSystem the code system
	 * @param versionIdOrTag the version id or tag
	 * @return the code system by version id or tag
	 */
	protected T getByVersionIdOrTag(
			NameVersionPair namePair){
		if(namePair == null){
			return null;
		}

		CodingScheme codingScheme = this.resolve(namePair.getName(), 
			Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion()));

		if(codingScheme != null && this.isValidCodingScheme(codingScheme)){
			return this.transform(codingScheme);
		} else {
			return null;
		}
	}
	
	protected CodingScheme resolve(String nameOrUri, CodingSchemeVersionOrTag versionIdOrTag){
		CodingScheme codingScheme;
		try {
			codingScheme = this.getLexBigService().resolveCodingScheme(nameOrUri, versionIdOrTag);
		} catch (LBException e) {
			//this could be just that LexEVS didn't find it. If so, return null.
			log.warn(e);
			return null;
		}
		
		return codingScheme;
	}

	public NameVersionPair getNamePair(
			VersionNameConverter nameConverter, 
			NameOrURI cts2NameOrURI,
			ResolvedReadContext cts2ReadContext) {
		if(cts2NameOrURI == null){
			return null;
		}
		String cts2Name;
		NameVersionPair namePair;
		
		if (cts2NameOrURI.getName() != null) {
			cts2Name = cts2NameOrURI.getName();
			if (!nameConverter.isValidVersionName(cts2Name)) {
				namePair = null;
			}
			else{
				namePair = nameConverter.fromCts2VersionName(cts2Name);		
			}
		} else {
			String fullUri = cts2NameOrURI.getUri();
			String uri = StringUtils.substringBeforeLast(fullUri, "/");
			String version = StringUtils.substringAfterLast(fullUri, "/");
			
			namePair = new NameVersionPair(uri, version);
		}

		return namePair;
	}
}
