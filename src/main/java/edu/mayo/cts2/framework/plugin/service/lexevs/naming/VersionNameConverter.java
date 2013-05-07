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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * CTS2 CodeSystemVersionNames will generally be in the form:
 * 
 * {Name}-{VersionId}
 * 
 * For example, SNOMEDCT-20120101
 * 
 * LexEVS will need this broken apart, for example
 * CodingSchemeName: SNOMEDCT, VersionId: 20120101
 */
@Component
public class VersionNameConverter {

	private static final String SEPARATOR = "-";
	
	/**
	 * To cts2 code system version name.
	 *
	 * @param lexEvsCodingSchemeName the lex evs coding scheme name
	 * @param version the version
	 * @return the string
	 */
	public String toCts2VersionName(String lexEvsCodingSchemeName, String version){
		return lexEvsCodingSchemeName + SEPARATOR + version;
	}
	
	/**
	 * From cts2 code system version name.
	 *
	 * @param cts2CodeSystemVersionName the cts2 code system version name
	 * @return the name version pair
	 * @throws InvaildVersionNameException 
	 */
	public NameVersionPair fromCts2VersionName(String cts2CodeSystemVersionName) throws InvaildVersionNameException{
		String[] nameParts = StringUtils.split(cts2CodeSystemVersionName, SEPARATOR);
		
		if(nameParts.length != 2){
			throw new InvaildVersionNameException(cts2CodeSystemVersionName);
		}
		
		return new NameVersionPair(nameParts[0], nameParts[1]);
	}
	
	public boolean isValidVersionName(String cts2CodeSystemVersionName){
		boolean answer = false;
		
		String[] nameParts = StringUtils.split(cts2CodeSystemVersionName, SEPARATOR);
		if(nameParts.length == 2){
			answer = true;
		}
		
		return answer;
	}
}
