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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingPropertyReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.EntityUriResolver;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 * A Basic EntityDirectoryBuilder.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class BasicEntityDirectoryBuilder<T> extends AbstractStateBuildingDirectoryBuilder<String,T> {

	private EntityUriResolver entityUriResolver;
	
	/**
	 * Instantiates a new basic entity directory builder.
	 *
	 * @param initialState the initial state
	 * @param callback the callback
	 * @param matchAlgorithmReferences the match algorithm references
	 * @param stateAdjustingPropertyReferences the state adjusting property references
	 */
	public BasicEntityDirectoryBuilder(
			EntityUriResolver entityUriResolver,
			AbstractStateBuildingDirectoryBuilder.Callback<String, T> callback,
			Set<MatchAlgorithmReference> matchAlgorithmReferences,
			Set<StateAdjustingPropertyReference<String>> stateAdjustingPropertyReferences) {
		super("", callback, matchAlgorithmReferences,
				stateAdjustingPropertyReferences);
		
		this.entityUriResolver = entityUriResolver;
	}

	public BasicEntityDirectoryBuilder<T> restrict(
			EntityDescriptionQuery query) {
		if(query != null){
			this.restrict(query.getFilterComponent());
			
			if(query.getRestrictions() != null &&
					CollectionUtils.isNotEmpty(query.getRestrictions().getEntities())){
				
				Set<EntityNameOrURI> entities = query.getRestrictions().getEntities();
				
				StringBuilder entityQueryString = new StringBuilder("(");
				
				Set<String> entitySearchStrings = new HashSet<String>();
				
				for(EntityNameOrURI nameOrUri : entities){
					ScopedEntityName name;
					if(nameOrUri.getEntityName() != null){
						name = nameOrUri.getEntityName();
					} else {
						name = this.entityUriResolver.resolveUri(nameOrUri.getUri());
					}
					
					entitySearchStrings.add(String.format("(code:%s AND namespace:%s)", name.getName(), name.getNamespace()));
				}
				entityQueryString.append(StringUtils.join(entitySearchStrings, " OR "));
				
				entityQueryString.append(")");
				
				this.updateState(this.getState() + entityQueryString.toString());
			}
		}
		
		return this;
	}

}
