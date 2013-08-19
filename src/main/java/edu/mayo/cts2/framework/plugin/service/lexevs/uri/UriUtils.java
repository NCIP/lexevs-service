/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * A Utility class for handling CTS2/LexGrid URIs.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public final class UriUtils {

	private static final Set<Character> SEPARATORS = 
		new HashSet<Character>(Arrays.asList(':', '#', '/'));

	private static final String URN_PREFIX = "urn:";

	private static final String URN_SEPARATOR = ":";
	
	/**
	 * The Constant DEFAULT_SEPARATOR.
	 */
	private static final String DEFAULT_SEPARATOR = "/";

	private UriUtils() {
		super();
	}

	/**
	 * Combine a URI and another token/string.
	 *
	 * @param uri the uri
	 * @param tokenToAppend the token to append
	 * @return the string
	 */
	public static String combine(String uri, String tokenToAppend) {
		char lastChar = uri.charAt(uri.length() - 1);
		if(SEPARATORS.contains(lastChar)){
			return uri + tokenToAppend;
		} else {
			if (StringUtils.startsWithIgnoreCase(uri, URN_PREFIX)) {
				return uri + URN_SEPARATOR + tokenToAppend;
			} else {
				return uri + DEFAULT_SEPARATOR + tokenToAppend;
			}
		}
	}

	/**
	 * Gets the local part of a URI.
	 *
	 * @param uri the uri
	 * @return the local part
	 */
	public static String getLocalPart(String uri) {
		int separator = getSeparatorPosition(uri);

		return StringUtils.substring(uri, separator + 1);
	}

	/**
	 * Gets the namespace part of a URI.
	 *
	 * @param uri the uri
	 * @return the namespace
	 */
	public static String getNamespace(String uri) {
		int separator = getSeparatorPosition(uri);

		return StringUtils.substring(uri, 0, separator);
	}
	
	public static char getSeparator(String uri) {
		int separator = getSeparatorPosition(uri);

		return uri.charAt(separator);
	}

	private static int getSeparatorPosition(String string) {
		char[] chars = string.toCharArray();

		for (int i = chars.length - 1; i > 0; i--) {
			if (SEPARATORS.contains(chars[i])) {
				return i;
			}
		}

		throw new BadUriException("Malformed URI: " + string);
	}
	
	public static class BadUriException extends IllegalArgumentException {

		private static final long serialVersionUID = 7847250670382555646L;
		
		private BadUriException(String message){
			super(message);
		}
		
	}
}
