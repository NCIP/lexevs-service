package edu.mayo.cts2.framework.plugin.service.lexevs;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class LexEvsOsgiClassLoader extends URLClassLoader implements
		InitializingBean, DisposableBean {
	
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
	
	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream stream = super.getResourceAsStream(name);
		if(stream == null){
			stream = this.osgiClassLoader.getResourceAsStream(name);
		}
		
		return stream;
	}
	
	@Override
	public URL getResource(String name) {
		URL resource = super.getResource(name);
		if(resource == null){
			resource = this.osgiClassLoader.getResource(name);
		}
		
		return resource;
	}

	public ClassLoader getOsgiClassLoader() {
		return this.osgiClassLoader;
	}

	@Override
	public void destroy() throws Exception {
		this.osgiClassLoader = null;
		Thread.currentThread().setContextClassLoader(null);
	}

}
