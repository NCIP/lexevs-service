/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
		if (StringUtils.startsWithIgnoreCase(uri, URN_PREFIX)) {
			return uri + URN_SEPARATOR + tokenToAppend;
		} else {
			return uri + DEFAULT_SEPARATOR + tokenToAppend;
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

	private static int getSeparatorPosition(String string) {
		char[] chars = string.toCharArray();

		for (int i = chars.length - 1; i > 0; i--) {
			if (SEPARATORS.contains(chars[i])) {
				return i;
			}
		}

		throw new IllegalStateException();
	}
}
