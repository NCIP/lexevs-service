package edu.mayo.cts2.framework.plugin.service.lexevs.transform;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

public abstract class AbstractBaseTransform<
	DescriptionDataType, 
	DescriptionDataIN, 
	DirectoryEntryDataType, 
	DirectoryEntryDataIN>
	implements LexEvsToCTS2Transformer<
		DescriptionDataType, 
		DescriptionDataIN, 
		DirectoryEntryDataType, 
		DirectoryEntryDataIN> {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	@Resource
	private TransformUtils transformUtils;
	
	@Resource
	private UrlConstructor urlConstructor;
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Resource
	private UriHandler uriHandler;

	public TransformUtils getTransformUtils() {
		return transformUtils;
	}

	public void setTransformUtils(TransformUtils transformUtils) {
		this.transformUtils = transformUtils;
	}

	public UrlConstructor getUrlConstructor() {
		return urlConstructor;
	}

	public void setUrlConstructor(UrlConstructor urlConstructor) {
		this.urlConstructor = urlConstructor;
	}

	public VersionNameConverter getVersionNameConverter() {
		return versionNameConverter;
	}

	public void setVersionNameConverter(VersionNameConverter versionNameConverter) {
		this.versionNameConverter = versionNameConverter;
	}

	public UriHandler getUriHandler() {
		return uriHandler;
	}

	public void setUriHandler(UriHandler uriHandler) {
		this.uriHandler = uriHandler;
	}

}
