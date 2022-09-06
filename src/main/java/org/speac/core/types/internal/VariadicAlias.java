package org.speac.core.types.internal;

import org.speac.core.types.language.Prototype;
import org.speac.utilities.FixedList;

public class VariadicAlias extends FunctionAlias {
	public static final FixedList<Integer> VARIADIC_CORRESPONDENCE = null;

	public VariadicAlias(Prototype alternativeName) {
		super(alternativeName, VariadicAlias.VARIADIC_CORRESPONDENCE);
	}
}
