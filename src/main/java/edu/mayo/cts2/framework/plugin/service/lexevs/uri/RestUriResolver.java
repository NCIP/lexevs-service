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
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import clojure.lang.RT;
import clojure.lang.Var;

/**
 * Client service based on an external URI Resolver JSON Service.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class RestUriResolver implements UriResolver {

	@Value("${uriResolutionServiceUrl}")
	private String uriResolutionServiceUrl;
	
	Var getUri;
	Var getName;
	Var getBaseEntityUri;
	Var getVersionName;
	Var getVersionUri;
	
	{
		try {
			RT.loadResourceScript("cts2/uri/UriResolutionService.clj");

			getUri = RT.var("cts2.uri", "getUri");
			getName = RT.var("cts2.uri", "getName");
			getBaseEntityUri = RT.var("cts2.uri", "getBaseEntityUri");
			getVersionName = RT.var("cts2.uri", "getVersionName");
			getVersionUri = RT.var("cts2.uri", "getVersionUri");

		} catch (Exception e) {
			throw new RuntimeException("Error starting Clojure.", e);
		}
	}
	
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
}
