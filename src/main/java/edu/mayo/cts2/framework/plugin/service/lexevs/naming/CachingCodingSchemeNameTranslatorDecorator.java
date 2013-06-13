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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class CachingCodingSchemeNameTranslatorDecorator 
	implements CodingSchemeNameTranslator, InitializingBean {

	@Resource
	private CodingSchemeNameTranslator decorated;
	
	private Map<String,String> cache = new HashMap<String,String>();
	
	private static final int FLUSH_PERIOD_MINUTES = 60;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.scheduleflushCache();
	}

	/* 
	 * Not the most sophisticated locking, but we don't want to lock unless we
	 * have to. If entries are in the cache, we should be able to retrieve them
	 * in non-blocking fashion.
	 * 
	 * (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator#translate(java.lang.String)
	 */
	@Override
	public String translate(String name) {
		//try an optimistic get
		String translatedName = this.cache.get(name);
		
		if(translatedName == null){
			synchronized(this.cache){
				translatedName = this.decorated.translate(name);
				this.cache.put(name, translatedName);
			}
		}
		
		return translatedName;
	}
	
	protected void scheduleflushCache(){
		TimerTask flushTask = new TimerTask(){
			@Override
			public void run() {
				synchronized(cache){
					cache.clear();
				}
			}
		};
		
		Timer timer = new Timer();
		
		int minutesInMillis = FLUSH_PERIOD_MINUTES * 60 * 1000;
		timer.schedule(flushTask, minutesInMillis, minutesInMillis);
	}

}
