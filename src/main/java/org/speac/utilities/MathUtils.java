package org.speac.utilities;

public final class MathUtils {
	public static double trim(double input, int decimalPlaces) {
		double fac = Math.pow(10, decimalPlaces);
		return Math.round((double)(int) (input * fac)) / fac;
	}

	public static double round(double input, int decimalPlaces) {
		double fac = Math.pow(10, decimalPlaces);
		return Math.round((input * fac)) / fac;
	}
}
