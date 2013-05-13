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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.extension.LocalIdValueSetDefinition;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
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
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#readByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public LocalIdValueSetDefinition readByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		throw new UnsupportedOperationException("Not supported in LexEVS.");
	}

	protected LocalIdValueSetDefinition getValueSetDefinition(NameOrURI parentIdentifier, VersionTagReference tag) {
		throw new UnsupportedOperationException("Not supported in LexEVS.");
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
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#read(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public LocalIdValueSetDefinition read(
			ValueSetDefinitionReadId identifier,
			ResolvedReadContext readContext) {
		String uriString = identifier.getUri();
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
			return new LocalIdValueSetDefinition(
				vsdTransformer.transformFullDescription(lexGridValueSetDefinition));			
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
		throw new UnsupportedOperationException();
	}
	
}
