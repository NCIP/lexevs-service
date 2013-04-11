package edu.mayo.cts2.framework.plugin.service.lexevs;

import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class LexEvsOsgiClassLoader extends URLClassLoader implements
		InitializingBean {

	@Value("${LG_RUNTIME_JAR}")
	private String lgRuntimeJar;

	@Value("${LG_PATCH_JAR}")
	private String lgPatchJar;

	private ClassLoader osgiClassLoader;

	public LexEvsOsgiClassLoader() {
		super(new URL[0]);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.osgiClassLoader = Thread.currentThread().getContextClassLoader();
		this.addURL(new URL(this.lgRuntimeJar));

		Thread.currentThread().setContextClassLoader(this);
	}

	protected Object getServiceClass(String clazz) throws Exception {
		return this.getServiceClass(clazz, false);
	}
	
	protected Object getServiceClass(String clazz, boolean forceFromJar) throws Exception {
		if(!forceFromJar){
			try {
				// it may be present in the classpath (for testing, especially)
				Class<?> impl = this.osgiClassLoader
						.loadClass(clazz);
	
				return impl.newInstance();
			} catch (ClassNotFoundException e) {
				// this is ok - we'll get it from the specified lbRuntime.jar
			}
		
			return this.loadClass(clazz).newInstance();
		} else {
			return super.loadClass(clazz).newInstance();
		}
	}

	@Override
	public Class<?> loadClass(String clazz) throws ClassNotFoundException {
		if (clazz.toLowerCase().contains("lexevs")
				|| clazz.toLowerCase().contains("lexbig")
				|| clazz.toLowerCase().contains("lexgrid")) {
			try {
				return osgiClassLoader.loadClass(clazz);
			} catch (ClassNotFoundException e) {
				return super.loadClass(clazz);
			}
		} else {
			return super.loadClass(clazz);
		}
	}
	
	public ClassLoader getOsgiClassLoader() {
		return this.osgiClassLoader;
	}

}
