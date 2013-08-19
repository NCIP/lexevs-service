/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.transform;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Property;
import org.apache.commons.lang.StringUtils;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.MapReference;
import edu.mayo.cts2.framework.model.core.MapVersionReference;
import edu.mayo.cts2.framework.model.core.NameAndMeaningReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.StatementTarget;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.core.ValueSetReference;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetDefinitionUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils;

/**
 * Common Transformation Utilities.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class TransformUtils implements LexEvsChangeEventObserver {
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Resource
	private UrlConstructor urlConstructor;
	
	@Resource
	private UriHandler uriHandler;
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;
	
	@Resource
	private CommonResolvedValueSetUtils commonResolvedValueSetUtils;
	
	@Resource
	private LexBIGService lexBigService;

	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	private Set<String> valueSetDefinitionUrisCache;
	
	private Object mutex = new Object();

	/**
	 * To property.
	 *
	 * @param property the property
	 * @return the edu.mayo.cts2.framework.model.core. property
	 */
	public static edu.mayo.cts2.framework.model.core.Property toProperty(Property property){
		edu.mayo.cts2.framework.model.core.Property cts2Prop = 
			new edu.mayo.cts2.framework.model.core.Property();
		
		PredicateReference predicateRef = new PredicateReference();
		predicateRef.setName(property.getPropertyName());
		predicateRef.setUri(property.getPropertyName());
		
		cts2Prop.setPredicate(predicateRef);
		
		StatementTarget target = new StatementTarget();
		target.setLiteral(ModelUtils.createOpaqueData(property.getValue().getContent()));
		
		cts2Prop.addValue(target);
		
		return cts2Prop;
	}
	
	/**
	 * To code system reference.
	 *
	 * @param name the name
	 * @return the code system reference
	 */
	public CodeSystemReference toCodeSystemReference(
			String name, String uri){
		name = codingSchemeNameTranslator.translateFromLexGrid(name);
		
		CodeSystemReference ref = new CodeSystemReference();
		ref.setContent(name);
		ref.setUri(uri);

		return ref;
	}
	
	public MapReference toMapReference(
			String name, String uri){
		name = codingSchemeNameTranslator.translateFromLexGrid(name);
		
		MapReference ref = new MapReference();
		ref.setContent(name);
		ref.setUri(uri);
		ref.setHref(this.urlConstructor.createMapUrl(name));

		return ref;
	}
	
	public ValueSetReference toValueSetReference(String valueSetName){	
		ValueSetReference ref = new ValueSetReference();
		ref.setContent(
			StringUtils.isNotBlank(valueSetName) ? valueSetName : ValueSetNameTranslator.UNNAMED_VALUESET);
		
		return ref;
	}
	
	public ValueSetDefinitionReference toValueSetDefinitionReference(String valueSetName, String definitionUri){	
		valueSetName = 
			StringUtils.isNotBlank(valueSetName) ? valueSetName : ValueSetNameTranslator.UNNAMED_VALUESET;
		
			ValueSetDefinitionReference ref = new ValueSetDefinitionReference();
		
		String definitionLocalId = ValueSetDefinitionUtils.getValueSetDefinitionLocalId(definitionUri);
		
		NameAndMeaningReference definition = new NameAndMeaningReference();
		definition.setContent(definitionLocalId);
		definition.setUri(definitionUri);
		
		boolean containsDefinition;
		synchronized(this.mutex){	
			if(this.valueSetDefinitionUrisCache == null){
				this.valueSetDefinitionUrisCache = 
					new HashSet<String>(this.lexEVSValueSetDefinitionServices.listValueSetDefinitionURIs());
			}
			
			containsDefinition = 
				this.valueSetDefinitionUrisCache.contains(definitionUri);
		}
		
		if(containsDefinition){
			definition.setHref(
				this.urlConstructor.createValueSetDefinitionUrl(valueSetName, definitionLocalId));
		}
		
		ref.setValueSetDefinition(definition);
		
		ref.setValueSet(this.toValueSetReference(valueSetName));
		
		return ref;
	}

	public CodeSystemVersionReference toCodeSystemVersionReference(
			String name, String version, String about) {
		name = codingSchemeNameTranslator.translateFromLexGrid(name);
		
		CodeSystemVersionReference ref = new CodeSystemVersionReference();
		ref.setCodeSystem(toCodeSystemReference(name, about));
		
		NameAndMeaningReference nameAndMeaning = new NameAndMeaningReference();
		
		String versionName = this.versionNameConverter.toCts2VersionName(name, version);
		nameAndMeaning.setContent(versionName);
		nameAndMeaning.setHref(
			this.urlConstructor.createCodeSystemVersionUrl(name, version));

		ref.setVersion(nameAndMeaning);
		
		return ref;
	}
	
	public MapVersionReference toMapVersionReference(
			String name, String version, String about) {
		MapVersionReference ref = new MapVersionReference();
		ref.setMap(toMapReference(name, about));
		
		NameAndMeaningReference nameAndMeaning = new NameAndMeaningReference();
		
		String versionName = this.versionNameConverter.toCts2VersionName(name, version);
		nameAndMeaning.setContent(versionName);
		nameAndMeaning.setHref(
			this.urlConstructor.createCodeSystemVersionUrl(name, version));

		ref.setMapVersion(nameAndMeaning);
		
		return ref;
	}
	
	public String createEntityHref(ResolvedConceptReference reference){
		String codingSchemeName = 
			this.codingSchemeNameTranslator.translateFromLexGrid(reference.getCodingSchemeName());
		
		String namespace = 
			this.codingSchemeNameTranslator.translateFromLexGrid(reference.getCodeNamespace());
		
		//if the namespace equals the CodingSchemeName, we don't need to add the namespace in the href.
		if(StringUtils.equals(codingSchemeName, namespace)){
			return this.urlConstructor.createEntityUrl(
					codingSchemeName, 
					reference.getCodingSchemeVersion(), 
					reference.getCode());
		} else { 
			Property valueSetCodeSystemVersionProp = null;
			CodingScheme cs = this.resolveCodingScheme(
				reference.getCodingSchemeURI(), reference.getCodingSchemeVersion());
				
			valueSetCodeSystemVersionProp = 
				this.commonResolvedValueSetUtils.getResolvedValueSetCodingSchemeProperty(cs);
			
			if(valueSetCodeSystemVersionProp == null){
				return this.urlConstructor.createEntityUrl(
					codingSchemeName, 
					reference.getCodingSchemeVersion(), 
					ModelUtils.createScopedEntityName(reference.getCode(), namespace));
			} else {
				String resolvedAgainstUri = valueSetCodeSystemVersionProp.getValue().getContent();
				String resolvedAgainstVersion = valueSetCodeSystemVersionProp.getPropertyQualifier()[0].getValue().getContent();
				
				CodingScheme actualCodingScheme = this.resolveCodingScheme(resolvedAgainstUri, resolvedAgainstVersion);
				
				if(actualCodingScheme != null){
					codingSchemeName = 
						this.codingSchemeNameTranslator.translateFromLexGrid(actualCodingScheme.getCodingSchemeName());
				
					return this.urlConstructor.createEntityUrl(
							codingSchemeName, 
							resolvedAgainstVersion, 
							ModelUtils.createScopedEntityName(reference.getCode(), namespace));
				} else {
					return null;
				}
			}
		}
	}
	
	private CodingScheme resolveCodingScheme(String identifier, String version){
		CodingScheme cs = null;
		try {
			cs = this.lexBigService.resolveCodingScheme(
					identifier, 
					Constructors.createCodingSchemeVersionOrTagFromVersion(version));
		} catch (LBException e) {
			//didn't find it
		}
		
		return cs;
	}

	public URIAndEntityName toUriAndEntityName(
			ResolvedConceptReference resolvedConceptReference) {
		URIAndEntityName uriAndName = new URIAndEntityName();
		uriAndName.setName(resolvedConceptReference.getCode());
		uriAndName.setNamespace(resolvedConceptReference.getCodeNamespace());
		uriAndName.setUri(this.uriHandler.getEntityUri(resolvedConceptReference));
		
		return uriAndName;
	}

	@Override
	public void onChange() {
		synchronized(this.mutex){
			this.valueSetDefinitionUrisCache = null;
		}
	}
	
}
