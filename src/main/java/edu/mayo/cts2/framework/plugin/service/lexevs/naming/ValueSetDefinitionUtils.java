package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public final class ValueSetDefinitionUtils {
	
	private ValueSetDefinitionUtils(){
		super();
	}

	public static String getValueSetDefinitionLocalId(String definitionUri){
		return 
			org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(definitionUri.getBytes()).substring(0, 4);
	}
	
}
