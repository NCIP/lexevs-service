package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;

import scala.actors.threadpool.Arrays;

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
		
		public int index(){
			return index;
		}
	}
	
	private final static String [][] DEFAULT_DATA = {
		{"11.11.0.1", "Auto", "Automobiles", "1.0"},
		{"9.0.0.1", "Car", "Vehicles", "1.0"},
		{"13.11.0.2", "Auto3", "Automobiles", "1.1"},
		{"1.2.3.4", "2Auto", "automobiles", "1.0"},
		{"5.6.7.8", "auto", "vehicles", "1.0"},
		{"7.6.5.4", "utoA", "hicle", "1.0"}
	};
	private final static int CODESYSTEM_FIELDCOUNT = DataFields.values().length;
	
	private List<String[]> codeSystemList = null;
	
	private int codeSystemCount = 0;
	
	
	@SuppressWarnings("unchecked")
	public LexEvsFakeData(){
		codeSystemList = Arrays.asList(DEFAULT_DATA);
		
		this.codeSystemCount = codeSystemList.size();
	}
	
	public LexEvsFakeData(int size){
		this.codeSystemCount = (size <= DEFAULT_DATA.length) ? size : DEFAULT_DATA.length;
		codeSystemList = new ArrayList<String[]>();
		for(int i=0; i < this.codeSystemCount; i++){
			codeSystemList.add(new String[CODESYSTEM_FIELDCOUNT]);
			this.setFields(i, DEFAULT_DATA[i]);
		}
	}

	public int size(){
		return this.codeSystemCount;
	}
	
	private void setFields(int index, String [] values){
		DataFields[] fields = DataFields.values();
		if(index < this.codeSystemList.size()){
			for(int i=0; i < fields.length; i++){
				this.codeSystemList.get(index)[fields[i].index()] = values[i];
			}
		}
	}
	
	public String getAbout(int i){
		String results = null;
		if(i < this.codeSystemCount){
			results = this.codeSystemList.get(i)[DataFields.ABOUT.index()];
		}
		return results;
	}

	public String getResourceSynopsis(int i){
		String results = null;
		if(i < this.codeSystemCount){
			results = this.codeSystemList.get(i)[DataFields.RESOURCE_SYNOPSIS.index()];
		}
		return results;
	}
	
	public String getResourceLocalName(int i){
		String results = null;
		if(i < this.codeSystemCount){
			results = this.codeSystemList.get(i)[DataFields.RESOURCE_LOCALNAME.index()];
		}
		return results;
	}
	
	public String getResourceVersion(int i){
		String results = null;
		if(i < this.codeSystemCount){
			results = this.codeSystemList.get(i)[DataFields.RESOURCE_VERSION.index()];
		}
		return results;
	}
	
	public String getResourceName(int i){
		String results = null;
		if(i < this.codeSystemCount){
			results = this.getResourceLocalName(i);
			results += "-";
			results += this.getResourceVersion(i); 
		}
		return results;
	}

	public int getAbout_ContainsCount(String testValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if(this.getAbout(i).toLowerCase().contains(testValue.toLowerCase())){ 
				count++;
			}
		}
		return count;
	}

	public int getResourceSynopsis_StartWithCount(String testValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if(this.getResourceSynopsis(i).toLowerCase().startsWith(testValue.toLowerCase())){
				count++;
			}
		}
		return count;
	}

	public int getResourceName_ExactMatchCount(String testValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if(this.getResourceName(i).toLowerCase().equals(testValue.toLowerCase())){
				count++;
			}
		}
		return count;
	}

	public int getAllFilters_Count(String aboutValue, String synopsisValue, String nameValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if (this.getAbout(i).toLowerCase().contains(aboutValue.toLowerCase())
					&& this.getResourceSynopsis(i).toLowerCase().startsWith(synopsisValue.toLowerCase())
					&& this.getResourceName(i).toLowerCase().equals(nameValue.toLowerCase())) {
				count++;
			}
		}
		return count;
	}
}
