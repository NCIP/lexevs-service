/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

@Component
public class DefaultCodingSchemeNameTranslator implements
		CodingSchemeNameTranslator, LexEvsChangeEventObserver, InitializingBean {

	private static final int FLUSH_PERIOD_MINUTES = 60;

	private Logger log = LogManager.getLogger(this.getClass());

	@Resource
	private UriResolver uriResolver;

	@Resource
	private LexBIGService lexBigService;

	private Map<String, String> lexgridToAliasMap = new HashMap<String, String>();
	private Map<String, String> aliasToLexGridMap = new HashMap<String, String>();
	private Map<String, String> lexgridUriToLexGridNameMap = new HashMap<String, String>();

	private Object mutex = new Object();

	@Override
	public void afterPropertiesSet() {
		this.scheduleflushCache();
	}

	protected void buildCaches() {
		synchronized(this.mutex){
			this.lexgridToAliasMap.clear();
			this.aliasToLexGridMap.clear();
			this.lexgridUriToLexGridNameMap.clear();
			
			try {
				for (CodingSchemeRendering csr : this.lexBigService
						.getSupportedCodingSchemes().getCodingSchemeRendering()) {
					String lexgridName = csr.getCodingSchemeSummary()
							.getLocalName();
					String officialName = this.uriResolver.idToName(
							lexgridName, IdType.CODE_SYSTEM);
					if (StringUtils.isNotBlank(officialName)) {
						this.aliasToLexGridMap.put(officialName, lexgridName);
						
						String uri = this.uriResolver.idToUri(officialName, IdType.CODE_SYSTEM);
						String baseUri = this.uriResolver.idToBaseUri(officialName);
						
						if(StringUtils.isNotBlank(uri)){
							this.aliasToLexGridMap.put(uri, lexgridName);
						}
						if(StringUtils.isNotBlank(baseUri)){
							this.aliasToLexGridMap.put(baseUri, lexgridName);
						}

						this.lexgridToAliasMap.put(lexgridName, officialName);
						
					}
					this.lexgridUriToLexGridNameMap.put(csr.getCodingSchemeSummary().getCodingSchemeURI(), csr.getCodingSchemeSummary().getLocalName());
				}
			} catch (LBInvocationException e) {
				this.log.warn(e);
			}
		}
	}

	@Override
	public String translateFromLexGrid(String lexgridName) {
		synchronized (this.mutex) {
			String alias = this.lexgridToAliasMap.get(lexgridName);
			if (alias != null) {
				return alias;
			} else {
				return lexgridName;
			}
		}
	}

	@Override
	public String translateToLexGrid(String alias) {
		synchronized (this.mutex) {
			String lexgridName = this.aliasToLexGridMap.get(alias);
			if (lexgridName != null) {
				return lexgridName;
			} else {
				return alias;
			}
		}
	}

	@Override
	public String translateLexGridURIToLexGrid(String uri) {
		synchronized (this.mutex) {
			String lexgridName = this.lexgridUriToLexGridNameMap.get(uri);
			if (lexgridName != null) {
				return lexgridName;
			} else {
				return uri;
			}
		}
	}
	
	protected void scheduleflushCache() {
		TimerTask flushTask = new TimerTask() {
			@Override
			public void run() {
				buildCaches();
			}
		};

		Timer timer = new Timer();

		int minutesInMillis = FLUSH_PERIOD_MINUTES * 60 * 1000;
		timer.schedule(flushTask, 0, minutesInMillis);
	}

	@Override
	public void onChange() {
		this.buildCaches();
	}

}
