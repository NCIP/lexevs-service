/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;

@Component
public class CodeSystemVersionUriResolver implements InitializingBean {

	private static final int FLUSH_PERIOD_MINUTES = 60;

	private Logger log = LogManager.getLogger(this.getClass());

	@Resource
	private UriHandler uriHandler;
	
	@Resource
	private UriResolver uriResolver;

	@Resource
	private LexBIGService lexBigService;

	private Map<String, NameVersionPair> uriToNameVersionPair = new HashMap<String, NameVersionPair>();

	private Object mutex = new Object();

	@Override
	public void afterPropertiesSet() {
		this.scheduleflushCache();
	}

	protected void buildCaches() {
		synchronized(this.mutex){
			try {
				for (CodingSchemeRendering csr : this.lexBigService
						.getSupportedCodingSchemes().getCodingSchemeRendering()) {
					CodingSchemeSummary summary = csr.getCodingSchemeSummary();
					
					NameVersionPair nameVersionPair = 
						new NameVersionPair(
							summary.getLocalName(),
							summary.getRepresentsVersion());
					
					String codeSystemVersionUri = 
						this.uriHandler.getCodeSystemVersionUri(summary);
					
					this.uriToNameVersionPair.put(codeSystemVersionUri, nameVersionPair);
				}
			} catch (LBInvocationException e) {
				this.log.warn(e);
			}
		}
	}

	public NameVersionPair resolveUri(String codeSystemVersionUri) {
		synchronized (this.mutex) {
			return this.uriToNameVersionPair.get(codeSystemVersionUri);
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

}
