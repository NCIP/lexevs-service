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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

@Component
public class DefaultCodingSchemeNameTranslator implements
		CodingSchemeNameTranslator, InitializingBean {

	private static final int FLUSH_PERIOD_MINUTES = 60;

	private Logger log = Logger.getLogger(this.getClass());

	@Resource
	private UriResolver uriResolver;

	@Resource
	private LexBIGService lexBigService;

	private Map<String, String> lexgridToAliasMap = new HashMap<String, String>();
	private Map<String, String> aliasToLexGridMap = new HashMap<String, String>();

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
					String lexgridName = csr.getCodingSchemeSummary()
							.getLocalName();
					String officialName = this.uriResolver.idToName(
							lexgridName, IdType.CODE_SYSTEM);
					if (StringUtils.isNotBlank(officialName)) {
						this.aliasToLexGridMap.put(officialName, lexgridName);
						this.lexgridToAliasMap.put(lexgridName, officialName);
					}
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
