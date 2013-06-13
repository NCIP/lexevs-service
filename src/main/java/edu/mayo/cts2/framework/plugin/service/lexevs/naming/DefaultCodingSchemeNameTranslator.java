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

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriResolver.IdType;

@Component
public class DefaultCodingSchemeNameTranslator implements CodingSchemeNameTranslator {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Resource
	private UriResolver uriResolver;
	
	@Resource
	private LexBIGService lexBigService;

	/**
	 * Translate the LexEVS CodingScheme Name into a CTS2 CodeSystem Name.
	 * 
	 * Usually, a LexEVS CodingScheme Name is also an appropriate CTS2
	 * CodeSystem Name, but there may be things we'd like to check.
	 * 
	 * For instance:
	 * 1) Check with the URI Resolver to see if there is an "official" name.
	 * 2) Pick a shorter name, or a name with no spaces or special characters,
	 * that will be more appropriate to have in a URL.
	 *
	 * @param name The LexEVS CodingScheme Name.
	 * @return The translated Name. This name must be guaranteed to be usable by
	 * LexEVS for lookup. NOTE that the name may be the same as was input, if no
	 * better alternatives are found.
	 */
	@Override
	public String translate(String name){
		String officialName = this.uriResolver.idToName(name, IdType.CODE_SYSTEM);
		CodingScheme cs;
		try {
			cs = this.lexBigService.resolveCodingScheme(name, null);
		} catch (LBException e) {
			log.warn(e);
			//we couldn't find it in LexEVS - return it as-is.
			return name;
		}
		
	
		if(StringUtils.isNotBlank(officialName)){
			//found a match... make sure it will resolve to the same CodeSystem
			try {
				CodingScheme checkCs = this.lexBigService.resolveCodingScheme(officialName, null);
				
				if(checkCs.getCodingSchemeURI().equals(cs.getCodingSchemeURI())){
					//they match -- return back the one from the URI Resolver.
					return officialName;
				}
			} catch (LBException e) {
				//we couldn't find it in LexEVS - must be a mismatch.
			}
		}
		
		//If we've gotten here, the URI Resolver either didn't help or gave us a mismatch.
		//The best we can do is return back the shortest local name.
		return this.getShortestLocalName(cs);
	}
	
	private String getShortestLocalName(CodingScheme cs){
		//start with the name
		String name = cs.getCodingSchemeName();
		
		for(String localName : cs.getLocalName()){
			if(StringUtils.isAlpha(localName) && (localName.length() < name.length())){
				name = localName;
			}
		}
		
		return name;
	}
}
