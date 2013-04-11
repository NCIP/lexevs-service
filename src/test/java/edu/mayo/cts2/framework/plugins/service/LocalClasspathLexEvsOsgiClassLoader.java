package edu.mayo.cts2.framework.plugins.service;

import edu.mayo.cts2.framework.plugin.service.lexevs.LexEvsOsgiClassLoader;

public class LocalClasspathLexEvsOsgiClassLoader extends LexEvsOsgiClassLoader {

	@Override
	public void afterPropertiesSet() throws Exception {
		//
	}

	public Class<?> loadClass(String clazz) throws ClassNotFoundException {
		return Thread.currentThread().getContextClassLoader().loadClass(clazz);
	}
	
	protected Object getServiceClass(String clazz, boolean forceFromJar) throws Exception {
		return this.loadClass(clazz).newInstance();
	}

	@Override
	public ClassLoader getOsgiClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	protected Object getServiceClass(String clazz) throws Exception {
		return Thread.currentThread().getContextClassLoader().loadClass(clazz).newInstance();
	}

}
