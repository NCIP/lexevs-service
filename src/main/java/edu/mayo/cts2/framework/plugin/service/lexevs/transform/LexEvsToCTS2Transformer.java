/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.transform;



/**
 * @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public interface LexEvsToCTS2Transformer <DescriptionDataType, DescriptionDataIN, DirectoryEntryDataType, DirectoryEntryDataIN> {
	
	// ResourceList
	// Description
	public DescriptionDataType transformFullDescription(DescriptionDataIN data);

	
	// ResourceSummaries
	// DirectoryEntry
	public DirectoryEntryDataType transformSummaryDescription(DirectoryEntryDataIN data);
}
