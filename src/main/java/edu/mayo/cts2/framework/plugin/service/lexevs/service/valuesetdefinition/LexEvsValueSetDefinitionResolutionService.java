/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSet;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.InvaildVersionNameException;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ResolvedValueSetNameTranslator;
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
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		return this.resolvedValueSetResolutionService.getSupportedSearchReferences();
	}

	@Override
	public Set<? extends ComponentReference> getSupportedSortReferences() {
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

//	@Override
//	public ResolvedValueSetResult<EntitySynopsis> resolveDefinition(
//			ValueSetDefinitionReadId definitionId,
//			Set<NameOrURI> codeSystemVersions, 
//			NameOrURI tag,
//			ResolvedValueSetResolutionEntityQuery query,
//			SortCriteria sortCriteria, 
//			ResolvedReadContext readContext,
//			Page page) {
//		String definitionName = definitionId.getName();
//		
//		ResolvedValueSetReadId id = new ResolvedValueSetReadId(
//			ResolvedValueSetNameTranslator.RESOLVED_VS_LOCAL_ID, 
//			definitionId.getValueSet(), 
//			ModelUtils.nameOrUriFromName(definitionName));
//		
//		Set<ResolvedFilter> filters = null;
//		if(query != null){
//			filters = query.getFilterComponent();
//		}
//		
//		try {
//			return this.resolvedValueSetResolutionService.getResolution(id, filters, page);
//		} catch (InvaildVersionNameException e) {
//			//Invalid name - return null;
//			return null;
//		}
//	}

	@Override
	public ResolvedValueSetResult<EntityDirectoryEntry> resolveDefinitionAsEntityDirectory(
			ValueSetDefinitionReadId definitionId,
			Set<NameOrURI> codeSystemVersions, NameOrURI tag,
			ResolvedValueSetResolutionEntityQuery query,
			SortCriteria sortCriteria, ResolvedReadContext readContext,
			Page page) {
		String definitionName = definitionId.getName();
		
		ResolvedValueSetReadId id = new ResolvedValueSetReadId(
				ResolvedValueSetNameTranslator.RESOLVED_VS_LOCAL_ID,
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
	public ResolvedValueSetResult<URIAndEntityName> resolveDefinition(
			ValueSetDefinitionReadId definitionId,
			Set<NameOrURI> codeSystemVersions, NameOrURI tag,
			SortCriteria sortCriteria, ResolvedReadContext readContext,
			Page page) {
		String definitionName = definitionId.getName();
		
		ResolvedValueSetReadId id = new ResolvedValueSetReadId(
			ResolvedValueSetNameTranslator.RESOLVED_VS_LOCAL_ID, 
			definitionId.getValueSet(), 
			ModelUtils.nameOrUriFromName(definitionName));
		
		Set<ResolvedFilter> filters = null;
//		if(query != null){
//			filters = query.getFilterComponent();
//		}
		
		try {
			return this.resolvedValueSetResolutionService.getResolution(id, filters, page);
		} catch (InvaildVersionNameException e) {
			//Invalid name - return null;
			return null;
		}
	}

	@Override
	public ResolvedValueSet resolveDefinitionAsCompleteSet(
			ValueSetDefinitionReadId definitionId,
			Set<NameOrURI> codeSystemVersions, NameOrURI tag,
			SortCriteria sortCriteria, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
	String definitionName = definitionId.getName();
		
		ResolvedValueSetReadId id = new ResolvedValueSetReadId(
			ResolvedValueSetNameTranslator.RESOLVED_VS_LOCAL_ID, 
			definitionId.getValueSet(), 
			ModelUtils.nameOrUriFromName(definitionName));

		try {
			return this.resolvedValueSetResolutionService.getResolution(id);
		} catch (InvaildVersionNameException e) {
			//Invalid name - return null;
			return null;
		}
	}
	
}
