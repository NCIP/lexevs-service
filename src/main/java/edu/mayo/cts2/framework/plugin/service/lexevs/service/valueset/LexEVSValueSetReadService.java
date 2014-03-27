package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.valueset.ValueSetCatalogEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetReadService;

@Component
public class LexEVSValueSetReadService extends AbstractLexEvsService implements
		ValueSetReadService {

	@Resource
	private LexEVSValueSetDefinitionToValueSetEntryTransform vsTransformer;
	
	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;
	
	@Override
	public ValueSetCatalogEntry read(NameOrURI identifier,
			ResolvedReadContext readContext) {
		// TODO Auto-generated method stub
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
		return null;
	}

}
