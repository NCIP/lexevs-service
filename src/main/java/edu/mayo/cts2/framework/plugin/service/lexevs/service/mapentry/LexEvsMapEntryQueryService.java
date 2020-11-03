/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.mapversion.MapEntryDirectoryEntry;
import edu.mayo.cts2.framework.model.mapversion.MapEntryListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResourceUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.MapResolvedConceptReferenceResults;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.ResolvedConceptReferenceResults;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryQueryService;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapEntryQueryService extends AbstractLexEvsService implements
		MapEntryQueryService {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private MappingToMapEntryTransform transformer;
		
	@Resource
	private MappingExtension lexMappingExtension;

	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(
			VersionNameConverter versionNameConverter) {
		this.nameConverter = versionNameConverter;
	}
	
	public void setMapEntryTransformer(
			MappingToMapEntryTransform mapTransformer) {
		this.transformer = mapTransformer;
	}
	
	public void setMappingExtension(MappingExtension extension){
		this.lexMappingExtension = extension;
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceSummaries(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<MapEntryDirectoryEntry> getResourceSummaries(
			MapEntryQuery query, SortCriteria sortCriteria, Page page) {
		
		return getResourceSummaries(query, sortCriteria, page, null);
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceSummaries(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page, String)
	 */
	@Override
	public DirectoryResult<MapEntryDirectoryEntry> getResourceSummaries(
			MapEntryQuery query, SortCriteria sortCriteria, Page page, String uri) {

		DirectoryResult<MapEntryDirectoryEntry> cts2DirectoryResult;
		if(this.validateMapEntryQuery(query)){
			MapResolvedConceptReferenceResults lexReferenceResults = CommonResourceUtils.getLexMapReferenceResults(query, sortCriteria, page, this.nameConverter, this.lexMappingExtension);
			
			cts2DirectoryResult = 
				CommonResourceUtils.createDirectoryResults(this.transformer, lexReferenceResults, Constants.SUMMARY_DESCRIPTION);
		} else {
			cts2DirectoryResult = 
				new DirectoryResult<MapEntryDirectoryEntry>(new ArrayList<MapEntryDirectoryEntry>(),true);
		}
		
		return cts2DirectoryResult;
	}
	
	protected boolean validateMapEntryQuery(MapEntryQuery query){
		if(query == null || 
				query.getRestrictions() == null|| 
				query.getRestrictions().getMapVersion() == null){
			throw new UnsupportedOperationException("MapEntry Queries without a MapVersion restriction are not supported.");
		}
		return this.nameConverter.isValidVersionName(query.getRestrictions().getMapVersion().getName());
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceList(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<MapEntryListEntry> getResourceList(MapEntryQuery query,
			SortCriteria sortCriteria, Page page) {
		DirectoryResult<MapEntryListEntry> cts2DirectoryResult;
		if (this.validateMapEntryQuery(query)) {
			MapResolvedConceptReferenceResults lexReferenceResults = CommonResourceUtils
					.getLexMapReferenceResults(query, sortCriteria, page,
							this.nameConverter, this.lexMappingExtension);
			cts2DirectoryResult = CommonResourceUtils.createDirectoryResults(
					this.transformer, lexReferenceResults,
					Constants.FULL_DESCRIPTION);
		} else {
			cts2DirectoryResult = new DirectoryResult<MapEntryListEntry>(
					new ArrayList<MapEntryListEntry>(), true);
		}

		return cts2DirectoryResult;

	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#count(edu.mayo.cts2.framework.service.profile.ResourceQuery)
	 */
	@Override
	public int count(MapEntryQuery query) {		
		ResolvedConceptReferenceResults lexReferenceResults = CommonResourceUtils.getLexMapReferenceResults(query, null, null, this.nameConverter, this.lexMappingExtension);
		return lexReferenceResults.getLexResolvedConceptReference().length;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedMatchAlgorithms()
	 */
	@Override
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedSearchReferences()
	 */
	@Override
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.getLexSupportedSearchReferences();
	}

	
	
	// Methods returning empty lists or sets
	// -------------------------------------
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getSupportedSortReferences()
	 */
	@Override
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return new HashSet<ComponentReference>();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseQueryService#getKnownProperties()
	 */
	@Override
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.BaseService#getKnownNamespaceList()
	 */
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
