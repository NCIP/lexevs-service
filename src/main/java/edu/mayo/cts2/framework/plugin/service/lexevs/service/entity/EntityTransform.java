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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.model.core.DescriptionInCodeSystem;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.entity.Designation;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.NamedEntityDescription;
import edu.mayo.cts2.framework.model.entity.types.DesignationRole;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;

/**
 * CTS2 <-> LexEVS Transform dealing with Entities and EntityDescriptions. 
 */
@Component
public class EntityTransform 
	extends AbstractBaseTransform<EntityDescription, ResolvedConceptReference, EntityDirectoryEntry, ResolvedConceptReference> 
	implements InitializingBean {

	@Resource
	private LexBIGService lexBigService;
	
	private LexBIGServiceConvenienceMethods lbscm;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.lbscm = (LexBIGServiceConvenienceMethods) 
				this.lexBigService.getGenericExtension("LexBIGServiceConvenienceMethods");
	}

	@Override
	public EntityDescription transformDescription(ResolvedConceptReference reference) {
		Assert.isTrue(reference.getEntity() != null, 
				"The Entity is null. Please resolve the CodedNodeSet with Resolve = true");

		Entity entity = reference.getEntity();
		
		NamedEntityDescription namedEntity = new NamedEntityDescription();
		namedEntity.setAbout(this.getUriHandler().getEntityUri(reference));
		
		namedEntity.setEntityID(
				ModelUtils.createScopedEntityName(
						entity.getEntityCode(), 
						entity.getEntityCodeNamespace()));
		
		namedEntity.setDescribingCodeSystemVersion(
			this.getTransformUtils().toCodeSystemVersionReference(
				reference.getCodingSchemeName(), 
				reference.getCodingSchemeVersion()));
		
		namedEntity.setDesignation(this.toDesignation(entity.getPresentation()));
		
		namedEntity.setParent(this.getParents(reference));
		
		EntityDescription ed = new EntityDescription();
		ed.setNamedEntity(namedEntity);
		
		return ed;
	}

	@Override
	public EntityDirectoryEntry transformDirectoryEntry(ResolvedConceptReference reference) {
		EntityDirectoryEntry entry = new EntityDirectoryEntry();
		entry.setAbout(this.getUriHandler().getEntityUri(reference));
		
		DescriptionInCodeSystem description = new DescriptionInCodeSystem();
		description.setDescribingCodeSystemVersion(
			this.getTransformUtils().toCodeSystemVersionReference(
				reference.getCodingSchemeName(), 
				reference.getCodingSchemeVersion()));
		
		if(reference.getEntityDescription() != null){
			description.setDesignation(reference.getEntityDescription().getContent());
		}
		
		entry.addKnownEntityDescription(description);
		
		return entry;
	}
	
	protected List<Designation> toDesignation(Presentation... presentations){
		List<Designation> returnList = new ArrayList<Designation>();
		
		for(Presentation presentation : presentations){
			Designation designation = new Designation();
			
			DesignationRole role;
			if(presentation.isIsPreferred()){
				role = DesignationRole.PREFERRED;
			} else {
				role = DesignationRole.ALTERNATIVE;
			}
			
			designation.setValue(
				ModelUtils.toTsAnyType(presentation.getValue().getContent()));
			
			designation.setDesignationRole(role);
		}
		
		return returnList;
	}
	
	protected List<URIAndEntityName> getParents(ResolvedConceptReference ref){
		List<URIAndEntityName> returnList = new ArrayList<URIAndEntityName>();
		
		AssociationList assocs;
		try {
			assocs = this.lbscm.getHierarchyLevelPrev(
					ref.getCodingSchemeURI(), 
					Constructors.createCodingSchemeVersionOrTagFromVersion(
							ref.getCodingSchemeVersion()), 
					null, 
					ref.getCode(), 
					false, 
					null);
		} catch (LBException e) {
			return null;
		}
		
		for(Association association : assocs.getAssociation()){
			for(ResolvedConceptReference parent : 
				association.getAssociatedConcepts().getAssociatedConcept()){
				
				URIAndEntityName parentName = new URIAndEntityName();
				parentName.setName(parent.getCode());
				parentName.setNamespace(parent.getCodeNamespace());
				parentName.setUri(this.getUriHandler().getEntityUri(parent));
				
				returnList.add(parentName);
			}
		}
		
		return returnList;
	}

}
