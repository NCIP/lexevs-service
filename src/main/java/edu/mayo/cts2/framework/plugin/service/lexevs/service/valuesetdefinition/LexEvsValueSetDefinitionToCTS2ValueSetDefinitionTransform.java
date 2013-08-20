/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.valuesetdefinition;

import java.net.URI;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.naming.SupportedNamespace;
import org.LexGrid.naming.SupportedProperty;
import org.LexGrid.valueSets.CodingSchemeReference;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.PropertyReference;
import org.LexGrid.valueSets.ValueSetDefinitionReference;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.apache.commons.lang.StringUtils;
import org.lexevs.dao.database.utility.DaoUtility;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.EntryDescription;
import edu.mayo.cts2.framework.model.core.FilterComponent;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.SourceAndNotation;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.ValueSetReference;
import edu.mayo.cts2.framework.model.core.types.AssociationDirection;
import edu.mayo.cts2.framework.model.core.types.SetOperator;
import edu.mayo.cts2.framework.model.core.types.TargetReferenceType;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.model.valuesetdefinition.AssociatedEntitiesReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.CompleteCodeSystemReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.CompleteValueSetReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.PropertyQueryReference;
import edu.mayo.cts2.framework.model.valuesetdefinition.SpecificEntityList;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinition;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ValueSetDefinitionListEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.types.LeafOrAll;
import edu.mayo.cts2.framework.model.valuesetdefinition.types.TransitiveClosure;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNamePair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.AbstractBaseTransform;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriUtils;

/**
 * Transforms a LexEVS ValueSetDefinition object into a CTS2 ValueSetDefinition object.
 * 
 * @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *
 */
