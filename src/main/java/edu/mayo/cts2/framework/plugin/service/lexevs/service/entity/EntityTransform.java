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

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.concepts.Entity;

import edu.mayo.cts2.framework.model.core.EntityReference;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.NamedEntityDescription;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.EntityUriHandler;

/**
 * 
 * CTS2 <-> LexEVS Transform dealing with Entities and EntityDescriptions. 
 *
 */
public class EntityTransform {

	@Resource 
	private EntityUriHandler entityUriHandler;
	/**
	 * LexEVS Entity to CTS2 EntityDescription.
	 *
	 * @param entity the entity
	 * @return the entity description
	 */
	public EntityDescription entityToEntityDescription(Entity entity){
		NamedEntityDescription namedEntity = new NamedEntityDescription();
		namedEntity.setAbout(this.entityUriHandler.getUri(entity));
		
		namedEntity.setEntityID(
				ModelUtils.createScopedEntityName(
						entity.getEntityCode(), 
						entity.getEntityCodeNamespace()));
		
		EntityDescription ed = new EntityDescription();
		ed.setNamedEntity(namedEntity);
		
		return ed;
	}

	public EntityDirectoryEntry transform(ResolvedConceptReference reference) {
		EntityDirectoryEntry entry = new EntityDirectoryEntry();
		entry.setAbout(this.entityUriHandler.getUri(reference));
		
		return entry;
	}
	
	
	public EntityDescription transform_EntityDescription(ResolvedConceptReference reference) {
		// TODO need to implement
		throw new UnsupportedOperationException();
	}
	
	public EntityReference transform_EntityReference(ResolvedConceptReference reference) {
		// TODO need to implement
		throw new UnsupportedOperationException();
	}
	
}
