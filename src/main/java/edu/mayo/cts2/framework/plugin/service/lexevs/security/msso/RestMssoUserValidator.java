/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.security.msso;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestMssoUserValidator implements MssoUserValidator {

	private String validationUrl = "https://mssotools.com/SOAPvalidate.asp?Subscriber_ID=";
	
	final Pattern pattern = Pattern.compile("<validation>(.+?)</validation>");
	
	private RestTemplate restClient = new RestTemplate();
	
	private static final int FLUSH_PERIOD_MINUTES = 60;
	
	private Object mutex = new Object();
	
	private Map<String,Boolean> tokenCache = new HashMap<String,Boolean>();
	
	@Override
	public boolean isValid(String userToken) {
		synchronized(this.mutex){
			if(! this.tokenCache.containsKey(userToken)){
				boolean response = this.makeRestCall(userToken);
				this.tokenCache.put(userToken, response);
			}
			
			return this.tokenCache.get(userToken);
		}
	}
	
	protected boolean makeRestCall(String token){
		String responseXml;
		try {
			responseXml = this.restClient.getForObject(validationUrl + token, String.class);
		} catch (HttpServerErrorException e){
			//error (probably from an invalid (non-number) token.
			return false;
		}

		String responseBoolean = this.getResponseBoolean(responseXml);
		
		return BooleanUtils.toBoolean(responseBoolean);
	}
	
	protected String getResponseBoolean(String xml){
		final Matcher matcher = this.pattern.matcher(xml);
		
		matcher.find();
		
		return matcher.group(1).trim();
	}
	
	protected void scheduleflushCache() {
		TimerTask flushTask = new TimerTask() {
			@Override
			public void run() {
				synchronized(mutex){
					tokenCache.clear();
				}
			}
		};

		Timer timer = new Timer();

		int minutesInMillis = FLUSH_PERIOD_MINUTES * 60 * 1000;
		timer.schedule(flushTask, 0, minutesInMillis);
	}

}
