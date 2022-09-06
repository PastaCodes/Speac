package org.speac.core.types.language;

import org.speac.core.types.internal.TokenizedName;
import org.speac.native_module.FunctionsHandling;
import org.speac.utilities.FixedList;

/**
 * Refers to a function implemented in Speac using the {@link FunctionsHandling#DEFINE} function
 * @see Function the union of org.speac functions and core functions
 * @see CoreFunction the only other type of function
 */
public class SpeacFunction extends Function {
	public final FixedList<TokenizedName> parameters; // Names of the parameters
	public final CodeBlock body; // The code and scope associated to this function

	public SpeacFunction(FixedList<TokenizedName> parameters, CodeBlock body) {
		super();
		this.parameters = parameters;
		this.body = body;
	}
	public SpeacFunction(Prototype prototype, FixedList<TokenizedName> parameters, CodeBlock body) {
		super(prototype);
		this.parameters = parameters;
		this.body = body;
	}
}
