package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public final class UriUtils {

	private static final Set<Character> SEPARATORS = 
		new HashSet<Character>(Arrays.asList(':', '#', '/'));

	private static final String URN_PREFIX = "urn:";

	private static final String URN_SEPARATOR = ":";
	private static final String DEFAULT_SEPARATOR = "/";

	private UriUtils() {
		super();
	}

	public static String combine(String uri, String tokenToAppend) {
		if (StringUtils.startsWithIgnoreCase(uri, URN_PREFIX)) {
			return uri + URN_SEPARATOR + tokenToAppend;
		} else {
			return uri + DEFAULT_SEPARATOR + tokenToAppend;
		}
	}

	public static String getLocalPart(String uri) {
		int separator = getSeparatorPosition(uri);

		return StringUtils.substring(uri, separator + 1);
	}

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
