package edu.mayo.cts2.framework.plugins.service;

import edu.mayo.cts2.framework.plugin.service.lexevs.LexEvsOsgiClassLoader;

public class LocalClasspathLexEvsOsgiClassLoader extends LexEvsOsgiClassLoader {

	private ClassLoader original;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.original = Thread.currentThread().getContextClassLoader();
	}

	public Class<?> loadClass(String clazz) throws ClassNotFoundException {
		return original.loadClass(clazz);
	}
	
	protected Object getServiceClass(String clazz, boolean forceFromJar) throws Exception {
		return this.loadClass(clazz).newInstance();
	}

	@Override
	public ClassLoader getOsgiClassLoader() {
		return original;
	}

}
