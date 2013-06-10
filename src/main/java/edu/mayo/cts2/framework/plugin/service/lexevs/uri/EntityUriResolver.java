package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

@Component
public class EntityUriResolver {
	
	private final String URI_SEPARATOR = "/";
	
	@Resource
	private UriResolver uriResolver;
	
	public ScopedEntityName resolveUri(String entityUri){
		String[] parts = this.splitNamespaceAndLocalPart(entityUri);
		
		String namespaceUri = parts[0];
		
		String namespace = this.uriResolver.idToName(namespaceUri, IdType.CODE_SYSTEM);
		
		if(StringUtils.isNotBlank(namespace)){
			ScopedEntityName name = new ScopedEntityName();
			name.setNamespace(namespace);
			name.setName(parts[1]);
			
			return name;
		} else {
			return null;
		}
	}
	
	protected String[] splitNamespaceAndLocalPart(String uri){
		return new String[]{
			StringUtils.substringBeforeLast(uri, URI_SEPARATOR),
			StringUtils.substringAfterLast(uri, URI_SEPARATOR)};
	}

}
