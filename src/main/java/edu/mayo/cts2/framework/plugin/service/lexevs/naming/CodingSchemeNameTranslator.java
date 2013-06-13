package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public interface CodingSchemeNameTranslator {

	/**
	 * Translate the LexEVS CodingScheme Name into a CTS2 CodeSystem Name.
	 * 
	 * Usually, a LexEVS CodingScheme Name is also an appropriate CTS2
	 * CodeSystem Name, but there may be things we'd like to check.
	 * 
	 * For instance:
	 * 1) Check with the URI Resolver to see if there is an "official" name.
	 * 2) Pick a shorter name, or a name with no spaces or special characters,
	 * that will be more appropriate to have in a URL.
	 *
	 * @param name The LexEVS CodingScheme Name.
	 * @return The translated Name. This name must be guaranteed to be usable by
	 * LexEVS for lookup. NOTE that the name may be the same as was input, if no
	 * better alternatives are found.
	 */
	public String translate(String name);
}
