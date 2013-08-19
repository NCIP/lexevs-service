/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
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
import edu.mayo.cts2.framework.model.mapversion.MapEntryListEntry;
import edu.mayo.cts2.framework.model.mapversion.MapSet;
import edu.mayo.cts2.framework.model.mapversion.MapTarget;
import edu.mayo.cts2.framework.model.mapversion.types.MapProcessingRule;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.MapResolvedConceptReference;

@Component
public class MappingToMapEntryTransform 
	extends AbstractBaseTransform<MapEntryListEntry, MapResolvedConceptReference, MapEntryDirectoryEntry, MapResolvedConceptReference>  {

	@Override
	public MapEntryListEntry transformFullDescription(MapResolvedConceptReference mapReference) {
		if(mapReference == null){
			return null;
		}
		
		ResolvedConceptReference resolvedConceptReference = mapReference;
		
		MapEntry mapEntry = new MapEntry();
		mapEntry.setMapFrom(this.getTransformUtils().toUriAndEntityName(resolvedConceptReference));
		mapEntry.setProcessingRule(MapProcessingRule.ALL_MATCHES);
		
		mapEntry.setAssertedBy(this.getTransformUtils().
			toMapVersionReference(
					mapReference.getMapName().getName(), 
					mapReference.getMapName().getVersion(), 
					null));
		
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
		
		mapEntry.addMapSet(mapSet);
		
		URIAndEntityName fromName = this.getTransformUtils().toUriAndEntityName(resolvedConceptReference);
		String encodedName = EncodingUtils.encodeScopedEntityName(fromName);

		String mapVersionName = 
			this.getVersionNameConverter().toCts2VersionName(
				mapReference.getMapName().getName(),
				mapReference.getMapName().getVersion());
		
		MapEntryListEntry listEntry = new MapEntryListEntry();
		listEntry.setEntry(mapEntry);
		listEntry.setResourceName(encodedName);
		listEntry.setHref(
				this.getUrlConstructor().createMapEntryUrl(
						mapReference.getMapName().getName(), 
						mapVersionName, 
						encodedName));

		return listEntry;
	}
	
	@Override
	public MapEntryDirectoryEntry transformSummaryDescription(MapResolvedConceptReference mapReference) {
		if(mapReference == null){
			return null;
		}
		
		ResolvedConceptReference resolvedConceptReference = mapReference;

		MapEntryDirectoryEntry mapEntryDirectoryEntry = new MapEntryDirectoryEntry();
		
		URIAndEntityName fromName = this.getTransformUtils().toUriAndEntityName(resolvedConceptReference);
		
		mapEntryDirectoryEntry.setMapFrom(fromName);
		
		String encodedName = EncodingUtils.encodeScopedEntityName(fromName);

		String mapVersionName = 
			this.getVersionNameConverter().toCts2VersionName(
				mapReference.getMapName().getName(),
				mapReference.getMapName().getVersion());
		
		mapEntryDirectoryEntry.setHref(
			this.getUrlConstructor().createMapEntryUrl(
					mapReference.getMapName().getName(), 
					mapVersionName, 
					encodedName));
		
		mapEntryDirectoryEntry.setResourceName(encodedName);
		
		mapEntryDirectoryEntry.setAssertedBy(this.getTransformUtils().
				toMapVersionReference(
						mapReference.getMapName().getName(), 
						mapReference.getMapName().getVersion(), 
						null));

		return mapEntryDirectoryEntry;
	}

}
