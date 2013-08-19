/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.extension.LocalIdValueSetDefinition;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetDefinitionUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNamePair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsValueSetDefinitionReadService extends AbstractLexEvsService
		implements ValueSetDefinitionReadService {
	
	@Resource
	private LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform vsdTransformer;
	
	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#readByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public LocalIdValueSetDefinition readByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		ValueSetNamePair name = valueSetNameTranslator.getCurrentDefinition(parentIdentifier.getName());
		
		if(name != null){
			ValueSetDefinitionReadId id = 
				new ValueSetDefinitionReadId(
					name.getDefinitionLocalId(), 
					ModelUtils.nameOrUriFromName(name.getValueSetName()));
			
			return this.read(id, readContext);
		} else {
			return null;
		}
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
	public LocalIdValueSetDefinition read(
			ValueSetDefinitionReadId identifier,
			ResolvedReadContext readContext) {
		String uriString;
		
		String localName = identifier.getName();
		if(localName != null){
			uriString = 
				this.valueSetNameTranslator.getDefinitionUri(
					identifier.getValueSet().getName(),
					identifier.getName());
		} else {
			uriString = identifier.getUri();
		}

		if(StringUtils.isBlank(uriString)){
			return null;
		}
		
		URI valueSetDefinitionURI;
		try {
			valueSetDefinitionURI = new URI(uriString);
		} catch (URISyntaxException uriSyntaxException) {
			throw new RuntimeException(uriSyntaxException);
		}

		org.LexGrid.valueSets.ValueSetDefinition lexGridValueSetDefinition;
		try {
			lexGridValueSetDefinition = getLexEVSValueSetDefinitionServices().getValueSetDefinition(valueSetDefinitionURI, null);
		} catch (LBException lbe) {
			throw new RuntimeException(lbe);
		}
			
		if (lexGridValueSetDefinition != null) {
			String localId = ValueSetDefinitionUtils.getValueSetDefinitionLocalId(uriString);
			return new LocalIdValueSetDefinition(
				localId,
				vsdTransformer.transformFullDescription(lexGridValueSetDefinition).getEntry(0));			
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#exists(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean exists(ValueSetDefinitionReadId identifier,
			ResolvedReadContext readContext) {
		return this.read(identifier, readContext) != null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}
	
}
