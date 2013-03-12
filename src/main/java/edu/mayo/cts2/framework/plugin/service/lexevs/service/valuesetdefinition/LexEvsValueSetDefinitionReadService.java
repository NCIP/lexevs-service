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
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.extension.LocalIdValueSetDefinition;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionHistoryService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsValueSetDefinitionReadService extends AbstractLexEvsService
		implements ValueSetDefinitionReadService,
		ValueSetDefinitionHistoryService {
	
	private LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform vsdTransformer = new LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform();

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.TagAwareReadService#readByTag(edu.mayo.cts2.framework.model.service.core.NameOrURI, edu.mayo.cts2.framework.model.core.VersionTagReference, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public LocalIdValueSetDefinition readByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		
		if (parentIdentifier == null) {
			throw new UnsupportedOperationException("The passed in edu.mayo.cts2.framework.model.service.core.NameOrURI parameter cannot be a null value.");
		} else {
			return getValueSetDefinition(parentIdentifier, tag);
		}
		
	}
	
	protected LocalIdValueSetDefinition getValueSetDefinition(NameOrURI parentIdentifier, VersionTagReference tag) {
		
		//String name = parentIdentifier.getName();
		String uriString = parentIdentifier.getUri();
		URI valueSetDefinitionURI;
		try {
			valueSetDefinitionURI = new URI(uriString);
		} catch (URISyntaxException uriSyntaxException) {
			throw new RuntimeException(uriSyntaxException);
		}
		
		String valueSetDefinitionRevisionId = (tag != null) ? tag.getContent() : null;
		org.LexGrid.valueSets.ValueSetDefinition lexGridValueSetDefinition = new org.LexGrid.valueSets.ValueSetDefinition();

		try {
			lexGridValueSetDefinition = getLexEVSValueSetDefinitionServices().getValueSetDefinition(valueSetDefinitionURI, valueSetDefinitionRevisionId);
		} catch (LBException lbe) {
			throw new RuntimeException(lbe);
		}
		
		// TODO Need to transform org.LexGrid.valueSets.ValueSetDefinition into a 
		//   edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition object and use it to
		//   construct a CTS2 LocalIdValueSetDefinition object	
		ValueSetDefinition valueSetDefinition = null;
		if (lexGridValueSetDefinition != null) {
			valueSetDefinition = vsdTransformer.transformToValueSetDefinition(lexGridValueSetDefinition);			
		} else {
			valueSetDefinition = new ValueSetDefinition();
		}
		
		// TODO Need CTS2 ValueSetDefinition as min for LocalIdValueSetDefinition constructor
		//   and maybe use it's 2 param constructor (has String localID)???
		LocalIdValueSetDefinition retVal = new LocalIdValueSetDefinition(valueSetDefinition); 
		
		return retVal;
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
	public LocalIdValueSetDefinition read(ValueSetDefinitionReadId identifier,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#exists(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean exists(ValueSetDefinitionReadId identifier,
			ResolvedReadContext readContext) {
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
	 * @see edu.mayo.cts2.framework.service.profile.HistoryService#getEarliestChange()
	 */
	@Override
	public Date getEarliestChange() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.HistoryService#getLatestChange()
	 */
	@Override
	public Date getLatestChange() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.HistoryService#getChangeHistory(java.lang.Object, java.util.Date, java.util.Date)
	 */
	@Override
	public DirectoryResult<ValueSetDefinition> getChangeHistory(
			String identifier, Date fromDate, Date toDate) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.HistoryService#getEarliestChangeFor(java.lang.Object)
	 */
	@Override
	public ValueSetDefinition getEarliestChangeFor(String identifier) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.HistoryService#getLastChangeFor(java.lang.Object)
	 */
	@Override
	public ValueSetDefinition getLastChangeFor(String identifier) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.HistoryService#getChangeHistoryFor(java.lang.Object)
	 */
	@Override
	public DirectoryResult<ValueSetDefinition> getChangeHistoryFor(
			String identifier) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedMatchAlgorithms()
	 */
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedSearchReferences()
	 */
	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedSortReferences()
	 */
	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getKnownProperties()
	 */
	@Override
	public Set<PredicateReference> getKnownProperties() {
		throw new UnsupportedOperationException();
	}

}
