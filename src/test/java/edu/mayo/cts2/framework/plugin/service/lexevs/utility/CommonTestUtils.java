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
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.EntityDescriptionQueryImpl;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class CommonTestUtils {
	public final static String [] VALID_URI_NAMES = {"Automobiles", "urn:oid:11.11.0.1"};
	public final static String [] VALID_VERSIONS = {"1.0"};
	
	public static String getValidNameAndVersion(int index){
		if(0 <= index && index < VALID_URI_NAMES.length){
			return VALID_URI_NAMES[index] + "-" + VALID_VERSIONS[index];
		}
		return null;
	}
	
	public static ArrayList<String> createInvalidNameURIs(int index){
		ArrayList<String> results = new ArrayList<String>();
		
		if(0 <= index && index < VALID_URI_NAMES.length){
			String name = VALID_URI_NAMES[index];
			String version = VALID_VERSIONS[index];
			
			results.add(name + version); 				//			MISSING_DASH ("Automobiles1.0"),
			results.add(name + " " + version);			//			SPACE_INSTEAD_OF_DASH("Automobiles 1.0"),
			results.add(name + "," + version); 			//			COMMA_INSTEAD_OF_DASH("Automobiles,1.0"),
			results.add(name + "-");					//			MISSING_VERSION_WITH_DASH("Automobiles-"),
			results.add(name);							//			MISSING_VERSION_WITHOUT_DASH("Automobiles"),
			results.add(name + "FOO-" + version);		//			NAME_WRONG("AutomobilesFOO-1.0"),
			results.add(name + "-" + version + "444");	//			VERSION_WRONG("Automobiles-1.0444");
		}
		return results;
	}

	public static String createValidValuesMessage(String values){
		return "Searching for (" + values + ") and should be found";
	}
	public static String createInvalidValuesMessage(String values){
		return "Searching for (" +  values + ") and should NOT be found.";
	}
	
	public static String createNullValueMessage(String field){
		return "Searching for NULL " + field + " and should NOT be found.";
	}
	
	public static EntityDescriptionQuery createQuery(String matchAlgorithmReference, String matchValue, String codeSystemVersion){
		// Create filters for query
		// ------------------------
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();	
		ResolvedFilter filter = CommonTestUtils.createFilter(matchAlgorithmReference,  matchValue, null);				
		filters.add(filter);
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName(codeSystemVersion));
		
		
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);
		return query;
	}

	public static ResolvedFilter createFilter(String matchAlgorithmReference, String matchValue, PropertyReference propertyReference){
		ResolvedFilter filter = new ResolvedFilter();			
		filter.setMatchAlgorithmReference(new MatchAlgorithmReference(matchAlgorithmReference));
		filter.setMatchValue(matchValue);
		filter.setPropertyReference(propertyReference);				// Should this field be used??			
		return filter;
	}



	
	// FILTER METHODS
	public static ResolvedFilter createFilter(PropertyReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setPropertyReference(property);
		
		return filter;
	}

	public static Set<ResolvedFilter> createFilterSet(PropertyReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setPropertyReference(property);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(filter));
		
		return filterSet;
	}
	
	public static Set<ResolvedFilter> createFilterSet(String about_contains, String resourceSynopsis_startsWith, String resourceName_exactMatch){
		ResolvedFilter aboutFilter = createFilter(
				StandardModelAttributeReference.ABOUT.getPropertyReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(), about_contains);

		ResolvedFilter synopsisFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_SYNOPSIS
						.getPropertyReference(),
				StandardMatchAlgorithmReference.STARTS_WITH
						.getMatchAlgorithmReference(), resourceSynopsis_startsWith);

		ResolvedFilter nameFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_NAME
						.getPropertyReference(),
				StandardMatchAlgorithmReference.EXACT_MATCH
						.getMatchAlgorithmReference(), resourceName_exactMatch);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(aboutFilter, synopsisFilter, nameFilter));
		
		return filterSet;
	}	
}
