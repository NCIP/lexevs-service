/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.codesystemversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntryListEntry;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService;

/**
 * The LexEVS CodeSystemVersionReadService Service implementation.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class LexEvsCodeSystemVersionReadService extends
		AbstractLexEvsCodeSystemService<CodeSystemVersionCatalogEntry>
		implements CodeSystemVersionReadService {

	@Resource
	private CodingSchemeToCodeSystemTransform transformer;

	@Resource
	private VersionNameConverter nameConverter;

	// ------ Local methods ----------------------
	public void setCodingSchemeToCodeSystemTransform(
			CodingSchemeToCodeSystemTransform transformer) {
		this.transformer = transformer;
	}

	public void setCodeSystemVersionNameConverter(
			VersionNameConverter nameConverter) {
		this.nameConverter = nameConverter;
	}

	// -------- Implemented methods ----------------
	@Override
	public CodeSystemVersionCatalogEntry readByTag(
			NameOrURI codeSystem,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		if(codeSystem == null || tag == null){
			return null;
		}
		
		return this.getByVersionIdOrTag(codeSystem, CommonUtils.convertTag(tag));
	}

	@Override
	public boolean existsByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		return this.readByTag(parentIdentifier, tag, readContext) != null;
	}

	@Override
	public List<VersionTagReference> getSupportedTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	@Override
	public CodeSystemVersionCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		NameVersionPair pair = this.getNamePair(nameConverter, identifier, readContext);
		
		return this.getByVersionIdOrTag(pair);
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		return this.read(identifier, readContext) != null;
	}

	@Override
	public boolean existsVersionId(NameOrURI codeSystem,
			String officialResourceVersionId) {
		return this.getCodeSystemByVersionId(codeSystem, officialResourceVersionId, null) != null;
	}

	@Override
	public CodeSystemVersionCatalogEntry getCodeSystemByVersionId(
			NameOrURI codeSystem, String officialResourceVersionId,
			ResolvedReadContext readContext) {
		if(codeSystem == null){
			return null;
		}
		
		CodingSchemeVersionOrTag versionOrTag = Constructors.createCodingSchemeVersionOrTagFromVersion(officialResourceVersionId);
		CodeSystemVersionCatalogEntry entry = this.getByVersionIdOrTag(codeSystem, versionOrTag);
        return entry;
	}

	@Override
	protected CodeSystemVersionCatalogEntry transform(CodingScheme codingScheme) {
		CodeSystemVersionCatalogEntryListEntry cs = this.transformer.transformFullDescription(codingScheme);
		
		return cs == null ? null : cs.getEntry();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
