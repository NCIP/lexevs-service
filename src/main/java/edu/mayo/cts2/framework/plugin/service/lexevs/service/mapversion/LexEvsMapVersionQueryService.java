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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapRole;
import edu.mayo.cts2.framework.model.service.mapversion.types.MapStatus;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceSummaryUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQueryService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapVersionQueryService extends AbstractLexEvsService
		implements MapVersionQueryService, InitializingBean {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeToMapVersionTransform codingSchemeToMapVersionTransform;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";	
	
	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(VersionNameConverter converter){
		this.nameConverter = converter;
	}
	
	public void setCodingSchemeToMapVersionTransform(CodingSchemeToMapVersionTransform transformer){
			this.codingSchemeToMapVersionTransform = transformer;
	}
	
	public void setMappingExtension(MappingExtension extension){
		this.mappingExtension = extension;
	}

	// -------- Implemented methods ----------------
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}
		
	@Override
	public int count(MapVersionQuery query) {
		LexBIGService lexBigService = this.getLexBigService();
		QueryData<MapVersionQuery> queryData = new QueryData<MapVersionQuery>(query);
		
		CodingSchemeRenderingList csrFilteredList;
		csrFilteredList = CommonResourceSummaryUtils.getCodingSchemeRenderingList(lexBigService, nameConverter, mappingExtension, queryData, null);
		return csrFilteredList.getCodingSchemeRendering().length;
	}
	
	@Override
	public DirectoryResult<MapVersionDirectoryEntry> getResourceSummaries(
			MapVersionQuery query, SortCriteria sortCriteria, Page page) {
		LexBIGService lexBigService = this.getLexBigService();
		CodingSchemeRendering[] csRendering = CommonResourceSummaryUtils.getCodingSchemeRendering(lexBigService, nameConverter, query, null, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPageFromArray(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		return CommonResourceSummaryUtils.createDirectoryResultWithEntrySummaryData(lexBigService, this.codingSchemeToMapVersionTransform, csRenderingPage, atEnd);
	}

	@Override
	public DirectoryResult<MapVersion> getResourceList(MapVersionQuery query,
			SortCriteria sortCriteria, Page page) {

		LexBIGService lexBigService = this.getLexBigService();
		CodingSchemeRendering[] csRendering = CommonResourceSummaryUtils.getCodingSchemeRendering(lexBigService, nameConverter, query, null, sortCriteria);
		CodingSchemeRendering[] csRenderingPage = (CodingSchemeRendering[]) CommonPageUtils.getPageFromArray(csRendering, page);
		boolean atEnd = (page.getEnd() >= csRendering.length) ? true : false;
		return CommonResourceSummaryUtils.createDirectoryResultWithRenderedEntryData(lexBigService, this.codingSchemeToMapVersionTransform, csRenderingPage, atEnd);
	}

	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {

		MatchAlgorithmReference exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		MatchAlgorithmReference contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		MatchAlgorithmReference startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();

		return new HashSet<MatchAlgorithmReference>(Arrays.asList(exactMatch,contains,startsWith));
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSearchReferences() {
		
		PropertyReference name = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();		
		PropertyReference about = StandardModelAttributeReference.ABOUT.getPropertyReference();	
		PropertyReference description = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		
		return new HashSet<PropertyReference>(Arrays.asList(name,about,description));
	}

	@Override
	public Set<? extends PropertyReference> getSupportedSortReferences() {
		return new HashSet<PropertyReference>();
	}

	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	@Override
	public DirectoryResult<EntityDirectoryEntry> mapVersionEntities(
			NameOrURI mapVersion, MapRole mapRole, MapStatus mapStatus,
			EntityDescriptionQuery query, SortCriteria sort, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectoryResult<EntityDescription> mapVersionEntityList(
			NameOrURI mapVersion, MapRole mapRole, MapStatus mapStatus,
			EntityDescriptionQuery query, SortCriteria sort, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityReferenceList mapVersionEntityReferences(NameOrURI mapVersion,
			MapRole mapRole, MapStatus mapStatus, EntityDescriptionQuery query,
			SortCriteria sort, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

}
