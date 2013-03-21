package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import java.util.ArrayList;
import java.util.List;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryReadService;
import edu.mayo.cts2.framework.service.profile.mapentry.name.MapEntryReadId;

public class LexEvsMapEntryReadService extends AbstractLexEvsService implements MapEntryReadService {

	@Override
	public MapEntry read(
			MapEntryReadId identifier,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(
			MapEntryReadId identifier,
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
