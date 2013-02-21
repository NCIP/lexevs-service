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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.OpaqueData;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService;

/**
 * The LexEVS CodeSystemVersionReadService Service implementation.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class LexEvsCodeSystemVersionReadService extends AbstractLexEvsService implements CodeSystemVersionReadService {

	private CodingSchemeToCodeSystemTransform transformer = new CodingSchemeToCodeSystemTransform();
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#readByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public CodeSystemVersionCatalogEntry readByTag(
			NameOrURI codeSystem,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		
		return this.getCodeSystemByVersionIdOrTag(
				codeSystem, 
				Constructors.createCodingSchemeVersionOrTagFromTag(tag.getContent()));
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#existsByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean existsByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#getSupportedTags()
	 */
	@Override
	public List<VersionTagReference> getSupportedTags() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#read(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public CodeSystemVersionCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#exists(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getServiceName()
	 */
	@Override
	public String getServiceName() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getServiceDescription()
	 */
	@Override
	public OpaqueData getServiceDescription() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getServiceVersion()
	 */
	@Override
	public String getServiceVersion() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getServiceProvider()
	 */
	@Override
	public SourceReference getServiceProvider() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService#existsVersionId(edu.mayo.cts2.framework.model.service.core.NameOrURI, java.lang.String)
	 */
	@Override
	public boolean existsVersionId(NameOrURI codeSystem,
			String officialResourceVersionId) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService#getCodeSystemByVersionId(edu.mayo.cts2.framework.model.service.core.NameOrURI, java.lang.String, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public CodeSystemVersionCatalogEntry getCodeSystemByVersionId(
			NameOrURI codeSystem, String officialResourceVersionId,
			ResolvedReadContext readContext) {
		
		return this.getCodeSystemByVersionIdOrTag(
				codeSystem, 
				Constructors.createCodingSchemeVersionOrTagFromVersion(officialResourceVersionId));
	}

	/**
	 * Gets the code system by version id or tag.
	 *
	 * @param codeSystem the code system
	 * @param versionIdOrTag the version id or tag
	 * @return the code system by version id or tag
	 */
	protected CodeSystemVersionCatalogEntry getCodeSystemByVersionIdOrTag(
			NameOrURI codeSystem, CodingSchemeVersionOrTag versionIdOrTag){
		String nameOrUri;
		if(codeSystem.getName() != null){
			nameOrUri = codeSystem.getName();
		} else {
			nameOrUri = codeSystem.getUri();
		}
		
		CodingScheme codingScheme;
		try {
			codingScheme = this.getLexBigService().resolveCodingScheme(nameOrUri, versionIdOrTag);
		} catch (LBException e) {
			//this could be just that LexEVS didn't find it. If so, return null.
			log.warn(e);
			return null;
		}
		
		return this.transformer.transform(codingScheme);
	}
}
