package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;

public class FakeRestrictToMatchingDesignations {
	String matchText;
	SearchDesignationOption option; 
	String matchAlgorithm;
	String language;
	
	public FakeRestrictToMatchingDesignations(String matchText,
			SearchDesignationOption option, String matchAlgorithm,
			String language){
		this.matchText = matchText;
		this.option = option;
		this.matchAlgorithm = matchAlgorithm;
		this.language = language;
	}
	
	public String getMatchText(){
		return this.matchText;
	}
	
	public SearchDesignationOption getOption(){
		return option;
	}
	
	public String getMatchAlgorithm(){
		return matchAlgorithm;
	}
	
	public String getLanguage(){
		return language;
	}
}
