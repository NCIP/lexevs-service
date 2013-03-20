package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.LexEvsFakeData.DataFields;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;

import scala.actors.threadpool.Arrays;

public class LexEvsFakeData {	
	public static enum DataFields{
		ABOUT (0),
		RESOURCE_SYNOPSIS (1),
		RESOURCE_LOCALNAME (2),
		RESOURCE_VERSION (3),
		RESOURCE_NAME (4);
		
		private int index;
		
		DataFields(int index){
			this.index = index;
		}
		
		public int index(){
			return index;
		}
	}
	
	private final static String [][] DEFAULT_DATA = {
		{"11.11.0.1", "Auto", "Automobiles", "1.0", ""},
		{"9.0.0.1", "Car", "Vehicles", "1.0", ""},
		{"13.11.0.2", "Auto3", "Automobiles", "1.1", ""},
		{"1.2.3.4", "2Auto", "automobiles", "1.0", ""},
		{"5.6.7.8", "auto", "vehicles", "1.0", ""},
		{"7.6.5.4", "utoA", "hicle", "1.0", ""}
	};
	private final static int CODESYSTEM_FIELDCOUNT = DataFields.values().length;
	
	private List<String[]> codeSystemList = null;
	
	private int codeSystemCount = 0;
	
	private void initializeDefaultData(){
		for(int i=0; i < DEFAULT_DATA.length; i++){
			DEFAULT_DATA[i][DataFields.RESOURCE_NAME.index()] = DEFAULT_DATA[i][DataFields.RESOURCE_LOCALNAME.index()];
			DEFAULT_DATA[i][DataFields.RESOURCE_NAME.index()] += "-";
			DEFAULT_DATA[i][DataFields.RESOURCE_NAME.index()] += DEFAULT_DATA[i][DataFields.RESOURCE_VERSION.index()];
		}
	}
	
	@SuppressWarnings("unchecked")
	public LexEvsFakeData() throws IOException{
		initializeDefaultData();
		codeSystemList = Arrays.asList(DEFAULT_DATA);
		
		this.codeSystemCount = codeSystemList.size();
	}
	
	public LexEvsFakeData(int size){
		initializeDefaultData();
		this.codeSystemCount = (size <= DEFAULT_DATA.length) ? size : DEFAULT_DATA.length;
		codeSystemList = new ArrayList<String[]>();
		for(int i=0; i < this.codeSystemCount; i++){
			codeSystemList.add(new String[CODESYSTEM_FIELDCOUNT]);
			this.setFields(i, DEFAULT_DATA[i]);
		}
	}

	public LexEvsFakeData(String [][] data){
		this.codeSystemCount = data.length;
		codeSystemList = new ArrayList<String[]>();
		for(int i=0; i < this.codeSystemCount; i++){
			codeSystemList.add(new String[CODESYSTEM_FIELDCOUNT]);
			this.setFields(i, data[i]);
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
	
	public String get(DataFields field, int i){
		String results = null;
		if(i < this.codeSystemCount){
			results = this.codeSystemList.get(i)[field.index()];
		}
		return results;
	}
	
//	public String getAbout(int i){
//		String results = null;
//		if(i < this.codeSystemCount){
//			results = this.codeSystemList.get(i)[DataFields.ABOUT.index()];
//		}
//		return results;
//	}
//
//	public String getResourceSynopsis(int i){
//		String results = null;
//		if(i < this.codeSystemCount){
//			results = this.codeSystemList.get(i)[DataFields.RESOURCE_SYNOPSIS.index()];
//		}
//		return results;
//	}
//	
//	public String getResourceLocalName(int i){
//		String results = null;
//		if(i < this.codeSystemCount){
//			results = this.codeSystemList.get(i)[DataFields.RESOURCE_LOCALNAME.index()];
//		}
//		return results;
//	}
//	
//	public String getResourceVersion(int i){
//		String results = null;
//		if(i < this.codeSystemCount){
//			results = this.codeSystemList.get(i)[DataFields.RESOURCE_VERSION.index()];
//		}
//		return results;
//	}
//	
//	public String getResourceName(int i){
//		String results = null;
//		if(i < this.codeSystemCount){
//			results = this.get(DataFields.RESOURCE_LOCALNAME, i);
//			results += "-";
//			results += this.get(DataFields.RESOURCE_VERSION, i); 
//		}
//		return results;
//	}
	
	public int getAbout_ContainsCount(String testValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if(this.get(DataFields.ABOUT, i).toLowerCase().contains(testValue.toLowerCase())){ 
				count++;
			}
		}
		return count;
	}

	public int getResourceSynopsis_StartWithCount(String testValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if(this.get(DataFields.RESOURCE_SYNOPSIS, i).toLowerCase().startsWith(testValue.toLowerCase())){
				count++;
			}
		}
		return count;
	}

	public int getResourceName_ExactMatchCount(String testValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if(this.get(DataFields.RESOURCE_NAME, i).toLowerCase().equals(testValue.toLowerCase())){
				count++;
			}
		}
		return count;
	}

	public int getAllFilters_Count(String aboutValue, String synopsisValue, String nameValue) {
		int count = 0;
		for(int i=0; i < this.codeSystemCount; i++){
			if (this.get(DataFields.ABOUT, i).toLowerCase().contains(aboutValue.toLowerCase())
					&& this.get(DataFields.RESOURCE_SYNOPSIS, i).toLowerCase().startsWith(synopsisValue.toLowerCase())
					&& this.get(DataFields.RESOURCE_NAME, i).toLowerCase().equals(nameValue.toLowerCase())) {
				count++;
			}
		}
		return count;
	}

	public int getCount(DataFields field,
			MatchAlgorithmReference matchAlgorithmReference, String testValue) {
		return 0;
	}			
//	public int getCount(DataFields field,
//			MatchAlgorithmReference matchAlgorithmReference, String testValue) {
//		int count = 0;
//		for(int i=0; i < this.codeSystemCount; i++){
//			String fieldValue = this.get(field, i).toLowerCase();
//			testValue = testValue.toLowerCase();
//			if(StandardMatchAlgorithmReference.EXACT_MATCH.equals(matchAlgorithmReference)){
//				if(this.get(field, i).toLowerCase().startsWith(testValue.toLowerCase())){
//					count++;
//				}
//		}
//		return count;
//	}
}
