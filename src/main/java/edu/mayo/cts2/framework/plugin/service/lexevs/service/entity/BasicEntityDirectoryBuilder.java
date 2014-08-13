/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.types.ActiveOrAll;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 * A Basic EntityDirectoryBuilder.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class BasicEntityDirectoryBuilder<T> extends AbstractStateBuildingDirectoryBuilder<String,T> {

	private EntityUriResolver entityUriResolver;
	
	private EntityNameQueryBuilder entityNameQueryBuilder;
	
	/**
	 * Instantiates a new basic entity directory builder.
	 *
	 * @param initialState the initial state
	 * @param callback the callback
	 * @param matchAlgorithmReferences the match algorithm references
	 * @param stateAdjustingPropertyReferences the state adjusting property references
	 */
	public BasicEntityDirectoryBuilder(
			EntityNameQueryBuilder entityNameQueryBuilder,
			EntityUriResolver entityUriResolver,
			AbstractStateBuildingDirectoryBuilder.Callback<String, T> callback,
			Set<MatchAlgorithmReference> matchAlgorithmReferences,
			Set<StateAdjustingComponentReference<String>> stateAdjustingPropertyReferences) {
		super("", callback, matchAlgorithmReferences,
				stateAdjustingPropertyReferences);
		
		this.entityUriResolver = entityUriResolver;
		this.entityNameQueryBuilder = entityNameQueryBuilder;
	}

	public BasicEntityDirectoryBuilder<T> restrict(
			EntityDescriptionQuery query) {
		if(query != null){
			this.restrict(query.getFilterComponent());
			StringBuilder entityQueryString = new StringBuilder("(");
			ActiveOrAll active= ActiveOrAll.ACTIVE_ONLY;
			
			if (query.getReadContext() != null) {
				active= query.getReadContext().getActive();
			}
			if (active.equals(ActiveOrAll.ACTIVE_ONLY)) {
				entityQueryString.append("*:* AND NOT active:false");
			} else {
				entityQueryString.append("*:*");
			}
			
			if(query.getRestrictions() != null &&
					CollectionUtils.isNotEmpty(query.getRestrictions().getEntities())){
				boolean addEndBracket = false;
				if (entityQueryString.length() >0) {
					addEndBracket = true;
					entityQueryString.append(" (");
				}
				
				Set<EntityNameOrURI> entities = query.getRestrictions().getEntities();
				
				Set<String> entitySearchStrings = new HashSet<String>();
				
				for(EntityNameOrURI nameOrUri : entities){
					ScopedEntityName name;
					if(nameOrUri.getEntityName() != null){
						name = nameOrUri.getEntityName();
					} else {
						name = this.entityUriResolver.resolveUri(nameOrUri.getUri());
					}
					
					entitySearchStrings.add(this.entityNameQueryBuilder.buildQuery(name));
				}
				
				entityQueryString.append(StringUtils.join(entitySearchStrings, " OR "));
				
				if (addEndBracket) {
					entityQueryString.append(")");
				}
				
			}
			entityQueryString.append(")");
			this.updateState(this.getState() + entityQueryString.toString());
		}
		
		return this;
	}

}
