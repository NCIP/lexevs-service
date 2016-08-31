/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import clojure.lang.RT;
import clojure.lang.Var;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Client service based on an external URI Resolver JSON Service.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component("restUriResolver")
public class RestUriResolver implements UriResolver, InitializingBean {

	@Value("${uriResolutionServiceUrl}")
	private String uriResolutionServiceUrl;
	
	Var getUri;
	Var getName;
	Var getBaseEntityUri;
	Var getVersionName;
	Var getVersionUri;
	Var getIds;
	
	public RestUriResolver(){
		super();
	}

	/**
	 * Instantiates a new rest uri resolver.
	 *
	 * @param uriResolutionServiceUrl the uri resolution service url
	 */
	public RestUriResolver(String uriResolutionServiceUrl){
		super();
		this.uriResolutionServiceUrl = uriResolutionServiceUrl;
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver#idToUri(java.lang.String, edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType)
	 */
	@Override
	public String idToUri(String id, IdType idType) {
		try {
			Object uri = getUri.invoke(uriResolutionServiceUrl, idType, id);
			if (uri != null) { 
				return uri.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver#idToName(java.lang.String, edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType)
	 */
	@Override
	public String idToName(String id, IdType idType) {
		try {
			Object name = getName.invoke(uriResolutionServiceUrl, idType, id);
			if (name != null) { 
				return name.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver#idToBaseUri(java.lang.String)
	 */
	@Override
	public String idToBaseUri(String id) {
		try {
			Object baseUri = getBaseEntityUri.invoke(uriResolutionServiceUrl, id);
			if (baseUri != null) { 
				return baseUri.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver#idAndVersionToVersionUri(java.lang.String, java.lang.String, edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType)
	 */
	public String idAndVersionToVersionUri(String id, String versionId,
			IdType itType) {
		try {
			Object versionUri = getVersionUri.invoke(uriResolutionServiceUrl, itType, id, versionId);
			if (versionUri != null) { 
				return versionUri.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver#idAndVersionToVersionName(java.lang.String, java.lang.String, edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType)
	 */
	public String idAndVersionToVersionName(String id, String versionId,
			IdType itType) {
		try {
			Object versionName = getVersionName.invoke(uriResolutionServiceUrl, itType, id, versionId);
			if (versionName != null) { 
				return versionName.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> idToIds(String id) {
		try {
			Object ids = getIds.invoke(uriResolutionServiceUrl, id);
			if (ids != null) { 
				return new HashSet<String>((Collection<? extends String>) ids);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			this.loadClojureScripts();
		} catch (Exception e) {
			throw new RuntimeException("Error starting Clojure.", e);
		} 
	}
	
	protected void loadClojureScripts() throws Exception {
		RT.loadResourceScript("cts2/uri/UriResolutionService.clj");

		getUri = RT.var("cts2.uri", "getUri");
		getName = RT.var("cts2.uri", "getName");
		getBaseEntityUri = RT.var("cts2.uri", "getBaseEntityUri");
		getVersionName = RT.var("cts2.uri", "getVersionName");
		getVersionUri = RT.var("cts2.uri", "getVersionUri");
		getIds = RT.var("cts2.uri", "getIds");
	}

}
