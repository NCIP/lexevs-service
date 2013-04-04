package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

interface UriResolver {

	public enum IdType {
		CODE_SYSTEM, VALUE_SET, CODE_SYSTEM_VERSION
	}

	public String idToUri(String id, IdType idType);

	public String idToName(String id, IdType idType);

	public String idToBaseUri(String id);
	
	public String idAndVersionToVersionUri(String id, String versionId,
			IdType itType);

	public String idAndVersionToVersionName(String id, String versionId,
			IdType itType);

}