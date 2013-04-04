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
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.FakeLexEvsSystem;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionQuery;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class MappingExtensionImpl implements MappingExtension{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6171317228717285533L;
	String name;
	String description;
	String provider;
	String version;
	
	FakeLexEvsSystem<MapVersion, MapVersionDirectoryEntry, MapVersionQuery, LexEvsMapVersionQueryService> fakeLexEvsSystem;
	
	
	public MappingExtensionImpl(){
		super();
	}
	
	public MappingExtensionImpl(FakeLexEvsSystem<MapVersion, MapVersionDirectoryEntry, MapVersionQuery, LexEvsMapVersionQueryService> fakeLexEvsSystem){
		this.fakeLexEvsSystem = fakeLexEvsSystem;
	}
	
	
	public MappingExtensionImpl(String name, String description, String provider, String version){
		super();
		this.name = name;
		this.description = description;
		this.provider = provider;
		this.version = version;
	}
	
	public void setFakeSystem(FakeLexEvsSystem<MapVersion, MapVersionDirectoryEntry, MapVersionQuery, LexEvsMapVersionQueryService> fakeLexEvsSystem){
		this.fakeLexEvsSystem = fakeLexEvsSystem;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setProvider(String provider){
		this.provider = provider;
	}
	
	public void setVersion(String version){
		this.version = version;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getProvider() {
		return provider;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public boolean isMappingCodingScheme(String codingSchemeName,
			CodingSchemeVersionOrTag codingSchemeVersionOrTag)
			throws LBParameterException {
		boolean answer = false;
		
		answer = fakeLexEvsSystem.isMappingCodingScheme(codingSchemeName, codingSchemeVersionOrTag);
		return answer;
	}

	@Override
	public ResolvedConceptReferencesIterator resolveMapping(
			String codingScheme,
			CodingSchemeVersionOrTag codingSchemeVersionOrTag,
			String relationsContainerName,
			List<MappingSortOption> sortOptionList) throws LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsoluteCodingSchemeVersionReferenceList getMappingCodingSchemesEntityParticipatesIn(
			String entityCode, String entityCodeNamespace)
			throws LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getMapping(String codingScheme,
			CodingSchemeVersionOrTag codingSchemeVersionOrTag,
			String relationsContainerName) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

}
