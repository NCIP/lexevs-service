/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import java.util.Arrays;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriUtils;

@Component
public class EntityUriResolver {
		
	@Resource
	private LexBIGService lexBigService;
	
	@Resource
	private UriResolver uriResolver;
	
	public ScopedEntityName resolveUri(String entityUri){
		String namePart = UriUtils.getLocalPart(entityUri);
		String namespacePart = UriUtils.getNamespace(entityUri);
		String namespacePartWithSeparator = namespacePart + UriUtils.getSeparator(entityUri);

		ScopedEntityName name = new ScopedEntityName();
		name.setName(namePart);
		
		for(String ns : Arrays.asList(namespacePart, namespacePartWithSeparator)){
			String namespace = this.uriResolver.idToName(ns, IdType.CODE_SYSTEM);
		
			if(StringUtils.isNotBlank(namespace)){
				name.setNamespace(namespace);
				
				return name;
			} else {
				try {
					CodingScheme cs = this.lexBigService.resolveCodingScheme(ns, null);
					name.setNamespace(cs.getCodingSchemeName());
					return name;
				} catch (LBException e) {
					//didn't find it here...
				}	
			}
		}
		
		return null;
	}

}
