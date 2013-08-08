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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.core.constants.URIHelperInterface;
import edu.mayo.cts2.framework.core.util.EncodingUtils;
import edu.mayo.cts2.framework.model.core.Comment;
import edu.mayo.cts2.framework.model.core.Definition;
import edu.mayo.cts2.framework.model.core.DescriptionInCodeSystem;
import edu.mayo.cts2.framework.model.core.EntityReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.Property;
import edu.mayo.cts2.framework.model.core.StatementTarget;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.entity.Designation;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.entity.NamedEntityDescription;
import edu.mayo.cts2.framework.model.entity.types.DesignationRole;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils.UriVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.XmlUtils;

/**
 * CTS2 <-> LexEVS Transform dealing with Entities and EntityDescriptions. 
 */
@Component
public class EntityTransform 
	extends AbstractBaseTransform<EntityListEntry, ResolvedConceptReference, EntityDirectoryEntry, ResolvedConceptReference> 
	implements InitializingBean {
	
	@Resource
	private UriResolver uriResolver;

	@Resource
	private LexBIGService lexBigService;
	
	private LexBIGServiceConvenienceMethods lbscm;
	
	@Resource
	private CommonResolvedValueSetUtils commonResolvedValueSetUtils;
	
	private static Set<String> ROOT_NODES = new HashSet<String>(Arrays.asList("@", "@@"));
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.lbscm = (LexBIGServiceConvenienceMethods) 
				this.lexBigService.getGenericExtension("LexBIGServiceConvenienceMethods");
	}

	@Override
	public EntityListEntry transformFullDescription(ResolvedConceptReference reference) {
		Assert.isTrue(reference.getEntity() != null, 
				"The Entity is null. Please resolve the CodedNodeSet with Resolve = true");

		Entity entity = reference.getEntity();
		
		NamedEntityDescription namedEntity = new NamedEntityDescription();
		namedEntity.setAbout(this.getUriHandler().getEntityUri(reference));
		
		namedEntity.setEntityID(
				ModelUtils.createScopedEntityName(
						entity.getEntityCode(), 
						this.sanitizeNamespace(entity.getEntityCodeNamespace())));
		
		String codingSchemeName = 
			this.getCodingSchemeNameTranslator().translateFromLexGrid(reference.getCodingSchemeName());
		
		namedEntity.setDescribingCodeSystemVersion(
			this.getTransformUtils().toCodeSystemVersionReference(
				codingSchemeName, 
				reference.getCodingSchemeVersion(),
				reference.getCodingSchemeURI()));
		
		namedEntity.setDesignation(this.toDesignation(entity.getPresentation()));
		namedEntity.setProperty(this.toProperty(
				codingSchemeName,
				reference.getCodingSchemeURI(),
				reference.getCodingSchemeVersion(),
				entity.getProperty()));
		namedEntity.setDefinition(this.toDefinition(entity.getDefinition()));
		namedEntity.setNote(this.toNote(entity.getComment()));
		
		namedEntity.setChildren(
			this.getUrlConstructor().createChildrenUrl(
					codingSchemeName, 
					reference.getCodingSchemeVersion(), 
					entity.getEntityCode()));
		
		try {
			namedEntity.setParent(this.getParents(reference));
		} catch (Exception e) {
			log.warn("Error resolving Parents for: " + reference.getCode());
			if(log.isDebugEnabled()){
				log.debug(e);
			}
		}
		
		URIAndEntityName entityType = new URIAndEntityName();
	    entityType.setName("Class");
	    entityType.setNamespace("owl");
	    entityType.setUri("http://www.w3.org/2002/07/owl#Class");

	    namedEntity.addEntityType(entityType);
	    
	    String entityHref = this.getTransformUtils().createEntityHref(reference);
	    
	    //NOTE: We probably want to check this with the CodedNodeGraph to
	    //see if there actually are associations to link to. That's added
	    //expense that I'm not sure if we want.
	    namedEntity.setSubjectOf(
	    	entityHref + "/" + URIHelperInterface.SUBJECTOF);

	 	namedEntity.setTargetOf(
			entityHref + "/" + URIHelperInterface.TARGETOF);
		
		EntityDescription ed = new EntityDescription();
		ed.setNamedEntity(namedEntity);
		
		EntityListEntry listEntry = new EntityListEntry();
		listEntry.setEntry(ed);
		listEntry.setResourceName(
			EncodingUtils.encodeScopedEntityName(
				ModelUtils.createScopedEntityName(
					reference.getCode(), 
					this.sanitizeNamespace(reference.getCodeNamespace()))));
		
		listEntry.setHref(this.getTransformUtils().createEntityHref(reference));
		
		return listEntry;
	}
	
	private List<Comment> toNote(org.LexGrid.concepts.Comment... comments) {
		List<Comment> returnList = new ArrayList<Comment>();
		
		for(org.LexGrid.concepts.Comment comment : comments){
			Comment cts2Comment = new Comment();
			cts2Comment.setValue(
				ModelUtils.toTsAnyType(comment.getValue().getContent()));
			
			returnList.add(cts2Comment);
		}
		
		return returnList;
	}
	
	private List<Definition> toDefinition(org.LexGrid.concepts.Definition... definitions) {
		List<Definition> returnList = new ArrayList<Definition>();
		
		for(org.LexGrid.concepts.Definition definition : definitions){
			Definition cts2Definition = new Definition();
			cts2Definition.setValue(
				ModelUtils.toTsAnyType(definition.getValue().getContent()));
			
			returnList.add(cts2Definition);
		}
		
		return returnList;
	}

	private List<Property> toProperty(
			String codingSchemeName,
			String codingSchemeUri,
			String codingSchemeVersion,
			org.LexGrid.commonTypes.Property... properties) {
		List<Property> returnList = new ArrayList<Property>();
		
		for(org.LexGrid.commonTypes.Property property : properties){
			Property cts2Prop = new Property();
			
			String propertyName = property.getPropertyName();
			
			String predicateUri = this.getUriHandler().getPredicateUri(
				codingSchemeUri, codingSchemeVersion, propertyName);

			PredicateReference ref = new PredicateReference();
			ref.setName(property.getPropertyName());
			ref.setNamespace(this.sanitizeNamespace(codingSchemeName));
			ref.setUri(predicateUri);

			cts2Prop.setPredicate(ref);
			
			StatementTarget target = new StatementTarget();
			target.setLiteral(
				ModelUtils.createOpaqueData(property.getValue().getContent()));
			
			cts2Prop.addValue(target);
			
			returnList.add(cts2Prop);
		}
		
		return returnList;
	}

	@Override
	public EntityDirectoryEntry transformSummaryDescription(ResolvedConceptReference reference) {
		EntityDirectoryEntry entry = new EntityDirectoryEntry();
		entry.setAbout(this.getUriHandler().getEntityUri(reference));
		
		entry.setName(
			ModelUtils.createScopedEntityName(
				reference.getCode(), 
				this.sanitizeNamespace(reference.getCodeNamespace())));

		DescriptionInCodeSystem description = new DescriptionInCodeSystem();
		description.setDescribingCodeSystemVersion(
			this.getTransformUtils().toCodeSystemVersionReference(
				reference.getCodingSchemeName(), 
				reference.getCodingSchemeVersion(),
				reference.getCodingSchemeURI()));
		
		description.setHref(this.getTransformUtils().createEntityHref(reference));
		
		if(reference.getEntityDescription() != null){
			description.setDesignation(reference.getEntityDescription().getContent());
		}
		
		entry.addKnownEntityDescription(description);
	
		return entry;
	}
	
	public EntityReference transformEntityReference(ResolvedConceptReferencesIterator itr) {
		
		try {
			if(! itr.hasNext()){
				return null;
			} else {
				
				EntityReference reference = new EntityReference();
				
				while(itr.hasNext()){
					ResolvedConceptReference ref = itr.next();
					
					//skip ResolvedValueSets
					if(this.commonResolvedValueSetUtils.isResolvedValueSet(
						new UriVersionPair(ref.getCodingSchemeURI(), ref.getCodingSchemeVersion()))){
						continue;
					}
					
					if(reference.getAbout() == null){
						reference.setAbout(this.getUriHandler().getEntityUri(ref));
					}
					if(reference.getName() == null){
						reference.setName(
								ModelUtils.createScopedEntityName(
									ref.getCode(), 
									this.sanitizeNamespace(ref.getCodeNamespace())));
					}
			
					DescriptionInCodeSystem description = new DescriptionInCodeSystem();
					description.setDescribingCodeSystemVersion(
						this.getTransformUtils().toCodeSystemVersionReference(
							ref.getCodingSchemeName(), 
							ref.getCodingSchemeVersion(),
							ref.getCodingSchemeURI()));
					
					description.setHref(this.getTransformUtils().createEntityHref(ref));
					
					if(ref.getEntityDescription() != null){
						description.setDesignation(ref.getEntityDescription().getContent());
					}
					
					reference.addKnownEntityDescription(description);
				}

				return reference;
			}
		} catch (LBException e) {
			//LexEVS did not find it or it is invalid... return null.
			return null;
		}
	}
	
	protected List<Designation> toDesignation(Presentation... presentations){
		List<Designation> returnList = new ArrayList<Designation>();
		
		for(Presentation presentation : presentations){
			Designation designation = new Designation();
			
			DesignationRole role;
			if(BooleanUtils.toBoolean(presentation.isIsPreferred())){
				role = DesignationRole.PREFERRED;
			} else {
				role = DesignationRole.ALTERNATIVE;
			}
			
			designation.setValue(
				ModelUtils.toTsAnyType(presentation.getValue().getContent()));
			
			designation.setDesignationRole(role);
			
			returnList.add(designation);
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
				
				if(! ROOT_NODES.contains(parent.getCode())){
					URIAndEntityName parentName = new URIAndEntityName();
					parentName.setName(parent.getCode());
					parentName.setNamespace(
						this.sanitizeNamespace(parent.getCodeNamespace()));
					parentName.setUri(this.getUriHandler().getEntityUri(parent));
					parentName.setHref(this.getTransformUtils().createEntityHref(parent));
					
					returnList.add(parentName);
				}
			}
		}
		
		return returnList;
	}
	
	protected String sanitizeNamespace(String namespace){
		namespace = this.getCodingSchemeNameTranslator().translateFromLexGrid(namespace);

		boolean isNamespaceValidNCName = XmlUtils.isNCName(namespace);
		if(isNamespaceValidNCName){
			return namespace;
		} else {
			//Last ditch effort... generate a random namespace.
			//If it gets here, it probably needs to be added
			//to the UriResolver.
			return "ns" + Integer.toString(namespace.hashCode());
		}
	}

}
