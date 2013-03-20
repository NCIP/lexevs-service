package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.GenericExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionReadService;

@Component
public class LexEvsMapVersionReadService
	extends AbstractLexEvsCodeSystemService<MapVersion>
	implements MapVersionReadService {

	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";

	@Override
	protected MapVersion transform(CodingScheme codingScheme) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public MapVersion readByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		
		// parentIdentifier has the mapped "codingSchemeName-version" value of the map - i.e. "Mapping Sample-1.0"
		// tag should be defaulted to current tag since that is the only one being supported for now
		// assume readContext is null 
		
		
		
		return null;
	}

	@Override
	public boolean existsByTag(NameOrURI parentIdentifier,
			VersionTagReference tag, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		
		// Will leverage a shared code method with readByTag method
		
		return false;
	}

	@Override
	public List<VersionTagReference> getSupportedTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	@Override
	public MapVersion read(NameOrURI identifier, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		
		// Similiar to readByTag but only passing in "codingSchemeName-version" value for identifier
		
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
		Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion());

		MappingExtension mappingExtension = null;
		try {
			mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
			//mappingExtension.
			// TODO What MappingExtensionImpl method needs to be called and what LexEVS object needs to be returned? Most 
			//   methods need the container name of the Relations (i.e. AutoToGMPMappings) as an input param.  Don't see
			//   how CTS2 is going to supply this value.
			
			// MappingExtensionImpl has isMappingCodingScheme method that returns boolean
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		// TODO Auto-generated method stub
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
