/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.ComponentReference;
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
		return createQuery(
				matchAlgorithmReference, 
				matchValue, 
				codeSystemVersion,
				StandardModelAttributeReference.RESOURCE_SYNOPSIS.getComponentReference());
	}
	
	public static EntityDescriptionQuery createQuery(String matchAlgorithmReference, String matchValue, String codeSystemVersion, ComponentReference componentReference){
		// Create filters for query
		// ------------------------
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();	
		ResolvedFilter filter = CommonTestUtils.createFilter(matchAlgorithmReference,  matchValue, componentReference);				
		filters.add(filter);
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(codeSystemVersion));
		
		
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);
		return query;
	}

	public static ResolvedFilter createFilter(String matchAlgorithmReference, String matchValue, ComponentReference componentReference){
		ResolvedFilter filter = new ResolvedFilter();			
		filter.setMatchAlgorithmReference(new MatchAlgorithmReference(matchAlgorithmReference));
		filter.setMatchValue(matchValue);
		filter.setComponentReference(componentReference);				// Should this field be used??			
		return filter;
	}



	
	// FILTER METHODS
	public static ResolvedFilter createFilter(ComponentReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setComponentReference(property);
		
		return filter;
	}

	public static Set<ResolvedFilter> createFilterSet(ComponentReference property, MatchAlgorithmReference algorithm, String matchValue){
		ResolvedFilter filter = new ResolvedFilter();
		filter.setMatchValue(matchValue);
		filter.setMatchAlgorithmReference(algorithm);
		filter.setComponentReference(property);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(filter));
		
		return filterSet;
	}
	
	public static Set<ResolvedFilter> createFilterSet(String about_contains, String resourceSynopsis_startsWith, String resourceName_exactMatch){
		ResolvedFilter aboutFilter = createFilter(
				StandardModelAttributeReference.ABOUT.getComponentReference(),
				StandardMatchAlgorithmReference.CONTAINS
						.getMatchAlgorithmReference(), about_contains);

		ResolvedFilter synopsisFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_SYNOPSIS
						.getComponentReference(),
				StandardMatchAlgorithmReference.STARTS_WITH
						.getMatchAlgorithmReference(), resourceSynopsis_startsWith);

		ResolvedFilter nameFilter = createFilter(
				StandardModelAttributeReference.RESOURCE_NAME
						.getComponentReference(),
				StandardMatchAlgorithmReference.EXACT_MATCH
						.getMatchAlgorithmReference(), resourceName_exactMatch);
		
		Set<ResolvedFilter> filterSet = new HashSet<ResolvedFilter>(
				Arrays.asList(aboutFilter, synopsisFilter, nameFilter));
		
		return filterSet;
	}	
}
