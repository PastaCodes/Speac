package org.speac.errors;

import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacStaticError;
import org.speac.core.types.language.CoreFunction;
import org.speac.core.types.language.CoreModule;

public final class CoreErrors {
	/**
	 * When a {@link CoreFunction} is called, some java code is executed
	 * if an exception is raised while running this code, it can be helpful to inform the user through a SpeacError.
	 * This normally shouldn't happen as it suggests that the {@link CoreModule} the code
	 * resides in, was implemented incorrectly
	 */
	public static final SpeacStaticError CORE_MODULE_EXCEPTION = new SpeacStaticError(
			"Core Module Exception",
			"An exception was thrown by a core module; view console for more details.",
			"Please report this problem to the author of the core module."
	);
	public static final SpeacStaticError UNEXPECTED_INDENTATION = new SpeacStaticError(
			"Unexpected Indentation",
			SpeacError.NO_DESCRIPTION,
			"Please remove this indentation."
	);
	/**
	 * Is raised when an instruction contains an undefined function
	 * On the other hand {@link CoreModuleErrors#UNKNOWN_FUNCTION_NAME} is raised when an undefined function is mentioned in a string
	 */
	public static final SpeacStaticError UNDEFINED_FUNCTION = new SpeacStaticError(
			"Undefined Function",
			"The specified function is not defined in the current scope.",
			"Please define this function or use a defined function in its place."
	);
	/**
	 * Is raised when an instruction contains an undefined variable
	 * On the other hand {@link CoreModuleErrors#UNKNOWN_VARIABLE_NAME} is raised when an undefined variable is mentioned in a string
	 */
	public static final SpeacStaticError UNDEFINED_VARIABLE = new SpeacStaticError(
			"Undefined Variable",
			"The specified variable is not defined in the current scope.",
			"Please define this variable or use a defined variable in its place."
	);
}
