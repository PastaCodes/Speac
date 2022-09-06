package org.speac.utilities;

public class StringUtils {
	/**
	 * Converts a string from snake case (or screaming snake case) to kebab case.
	 *
	 * Examples:
	 * function_name -> function-name
	 * FUNCTION_NAME -> function-name
	 */
	public static String snakeCaseToKebabCase(String input) {
		return input.toLowerCase().replaceAll("_", "-");
	}

	public static boolean stringMatchesDouble(String test) {
		if (test == null)
			return false;
		try {
			Double.parseDouble(test);
			return true;
		} catch (NumberFormatException exception) {
			return false;
		}
	}

	public static boolean stringMatchesInteger(String test) {
		if (test == null)
			return false;
		try {
			Integer.parseInt(test);
			return true;
		} catch (NumberFormatException exception) {
			return false;
		}
	}
}
