package org.speac.errors;

import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacStaticError;
import org.speac.core.types.language.Data;
import org.speac.utilities.FixedList;

public class CoreModuleErrors {
	/**
	 * Raised when adding an alias to a function and
	 * either the new alias is variadic but the new one isn't
	 * or the old one is variadic but the new one isn't
	 */
	public static record InconsistentVariadicAlias(Type type) implements SpeacError {
		public enum Type {
			ALIAS_BUT_NOT_OLD,
			OLD_BUT_NOT_ALIAS
		}

		@Override public String name() {
			return "Inconsistent Variadic Alias";
		}

		@Override public String description() {
			return switch (this.type) {
				case ALIAS_BUT_NOT_OLD -> "The new alias is variadic but the old one wasn't.";
				case OLD_BUT_NOT_ALIAS -> "The new alias isn't variadic but the old one was.";
			};
		}

		@Override public String solution() {
			return "Please use a consistent alias.";
		}
	}
	public static final SpeacStaticError ALIAS_ARGUMENTS_MISMATCH = new SpeacStaticError(
			"Alias Arguments Mismatch",
			"The arguments for the new alias don't match with the ones used in the old one.",
			"Please use the same arguments."
	);
	/**
	 * Raised when the type of an argument passed to a function is not of the expected type
	 */
	public static record InvalidArgumentType(int position, Data.Type expected, Data.Type given) implements SpeacError {
		@Override public String name() {
			return "Invalid Argument Type";
		}

		@Override public String description() {
			return "Argument " + this.position + " was of type " + this.given + ".";
		}

		@Override public String solution() {
			return "Please supply an argument of type " + this.expected + ".";
		}
	}
	public static record InvalidArgumentTypeL(int position, FixedList<Data.Type> expected, Data.Type given) implements SpeacError {
		@Override public String name() {
			return "Invalid Argument Type";
		}

		@Override public String description() {
			return "Argument " + this.position + " was of type " + this.given + ".";
		}

		@Override public String solution() {
			return "Please supply an argument with type amongst " + this.expected + ".";
		}
	}
	/**
	 * Used mostly for math
	 */
	public static record InvalidArgumentTypesCombination(FixedList<Data.Type> given) implements SpeacError {
		@Override public String name() {
			return "Invalid Argument Types Combination";
		}

		@Override public String description() {
			return "The given arguments have types " + this.given + ", which are unsupported for the selected function.";
		}

		@Override public String solution() {
			return "Please supply arguments with a supported type combination.";
		}
	}
	/**
	 * Raised when a string containing a variable name is passed to a function
	 * and the referenced variable has the wrong type
	 */
	public static record InvalidReferencedType(Data.Type expected, Data.Type given) implements SpeacError {
		@Override public String name() {
			return "Invalid Referenced Variable Type";
		}

		@Override public String description() {
			return "The supplied name refers to a variable of type " + this.given + ".";
		}

		@Override public String solution() {
			return "Please supply the name of a variable of type " + this.expected + ".";
		}
	}
	public static final SpeacStaticError INVALID_MODULE = new SpeacStaticError(
			"Invalid Module",
			"This module couldn't be found or loaded.",
			"Please check that the name is correct and that the module is in the correct folder."
	);
	public static final SpeacStaticError INVALID_PROTOTYPE = new SpeacStaticError(
			"Invalid Prototype",
			SpeacError.NO_DESCRIPTION,
			"Please use a valid prototype."
	);
	/**
	 * Raised both when defining a function and when adding an alias to a function
	 */
	public static final SpeacStaticError DUPLICATE_PARAMETER = new SpeacStaticError(
			"Duplicate Parameter",
			SpeacError.NO_DESCRIPTION,
			"Please use unique parameters in this context."
	);
	// TODO check if this error is needed at all
	public static final SpeacStaticError INVALID_VARIABLE_NAME = new SpeacStaticError(
			"Invalid Variable Name",
			SpeacError.NO_DESCRIPTION,
			"Please use a valid name."
	);
	/**
	 * Is raised when an undefined function is mentioned in a string
	 * On the other hand {@link CoreErrors#UNDEFINED_FUNCTION} is raised when an instruction contains an undefined function
	 */
	public static final SpeacStaticError UNKNOWN_FUNCTION_NAME = new SpeacStaticError(
			"Unknown Function Name",
			"No function with this name is defined in the selected scope.",
			"Please define a function with this name or use the name of a defined function."
	);
	/**
	 * Is raised when an undefined variable is mentioned in a string
	 * On the other hand {@link CoreErrors#UNDEFINED_VARIABLE} is raised when an instruction contains an undefined variable
	 */
	public static final SpeacStaticError UNKNOWN_VARIABLE_NAME = new SpeacStaticError(
			"Unknown Variable Name",
			"No variable with this name is defined in the selected scope.",
			"Please define a variable with this name or use the name of a defined variable."
	);
	/**
	 * Is raised most likely when parsing a number from a string and said string does not represent a number
	 */
	public static final SpeacStaticError ILLEGAL_CONVERSION = new SpeacStaticError(
			"Illegal Conversion",
			"Could not perform the requested conversion on the given argument.",
			"Please supply an argument that allows such a conversion."
	);
	/**
	 * Raised when borrow or lend functions are called from the top level scope
	 */
	public static final SpeacStaticError ACCESSING_PARENT_OF_ROOT_SCOPE = new SpeacStaticError(
			"Accessing Parent Scope In The Root Scope",
			SpeacError.NO_DESCRIPTION,
			"Please only ever use this function inside a code block."
	);
	/**
	 * Some variadic functions may require a minimum amount of arguments
	 */
	public static record NotEnoughVarargs(int expectedMinimum, int given) implements SpeacError {
		@Override public String name() {
			return "Not Enough Variable Arguments";
		}

		@Override public String description() {
			return "The specified function requires " + this.expectedMinimum + " or more arguments but only " + this.given + " were given.";
		}

		@Override public String solution() {
			return "Please supply more arguments.";
		}
	}
	public static final SpeacStaticError MISSING_IF = new SpeacStaticError(
			"Missing If Call",
			"No previous call to the if function was detected in the current scope.",
			"Please call the if function before this function."
	);
	/**
	 * Raised when an index out of bounds is requested from a list
	 */
	public static final SpeacStaticError INDEX_ERROR = new SpeacStaticError(
			"Invalid Index",
			"The specified index is not applicable to the list.",
			"Please use an integer index between 0 (included) and the length of the list (not included)."
	);
	public static final SpeacStaticError TRIMMING_TO_ZERO_DECIMAL_PLACES = new SpeacStaticError(
			"Trimming To Zero Decimal Places",
			SpeacError.NO_DESCRIPTION,
			"Please use the 'trimmed' function in this case."
	);
	public static final SpeacStaticError ROUNDING_TO_ZERO_DECIMAL_PLACES = new SpeacStaticError(
			"Rounding To Zero Decimal Places",
			SpeacError.NO_DESCRIPTION,
			"Please use the 'rounded' function in this case."
	);
	public static final SpeacStaticError TRIMMING_TO_NEGATIVE_DECIMAL_PLACES = new SpeacStaticError(
			"Trimming To Negative Decimal Places",
			SpeacError.NO_DESCRIPTION,
			"Please specify a positive number of decimal places to trim to."
	);
	public static final SpeacStaticError ROUNDING_TO_NEGATIVE_DECIMAL_PLACES = new SpeacStaticError(
			"Rounding To Negative Decimal Places",
			SpeacError.NO_DESCRIPTION,
			"Please specify a positive number of decimal places to round to."
	);
}
