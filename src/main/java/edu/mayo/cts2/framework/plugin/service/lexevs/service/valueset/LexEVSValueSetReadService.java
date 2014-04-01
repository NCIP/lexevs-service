/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetReadService;


/**
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 */
@Component
public class LexEVSValueSetReadService extends AbstractLexEvsService implements
		ValueSetReadService {

	@Resource
	private ValueSetTransform transform;

	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;

	@Override
	public ValueSetCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		String uriString = null;

		String localName = identifier.getName();

		if (localName != null) {
			uriString = this.valueSetNameTranslator
					.getRegularValueSetCurrentDefinition(localName);
		} else {
			uriString = identifier.getUri();
		}

		URI valueSetDefinitionURI;
		try {
			valueSetDefinitionURI = new URI(uriString);
		} catch (URISyntaxException uriSyntaxException) {
			throw new RuntimeException(uriSyntaxException);
		}

		org.LexGrid.valueSets.ValueSetDefinition lexGridValueSetDefinition;
		try {
			lexGridValueSetDefinition = getLexEVSValueSetDefinitionServices()
					.getValueSetDefinition(valueSetDefinitionURI, null);
		} catch (LBException lbe) {
			throw new RuntimeException(lbe);
		}

		if (lexGridValueSetDefinition != null) {
			ValueSetCatalogEntry entry = this.transform
					.transformFullDescription(lexGridValueSetDefinition);
			return entry;
		} else {
			return null;
		}
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		throw new UnsupportedOperationException();
	}

}
