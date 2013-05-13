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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder.Callback;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionQuery;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionQueryService;

/**
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsValueSetDefinitionQueryService extends AbstractLexEvsService
		implements ValueSetDefinitionQueryService {

	@Resource
	private LexEVSValueSetDefinitionServices definitionServices;
	
	@Resource
	private LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform transformer;
	
	private interface TransformClosure<T>{
		T transform(org.LexGrid.valueSets.ValueSetDefinition item);
	}
	
	private final Callback<List<String>, edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition> 
		listCallack = 
			new DefinitionCallback<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition>(
				new TransformClosure<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition>() {

					@Override
					public edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition transform(
							ValueSetDefinition item) {
						return transformer.transformFullDescription(item);
					}

				});
	
	private final Callback<List<String>, ValueSetDefinitionDirectoryEntry> summariesCallack = 
		new DefinitionCallback<ValueSetDefinitionDirectoryEntry>(
			new TransformClosure<ValueSetDefinitionDirectoryEntry>() {

				@Override
				public ValueSetDefinitionDirectoryEntry transform(
						ValueSetDefinition item) {
					return transformer.transformSummaryDescription(item);
				}

			});
	
	private class DefinitionCallback<T> implements Callback<List<String>, T> {
		
		private TransformClosure<T> transformClosure;
		
		private DefinitionCallback(TransformClosure<T> transformClosure){
			super();
			this.transformClosure = transformClosure;
		}

		@Override
		public DirectoryResult<T> execute(List<String> state, int start,
				int maxResults) {
			List<T> returnList = new ArrayList<T>();

			for(String uri : state){
				org.LexGrid.valueSets.ValueSetDefinition definition;
				try {
					definition = definitionServices.getValueSetDefinition(new URI(uri), null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				returnList.add(this.transformClosure.transform(definition));
			}
			
			return 
				new DirectoryResult<T>(
						returnList, 
						start + maxResults > state.size());
		}

		@Override
		public int executeCount(List<String> state) {
			return state.size();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceSummaries(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<ValueSetDefinitionDirectoryEntry> getResourceSummaries(
			ValueSetDefinitionQuery query, 
			SortCriteria sortCriteria, Page page) {
		List<String> uris = this.definitionServices.listValueSetDefinitionURIs();
		
		ValueSetDefinitionDirectoryBuilder<ValueSetDefinitionDirectoryEntry> builder = 
			new ValueSetDefinitionDirectoryBuilder<ValueSetDefinitionDirectoryEntry>(
					uris, 
					this.summariesCallack, 
					null, 
					null);
		
		return builder.
				addMaxToReturn(page.getMaxToReturn()).
				addStart(page.getStart()).
				resolve();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#getResourceList(edu.mayo.cts2.framework.service.profile.ResourceQuery, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition> getResourceList(
			ValueSetDefinitionQuery query, SortCriteria sortCriteria, Page page) {
		List<String> uris = this.definitionServices.listValueSetDefinitionURIs();
		
		ValueSetDefinitionDirectoryBuilder<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition> builder = 
			new ValueSetDefinitionDirectoryBuilder<edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition>(
					uris, 
					this.listCallack, 
					null, 
					null);
		
		return builder.
				addMaxToReturn(page.getMaxToReturn()).
				addStart(page.getStart()).
				resolve();
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.QueryService#count(edu.mayo.cts2.framework.service.profile.ResourceQuery)
	 */
	@Override
	public int count(ValueSetDefinitionQuery query) {
		List<String> uris = this.definitionServices.listValueSetDefinitionURIs();
		
		ValueSetDefinitionDirectoryBuilder<ValueSetDefinitionDirectoryEntry> builder = 
			new ValueSetDefinitionDirectoryBuilder<ValueSetDefinitionDirectoryEntry>(
					uris, 
					this.summariesCallack, 
					null, 
					null);
		
		return builder.count();
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
