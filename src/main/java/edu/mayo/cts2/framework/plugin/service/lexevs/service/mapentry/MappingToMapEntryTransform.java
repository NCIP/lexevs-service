/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.core.util.EncodingUtils;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.mapversion.MapEntryDirectoryEntry;
import edu.mayo.cts2.framework.model.mapversion.MapSet;
import edu.mayo.cts2.framework.model.mapversion.MapTarget;
import edu.mayo.cts2.framework.model.mapversion.types.MapProcessingRule;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;

@Component
public class MappingToMapEntryTransform 
	extends AbstractBaseTransform<MapEntry, ResolvedConceptReference, MapEntryDirectoryEntry, ResolvedConceptReference>  {

	@Override
	public MapEntry transformFullDescription(ResolvedConceptReference resolvedConceptReference) {
		if(resolvedConceptReference == null){
			return null;
		}
		
		MapEntry mapEntry = new MapEntry();
		mapEntry.setMapFrom(this.getTransformUtils().toUriAndEntityName(resolvedConceptReference));
		mapEntry.setProcessingRule(MapProcessingRule.ALL_MATCHES);
		
		mapEntry.setAssertedBy(this.getTransformUtils().
			toMapVersionReference(
					resolvedConceptReference.getCodingSchemeName(), 
					resolvedConceptReference.getCodingSchemeVersion(), 
					resolvedConceptReference.getCodingSchemeURI()));
		
		MapSet mapSet = new MapSet();
		mapSet.setEntryOrder(1L);
		mapSet.setProcessingRule(MapProcessingRule.ALL_MATCHES);
		
		AssociationList source = resolvedConceptReference.getSourceOf();

		for(Association association : source.getAssociation()){
			for(AssociatedConcept ac :
				association.getAssociatedConcepts().getAssociatedConcept()){
				MapTarget target = new MapTarget();
				target.setEntryOrder((long) (mapSet.getMapTargetCount() + 1));
				target.setMapTo(this.getTransformUtils().toUriAndEntityName(ac));
				mapSet.addMapTarget(target);
			}
		}
		
		mapEntry.setMapSet(0, mapSet);

		return mapEntry;
	}
	
	@Override
	public MapEntryDirectoryEntry transformSummaryDescription(ResolvedConceptReference resolvedConceptReference) {
		if(resolvedConceptReference == null){
			return null;
		}
		
		MapEntryDirectoryEntry mapEntryDirectoryEntry = new MapEntryDirectoryEntry();
		
		URIAndEntityName fromName = this.getTransformUtils().toUriAndEntityName(resolvedConceptReference);
		
		mapEntryDirectoryEntry.setMapFrom(fromName);
		
		String mapName = resolvedConceptReference.getCodingSchemeName();
		String versionName = this.getVersionNameConverter().
			toCts2VersionName(mapName, resolvedConceptReference.getCodingSchemeVersion());
		
		String encodedName = EncodingUtils.encodeScopedEntityName(fromName);
		
		mapEntryDirectoryEntry.setHref(
			this.getUrlConstructor().createMapEntryUrl(
					mapName, 
					versionName, 
					encodedName));
		
		mapEntryDirectoryEntry.setResourceName(encodedName);

		return mapEntryDirectoryEntry;
	}

}
