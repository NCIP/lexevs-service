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
		
		public int get(){
			return index;
		}
	}
	
	private final static String [][] CODESYSTEMS = {
		{"11.11.0.1", "Auto", "Automobiles", "1.0"},
		{"9.0.0.1", "Car", "Vehicles", "1.0"},
		{"13.11.0.2", "Auto3", "Automobiles", "1.1"},
		{"1.2.3.4", "2Auto", "automobiles", "1.0"},
		{"5.6.7.8", "auto", "vehicles", "1.0"},
		{"7.6.5.4", "utoA", "hicle", "1.0"}
	};
	
	private final int CODESYSTEM_COUNT = CODESYSTEMS.length;

	public int getCodeSystemCount(){
		return CODESYSTEM_COUNT;
	}
	
	public String getAbout(int i){
		String results = null;
		if(i < CODESYSTEM_COUNT){
			results = CODESYSTEMS[i][DataFields.ABOUT.get()];
		}
		return results;
	}
	
	public String getResourceSynopsis(int i){
		String results = null;
		if(i < CODESYSTEM_COUNT){
			results = CODESYSTEMS[i][DataFields.RESOURCE_SYNOPSIS.get()];
		}
		return results;
	}
	
	public String getResourceLocalName(int i){
		String results = null;
		if(i < CODESYSTEM_COUNT){
			results = CODESYSTEMS[i][DataFields.RESOURCE_LOCALNAME.get()];
		}
		return results;
	}
	
	public String getResourceVersion(int i){
		String results = null;
		if(i < CODESYSTEM_COUNT){
			results = CODESYSTEMS[i][DataFields.RESOURCE_VERSION.get()];
		}
		return results;
	}
	
	public String getResourceName(int i){
		String results = null;
		if(i < CODESYSTEM_COUNT){
			results = CODESYSTEMS[i][DataFields.RESOURCE_LOCALNAME.get()] + ":"; // + i;
			results += "-";
			results += CODESYSTEMS[i][DataFields.RESOURCE_VERSION.get()]; // + i;
		}
		return results;
	}

	public int getAboutContainsCount(int matchingCodingSchemeIndex, String testValue) {
		int count = 0;
		for(int i=0; i < CODESYSTEM_COUNT; i++){
			if(CODESYSTEMS[i][DataFields.ABOUT.get()].contains(testValue)){
				count++;
			}
		}
		return count;
	}

	public int getSynopsisStartWithCount(int matchingCodingSchemeIndex, String testValue) {
		int count = 0;
		for(int i=0; i < CODESYSTEM_COUNT; i++){
			if(CODESYSTEMS[i][DataFields.RESOURCE_SYNOPSIS.get()].startsWith(testValue)){
				count++;
			}
		}
		return count;
	}

	public int getNameExactMatchCount(int matchingCodingSchemeIndex, String testValue) {
		int count = 0;
		for(int i=0; i < CODESYSTEM_COUNT; i++){
			if(this.getResourceName(i).equals(testValue)){
				count++;
			}
		}
		return count;
	}

	public int getAllFiltersCount(int aboutIndex, int synopsisIndex, int nameIndex) {
		// TODO Auto-generated method stub
		return 0;
	}
}
