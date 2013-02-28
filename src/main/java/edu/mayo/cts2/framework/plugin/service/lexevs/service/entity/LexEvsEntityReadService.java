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

import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Entity;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.EntityReference;
import edu.mayo.cts2.framework.model.core.OpaqueData;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.SourceReference;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityList;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionReadService;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

/**
 * LexEVS CodedNodeSet implementation of EntityDescriptionReadService.
 */
public class LexEvsEntityReadService extends AbstractLexEvsService 
	implements EntityDescriptionReadService {

	private EntityTransform entityTransform = new EntityTransform();
	
	@Resource
	private CodeSystemVersionNameConverter codeSystemVersionNameConverter;

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#read(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public EntityDescription read(
			EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {
		String cts2CodeSystemVersionName = identifier.getCodeSystemVersion().getName();
		
		NameVersionPair codingSchemeName =
			this.codeSystemVersionNameConverter.fromCts2CodeSystemVersionName(cts2CodeSystemVersionName);
		
		ScopedEntityName entityName = identifier.getEntityName();
		
		try {
			CodedNodeSet cns = this.getLexBigService().
				getNodeSet(
					codingSchemeName.getName(), 
					Constructors.createCodingSchemeVersionOrTagFromVersion(codingSchemeName.getVersion()), 
					null);
			
			cns = cns.restrictToCodes(
				Constructors.createConceptReferenceList(
						entityName.getName(), 
						entityName.getNamespace(),
						codingSchemeName.getName()));
			
			ResolvedConceptReferencesIterator iterator = 
				cns.resolve(null, null, null);
			
			if(! iterator.hasNext()){
				return null;
			} else {
				Entity entity = iterator.next().getEntity();
				
				return this.entityTransform.entityToEntityDescription(entity);
			}
		} catch (LBException e) {
			throw new RuntimeException();
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.ReadService#exists(java.lang.Object, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public boolean exists(EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OpaqueData getServiceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SourceReference getServiceProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionReadService#readEntityDescriptions(edu.mayo.cts2.framework.model.service.core.EntityNameOrURI, edu.mayo.cts2.framework.model.core.SortCriteria, edu.mayo.cts2.framework.model.command.ResolvedReadContext, edu.mayo.cts2.framework.model.command.Page)
	 */
	@Override
	public DirectoryResult<EntityListEntry> readEntityDescriptions(
			EntityNameOrURI entityId, SortCriteria sortCriteria,
			ResolvedReadContext readContext, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionReadService#availableDescriptions(edu.mayo.cts2.framework.model.service.core.EntityNameOrURI, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public EntityReference availableDescriptions(EntityNameOrURI entityId,
			ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionReadService#readEntityDescriptions(edu.mayo.cts2.framework.model.service.core.EntityNameOrURI, edu.mayo.cts2.framework.model.command.ResolvedReadContext)
	 */
	@Override
	public EntityList readEntityDescriptions(EntityNameOrURI entityId,
			ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodeSystemReference> getKnownCodeSystems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodeSystemVersionReference> getKnownCodeSystemVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VersionTagReference> getSupportedVersionTags() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CodeSystemVersionNameConverter getCodeSystemVersionNameConverter() {
		return codeSystemVersionNameConverter;
	}

	public void setCodeSystemVersionNameConverter(
			CodeSystemVersionNameConverter codeSystemVersionNameConverter) {
		this.codeSystemVersionNameConverter = codeSystemVersionNameConverter;
	}

	public EntityTransform getEntityTransform() {
		return entityTransform;
	}

	public void setEntityTransform(EntityTransform entityTransform) {
		this.entityTransform = entityTransform;
	}

}