@Component
public class LexEvsValueSetDefinitionToCTS2ValueSetDefinitionTransform 
	extends AbstractBaseTransform<
		ValueSetDefinitionListEntry, 
		org.LexGrid.valueSets.ValueSetDefinition, 
		ValueSetDefinitionDirectoryEntry, 
		org.LexGrid.valueSets.ValueSetDefinition> {
	
	@Resource
	private UriHandler uriHandler;
	
	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	@Resource
	private ValueSetNameTranslator valueSetNameTranslator;
	
	@Resource
	private LexBIGService lexBigService;

	public ValueSetDefinitionListEntry transformFullDescription(org.LexGrid.valueSets.ValueSetDefinition lexEvsVSD) {
		if (lexEvsVSD == null) {
			return null;
		}
		
		ValueSetDefinition cts2VSD = new ValueSetDefinition();

		cts2VSD.setAbout(lexEvsVSD.getValueSetDefinitionURI());
		cts2VSD.setDocumentURI(lexEvsVSD.getValueSetDefinitionURI());

		cts2VSD.setFormalName(lexEvsVSD.getValueSetDefinitionURI());

		SourceAndNotation sourceAndNotation = new SourceAndNotation();
		sourceAndNotation.setSourceAndNotationDescription("LexEVS");
		cts2VSD.setSourceAndNotation(sourceAndNotation);
		
		if(lexEvsVSD.getEntityDescription() != null){
			String content = lexEvsVSD.getEntityDescription().getContent();
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(content));
			
			cts2VSD.setResourceSynopsis(description);
		}
		
		cts2VSD.setDefinedValueSet(
			this.getTransformUtils().toValueSetReference(
				lexEvsVSD.getValueSetDefinitionName()));
		
		for(DefinitionEntry entry : lexEvsVSD.getDefinitionEntry()){
			ValueSetDefinitionEntry cts2Entry = new ValueSetDefinitionEntry();
			
			if(entry.getCodingSchemeReference() != null){
				this.add(entry.getCodingSchemeReference(), cts2Entry, lexEvsVSD);
			}
			if(entry.getEntityReference() != null){
				this.add(entry.getEntityReference(), cts2Entry, lexEvsVSD);
			}
			if(entry.getPropertyReference() != null){
				this.add(entry.getPropertyReference(), cts2Entry, lexEvsVSD);
			}
			if(entry.getValueSetDefinitionReference() != null){
				this.add(entry.getValueSetDefinitionReference(), cts2Entry);
			}
			
			cts2Entry.setEntryOrder(entry.getRuleOrder());
			cts2Entry.setOperator(this.toSetOperator(entry.getOperator()));
			
			cts2VSD.addEntry(cts2Entry);
		}
	
		ValueSetDefinitionListEntry listEntry = new ValueSetDefinitionListEntry();
		listEntry.addEntry(cts2VSD);
		
		ValueSetNamePair pair = this.valueSetNameTranslator.
				getDefinitionNameAndVersion(
					lexEvsVSD.getValueSetDefinitionURI());
			
		listEntry.setResourceName(pair.getDefinitionLocalId());
		
		listEntry.setHref(
			this.getUrlConstructor().
				createValueSetDefinitionUrl(
					pair.getValueSetName(), 
					pair.getDefinitionLocalId()));
		
		
		return listEntry;
	}
	
	private SetOperator toSetOperator(DefinitionOperator operator){
		switch(operator) {
			case OR : return SetOperator.UNION;
			case AND : return SetOperator.INTERSECT;
			case SUBTRACT : return SetOperator.SUBTRACT;
			default : throw new IllegalStateException();
		}
	}
	
	private void add(
			ValueSetDefinitionReference valueSetDefinitionReference,
			ValueSetDefinitionEntry cts2Entry) {
		String uri = valueSetDefinitionReference.getValueSetDefinitionURI();
	
		String name;
		try {
			name = this.lexEVSValueSetDefinitionServices.getValueSetDefinition(new URI(uri), null).getValueSetDefinitionName();
		} catch (Exception e) {
			//couldn't find it in LexEVS -- use the URI.
			name = uri;
		}
				
		ValueSetReference ref = this.getTransformUtils().toValueSetReference(name);

		CompleteValueSetReference vsr = new CompleteValueSetReference();
		vsr.setValueSet(ref);
		
		cts2Entry.setCompleteValueSet(vsr);
	}

	private void add(
			PropertyReference propertyReference,
			ValueSetDefinitionEntry cts2Entry,
			org.LexGrid.valueSets.ValueSetDefinition definition) {
		PropertyQueryReference pqr = new PropertyQueryReference();

		String codingScheme = propertyReference.getCodingScheme();
		SupportedCodingScheme supportedCodingScheme = this.findCodingScheme(definition, codingScheme);
		pqr.setCodeSystem(
			this.getTransformUtils().toCodeSystemReference(
				supportedCodingScheme.getLocalId(), 
				supportedCodingScheme.getUri()));
		
		String propertyName = propertyReference.getPropertyName();
		String matchAlgorithm = propertyReference.getPropertyMatchValue().getMatchAlgorithm();
		String matchValue = propertyReference.getPropertyMatchValue().getContent();
				
		SupportedProperty supportedProperty = this.findProperty(definition.getMappings(), propertyName);
		
		String propName;
		String propUri;
		if(StringUtils.isBlank(propertyName)){
			//TODO: What is the correct behavior here?
			propName = "UNSPECIFIED";
			propUri = "UNSPECIFIED";
		} else {
			propName = supportedProperty.getLocalId();
			propUri = supportedProperty.getUri();
		}
		
		FilterComponent filter = new FilterComponent();
		filter.setMatchAlgorithm(new MatchAlgorithmReference());
		filter.getMatchAlgorithm().setContent(matchAlgorithm);
		filter.setReferenceTarget(new URIAndEntityName());
		filter.getReferenceTarget().setName(propName);
		filter.getReferenceTarget().setUri(propUri);
		filter.setReferenceType(TargetReferenceType.PROPERTY);
		
		filter.setMatchValue(matchValue);
		
		pqr.setFilter(filter);
		cts2Entry.setPropertyQuery(pqr);
	}
	
	private SupportedCodingScheme findCodingScheme(
			org.LexGrid.valueSets.ValueSetDefinition definition, String localId){
		if(StringUtils.isBlank(localId)){
			localId = definition.getDefaultCodingScheme();
		}
		return DaoUtility.getURIMap(definition.getMappings(), SupportedCodingScheme.class, localId);
	}
	
	private SupportedNamespace findNamespace(Mappings mappings, String localId){
		return DaoUtility.getURIMap(mappings, SupportedNamespace.class, localId);
	}
	
	private SupportedProperty findProperty(Mappings mappings, String localId){
		return DaoUtility.getURIMap(mappings, SupportedProperty.class, localId);
	}
	
	private SupportedAssociation findAssociation(Mappings mappings, String localId){
		return DaoUtility.getURIMap(mappings, SupportedAssociation.class, localId);
	}

	private void add(
			EntityReference entityReference,
			ValueSetDefinitionEntry cts2Entry, 
			org.LexGrid.valueSets.ValueSetDefinition lexEvsVSD) {
		URIAndEntityName uriAndName = new URIAndEntityName();
		uriAndName.setName(entityReference.getEntityCode());
		
		String namespace = entityReference.getEntityCodeNamespace();
		if(StringUtils.isBlank(namespace)){
			namespace = lexEvsVSD.getDefaultCodingScheme();
		}
		
		uriAndName.setNamespace(namespace);
		
		SupportedNamespace supportedNamespace = 
			this.findNamespace(lexEvsVSD.getMappings(), namespace);
		
		if(supportedNamespace == null){
			log.warn("EntityRefernece: " + 
					entityReference.getEntityCodeNamespace()  +
					":" + entityReference.getEntityCode() +
					" does not have a valid namespace.");
			
			try {
				CodingScheme cs = this.lexBigService.resolveCodingScheme(namespace, null);
				SupportedNamespace sns = new SupportedNamespace();
				sns.setContent(cs.getCodingSchemeName());
				sns.setEquivalentCodingScheme(cs.getCodingSchemeName());
				sns.setLocalId(cs.getCodingSchemeName());
				sns.setUri(cs.getCodingSchemeURI());
				
				supportedNamespace = sns;
			} catch (LBException e) {
				log.info(e);
				
				//we have to punt here
				SupportedNamespace sns = new SupportedNamespace();
				sns.setContent("unknown");
				sns.setEquivalentCodingScheme("unknown");
				sns.setLocalId("unknown");
				sns.setUri("unknown");
			}
		}
		
		uriAndName.setUri(
			UriUtils.combine(
				supportedNamespace.getUri(),
				entityReference.getEntityCode()));
		
		if(entityReference.getReferenceAssociation() != null){
			AssociatedEntitiesReference ref = new AssociatedEntitiesReference();
			ref.setReferencedEntity(uriAndName);
			ref.setDirection(
				entityReference.getTargetToSource() ? 
					AssociationDirection.TARGET_TO_SOURCE : 
					AssociationDirection.SOURCE_TO_TARGET);
			
			String cs = supportedNamespace.getEquivalentCodingScheme();
			SupportedCodingScheme supportedCodingScheme = 
				this.findCodingScheme(lexEvsVSD, cs);
			
			CodeSystemReference codingSchemeRef = this.getTransformUtils().toCodeSystemReference(
				supportedCodingScheme.getLocalId(), 
				supportedCodingScheme.getUri());
			
			ref.setCodeSystem(codingSchemeRef);
			
			ref.setTransitivity(
					entityReference.getTransitiveClosure() ? 
						TransitiveClosure.TRANSITIVE_CLOSURE : 
						TransitiveClosure.DIRECTLY_ASSOCIATED);
			
			ref.setLeafOnly(
					entityReference.getLeafOnly() ?
						LeafOrAll.LEAF_ONLY :
						LeafOrAll.ALL_INTERMEDIATE_NODES);
			
			String association = entityReference.getReferenceAssociation();
			PredicateReference predicate = new PredicateReference();
			predicate.setName(association);
			
			//namespace for a predicate is not guaranteed in LexEVS... not sure 
			//how we want to handle this. For now, assume the same namespace
			//as the starting entity.		
			SupportedAssociation supportedAssociation = 
					this.findAssociation(lexEvsVSD.getMappings(), association);
			
			if(supportedAssociation == null){
				predicate.setNamespace(supportedCodingScheme.getLocalId());
			} else {
				predicate.setNamespace(supportedAssociation.getEntityCodeNamespace());
			}
			
			ref.setPredicate(predicate);
			
			cts2Entry.setAssociatedEntities(ref);
		} else {
			SpecificEntityList sel = new SpecificEntityList();
			
			sel.addReferencedEntity(uriAndName);
			
			cts2Entry.setEntityList(sel);
		}
	}

	private void add(
			CodingSchemeReference codingSchemeReference,
			ValueSetDefinitionEntry cts2Entry,
			org.LexGrid.valueSets.ValueSetDefinition definition) {
		CompleteCodeSystemReference ccr = new CompleteCodeSystemReference();

		String codeSystemName = codingSchemeReference.getCodingScheme();
		
		SupportedCodingScheme supportedCodingScheme = 
				this.findCodingScheme(definition, codeSystemName);
		
		ccr.setCodeSystem(
			this.getTransformUtils().toCodeSystemReference(
				supportedCodingScheme.getLocalId(), 
				supportedCodingScheme.getUri()));
		
		cts2Entry.setCompleteCodeSystem(ccr);
	}

	@Override
	public ValueSetDefinitionDirectoryEntry transformSummaryDescription(org.LexGrid.valueSets.ValueSetDefinition lexEvsVSD) {
		if (lexEvsVSD == null) {
			return null;
		}
		
		ValueSetDefinitionDirectoryEntry vsdDirEntry = new ValueSetDefinitionDirectoryEntry();
		
		vsdDirEntry.setAbout(lexEvsVSD.getValueSetDefinitionURI());
		vsdDirEntry.setDocumentURI(lexEvsVSD.getValueSetDefinitionURI());
		vsdDirEntry.setFormalName(lexEvsVSD.getValueSetDefinitionName());
		
		if(lexEvsVSD.getEntityDescription() != null){
			String content = lexEvsVSD.getEntityDescription().getContent();
			EntryDescription description = new EntryDescription();
			description.setValue(ModelUtils.toTsAnyType(content));
			
			vsdDirEntry.setResourceSynopsis(description);
		}
		
		ValueSetNamePair pair = this.valueSetNameTranslator.
			getDefinitionNameAndVersion(
				lexEvsVSD.getValueSetDefinitionURI());
		
		vsdDirEntry.setHref(
			this.getUrlConstructor().
				createValueSetDefinitionUrl(
					pair.getValueSetName(), 
					pair.getDefinitionLocalId()));
		
		ValueSetReference vsReference = 
			this.getTransformUtils().toValueSetReference(lexEvsVSD.getValueSetDefinitionName());
		vsdDirEntry.setDefinedValueSet(vsReference);
		
		return vsdDirEntry;
	}
	
}
