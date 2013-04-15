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
package edu.mayo.cts2.framework.plugin.service.lexevs.transform;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.commonTypes.Property;
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
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

/**
 * Common Transformation Utilities.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class TransformUtils {
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Resource
	private UrlConstructor urlConstructor;
	
	@Resource
	private UriHandler uriHandler;

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
		CodeSystemReference ref = new CodeSystemReference();
		ref.setContent(name);
		ref.setUri(uri);

		return ref;
	}
	
	public MapReference toMapReference(
			String name, String uri){
		MapReference ref = new MapReference();
		ref.setContent(name);
		ref.setUri(uri);
		ref.setHref(this.urlConstructor.createMapUrl(name));

		return ref;
	}

	public CodeSystemVersionReference toCodeSystemVersionReference(
			String name, String version, String about) {
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
		return this.urlConstructor.createEntityUrl(
				reference.getCodingSchemeName(), 
				reference.getCodingSchemeVersion(), 
				ModelUtils.createScopedEntityName(reference.getCode(), reference.getCodeNamespace()));
	}

	public URIAndEntityName toUriAndEntityName(
			ResolvedConceptReference resolvedConceptReference) {
		URIAndEntityName uriAndName = new URIAndEntityName();
		uriAndName.setName(resolvedConceptReference.getCode());
		uriAndName.setNamespace(resolvedConceptReference.getCodeNamespace());
		uriAndName.setUri(this.uriHandler.getEntityUri(resolvedConceptReference));
		
		return uriAndName;
	}
	
}
