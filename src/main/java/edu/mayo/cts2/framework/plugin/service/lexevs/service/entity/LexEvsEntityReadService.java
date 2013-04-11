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
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.EntityReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDescription;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionReadService;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

/**
 * LexEVS CodedNodeSet implementation of EntityDescriptionReadService.
 * 
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsEntityReadService extends AbstractLexEvsService 
	implements EntityDescriptionReadService {

	@Resource
	private EntityTransform transformer;
	
	@Resource
	private VersionNameConverter nameConverter;

	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(
			VersionNameConverter versionNameConverter) {
		this.nameConverter = versionNameConverter;
	}

	public void setEntityTransform(EntityTransform entityTransform) {
		this.transformer = entityTransform;
	}

	// -------- Implemented methods ----------------
	@Override
	public EntityDescription read(
			EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {
		LexBIGService lexBigService = this.getLexBigService();
		ResolvedConceptReference entity = CommonUtils.getResolvedConceptReference(lexBigService, nameConverter, identifier, readContext);
		
		if(entity == null){
			return null;
		} else {
			return this.transformer.transformFullDescription(entity);
		}
	}

	@Override
	public boolean exists(EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {
		LexBIGService lexBigService = this.getLexBigService();
		
		ResolvedConceptReference entity = CommonUtils.getResolvedConceptReference(lexBigService, nameConverter, identifier,	readContext);
		return (entity == null) ? false : true;
	}
	
	@Override
	public List<VersionTagReference> getSupportedVersionTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}	

	// Not going to implement following methods
	// ----------------------------------------
	@Override
	public DirectoryResult<EntityListEntry> readEntityDescriptions(
			EntityNameOrURI entityId, SortCriteria sortCriteria,
			ResolvedReadContext readContext, Page page) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EntityReference availableDescriptions(EntityNameOrURI entityId,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<EntityListEntry> readEntityDescriptions(EntityNameOrURI entityId,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<CodeSystemVersionReference> getKnownCodeSystemVersions() {
		return new ArrayList<CodeSystemVersionReference>();
	}

	@Override
	public List<CodeSystemReference> getKnownCodeSystems() {
		return new ArrayList<CodeSystemReference>();
	}
	
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}
}
