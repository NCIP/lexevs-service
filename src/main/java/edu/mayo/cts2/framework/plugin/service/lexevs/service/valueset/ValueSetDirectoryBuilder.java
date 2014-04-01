/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valueset;

import java.util.List;
import java.util.Set;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;

/**
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 * @param <T>
 */
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
