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

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSet;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.InvaildVersionNameException;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResolutionEntityQuery;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ResolvedValueSetResult;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionResolutionService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;

@Component
public class LexEvsValueSetDefinitionResolutionService extends AbstractLexEvsService
		implements ValueSetDefinitionResolutionService {

	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	@Resource
	private ResolvedValueSetResolutionService resolvedValueSetResolutionService;
	
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return this.resolvedValueSetResolutionService.getSupportedMatchAlgorithms();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		return this.resolvedValueSetResolutionService.getSupportedSearchReferences();
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return this.resolvedValueSetResolutionService.getSupportedSortReferences();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return this.resolvedValueSetResolutionService.getKnownProperties();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return this.resolvedValueSetResolutionService.getKnownNamespaceList();
	}

	@Override
	public ResolvedValueSetResult<EntitySynopsis> resolveDefinition(
			ValueSetDefinitionReadId definitionId,
			Set<NameOrURI> codeSystemVersions, 
			NameOrURI tag,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, 
			ResolvedReadContext readContext,
			Page page) {
		String definitionName = definitionId.getName();
		
		ResolvedValueSetReadId id = new ResolvedValueSetReadId(
			definitionName, 
			definitionId.getValueSet(), 
			ModelUtils.nameOrUriFromName(definitionName));
		
		Set<ResolvedFilter> filters = null;
		if(query != null){
			filters = query.getFilterComponent();
		}
		
		try {
			return this.resolvedValueSetResolutionService.getResolution(id, filters, page);
		} catch (InvaildVersionNameException e) {
			//Invalid name - return null;
			return null;
		}
	}

	@Override
	public ResolvedValueSetResult<EntityDirectoryEntry> resolveDefinitionAsEntityDirectory(
			ValueSetDefinitionReadId definitionId,
			Set<NameOrURI> codeSystemVersions, NameOrURI tag,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, ResolvedReadContext readContext,
			Page page) {
		String definitionName = definitionId.getName();
		
		ResolvedValueSetReadId id = new ResolvedValueSetReadId(
				definitionName, 
				definitionId.getValueSet(), 
				ModelUtils.nameOrUriFromName(definitionName));
		
		try {
			return this.resolvedValueSetResolutionService.getEntities(id, query, sortCriteria, page);
		} catch (InvaildVersionNameException e) {
			//Invalid name - return null;
			return null;
		}
	}

	@Override
	public ResolvedValueSet resolveDefinitionAsCompleteSet(
			ValueSetDefinitionReadId definitionId,
			Set<NameOrURI> codeSystemVersions, 
			NameOrURI tag,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException("Cannot resolve the complete ResolvedValueSet yet...");
	}
	
	
}
