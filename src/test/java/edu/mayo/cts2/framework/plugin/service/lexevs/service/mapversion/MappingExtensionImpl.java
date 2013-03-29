package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

public class MappingExtensionImpl implements MappingExtension{
	String name;
	String description;
	String provider;
	String version;
	
	public MappingExtensionImpl(){
		super();
	}
	
	public MappingExtensionImpl(String name, String description, String provider, String version){
		super();
		this.name = name;
		this.description = description;
		this.provider = provider;
		this.version = version;
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
	public boolean isMappingCodingScheme(String codingScheme,
			CodingSchemeVersionOrTag codingSchemeVersionOrTag)
			throws LBParameterException {
		// TODO Auto-generated method stub
		return false;
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
