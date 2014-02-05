/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import java.util.List;
import java.util.Set;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;

/**
 * A Basic EntityDirectoryBuilder.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class ValueSetDefinitionDirectoryBuilder<T> extends AbstractStateBuildingDirectoryBuilder<List<String>,T> {

	/**
	 * Instantiates a new basic entity directory builder.
	 *
	 * @param initialState the initial state
	 * @param callback the callback
	 * @param matchAlgorithmReferences the match algorithm references
	 * @param stateAdjustingComponentReferences the state adjusting property references
	 */
	public ValueSetDefinitionDirectoryBuilder(
			List<String> uris,
			AbstractStateBuildingDirectoryBuilder.Callback<List<String>, T> callback,
			Set<MatchAlgorithmReference> matchAlgorithmReferences,
			Set<StateAdjustingComponentReference<List<String>>> stateAdjustingComponentReferences) {
		super(uris, callback, matchAlgorithmReferences,
				stateAdjustingComponentReferences);
	}

}
