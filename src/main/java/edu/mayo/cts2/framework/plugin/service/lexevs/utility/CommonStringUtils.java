package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

public class CommonStringUtils {
	public static boolean searchContains(String sourceValue, String searchValue, boolean caseSensitive) {
		if (caseSensitive) {
			if (sourceValue.indexOf(searchValue) != -1) {
				return true;
			}
		} else {
			if (sourceValue.toLowerCase().indexOf(searchValue.toLowerCase()) != -1) {
				return true;
			}						
		}
		return false;
	}

	public static boolean searchExactMatch(String sourceValue, String searchValue, boolean caseSensitive) {
		if (caseSensitive) {
			if (sourceValue.equals(searchValue)) {
				return true;
			}
		} else {
			if (sourceValue.equalsIgnoreCase(searchValue)) {
				return true;
			}						
		}
		return false;
	}


	public static boolean searchStartsWith(String sourceValue, String searchValue, boolean caseSensitive) {
		if (caseSensitive) {
			if (sourceValue.startsWith(searchValue)) {
				return true;
			}
		} else {
			if (sourceValue.toLowerCase().startsWith(searchValue.toLowerCase())) {
				return true;
			}						
		}
		return false;
	}
}
