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

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.concepts.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.NamedEntityDescription;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

/**
 * 
 * CTS2 <-> LexEVS Transform dealing with Entities and EntityDescriptions. 
 *
 */
@Component
public class EntityTransform {

	@Autowired
	private UriHandler uriHandler;

	public EntityDescription transformToEntity(ResolvedConceptReference reference) {
		Assert.isTrue(reference.getEntity() != null, 
				"The Entity is null. Please resolve the CodedNodeSet with Resolve = true");

		Entity entity = reference.getEntity();
		
		NamedEntityDescription namedEntity = new NamedEntityDescription();
		namedEntity.setAbout(this.uriHandler.getEntityUri(reference));
		
		namedEntity.setEntityID(
				ModelUtils.createScopedEntityName(
						entity.getEntityCode(), 
						entity.getEntityCodeNamespace()));
		
		EntityDescription ed = new EntityDescription();
		ed.setNamedEntity(namedEntity);
		
		return ed;
	}

	public EntityDirectoryEntry transformToEntry(ResolvedConceptReference reference) {
		EntityDirectoryEntry entry = new EntityDirectoryEntry();
		entry.setAbout(this.uriHandler.getEntityUri(reference));
		
		return entry;
	}

}
