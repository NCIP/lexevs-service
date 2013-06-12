package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

@Component
public class EntityUriResolver {
	
	private final String URI_SEPARATOR = "/";
	
	@Resource
	private LexBIGService lexBigService;
	
	@Resource
	private UriResolver uriResolver;
	
	public ScopedEntityName resolveUri(String entityUri){
		String[] parts = this.splitNamespaceAndLocalPart(entityUri);
		
		String namespaceUri = parts[0];
		
		String namespace = this.uriResolver.idToName(namespaceUri, IdType.CODE_SYSTEM);
		
		ScopedEntityName name = new ScopedEntityName();
		name.setName(parts[1]);

		if(StringUtils.isNotBlank(namespace)){
			name.setNamespace(namespace);
			
			return name;
		} else {
			try {
				CodingScheme cs = this.lexBigService.resolveCodingScheme(namespaceUri, null);
				name.setNamespace(cs.getCodingSchemeName());
				return name;
			} catch (LBException e) {
				//didn't find it here... return null.
				return null;
			}	
		}
	}
	
	protected String[] splitNamespaceAndLocalPart(String uri){
		return new String[]{
			StringUtils.substringBeforeLast(uri, URI_SEPARATOR),
			StringUtils.substringAfterLast(uri, URI_SEPARATOR)};
	}

}
