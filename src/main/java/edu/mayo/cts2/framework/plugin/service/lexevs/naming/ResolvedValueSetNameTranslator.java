package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public interface ResolvedValueSetNameTranslator {

	public static final String RESOLVED_VS_LOCAL_ID = "1";
	
	public NameVersionPair getNameVersionPair(ResolvedValueSetNameTriple resolvedValueSetNameTriple);

	public ResolvedValueSetNameTriple getResolvedValueSetNameTriple(
			String codingSchemeURI);

}
