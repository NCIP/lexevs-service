package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

public class LexEvsUtils {

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
	
	public static Set<ResolvedFilter> createFilterSet(LexEvsFakeData fakeData, int aboutIndex, int synopsisIndex,
			int nameIndex) {
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();
		
		PropertyReference propertyReference = StandardModelAttributeReference.ABOUT.getPropertyReference();
		MatchAlgorithmReference matchAlgorithmReference = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference();
		String matchValue = fakeData.getScheme_DataField(aboutIndex, propertyReference);
		ResolvedFilter filter1 = LexEvsUtils.createFilter(propertyReference, matchAlgorithmReference, matchValue);
					
		filters.add(filter1);
		
		propertyReference = StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference();
		matchAlgorithmReference = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference();
		matchValue = fakeData.getScheme_DataField(synopsisIndex, propertyReference);
		ResolvedFilter filter2 = LexEvsUtils.createFilter(propertyReference, matchAlgorithmReference, matchValue);
					
		filters.add(filter2);
		
		propertyReference = StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference();
		matchAlgorithmReference = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference();
		matchValue = fakeData.getScheme_DataField(nameIndex, propertyReference);
		ResolvedFilter filter3 = LexEvsUtils.createFilter(propertyReference, matchAlgorithmReference, matchValue);
					
		filters.add(filter3);
		
		return filters;
	}
	
	
}
