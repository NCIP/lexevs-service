package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.extension.LocalIdValueSetDefinition;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetDefinitionUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNamePair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetReadService;

@Component
public class LexEVSValueSetReadService extends AbstractLexEvsService implements
		ValueSetReadService {

	@Resource
	private LexEVSValueSetDefinitionToValueSetEntryTransform vsTransformer;
	
	@Resource
	private ValueSetTransform transform;
	
	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;
	
	
	@Override
	public ValueSetCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		String uriString = null;
		
		String localName = identifier.getName();
		
		if(localName != null){
			uriString = 
				this.valueSetNameTranslator.getRegularValueSetCurrentDefinition(localName);
		}
		else
		{
			uriString = identifier.getUri();
		}
	
		URI valueSetDefinitionURI;
		try {
			valueSetDefinitionURI = new URI(uriString);
		} catch (URISyntaxException uriSyntaxException) {
			throw new RuntimeException(uriSyntaxException);
		}

		org.LexGrid.valueSets.ValueSetDefinition lexGridValueSetDefinition;
		try {
			lexGridValueSetDefinition = getLexEVSValueSetDefinitionServices().getValueSetDefinition(valueSetDefinitionURI, null);
		} catch (LBException lbe) {
			throw new RuntimeException(lbe);
		}
			
		if (lexGridValueSetDefinition != null) {
			ValueSetCatalogEntry entry = this.transform.transformFullDescription(lexGridValueSetDefinition);
			return entry;			
		} else {
			return null;
		}
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		// TODO Auto-generated method stub
		return null;
	}

}
