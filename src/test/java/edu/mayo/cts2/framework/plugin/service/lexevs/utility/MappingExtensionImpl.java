/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;

public class MappingExtensionImpl<DescriptionTemplate, EntryTemplate, QueryTemplate extends ResourceQuery, Service extends QueryService<?,?,?>> implements MappingExtension{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6171317228717285533L;
	String name;
	String description;
	String provider;
	String version;
	
	FakeLexEvsSystem<DescriptionTemplate, EntryTemplate, ResourceQuery, QueryService<?,?,?>> fakeLexEvsSystem;

	
	public MappingExtensionImpl(){
		super();
	}
	
	public MappingExtensionImpl(FakeLexEvsSystem<DescriptionTemplate, EntryTemplate, ResourceQuery, QueryService<?,?,?>> fakeLexEvsSystem){
		this.fakeLexEvsSystem = fakeLexEvsSystem;
	}
	
	public MappingExtensionImpl(String name, String description, String provider, String version){
		super();
		this.name = name;
		this.description = description;
		this.provider = provider;
		this.version = version;
	}
	
	public void setFakeSystem(FakeLexEvsSystem<DescriptionTemplate, EntryTemplate, ResourceQuery, QueryService<?,?,?>> fakeLexEvsSystem){
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

		List<FakeCodedNode> list = new ArrayList<FakeCodedNode>();
		
		if(fakeLexEvsSystem.isMappingCodingScheme(codingScheme, codingSchemeVersionOrTag)){
			String version = (codingSchemeVersionOrTag == null) ? null : codingSchemeVersionOrTag.getVersion();
			FakeCodedNode node = new FakeCodedNode(codingScheme, version);
			
			list.add(node);	
		}
		ResolvedConceptReferencesIterator iterator = new FakeResolvedConceptReferencesIteratorImpl(list);
		return iterator;
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
