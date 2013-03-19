package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

public class LexEvsFakeData {	
	public enum DataFields{
		ABOUT (0),
		RESOURCE_SYNOPSIS (1),
		RESOURCE_LOCALNAME (2),
		RESOURCE_VERSION (3);
		
		private int index;
		
		DataFields(int index){
			this.index = index;
		}
	}
	
	private final static String [][][][] CODESYSTEMS = {
		{{{"11.11.0.1"}}, {{"Auto"}}, {{"Automobiles"}}, {{"1.0"}}},
		{{{"9.0.0.1"}}, {{"Car"}}, {{"Vehicles"}}, {{"1.0"}}},
		{{{"13.11.0.2"}}, {{"Auto3"}}, {{"Automobiles"}}, {{"1.1"}}},
		{{{"1.2.3.4"}}, {{"2Auto"}}, {{"automobiles"}}, {{"1.0"}}},
		{{{"5.6.7.8"}}, {{"auto"}}, {{"vehicles"}}, {{"1.0"}}},
		{{{"7.6.5.4"}}, {{"utoA"}}, {{"hicle"}}, {{"1.0"}}}
	};
	
	private final int CODESYSTEM_COUNT = CODESYSTEMS.length;

	public int getCodeSystemCount(){
		return CODESYSTEM_COUNT;
	}
	
	public String getAbout(int i){
		return CODESYSTEMS[i];
	}
	
	public String getResourceSynopsis(int i){
		
	}
	
	public String getResourceLocalName(int i){
		
	}
	
	public String getResourceVersion(int i){
		
	}
	
	public String getResourceName(int i){
		String resourceName = LOCALNAME_VALUES[nameIndex] + ":" + nameIndex + "-" + 
	   			  VERSION_VALUES[nameIndex] + ":" + nameIndex;

	}
}
