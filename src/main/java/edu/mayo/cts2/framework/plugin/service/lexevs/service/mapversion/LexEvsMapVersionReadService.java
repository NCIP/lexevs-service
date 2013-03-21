package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionReadService;

@Component
public class LexEvsMapVersionReadService
	extends AbstractLexEvsCodeSystemService<MapVersion>
	implements MapVersionReadService, InitializingBean {

	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeToMapVersionTransform codingSchemeToMapVersionTransform;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";
	

	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}

	/*
	 * As part of the transfrom service, validate if it is in fact a valid
	 * LexEVS Mapping CodingScheme. If not, return null.
	 * 
	 * (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService#transform(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	protected MapVersion transform(CodingScheme codingScheme) {
		if(! this.validateMappingCodingScheme(
				codingScheme.getCodingSchemeURI(), 
				codingScheme.getRepresentsVersion())){
			return null;
		} else {
			return this.codingSchemeToMapVersionTransform.transform(codingScheme);
		}
	}
	
	protected boolean validateMappingCodingScheme(String uri, String version){
		try {
			return this.mappingExtension.
				isMappingCodingScheme(
						uri, 
						Constructors.createCodingSchemeVersionOrTagFromVersion(version));
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public MapVersion readByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		
		return this.getByVersionIdOrTag(parentIdentifier, 
				this.convertTag(tag));
	}

	@Override
	public boolean existsByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		return this.readByTag(parentIdentifier, tag, readContext) != null;
	}

	@Override
	public List<VersionTagReference> getSupportedTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	@Override
	public MapVersion read(NameOrURI identifier, ResolvedReadContext readContext) {
		
		String name;
		if(identifier.getName() != null){
			name = identifier.getName();
			if(!this.nameConverter.isValidCodeSystemVersionName(name)){
				return null;
			}
		} else {
			throw new UnsupportedOperationException("Cannot resolve by DocumentURI yet.");
		}
		
		NameVersionPair namePair = this.nameConverter.fromCts2CodeSystemVersionName(name);
		CodingSchemeVersionOrTag version = 
			Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion());
		
		return this.getByVersionIdOrTag
				(ModelUtils.nameOrUriFromName(
						namePair.getName()), 
						version);
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		return this.read(identifier, readContext) != null;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
