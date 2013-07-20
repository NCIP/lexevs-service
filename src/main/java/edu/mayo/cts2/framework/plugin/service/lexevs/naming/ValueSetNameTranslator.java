package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public interface ValueSetNameTranslator {

	public String getDefinitionUri(String valueSetName, String definitionLocalId);
	
	public ValueSetNamePair getDefinitionNameAndVersion(String uri);

	public ValueSetNamePair getCurrentDefinition(String valueSetName);

}
