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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService;

/**
 * The LexEVS CodeSystemVersionReadService Service implementation.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class LexEvsCodeSystemVersionReadService 
	extends AbstractLexEvsCodeSystemService<CodeSystemVersionCatalogEntry> 
	implements CodeSystemVersionReadService {
	
	@Resource
	private CodingSchemeToCodeSystemTransform transformer;
	
	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	
	// ------ Local methods ----------------------	
	public void setCodingSchemeToCodeSystemTransform(
			CodingSchemeToCodeSystemTransform transformer) {
		this.transformer = transformer;
	}
	
	public void setCodeSystemVersionNameConverter(CodeSystemVersionNameConverter nameConverter){
		this.nameConverter = nameConverter;
	}
	
	
	// -------- Implemented methods ----------------
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#readByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public CodeSystemVersionCatalogEntry readByTag(
			NameOrURI codeSystem,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		
		return this.getByVersionIdOrTag(
				codeSystem, 
				this.convertTag(tag));
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#existsByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean existsByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		return this.readByTag(parentIdentifier, tag, readContext) != null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#getSupportedTags()
	 */
	@Override
	public List<VersionTagReference> getSupportedTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#read(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public CodeSystemVersionCatalogEntry read(
			NameOrURI identifier,
			ResolvedReadContext readContext) {
		String name;
		if(identifier.getName() != null){
			name = identifier.getName();
			if(!this.nameConverter.isValidCodeSystemVersionName(name)){
				return null;
			}
		} else {
			throw new UnsupportedOperationException("Cannot resolve by DocumentURI yet.");
		}
		
		NameVersionPair namePair = 
			this.nameConverter.fromCts2CodeSystemVersionName(name);
		
		return this.getByVersionIdOrTag(
			ModelUtils.nameOrUriFromName(namePair.getName()),
			Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion()));
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#exists(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		return this.read(identifier, readContext) != null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService#existsVersionId(edu.mayo.cts2.framework.model.service.core.NameOrURI, java.lang.String)
	 */
	@Override
	public boolean existsVersionId(NameOrURI codeSystem,
			String officialResourceVersionId) {
		return this.getCodeSystemByVersionId(
			codeSystem, 
			officialResourceVersionId, 
			null) != null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService#getCodeSystemByVersionId(edu.mayo.cts2.framework.model.service.core.NameOrURI, java.lang.String, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public CodeSystemVersionCatalogEntry getCodeSystemByVersionId(
			NameOrURI codeSystem, String officialResourceVersionId,
			ResolvedReadContext readContext) {
		
		return this.getByVersionIdOrTag(
				codeSystem, 
				Constructors.createCodingSchemeVersionOrTagFromVersion(officialResourceVersionId));
	}

	@Override
	protected CodeSystemVersionCatalogEntry transform(CodingScheme codingScheme) {
		return this.transformer.transform(codingScheme);
	}

}
