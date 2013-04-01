package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

public class FakeCodedNode {
	String codingScheme;
	String version;
	
	public FakeCodedNode(String codingScheme, String version) {
		this.codingScheme = codingScheme;
		this.version = version;
	}
	
	public String getCodingScheme(){
		return this.codingScheme;
	}
	
	public String getVersion(){
		return this.version;
	}

}
