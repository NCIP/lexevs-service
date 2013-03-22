package edu.mayo.cts2.framework.plugin.service.lexevs.service.mapentry;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.mapversion.MapEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryReadService;
import edu.mayo.cts2.framework.service.profile.mapentry.name.MapEntryReadId;

@Component
public class LexEvsMapEntryReadService extends AbstractLexEvsService implements MapEntryReadService, InitializingBean {

	@Resource
	private CodeSystemVersionNameConverter nameConverter;
	
	@Resource
	private MappingToMapEntryTransform mappingToMapEntryTransform;
	
	private MappingExtension mappingExtension;
	
	public static final String MAPPING_EXTENSION = "MappingExtension";
	

	@Override
	public void afterPropertiesSet() throws Exception {
		this.mappingExtension = (MappingExtension)this.getLexBigService().getGenericExtension(MAPPING_EXTENSION);
	}

	
	@Override
	public MapEntry read(
			MapEntryReadId identifier,
			ResolvedReadContext readContext) {
		
		// Parse identifier into pieces to create a LexEVS Mapping object
		ScopedEntityName scopedEntityName = identifier.getEntityName();
		String sourceEntityCode = scopedEntityName.getName();
		
		String mapVersion = null;
		NameOrURI nameOrURI = identifier.getMapVersion();
		if (nameOrURI != null) {
			mapVersion = nameOrURI.getName() != null ? nameOrURI.getName() : nameOrURI.getUri();
		}
				
		String mappingUri = identifier.getUri(); // Is this the sourceEntity's URI? Not the Map's codingSchemeURI, right?		
		String relationsContainerName = null;   // if left null, the Mapping.resolveMapping() throws NullPointerException
		relationsContainerName = "AutoToGMPMappings"; 
		
		Mapping mapping = null;
		ResolvedConceptReferencesIterator resolvedConceptReferencesIterator;
		ResolvedConceptReference resolvedConceptReference = null;
		try {
/*			mapping = mappingExtension.getMapping(
					mappingUri, 
					Constructors.createCodingSchemeVersionOrTagFromVersion(mapVersion), 
					relationsContainerName);
*/			
			mapping = mappingExtension.getMapping(
					mapVersion, 
					Constants.CURRENT_LEXEVS_TAG, 
					relationsContainerName);

			mapping = mapping.restrictToCodes(Constructors.createConceptReferenceList(sourceEntityCode), SearchContext.SOURCE_CODES);
//			Mapping mapping2 = mapping.restrictToCodes(Constructors.createConceptReferenceList(sourceEntityCode), SearchContext.SOURCE_OR_TARGET_CODES);
			
			resolvedConceptReferencesIterator = mapping.resolveMapping();
			
			if (resolvedConceptReferencesIterator.hasNext()) {
				resolvedConceptReference = resolvedConceptReferencesIterator.next();				
			}
//			ResolvedConceptReferencesIterator itr = mapping2.resolveMapping();
			
			// if relationsContainerName is null, below method throws NullPointerException
			ResolvedConceptReferencesIterator itr = mappingExtension.resolveMapping(
					mapVersion, 
					Constants.CURRENT_LEXEVS_TAG, 
					relationsContainerName, 
					null);

//			ResolvedConceptReferenceList rcrList = itr.get(0,5);  // Unsupported Operation
//			ResolvedConceptReferenceList rcrList = itr.getNext(); // Unsupported Operation
			
			// hasNext will fail - Bad SQL Grammar error - ibatis 
			while (itr.hasNext())  {
				@SuppressWarnings("unused")
				ResolvedConceptReference next = itr.next();
				//System.out.println();
			}
			
			// null relation container name throws NullPointerException
//			ResolvedConceptReferencesIterator itr2 = mappingExtension.resolveMapping(
//					mapVersion, 
//					Constants.CURRENT_LEXEVS_TAG, 
//					null, 
//					null);

			
			if (resolvedConceptReferencesIterator != null && resolvedConceptReferencesIterator.numberRemaining() == 1) {
				resolvedConceptReference = resolvedConceptReferencesIterator.next();
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (resolvedConceptReference != null) {
			//resolvedConceptReference.
			return new MapEntry();
			//return this.mappingToMapEntryTransform.transform(resolvedConceptReference);
		} else {
			return new MapEntry();
		}
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
