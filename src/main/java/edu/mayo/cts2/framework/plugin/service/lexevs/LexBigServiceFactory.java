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
package edu.mayo.cts2.framework.plugin.service.lexevs;

import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.Map;

import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * A factory for creating LexBigService objects.
 */
public class LexBigServiceFactory implements FactoryBean<LexBIGService>, DisposableBean {

	private static final String LG_CONFIG_FILE_ENV = "LG_CONFIG_FILE";
	
	protected Logger log = Logger.getLogger(this.getClass());

	private LexBIGService lexBIGService;

	private String lexevsRemoteApiUrl;

	private Boolean useRemoteApi;
	
	private String lgConfigFile;
	
	private boolean hasBeenConfigured = false;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public LexBIGService getObject() throws Exception {
		while(! this.hasBeenConfigured){
			this.log.warn("Waiting for the Configuration Service to start...");
			Thread.sleep(4000);
		}
		if (BooleanUtils.toBoolean(this.useRemoteApi) && StringUtils.isNotBlank(this.lexevsRemoteApiUrl)) {
			return (LexEVSApplicationService) ApplicationServiceProvider
					.getApplicationServiceFromUrl(this.lexevsRemoteApiUrl, "EvsServiceInfo");
		} else {
			if(StringUtils.isBlank(this.lgConfigFile)){
				throw new IllegalStateException(LG_CONFIG_FILE_ENV + " value is empty.");
			}
			System.setProperty(LG_CONFIG_FILE_ENV, this.lgConfigFile);
			
			this.lexBIGService = LexBIGServiceImpl.defaultInstance();
			return this.lexBIGService;
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return LexBIGService.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void destroy() throws Exception {
		if(this.lexBIGService != null){
			log.info("Shutting down local LexEVS.");
			this.lexBIGService.getServiceManager(null).shutdown();
		}
	}
	
	public void updateCallback(Map<String,?> properties){
		this.lexevsRemoteApiUrl = (String)properties.get("lexevsRemoteApiUrl");
		this.lgConfigFile = (String)properties.get(LG_CONFIG_FILE_ENV);
		this.useRemoteApi = BooleanUtils.toBoolean(properties.get("useRemoteApi").toString());
		
		this.hasBeenConfigured = true;
	}

	public String getLexevsRemoteApiUrl() {
		return lexevsRemoteApiUrl;
	}

	public void setLexevsRemoteApiUrl(String lexevsRemoteApiUrl) {
		this.lexevsRemoteApiUrl = lexevsRemoteApiUrl;
	}

	public Boolean getUseRemoteApi() {
		return useRemoteApi;
	}

	public void setUseRemoteApi(Boolean useRemoteApi) {
		this.useRemoteApi = useRemoteApi;
	}

	public String getLgConfigFile() {
		return lgConfigFile;
	}

	public void setLgConfigFile(String lgConfigFile) {
		this.lgConfigFile = lgConfigFile;
	}

}
