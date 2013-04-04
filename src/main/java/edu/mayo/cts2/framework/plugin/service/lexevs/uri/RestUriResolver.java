package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import clojure.lang.RT;
import clojure.lang.Var;

public class RestUriResolver implements UriResolver {

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

	protected RestUriResolver(String uriResolutionServiceUrl){
		super();
		this.uriResolutionServiceUrl = uriResolutionServiceUrl;
	}
	
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
