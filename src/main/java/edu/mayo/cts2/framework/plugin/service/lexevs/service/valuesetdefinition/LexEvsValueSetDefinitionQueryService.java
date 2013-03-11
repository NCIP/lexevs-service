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
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.lexgrid.valuesets.dto.ResolvedValueSetCodedNodeSet;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.command.restriction.ValueSetDefinitionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionQuery;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionQueryService;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsValueSetDefinitionQueryService extends AbstractLexEvsService
		implements ValueSetDefinitionQueryService {

	private LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform vsdTransformer = new LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform();

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceSummaries(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<ValueSetDefinitionDirectoryEntry> getResourceSummaries(
			ValueSetDefinitionQuery query, SortCriteria sortCriteria, Page page) {
		
		
		ValueSetDefinitionQueryServiceRestrictions vsdQueryServiceRestrictions = null;
		if (query != null) {
			vsdQueryServiceRestrictions = query.getRestrictions();
		}
		NameOrURI nameOrURI = null;
		if (vsdQueryServiceRestrictions != null) {
			nameOrURI = vsdQueryServiceRestrictions.getValueSet();
		}
		String codeSystemName = null; // maps to coding scheme name
		if (nameOrURI != null) {
			if (nameOrURI.getName() != null) {
				codeSystemName = nameOrURI.getName();
			} else {
				codeSystemName = nameOrURI.getUri();
			}
		}
			
		// *** Block below does not work ***
		
/*		
		URI valueSetDefinitionURI = null;
		try {
			valueSetDefinitionURI = new URI(codeSystemName);
		} catch (URISyntaxException uriSyntaxException) {
			throw new RuntimeException(uriSyntaxException);
		}		
		// TODO Do null items below need to get built or can/should they be null ???
		String valueSetDefinitionRevisionId = null;
		AbsoluteCodingSchemeVersionReferenceList csVersionList = null;
		//AbsoluteCodingSchemeVersionReferenceList csVersionList = new AbsoluteCodingSchemeVersionReferenceList();
		AbsoluteCodingSchemeVersionReference codingSchemeVersionRef = new AbsoluteCodingSchemeVersionReference();
		// codingSchemeVersionRef.setCodingSchemeURN(codingSchemeURN);  // TODO from where ???
		String versionTag = null;
		ResolvedValueSetCodedNodeSet resolvedValueSetCodedNodeSet;
		try {
			resolvedValueSetCodedNodeSet = 
				getLexEVSValueSetDefinitionServices().getCodedNodeSetForValueSetDefinition(valueSetDefinitionURI, valueSetDefinitionRevisionId, csVersionList, versionTag);
		} catch (LBException lbe) {
			throw new RuntimeException(lbe);			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// TODO Do we need to handle how the hard coded items below get managed in a dynamic manner
		LocalNameList propertyNames = null;  
		PropertyType[] propertyTypes = null; 
		SortOptionList sortOptions = null;  
		LocalNameList filterOptions = null; 		
		int maxToReturn = -1;
		
		ResolvedConceptReferenceList rcrList;
		try {
			rcrList = resolvedValueSetCodedNodeSet.getCodedNodeSet().resolveToList(sortOptions, filterOptions, propertyNames, propertyTypes, maxToReturn);
		} catch (LBInvocationException lbie) {
			throw new RuntimeException(lbie);			
		} catch (LBParameterException lbpe) {
			throw new RuntimeException(lbpe);			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
*/		
		
		// TODO What method(s) need to be invoked on rcrList so that loop can be used to retrieve LexEVS objects and convert
		//   them to CTS2 ValueSetDefinitionDirectoryEntry objects ???  Need a new transform class?
		
		// TODO Incorporated paging from Page parameter
		
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceList(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<ValueSetDefinition> getResourceList(
			ValueSetDefinitionQuery query, SortCriteria sortCriteria, Page page) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#count(edu.mayo.cts2.framework.service.profile.ResourceQuery)
	 */
	@Override
	public int count(ValueSetDefinitionQuery query) {
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

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		throw new UnsupportedOperationException();
	}

}
