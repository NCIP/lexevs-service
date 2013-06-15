package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

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
		
		ScopedEntityName name = new ScopedEntityName();
		name.setName(namePart);

		String namespace = this.uriResolver.idToName(namespacePart, IdType.CODE_SYSTEM);
		
		if(StringUtils.isNotBlank(namespace)){
			name.setNamespace(namespace);
			
			return name;
		} else {
			try {
				CodingScheme cs = this.lexBigService.resolveCodingScheme(namespacePart, null);
				name.setNamespace(cs.getCodingSchemeName());
				return name;
			} catch (LBException e) {
				//didn't find it here... return null.
				return null;
			}	
		}
	}

}
