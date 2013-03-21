package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.service.meta.StandardMatchAlgorithmReference;
import edu.mayo.cts2.framework.service.meta.StandardModelAttributeReference;

import scala.actors.threadpool.Arrays;

public class LexEvsFakeData {	
	public static enum DataField{
		ABOUT (0, StandardModelAttributeReference.ABOUT.getPropertyReference()),
		RESOURCE_SYNOPSIS (1, StandardModelAttributeReference.RESOURCE_SYNOPSIS.getPropertyReference()),
		RESOURCE_LOCALNAME (2, null),
		RESOURCE_VERSION (3, null),
		RESOURCE_NAME (4, StandardModelAttributeReference.RESOURCE_NAME.getPropertyReference());
		
		private int index;
		private PropertyReference propertyReference;
		DataField(int index, PropertyReference propertyReference){
			this.index = index;
			this.propertyReference = propertyReference;
		}
		
		public int index(){
			return this.index;
		}
		
		public PropertyReference propertyReference(){
			return this.propertyReference;
		}
	}
	
	public static enum FakeMatchAlgorithmReference{
		CONTAINS (StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference()),
		STARTS_WITH (StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference()),
		EXACT_MATCH (StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference());
		
		MatchAlgorithmReference matchAlgorithmReference;
		
		FakeMatchAlgorithmReference(MatchAlgorithmReference ref){
			this.matchAlgorithmReference = ref;
		}
		
		public MatchAlgorithmReference getMatchAlgorithmReference(){
			return matchAlgorithmReference;
		}
	}
	
	public static class FakeDataFilter{
		DataField dataField;
		FakeMatchAlgorithmReference matchAlgorithmReference;
		
		public FakeDataFilter(DataField field, FakeMatchAlgorithmReference ref){
			this.dataField = field;
			this.matchAlgorithmReference = ref;
		}
		
		public DataField getDataField(){
			return dataField;
		}
		
		public FakeMatchAlgorithmReference getMatchAlgorithmReference(){
			return matchAlgorithmReference;
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
	
	private final static int CODESYSTEM_FIELDCOUNT = DataField.values().length;
	
	private List<String[]> codeSystemList = null;
	
	private int codeSystemCount = 0;
	
	private void initializeDefaultData(){
		for(int i=0; i < DEFAULT_DATA.length; i++){
			DEFAULT_DATA[i][DataField.RESOURCE_NAME.index()] = DEFAULT_DATA[i][DataField.RESOURCE_LOCALNAME.index()];
			DEFAULT_DATA[i][DataField.RESOURCE_NAME.index()] += "-";
			DEFAULT_DATA[i][DataField.RESOURCE_NAME.index()] += DEFAULT_DATA[i][DataField.RESOURCE_VERSION.index()];
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
		DataField[] fields = DataField.values();
		if(index < this.codeSystemList.size()){
			for(int i=0; i < fields.length; i++){
				this.codeSystemList.get(index)[fields[i].index()] = values[i];
			}
		}
	}
	
	public String getScheme_DataField(int schemeIndex, DataField dataField){
		String results = null;
		if(schemeIndex < this.codeSystemCount){
			results = this.codeSystemList.get(schemeIndex)[dataField.index()];
		}
		return results;
	}

	public String getScheme_DataField(int schemeIndex,
			PropertyReference propertyReference) {
		String results = null;
		if(schemeIndex < this.codeSystemCount){
			int fieldIndex = this.getPropertyReferenceIndex(propertyReference);
			
			results = this.codeSystemList.get(schemeIndex)[fieldIndex];
		}
		return results;
	}

	
	private int getPropertyReferenceIndex(PropertyReference propertyReference) {
		int index = 0;
		DataField [] fields = DataField.values();
		for(int i=0; i < fields.length; i++){
			PropertyReference ref = fields[i].propertyReference();
			if(ref != null){
				if(ref.equals(propertyReference)){
					index = i;
				}
			}
		}
		return index;
	}

	public int getCount(Set<ResolvedFilter> filters) {
		int count = 0;
		String exactMatch = StandardMatchAlgorithmReference.EXACT_MATCH.getMatchAlgorithmReference().getContent().toLowerCase();
		String contains = StandardMatchAlgorithmReference.CONTAINS.getMatchAlgorithmReference().getContent().toLowerCase();
		String startsWith = StandardMatchAlgorithmReference.STARTS_WITH.getMatchAlgorithmReference().getContent().toLowerCase();
		
		for(int schemeIndex=0; schemeIndex < this.codeSystemCount; schemeIndex++){
			boolean found = true;
			Iterator<ResolvedFilter> filterIterator = filters.iterator();
			while(found && filterIterator.hasNext()){
				ResolvedFilter filter = filterIterator.next();
				String matchAlgorithmReferenceName = filter.getMatchAlgorithmReference().getContent().toLowerCase();
				PropertyReference propertyReference = filter.getPropertyReference();
				String matchValue = filter.getMatchValue().toLowerCase();
				
				String dataValue = this.getScheme_DataField(schemeIndex, propertyReference).toLowerCase();
				if(matchAlgorithmReferenceName.equals(exactMatch)){
					if(dataValue.equals(matchValue) == false){
						found = false;
					}
				}
				else if(matchAlgorithmReferenceName.equals(contains)){
					if(dataValue.contains(matchValue) == false){
						found = false;
					}
				}
				else if(matchAlgorithmReferenceName.equals(startsWith)){
					if(dataValue.startsWith(matchValue) == false){
						found = false;
					}
				}
				else{
					found = false;
				}
				
			}
			if(found){
				count++;
			}
		}
		return count;
	}	
}
