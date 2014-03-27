package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.util.List;
import java.util.Set;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;



public class ValueSetDirectoryBuilder<T> extends
AbstractStateBuildingDirectoryBuilder<List<String>,T> {

	public ValueSetDirectoryBuilder(
			List<String> uris,
			AbstractStateBuildingDirectoryBuilder.Callback<List<String>, T> callback,
			Set<MatchAlgorithmReference> matchAlgorithmReferences,
			Set<StateAdjustingComponentReference<List<String>>> stateAdjustingComponentReferences) {
		super(uris, callback, matchAlgorithmReferences,
				stateAdjustingComponentReferences);
	}

}
